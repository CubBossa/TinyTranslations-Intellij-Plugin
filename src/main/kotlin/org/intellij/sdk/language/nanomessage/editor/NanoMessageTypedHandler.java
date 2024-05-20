package org.intellij.sdk.language.nanomessage.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.jetbrains.annotations.NotNull;

public class NanoMessageTypedHandler extends TypedHandlerDelegate {

  @NotNull
  @Override
  public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
    if (c != '{') {
      return Result.CONTINUE;
    }
    if (!file.getLanguage().equals(NanoMessageLanguage.INSTANCE)) {
      return Result.CONTINUE;
    }
    EditorModificationUtil.insertStringAtCaret(editor, "}", false, 0);
    return Result.CONTINUE;
  }
}
