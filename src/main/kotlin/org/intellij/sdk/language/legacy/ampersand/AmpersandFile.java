package org.intellij.sdk.language.legacy.ampersand;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class AmpersandFile extends PsiFileBase {

  protected AmpersandFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, AmpersandLanguage.INSTANCE);
  }

  @Override
  public @NotNull FileType getFileType() {
    return AmpersandFileType.INSTANCE;
  }
}
