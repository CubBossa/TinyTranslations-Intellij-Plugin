package org.intellij.sdk.language.legacy.common.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.legacy.common.LegacyLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LegacyTokenType extends IElementType {

  public LegacyTokenType(@NonNls @NotNull String debugName) {
    super(debugName, LegacyLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "LegacyTokenType." + super.toString();
  }
}
