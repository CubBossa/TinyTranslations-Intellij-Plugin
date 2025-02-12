package org.intellij.sdk.language.minimessage.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.xml.XmlTagDelegate;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiniMessageTagImpl extends XmlTagImpl implements XmlTag  {

    public MiniMessageTagImpl() {
    }

    @Override
    protected @NotNull XmlTagDelegate createDelegate() {
        return new MiniMessageTagImplDelegate();
    }

    protected class MiniMessageTagImplDelegate extends MiniMessageTagDelegate {
        public MiniMessageTagImplDelegate() {
            super(MiniMessageTagImpl.this);
        }

        protected void deleteChildInternalSuper(@NotNull ASTNode child) {
            this.deleteChildInternalSuper(child);
        }

        protected TreeElement addInternalSuper(TreeElement first, ASTNode last, @Nullable ASTNode anchor, @Nullable Boolean before) {
            return this.addInternalSuper(first, last, anchor, before);
        }
    }
}
