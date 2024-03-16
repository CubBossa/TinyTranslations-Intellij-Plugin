package org.intellij.sdk.language.minimessage.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.util.NlsContexts.ParsingError;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.ICustomParsingType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.containers.Stack;
import com.intellij.xml.psi.XmlPsiBundle;
import org.intellij.sdk.language.minimessage.MiniMessageTokenType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.intellij.psi.xml.XmlElementType.*;
import static org.intellij.sdk.language.minimessage.MiniMessageTokenType.*;

public class MiniMessageParsing {
    private static final int BALANCING_DEPTH_THRESHOLD = 1000;

    protected final PsiBuilder myBuilder;
    private final Stack<String> myTagNamesStack = new Stack<>();
    private final Collection<String> preTags = new ArrayList<>();

    public MiniMessageParsing(final PsiBuilder builder) {
        myBuilder = builder;
    }

    public MiniMessageParsing withPreTags(Iterable<String> preTags) {
        preTags.forEach(this.preTags::add);
        return this;
    }

    public MiniMessageParsing withPreTag(String preTag) {
        this.preTags.add(preTag);
        return this;
    }

    public void parseDocument() {
        while (!eof()) {
            parseTagContent(null);
        }
    }

    protected void parseTag() {
        assert token() == XML_START_TAG_START : "Tag start expected";
        final PsiBuilder.Marker tag = mark();

        final String tagName = parseTagHeader(tag);
        if (tagName == null) return;

        final PsiBuilder.Marker content = mark();
        parseTagContent(tagName);

        if (token() == XML_END_TAG_START) {
            final PsiBuilder.Marker footer = mark();
            advance();

            if (token() == XML_NAME) {
                String endName = myBuilder.getTokenText();
                if (endName == null || myTagNamesStack.contains(endName) && !endName.equals(myTagNamesStack.peek())) {
                    footer.rollbackTo();
                    myTagNamesStack.pop();
                    content.drop();
                    tag.done(MM_TAG);
                    return;
                }

                advance();
            }
            else {
                error(XmlPsiBundle.message("xml.parsing.closing.tag.name.missing"));
            }
            footer.drop();

            while (token() != XmlTokenType.XML_TAG_END && token() != XmlTokenType.XML_START_TAG_START && token() != XmlTokenType.XML_END_TAG_START && !eof()) {
                error(XmlPsiBundle.message("xml.parsing.unexpected.token"));
                advance();
            }

            if (token() == XML_TAG_END) {
                advance();
            }
            else {
                error(XmlPsiBundle.message("xml.parsing.closing.tag.is.not.done"));
            }
        }

        content.drop();
        myTagNamesStack.pop();
        tag.done(MM_TAG);
    }

    private @Nullable String parseTagHeader(final PsiBuilder.Marker tag) {
        advance();

        final String tagName;
        if (token() != XML_NAME || myBuilder.rawLookup(-1) == TokenType.WHITE_SPACE) {
            error(XmlPsiBundle.message("xml.parsing.tag.name.expected"));
            tagName = "";
        }
        else {
            tagName = myBuilder.getTokenText();
            assert tagName != null;
            advance();
        }
        myTagNamesStack.push(tagName);

        do {
            final IElementType tt = token();
            if (tt == MM_ATTRIBUTE_SEPARATOR) {
                parseAttribute();
            }
            else {
                break;
            }
        }
        while (true);

        if (token() == XML_EMPTY_ELEMENT_END) {
            advance();
            myTagNamesStack.pop();
            tag.done(MM_TAG);
            return null;
        }

        if (token() == XML_TAG_END) {
            advance();
        }
        else {
            error(XmlPsiBundle.message("xml.parsing.tag.start.is.not.closed"));
            myTagNamesStack.pop();
            tag.done(MM_TAG);
            return null;
        }

        if (myTagNamesStack.size() > BALANCING_DEPTH_THRESHOLD) {
            error(XmlPsiBundle.message("xml.parsing.way.too.unbalanced"));
            tag.done(MM_TAG);
            return null;
        }

        return tagName;
    }

    public boolean checkTagContentDelegate() {
        return false;
    }

    public void parseTagContentDelegate() {
    }

    public void parseTagContent(String tagName) {
        PsiBuilder.Marker xmlText = null;
        while (true) {
            final IElementType tt = token();
            if (tt == null) {
                break;
            }
            if (tagName != null && tt == XML_END_TAG_START) {
                PsiBuilder.Marker x = mark();
                advance();
                if (myTagNamesStack.contains(myBuilder.getTokenText())) {
                    x.rollbackTo();
                    break;
                }
                x.rollbackTo();
            }

            if (tagName == null || preTags.stream().noneMatch(tagName::equalsIgnoreCase)) {

                if (checkTagContentDelegate()) {
                    xmlText = terminateText(xmlText);
                    parseTagContentDelegate();
                }
                else if (tt == XML_START_TAG_START) {
                    xmlText = terminateText(xmlText);
                    parseTag();
                }
                else if (isCommentToken(tt)) {
                    xmlText = terminateText(xmlText);
                    parseComment();
                }
                else if (tt == XML_BAD_CHARACTER) {
                    xmlText = startText(xmlText);
                    final PsiBuilder.Marker error = mark();
                    advance();
                    error.error(XmlPsiBundle.message("xml.parsing.unescaped.ampersand.or.nonterminated.character.entity.reference"));
                }
                else if (tt instanceof ICustomParsingType || tt instanceof ILazyParseableElementType) {
                    xmlText = terminateText(xmlText);
                    advance();
                }
                else {
                    xmlText = startText(xmlText);
                    advance();
                }
            } else {
                xmlText = startText(xmlText);
                advance();
            }
        }

        terminateText(xmlText);
    }

    protected boolean isCommentToken(final IElementType tt) {
        return tt == XML_COMMENT_START;
    }

    private @NotNull PsiBuilder.Marker startText(@Nullable PsiBuilder.Marker xmlText) {
        if (xmlText == null) {
            xmlText = mark();
        }
        return xmlText;
    }

    protected final PsiBuilder.Marker mark() {
        return myBuilder.mark();
    }

    private static @Nullable PsiBuilder.Marker terminateText(@Nullable PsiBuilder.Marker xmlText) {
        if (xmlText != null) {
            xmlText.done(XML_TEXT);
        }
        return null;
    }

    protected void parseComment() {
        final PsiBuilder.Marker comment = mark();
        advance();
        while (true) {
            final IElementType tt = token();
            if (tt == XML_COMMENT_CHARACTERS|| tt == XML_CONDITIONAL_COMMENT_START
                    || tt == XML_CONDITIONAL_COMMENT_START_END || tt == XML_CONDITIONAL_COMMENT_END_START
                    || tt == XML_CONDITIONAL_COMMENT_END) {
                advance();
                continue;
            }
            else if (tt == XML_BAD_CHARACTER) {
                final PsiBuilder.Marker error = mark();
                advance();
                error.error(XmlPsiBundle.message("xml.parsing.bad.character"));
                continue;
            }
            if (tt == XML_COMMENT_END) {
                advance();
            }
            break;
        }
        comment.done(XML_COMMENT);
    }

    protected void parseAttribute() {
        final PsiBuilder.Marker att = mark();
        advance();
        parseAttributeValue();
        att.done(XML_ATTRIBUTE);
    }

    private static final Collection<IElementType> ATTRIBUTE_DELIMITERS = List.of(
            MM_ATTRIBUTE_SEPARATOR, XML_ATTRIBUTE_VALUE_END_DELIMITER, XML_TAG_END,
            XML_END_TAG_START, XML_EMPTY_ELEMENT_END, XML_START_TAG_START
    );

    protected Collection<IElementType> attributeDelimiters() {
        return ATTRIBUTE_DELIMITERS;
    }

    private void parseAttributeValue() {
        final PsiBuilder.Marker attValue = mark();
        if (token() == XML_ATTRIBUTE_VALUE_TOKEN) {
            while (true) {
                final IElementType tt = token();
                if (tt == null || attributeDelimiters().stream().anyMatch(t -> tt == t)) {
                    break;
                }

                if (tt == XML_BAD_CHARACTER) {
                    final PsiBuilder.Marker error = mark();
                    advance();
                    error.error(XmlPsiBundle.message("xml.parsing.unescaped.ampersand.or.nonterminated.character.entity.reference"));
                }
                else {
                    advance();
                }
            }
        }
        else if (token() == XML_ATTRIBUTE_VALUE_START_DELIMITER) {
            while (true) {
                final IElementType tt = token();
                if (tt == null || tt == XML_ATTRIBUTE_VALUE_END_DELIMITER || tt == XML_END_TAG_START || tt == XML_EMPTY_ELEMENT_END ||
                        tt == XML_START_TAG_START) {
                    break;
                }

                if (tt == XML_BAD_CHARACTER) {
                    final PsiBuilder.Marker error = mark();
                    advance();
                    error.error(XmlPsiBundle.message("xml.parsing.unescaped.ampersand.or.nonterminated.character.entity.reference"));
                }
                else {
                    advance();
                }
            }

            if (token() == XML_ATTRIBUTE_VALUE_END_DELIMITER) {
                advance();
            }
            else {
                error(XmlPsiBundle.message("xml.parsing.unclosed.attribute.value"));
            }
        }
//        final IElementType tt = token();
//        if (!(tt == null || tt == XML_TAG_END || tt == XML_EMPTY_ELEMENT_END || tt == MiniMessageTokenType.MM_ATTRIBUTE_SEPARATOR)) {
//            error(XmlPsiBundle.message("xml.parsing.attribute.value.expected"));
//        }

        attValue.done(XML_ATTRIBUTE_VALUE);
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

    protected void error(@NotNull @ParsingError String message) {
        myBuilder.error(message);
    }
}
