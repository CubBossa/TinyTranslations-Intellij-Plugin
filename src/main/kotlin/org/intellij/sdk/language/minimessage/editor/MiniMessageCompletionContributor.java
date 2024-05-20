package org.intellij.sdk.language.minimessage.editor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.ProcessingContext;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.intellij.sdk.language.Constants;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.intellij.sdk.language.minimessage.MiniMessageTokenType;
import org.intellij.sdk.language.minimessage.tag.Argument;
import org.intellij.sdk.language.minimessage.tag.MiniMessageTag;
import org.jetbrains.annotations.NotNull;

public class MiniMessageCompletionContributor extends CompletionContributor {

  public MiniMessageCompletionContributor() {

    extend(CompletionType.BASIC, psiElement(XmlTokenType.XML_NAME).afterLeaf("<", "{"), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters params,
                                    @NotNull ProcessingContext processingContext,
                                    @NotNull CompletionResultSet completionResultSet) {

        completionResultSet.addAllElements(colorCompletions());
        if (params.getPosition().getContainingFile().getLanguage() instanceof MiniMessageLanguage mml) {
          mml.getTags().forEach(miniMessageTag -> completionResultSet.addAllElements(miniMessageTag.getCompletions("").stream().toList()));
        }
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
        if (!(element.getContainingFile().getLanguage() instanceof MiniMessageLanguage mml)) {
          return;
        }

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

          List<MiniMessageTag> tags = mml.getTags();

          XmlTag x = attr.getParent();
          MiniMessageTag tag = tags.stream().filter(miniMessageTag -> miniMessageTag.check(x.getName())).findFirst().orElse(null);
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
      result.add(LookupElementBuilder
      .create(name)
      .withTypeText("color")
      .withIcon(Constants.createImageIcon(new Color(el.value()), 14, 14)));
    });
    return result;
  }
}
