package org.intellij.sdk.language.legacy.common.parser;

import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.COLOR;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.DECO;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.FORMATTER;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.HEXCOLOR_HASH;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.HEXCOLOR_X;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.SPECIAL;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.STRING;
import static org.intellij.sdk.language.legacy.common.psi.LegacyTypes.SYMBOL;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyParsing {

  protected final PsiBuilder myBuilder;

  public LegacyParsing(PsiBuilder psiBuilder) {
    this.myBuilder = psiBuilder;
  }

  public void parseDocument() {
    while (!eof()) {
      boolean r = parseFormatter();
      if (!r) parseText();
    }
  }

  private boolean parseFormatter() {
    PsiBuilder.Marker m = mark();
    if (token() != SYMBOL) {
      m.drop();
      return false;
    }
    advance();
    if (parseUglyHex()) {
    } else if (token() == COLOR
    || token() == DECO
    || token() == SPECIAL
    || token() == HEXCOLOR_HASH) {
      advance();
    } else {
      m.rollbackTo();
      return false;
    }
    m.done(FORMATTER);
    return true;
  }

  private boolean parseUglyHex() {
    PsiBuilder.Marker m = mark();

    if (token() != HEXCOLOR_X) {
      m.rollbackTo();
      return false;
    }
    advance();
    for (int i = 0; i < 6; i++) {
      if (token() != SYMBOL) {
        m.rollbackTo();
        return false;
      }
      advance();
      if (token() != COLOR) {
        m.rollbackTo();
        return false;
      }
      advance();
    }
    m.drop();
    return true;
  }

  private boolean parseText() {
    PsiBuilder.Marker m = mark();
    boolean r = false;
    while (!eof()) {
      PsiBuilder.Marker p = mark();
      if (parseFormatter()) {
        p.rollbackTo();
        break;
      }
      p.drop();
      advance();
      if (!r) r = true;
    }
    m.done(STRING);
    return r;
  }

  protected final PsiBuilder.Marker mark() {
    return myBuilder.mark();
  }

  protected final @Nullable IElementType token() {
    return myBuilder.getTokenType();
  }

  protected final boolean eof() {
    return myBuilder.eof();
  }

  protected final void advance() {
    myBuilder.advanceLexer();
  }

  private void error(@NotNull @NlsContexts.ParsingError String message) {
    myBuilder.error(message);
  }
}
