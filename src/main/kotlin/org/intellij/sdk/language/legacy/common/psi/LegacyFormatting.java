package org.intellij.sdk.language.legacy.common.psi;

import com.intellij.psi.PsiElement;
import java.awt.Color;
import org.jetbrains.annotations.Nullable;

public interface LegacyFormatting extends PsiElement {

  enum TextDecoration {
    ITALIC, BOLD, STRIKETHROUGH, UNDERLINED, OBFUSCATED
  }

  boolean isTypeEquals(LegacyFormatting other);

  boolean isColor();

  boolean isHexColor();

  @Nullable Color getColor();

  void setColor(Color color);

  boolean isDeco();

  @Nullable TextDecoration getDeco();

  void setDeco(TextDecoration deco);

  boolean isReset();

  void setReset();
}
