package org.intellij.sdk.language.minimessage.highlight;

import com.intellij.lexer.DelegateLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import org.intellij.sdk.language.minimessage.lexer.MiniMessageLexerAdapter;

public class MiniMessageHighlightingLexer extends DelegateLexer {

  public MiniMessageHighlightingLexer() {
    super(new MiniMessageLexerAdapter());
  }

  public IElementType getTokenType() {
    IElementType tokenType = this.getDelegate().getTokenType();
    if (tokenType == null) {
      return null;
    } else {
      tokenType = this.fixWrongTokenTypes(tokenType);
      return tokenType;
    }
  }

  private IElementType fixWrongTokenTypes(IElementType tokenType) {
    int state = this.getState() & 31;
    if (tokenType == XmlTokenType.XML_NAME) {
      if (state == 2 || state == 8) {
        return XmlTokenType.XML_TAG_NAME;
      }
    } else {
      if (tokenType == XmlTokenType.XML_WHITE_SPACE) {
        IElementType var10000;
        switch (state) {
          case 12:
          case 14:
            var10000 = XmlTokenType.TAG_WHITE_SPACE;
            break;
          default:
            var10000 = XmlTokenType.XML_REAL_WHITE_SPACE;
        }

        return var10000;
      }

      if ((tokenType == XmlTokenType.XML_CHAR_ENTITY_REF || tokenType == XmlTokenType.XML_ENTITY_REF_TOKEN) && state == 10) {
        return XmlTokenType.XML_COMMENT_CHARACTERS;
      }
    }

    return tokenType;
  }
}
