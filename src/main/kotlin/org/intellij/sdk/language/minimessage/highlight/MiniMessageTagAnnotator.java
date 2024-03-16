package org.intellij.sdk.language.minimessage.highlight;

import com.intellij.codeInsight.editorActions.XmlGtTypedHandler;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.intellij.sdk.language.minimessage.editor.MiniMessageRemoveUnexpectedArgumentsFix;
import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;
import org.intellij.sdk.language.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class MiniMessageTagAnnotator implements Annotator, DumbAware {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder annotationHolder) {
        if (!(element instanceof XmlTag tag)) {
            return;
        }
        if (element.getContainingFile().getLanguage() instanceof MiniMessageLanguage mml) {
            PsiElement[] els = new PsiElement[tag.getAttributes().length + 1];
            els[0] = tag.getChildren()[1];
            for (int i = 1; i <= tag.getAttributes().length; i++) {
                els[i] = tag.getAttributes()[i - 1].getValueElement();
            }
            for (MiniMessageTag mmTag : mml.getTags()) {
                if (!mmTag.check(tag.getName())) {
                    continue;
                }

                new MiniMessageTagAnnotationProcessor(tag, annotationHolder).annotate(els, mmTag, 1).getValue().run();
            }
        }
    }

    private static class MiniMessageTagAnnotationProcessor {

        private final XmlTag tag;
        private final AnnotationHolder annotationHolder;

        public MiniMessageTagAnnotationProcessor(XmlTag tag, AnnotationHolder annotationHolder) {
            this.tag = tag;
            this.annotationHolder = annotationHolder;
        }

        private Map.Entry<Integer, Runnable> annotate(PsiElement[] attributes, Argument argument, int level) {
            // There is no PsiElement to match any child of argument
            if (attributes.length <= level) {
                // If argument has no children it's fine, we don't complain
                if (argument.getChildren().isEmpty()) {
                    return Map.entry(1, () -> {});
                }
                // If none of the children is mandatory it's fine.
                if (argument.getChildren().stream().anyMatch(Argument::isOptional)) {
                    return Map.entry(1, () -> {});
                }
                // Otherwise we complain that we miss an attribute. We complain on the last possible PsiElement
                return Map.entry(1, () -> {
                    annotationHolder.newAnnotation(HighlightSeverity.ERROR, "Required argument missing: " + argument.getChildren().stream()
                                    .filter(a -> !a.isOptional()).map(Argument::getName)
                                    .collect(Collectors.joining("|")))
                            .range(attributes.length == 0 ? tag.getChildren()[1].getTextRange() : attributes[attributes.length - 1].getTextRange())
                            .create();
                });
            }
            // The provided argument has no children, so nothing to check
            if (argument.getChildren().isEmpty()) {
                var attr = attributes[level];
                return Map.entry(1, () -> {
                    annotationHolder.newAnnotation(HighlightSeverity.WARNING, "unexpected argument '" + attr.getText() + "'.")
                            .range(attr.getTextRange())
                            // Remove one from level because the tag itself is not an attribute but included in level count
                            .withFix(new MiniMessageRemoveUnexpectedArgumentsFix(tag, level - 1))
                            .create();
                });
            }

            // Validate the PsiElement value
            String text = StringUtil.unquote(attributes[level].getText());
            int successfulArgumentLength = 0;
            Argument chosenOne = null;
            Runnable annotateLater = () -> {};
            // Pick the child argument that matches with the most possible length
            for (Argument child : argument.getChildren()) {
                if (!child.check(text)) {
                    continue;
                }

                var e = annotate(attributes, child, level + 1);
                if (e.getKey() > successfulArgumentLength) {
                    chosenOne = child;
                    annotateLater = e.getValue();
                    successfulArgumentLength = e.getKey();
                }
            }

            // No child argument matched, so we complain
            if (chosenOne == null) {
                PsiElement attr = attributes[level];
                return Map.entry(successfulArgumentLength + 1, () -> {
                    annotationHolder.newAnnotation(HighlightSeverity.WARNING, "One of the following values expected: " + argument.getChildren().stream().map(Argument::getName).collect(Collectors.joining("|")))
                            .range(attr.getTextRange())
                            .create();
                });
            } else {
                // + 1 because it was success on this argument
                return Map.entry(successfulArgumentLength + 1, annotateLater);
            }
        }
    }
}
