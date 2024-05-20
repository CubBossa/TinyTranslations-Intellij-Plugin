package org.intellij.sdk.language.nanomessage.lexer;

import com.intellij.lexer.FlexAdapter;

public class NanoMessageLexerAdapter extends FlexAdapter {

  public NanoMessageLexerAdapter() {
    super(new NanoMessageLexer(null));
  }
}
