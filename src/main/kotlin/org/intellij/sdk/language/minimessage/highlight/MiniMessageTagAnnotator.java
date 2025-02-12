package org.intellij.sdk.language.minimessage.highlight;

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
            for (MiniMessageTag mmTag : mml.getTags()) {
                if (!mmTag.check(tag.getName())) {
                    continue;
                }

                PsiElement[] els = new PsiElement[tag.getAttributes().length + 1];
                els[0] = tag.getChildren()[1];
                for (int i = 1; i <= tag.getAttributes().length; i++) {
                    els[i] = tag.getAttributes()[i - 1].getValueElement();
                }

                annotateRecursively(tag, annotationHolder, els, mmTag, 1).getValue().run();
            }
        }
    }

    private Map.Entry<Integer, Runnable> annotateRecursively(XmlTag tag, AnnotationHolder holder, PsiElement[] attributes, Argument argument, int level) {
        if (attributes.length <= level) {
            if (argument.getChildren().isEmpty()) {
                return Map.entry(level, () -> {});
            }
            if (argument.getChildren().stream().anyMatch(Argument::isOptional)) {
                return Map.entry(level, () -> {});
            }
            return Map.entry(level, () -> {
                holder.newAnnotation(HighlightSeverity.ERROR, "Required argument missing: " + argument.getChildren().stream()
                                .filter(a -> !a.isOptional()).map(Argument::getName)
                                .collect(Collectors.joining("|")))
                        .range(attributes.length == 0 ? tag.getChildren()[1].getTextRange() : attributes[attributes.length - 1].getTextRange())
                        .create();
            });
        }
        if (argument.getChildren().isEmpty()) {
            var attr = attributes[level];
            return Map.entry(level, () -> {
                holder.newAnnotation(HighlightSeverity.WARNING, "unexpected argument '" + attr.getText() + "'.")
                        .range(attr.getTextRange())
                        // Remove one from level because the tag itself is not an attribute but included in level count
                        .withFix(new MiniMessageRemoveUnexpectedArgumentsFix(tag, level - 1))
                        .create();
            });
        }

        String text = StringUtil.unquote(attributes[level].getText());
        int l = 0;
        Argument chosenOne = null;
        Runnable annotateLater = () -> {};
        for (Argument child : argument.getChildren()) {
            if (!child.check(text)) {
                continue;
            }

            var e = annotateRecursively(tag, holder, attributes, child, level + 1);
            if (e.getKey() > l) {
                chosenOne = child;
                annotateLater = e.getValue();
                l = e.getKey();
            }
        }
        if (chosenOne == null) {
            PsiElement attr = attributes[level];
            return Map.entry(l, () -> {
                holder.newAnnotation(HighlightSeverity.WARNING, "One of the following values expected: " + argument.getChildren().stream().map(Argument::getName).collect(Collectors.joining("|")))
                        .range(attr.getTextRange())
                        .create();
            });
        } else {
            return Map.entry(l, annotateLater);
        }
    }
}
