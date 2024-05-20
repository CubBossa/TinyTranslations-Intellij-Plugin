package org.intellij.sdk.language.legacy.common.editor;

import com.intellij.openapi.editor.ElementColorProvider;
import com.intellij.psi.PsiElement;
import java.awt.Color;
import org.intellij.sdk.language.legacy.common.psi.LegacyFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyElementColorProvider implements ElementColorProvider {

  @Override
  public @Nullable Color getColorFrom(@NotNull PsiElement psiElement) {
    if (psiElement instanceof LegacyFormatter formatter) {
      return formatter.getColor();
    }
    return null;
  }

  @Override
  public void setColorTo(@NotNull PsiElement psiElement, @NotNull Color color) {
    if (psiElement instanceof LegacyFormatter formatter) {
      formatter.setColor(color);
    }
  }
}