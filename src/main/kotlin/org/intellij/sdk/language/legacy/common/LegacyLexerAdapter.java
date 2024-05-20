package org.intellij.sdk.language.legacy.common;

import com.intellij.lexer.FlexAdapter;
import org.intellij.sdk.language.legacy.common.lexer.LegacyLexer;

public class LegacyLexerAdapter extends FlexAdapter {

  public LegacyLexerAdapter(LegacyLanguage language) {
    super(new LegacyLexer(language.getAlternateColorCode(), language.isHashHexFormat()));
  }
}
