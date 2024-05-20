package org.intellij.sdk.language.nanomessage.psi;

import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;

public class LocalStylesFile {

  public static PropertiesFile getFile(Project project) {
    VirtualFile file = FilenameIndex.getVirtualFilesByName("styles.properties", GlobalSearchScope.allScope(project))
    .stream().findFirst().orElse(null);
    if (file != null && file.getParent() != null && file.getParent().getName().equals("lang")) {
      PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
      if (psiFile != null && psiFile.getFileType() == PropertiesFileType.INSTANCE) {
        return (PropertiesFile) psiFile;
      }
    }
    return null;
  }
}
