package org.intellij.sdk.language.nanomessage.psi;

import com.intellij.psi.tree.IElementType;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class NanoMessageElementType extends IElementType {

  public NanoMessageElementType(@NonNls @NotNull String debugName) {
    super(debugName, NanoMessageLanguage.INSTANCE);
  }

  public static final IElementType NM_PLACEHOLDER = new NanoMessageElementType("NM_PLACEHOLDER");
  public static final IElementType NM_CHOICE = new NanoMessageElementType("NM_CHOICE");
}
