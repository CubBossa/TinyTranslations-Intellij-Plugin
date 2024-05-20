package org.intellij.sdk.language.tinytranslations;

import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import javax.swing.Icon;
import org.intellij.sdk.language.TinyTranslationsIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TinyTranslationsIconProvider extends IconProvider {

  @Override
  public @Nullable Icon getIcon(@NotNull PsiElement psiElement, int i) {
    if (psiElement instanceof PsiDirectory dir) {
      if (dir.getName().equalsIgnoreCase("lang")) {
        return TinyTranslationsIcons.Directory;
      }
    }
    return null;
  }
}
