package org.intellij.sdk.language.nanomessage.parser;

import static com.intellij.psi.xml.XmlElementType.XML_TAG;
import static com.intellij.psi.xml.XmlTokenType.XML_NAME;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.xml.psi.XmlPsiBundle;
import java.util.ArrayList;
import java.util.Collection;
import org.intellij.sdk.language.minimessage.MiniMessageTokenType;
import org.intellij.sdk.language.minimessage.parser.MiniMessageParsing;
import org.intellij.sdk.language.nanomessage.NanoMessageTokenType;

public class NanoMessageParsing extends MiniMessageParsing {

  public NanoMessageParsing(PsiBuilder builder) {
    super(builder);
  }

  @Override
  public boolean checkTagContentDelegate() {
    return token() == NanoMessageTokenType.NM_PLACEHOLDER_START;
  }

  @Override
  protected Collection<IElementType> attributeDelimiters() {
    var ret = new ArrayList<>(super.attributeDelimiters());
    ret.add(NanoMessageTokenType.NM_PLACEHOLDER_END);
    ret.add(NanoMessageTokenType.NM_CHOICE_MARKER);
    return ret;
  }

  @Override
  public void parseTagContentDelegate() {
    assert token() == NanoMessageTokenType.NM_PLACEHOLDER_START;

    final PsiBuilder.Marker tag = mark();
    advance();

    final String tagName;
    if (token() != XML_NAME || myBuilder.rawLookup(-1) == TokenType.WHITE_SPACE) {
      error(XmlPsiBundle.message("xml.parsing.tag.name.expected"));
    } else {
      tagName = myBuilder.getTokenText();
      assert tagName != null;
      advance();
    }

    boolean includesChoice = false;
    do {
      final IElementType tt = token();
      if (tt == NanoMessageTokenType.NM_CHOICE_MARKER) {
        if (includesChoice) {
          error("Placeholder is already a choice placeholder. Use only one '?' per placeholder.");
        }
        parseAttribute();
        includesChoice = true;
      } else if (tt == MiniMessageTokenType.MM_ATTRIBUTE_SEPARATOR) {
        parseAttribute();
      } else {
        break;
      }
    }
    while (true);

    if (token() == NanoMessageTokenType.NM_PLACEHOLDER_END) {
      advance();
      tag.done(XML_TAG);
    } else {
      error(XmlPsiBundle.message("xml.parsing.tag.start.is.not.closed"));
      tag.done(XML_TAG);
    }
  }
}
