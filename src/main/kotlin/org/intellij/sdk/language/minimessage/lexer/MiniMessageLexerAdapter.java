package org.intellij.sdk.language.minimessage.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.xml.XmlTokenType;

public class MiniMessageLexerAdapter extends MergingLexerAdapter {

	public MiniMessageLexerAdapter() {
		super(new MergingLexerAdapter(new FlexAdapter(new MiniMessageLexer(null)), TokenSet.create(
				XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN
		)), TokenSet.create(
				XmlTokenType.XML_DATA_CHARACTERS
		));
	}
}
