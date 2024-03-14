package org.intellij.sdk.language.minimessage.editor;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.XmlTypedHandlersAdditionalSupport;
import com.intellij.psi.PsiFile;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.jetbrains.annotations.NotNull;

public class MiniMessageHandlersAdditionalSupport implements XmlTypedHandlersAdditionalSupport {
    @Override
    public boolean isAvailable(@NotNull PsiFile psiFile, @NotNull Language language) {
        return language.isKindOf(MiniMessageLanguage.INSTANCE);
    }
}
