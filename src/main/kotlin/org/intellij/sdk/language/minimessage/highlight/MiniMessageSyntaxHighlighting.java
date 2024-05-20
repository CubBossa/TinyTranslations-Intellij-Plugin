package org.intellij.sdk.language.minimessage.highlight;

import com.intellij.ide.highlighter.XmlFileHighlighter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class MiniMessageSyntaxHighlighting implements SyntaxHighlighter {

  private final XmlFileHighlighter delegate;

  public MiniMessageSyntaxHighlighting() {
    delegate = new XmlFileHighlighter();
  }

  @Override
  public @NotNull Lexer getHighlightingLexer() {
    return new MiniMessageHighlightingLexer();
  }

  @Override
  public TextAttributesKey @NotNull [] getTokenHighlights(IElementType iElementType) {
    return TextAttributesKey.EMPTY_ARRAY; //delegate.getTokenHighlights(iElementType);
  }
}
