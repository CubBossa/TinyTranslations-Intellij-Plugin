package org.intellij.sdk.language.minimessage.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlTagDelegate;
import org.jetbrains.annotations.NotNull;

public abstract class MiniMessageTagDelegate extends XmlTagDelegate {

    public MiniMessageTagDelegate(@NotNull MiniMessageTag tag) {
        super(tag);
    }

    public PsiElement setName(String name) {
        return null;
    }
}
