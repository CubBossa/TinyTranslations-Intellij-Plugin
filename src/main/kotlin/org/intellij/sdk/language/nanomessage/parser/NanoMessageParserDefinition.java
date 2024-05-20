package org.intellij.sdk.language.nanomessage.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lang.xml.XMLParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.intellij.sdk.language.nanomessage.NanoMessageFile;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.intellij.sdk.language.nanomessage.lexer.NanoMessageLexerAdapter;
import org.jetbrains.annotations.NotNull;

public class NanoMessageParserDefinition extends XMLParserDefinition {

  public static final IFileElementType FILE = new IFileElementType(NanoMessageLanguage.INSTANCE);

  @Override
  public @NotNull Lexer createLexer(Project project) {
    return new NanoMessageLexerAdapter();
  }

  @Override
  public @NotNull PsiParser createParser(Project project) {
    return new NanoMessageParser();
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE;
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new NanoMessageFile(viewProvider);
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode node) {
    return super.createElement(node);
  }
}
