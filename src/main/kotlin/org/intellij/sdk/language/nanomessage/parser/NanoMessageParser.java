package org.intellij.sdk.language.nanomessage.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.intellij.sdk.language.minimessage.parser.MiniMessageParsing;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NanoMessageParser implements PsiParser, LightPsiParser {

	private static final List<String> PRE = List.of("pre", "nbt", "json", "gson", "legacy");

	public NanoMessageParser() {
	}

	public @NotNull ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder) {
		this.parseLight(root, builder);
		return builder.getTreeBuilt();
	}

	public void parseLight(IElementType root, PsiBuilder builder) {
		builder.enforceCommentTokens(TokenSet.EMPTY);
		PsiBuilder.Marker file = builder.mark();
		(new NanoMessageParsing(builder)).parseDocument();
		file.done(root);
	}
}
