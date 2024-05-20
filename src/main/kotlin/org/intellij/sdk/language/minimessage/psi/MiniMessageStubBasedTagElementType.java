package org.intellij.sdk.language.minimessage.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.stub.XmlStubBasedElementType;
import com.intellij.psi.impl.source.xml.stub.XmlTagStubImpl;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.ICompositeElementType;
import com.intellij.psi.xml.IXmlTagElementType;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class MiniMessageStubBasedTagElementType
extends XmlStubBasedElementType<XmlTagStubImpl, MiniMessageStubBasedTagImpl>
implements ICompositeElementType, IXmlTagElementType {

  public MiniMessageStubBasedTagElementType(@NotNull String debugName, @NotNull Language language) {
    super(debugName, language);
  }

  @Override
  public @NotNull MiniMessageStubBasedTagImpl createPsi(@NotNull ASTNode node) {
    return new MiniMessageStubBasedTagImpl(node);
  }

  @Override
  public MiniMessageStubBasedTagImpl createPsi(@NotNull XmlTagStubImpl stub) {
    return new MiniMessageStubBasedTagImpl(stub, this);
  }

  @Override
  public @NotNull XmlTagStubImpl createStub(@NotNull MiniMessageStubBasedTagImpl psi, StubElement<? extends PsiElement> parentStub) {
    return new XmlTagStubImpl(psi, parentStub, this);
  }

  @Override
  public void serialize(@NotNull XmlTagStubImpl stub, @NotNull StubOutputStream dataStream) throws IOException {
    stub.serialize(dataStream);
  }

  @Override
  public @NotNull XmlTagStubImpl deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new XmlTagStubImpl(parentStub, dataStream, this);
  }
}
