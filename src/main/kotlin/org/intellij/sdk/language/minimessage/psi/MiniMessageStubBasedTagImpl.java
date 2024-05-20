package org.intellij.sdk.language.minimessage.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.xml.XmlStubBasedTag;
import com.intellij.psi.impl.source.xml.XmlTagDelegate;
import com.intellij.psi.impl.source.xml.stub.XmlTagStubImpl;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiniMessageStubBasedTagImpl extends XmlStubBasedTag implements MiniMessageTag {

  public MiniMessageStubBasedTagImpl(@NotNull XmlTagStubImpl stub, @NotNull IStubElementType<? extends XmlTagStubImpl, ? extends XmlStubBasedTag> nodeType) {
    super(stub, nodeType);
  }

  public MiniMessageStubBasedTagImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  protected XmlTagDelegate createDelegate() {
    return new MiniMessageStubBasedTagImplDelegate();
  }

  protected class MiniMessageStubBasedTagImplDelegate extends MiniMessageTagDelegate {

    public MiniMessageStubBasedTagImplDelegate() {
      super(MiniMessageStubBasedTagImpl.this);
    }

    @Override
    protected void deleteChildInternalSuper(@NotNull ASTNode child) {
      MiniMessageStubBasedTagImpl.this.deleteChildInternalSuper(child);
    }

    @Override
    protected TreeElement addInternalSuper(TreeElement first, ASTNode last, @Nullable ASTNode anchor, @Nullable Boolean before) {
      return MiniMessageStubBasedTagImpl.this.addInternalSuper(first, last, anchor, before);
    }
  }
}
