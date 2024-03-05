package org.intellij.sdk.language.minimessage.editor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.*;
import com.intellij.util.Function;
import com.intellij.util.ProcessingContext;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.intellij.sdk.language.minimessage.MiniMessageTokenType;
import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static org.intellij.sdk.language.TinyTranslationsIcons.Tag;

public class MiniMessageCompletionContributor extends CompletionContributor {

    public MiniMessageCompletionContributor() {

        extend(CompletionType.BASIC, psiElement(XmlTokenType.XML_NAME).afterLeaf("<", "{"), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          @NotNull ProcessingContext processingContext,
                                          @NotNull CompletionResultSet completionResultSet) {

                completionResultSet.addAllElements(colorCompletions());

                Constants.TAGS.forEach(miniMessageTag -> {
                    boolean needsArg = !miniMessageTag.getChildren().isEmpty() && miniMessageTag.getChildren().stream()
                            .noneMatch(Argument::isOptional);
                    completionResultSet.addElement(LookupElementBuilder
                            .create(miniMessageTag.getName() + (needsArg ? ":" : ""))
                            .withPresentableText(miniMessageTag.getName()));
                });

                completionResultSet.addElement(element("bold", "decoration",
                        Tag, e -> e.withBoldness(true)));
                completionResultSet.addElement(element("underlined", "decoration",
                        Tag, e -> e.withItemTextUnderlined(true)));
                completionResultSet.addElement(element("italic", "decoration",
                        Tag, e -> e.withItemTextItalic(true)));
                completionResultSet.addElement(element("strikethrough", "decoration",
                        Tag, e -> e.withStrikeoutness(true)));
                completionResultSet.addElement(element("obfuscated", "decoration",
                        Tag));
            }
        });

        extend(CompletionType.BASIC, StandardPatterns.or(
                psiElement(XmlElementType.XML_ATTRIBUTE_VALUE),
                psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN),
                psiElement().afterLeaf(psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)),
                psiElement().afterLeaf(psiElement(MiniMessageTokenType.MM_ATTRIBUTE_SEPARATOR)),
                psiElement().afterLeaf(psiElement(XmlTokenType.XML_ATTRIBUTE_VALUE_START_DELIMITER))
        ), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters params,
                                          @NotNull ProcessingContext processingContext,
                                          @NotNull CompletionResultSet completionResultSet) {


                PsiElement element = params.getPosition();

                XmlAttribute attr = null;
                if (element instanceof XmlAttribute t) {
                    attr = t;
                } else if (element.getParent() instanceof XmlAttribute t) {
                    attr = t;
                } else if (element.getParent().getParent() instanceof XmlAttribute t) {
                    attr = t;
                }
                if (attr != null) {
                    Editor editor = params.getEditor();
                    int offset = editor.getCaretModel().getOffset();
                    String prefix = attr.getValue().substring(0, (offset - attr.getTextOffset() - 1));
                    completionResultSet = completionResultSet.withPrefixMatcher(prefix);

                    XmlTag x = attr.getParent();
                    MiniMessageTag tag = Constants.TAGS.stream().filter(miniMessageTag -> miniMessageTag.check(x.getName())).findFirst().orElse(null);
                    if (tag == null) {
                        return;
                    }
                    Collection<Argument> matches = new HashSet<>();
                    matches.add(tag);
                    int offsetInParent = -2;
                    for (int i = 0; i < offsetInParent; i++) {
                        final int fi = i;
                        matches = new HashSet<>(matches).stream()
                                .map(argument -> argument.getChildren().stream()
                                        .filter(c -> c.check(x.getAttributes()[fi].getValue()))
                                        .toList())
                                .flatMap(Collection::stream)
                                .toList();
                    }
                    matches = matches.stream().flatMap(argument -> argument.getChildren().stream()).toList();

                    for (Argument argument : matches) {
                        completionResultSet.addAllElements(argument.getCompletions(prefix));
                    }
                }
            }
        });
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getPosition().getNode().getElementType() == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
            XmlAttributeValue val = (XmlAttributeValue) parameters.getPosition().getParent();
            parameters = parameters.withPosition(val, val.getTextOffset());
        }
        super.fillCompletionVariants(parameters, result);
    }

    public static Iterable<LookupElement> colorCompletions() {
        Collection<LookupElement> result = new ArrayList<>();
        Constants.COLORS.values().forEach((el) -> {
            String name = el.toString().toLowerCase();
            result.add(element(name, "color",
                    Constants.createImageIcon(new Color(el.value()), 14, 14)));
        });
        return result;
    }

    public static LookupElement element(String name, String type, Icon icon) {
        return element(name, type, icon, null);
    }

    public static LookupElement element(String name, String type, Icon icon, Function<LookupElementBuilder, LookupElementBuilder> fun) {
        var el = LookupElementBuilder
                .create(name)
                .withPresentableText(name)
                .withTypeText(type)
                .withIcon(icon);
        if (fun != null) {
            el = fun.apply(el);
        }
        return el;
    }
}
