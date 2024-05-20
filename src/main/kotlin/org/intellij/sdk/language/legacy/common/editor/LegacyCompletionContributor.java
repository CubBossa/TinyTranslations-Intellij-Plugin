package org.intellij.sdk.language.legacy.common.editor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.intellij.sdk.language.Constants;
import org.jetbrains.annotations.NotNull;

public class LegacyCompletionContributor extends CompletionContributor {

  public LegacyCompletionContributor() {
    extend(CompletionType.BASIC, psiElement().afterLeaf("&"), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters completionParameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet completionResultSet) {
        Constants.AMP_SUGGESTIONS_LIST.forEach(suggestion -> {
          completionResultSet.addElement(LookupElementBuilder
          .create(suggestion.sug())
          .withTypeText(suggestion.name())
          .withIcon(suggestion.icon()));
        });
      }
    });
  }
}
