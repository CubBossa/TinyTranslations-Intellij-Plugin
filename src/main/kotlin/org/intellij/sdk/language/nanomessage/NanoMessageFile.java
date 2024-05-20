package org.intellij.sdk.language.nanomessage;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class NanoMessageFile extends PsiFileBase {

  public NanoMessageFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, NanoMessageLanguage.INSTANCE);
  }

  @Override
  public @NotNull FileType getFileType() {
    return NanoMessageFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "NanoMessage File";
  }
}
