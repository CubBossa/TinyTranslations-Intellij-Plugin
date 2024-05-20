package org.intellij.sdk.language.minimessage.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class MiniMessageParser implements PsiParser, LightPsiParser {

  public MiniMessageParser() {
  }

  public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
    this.parseLight(root, builder);
    return builder.getTreeBuilt();
  }

  public void parseLight(IElementType root, PsiBuilder builder) {
    builder.enforceCommentTokens(TokenSet.EMPTY);
    PsiBuilder.Marker file = builder.mark();
    (new MiniMessageParsing(builder)).parseDocument();
    file.done(root);
  }
}
