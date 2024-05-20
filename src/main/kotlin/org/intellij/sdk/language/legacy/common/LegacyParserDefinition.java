package org.intellij.sdk.language.legacy.common;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.sdk.language.legacy.common.parser.CustomLegacyParser;
import org.intellij.sdk.language.legacy.common.psi.LegacyTypes;
import org.jetbrains.annotations.NotNull;

public class LegacyParserDefinition implements ParserDefinition {

  public static final IFileElementType FILE = new IFileElementType(LegacyLanguage.INSTANCE);

  @Override
  public @NotNull Lexer createLexer(Project project) {
    return new LegacyLexerAdapter(LegacyLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiParser createParser(Project project) {
    return new CustomLegacyParser();
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE;
  }

  @Override
  public @NotNull TokenSet getCommentTokens() {
    return TokenSet.EMPTY;
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements() {
    return TokenSet.EMPTY;
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode astNode) {
    return LegacyTypes.Factory.createElement(astNode);
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
    return new LegacyFile(fileViewProvider);
  }
}