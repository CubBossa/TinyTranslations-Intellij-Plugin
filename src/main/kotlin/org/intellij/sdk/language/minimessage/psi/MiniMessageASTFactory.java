package org.intellij.sdk.language.minimessage.psi;

import static org.intellij.sdk.language.minimessage.MiniMessageTokenType.MM_TAG;
import com.intellij.lang.ASTFactory;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LazyParseableElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.XmlFileElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ILazyParseableElementType;
import org.intellij.sdk.language.minimessage.parser.MiniMessageParserDefinition;
import org.intellij.sdk.language.nanomessage.parser.NanoMessageParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiniMessageASTFactory extends ASTFactory {

  @Override
  public @Nullable LazyParseableElement createLazy(@NotNull ILazyParseableElementType type, CharSequence text) {
    if (type == MiniMessageParserDefinition.FILE) {
      return new XmlFileElement(type, text);
    }
    if (type == NanoMessageParserDefinition.FILE) {
      return new XmlFileElement(type, text);
    }
    return super.createLazy(type, text);
  }

  @Override
  public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull CharSequence text) {
    return super.createLeaf(type, text);
  }

  @Override
  public CompositeElement createComposite(@NotNull IElementType type) {
    if (type == MM_TAG) {
      return new MiniMessageTagImpl();
    }
    return super.createComposite(type);
  }
}
