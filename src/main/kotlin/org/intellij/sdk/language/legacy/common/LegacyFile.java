package org.intellij.sdk.language.legacy.common;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class LegacyFile extends PsiFileBase {

  protected LegacyFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, LegacyLanguage.INSTANCE);
  }

  @Override
  public @NotNull FileType getFileType() {
    return LegacyFileType.INSTANCE;
  }
}
