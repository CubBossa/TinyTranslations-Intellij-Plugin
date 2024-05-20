package org.intellij.sdk.language.minimessage.editor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlText;
import com.intellij.spellchecker.inspections.PlainTextSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

public class MiniMessageSpellCheckingStrategy extends SpellcheckingStrategy {

  @Override
  public @NotNull Tokenizer<?> getTokenizer(PsiElement element) {
    if (element instanceof XmlText) {
      return new XmlTextTokenizer();
    }
    return super.getTokenizer(element);
  }

  public static class XmlTextTokenizer extends Tokenizer<XmlText> {

    @Override
    public void tokenize(@NotNull XmlText xmlText, @NotNull TokenConsumer tokenConsumer) {
      tokenConsumer.consumeToken(xmlText, false, PlainTextSplitter.getInstance());
    }
  }
}
