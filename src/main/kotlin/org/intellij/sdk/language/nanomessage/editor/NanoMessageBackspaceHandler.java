package org.intellij.sdk.language.nanomessage.editor;

import static org.intellij.sdk.language.nanomessage.psi.NanoMessagePsiUtils.beforeIs;
import static org.intellij.sdk.language.nanomessage.psi.NanoMessagePsiUtils.nextIs;
import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.sdk.language.nanomessage.NanoMessageFile;
import org.intellij.sdk.language.nanomessage.NanoMessageTokenType;
import org.jetbrains.annotations.NotNull;

public class NanoMessageBackspaceHandler extends BackspaceHandlerDelegate {

  @Override
  public void beforeCharDeleted(char c, @NotNull PsiFile psiFile, @NotNull Editor editor) {

  }

  @Override
  public boolean charDeleted(char c, @NotNull PsiFile file, @NotNull Editor editor) {
    if (!(file instanceof NanoMessageFile)) {
      return true;
    }
    int offset = editor.getCaretModel().getOffset();
    PsiElement at = file.findElementAt(offset);

    if (at == null) {
      return true;
    }

    if (c == '{') {
      if (nextIs(at, NanoMessageTokenType.NM_PLACEHOLDER_END)) {
        editor.getDocument().deleteString(offset, offset + 1);
      }
    } else if (c == '}') {
      if (beforeIs(at, NanoMessageTokenType.NM_PLACEHOLDER_START)) {
        editor.getDocument().deleteString(offset - 1, offset);
      }
    }
    return false;
  }
}
