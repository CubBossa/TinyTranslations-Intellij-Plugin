package org.intellij.sdk.language.nanomessage.highlight;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.nanomessage.lexer.NanoMessageLexerAdapter;
import org.jetbrains.annotations.NotNull;

public class NanoMessageSyntaxHighlighter extends SyntaxHighlighterBase {

  public static final TextAttributesKey PLACEHOLDER = createTextAttributesKey("NANOMESSAGE_PLACEHOLDER", DefaultLanguageHighlighterColors.IDENTIFIER);//color(new Color(0x56A8F5)));
  public static final TextAttributesKey CHOICE = createTextAttributesKey("NANOMESSAGE_CHOICE", DefaultLanguageHighlighterColors.IDENTIFIER); //color(new Color(0x6E7FF5)));

  @Override
  public @NotNull Lexer getHighlightingLexer() {
    return new NanoMessageLexerAdapter();
  }

  @Override
  public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
    return TextAttributesKey.EMPTY_ARRAY;
  }
}
