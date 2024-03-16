package org.intellij.sdk.language.minimessage.editor;

import com.intellij.application.options.editor.WebEditorOptions;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.jetbrains.annotations.NotNull;

public class MiniMessageTypedHandler extends TypedHandlerDelegate {

    @Override
    public @NotNull Result beforeCharTyped(final char c, final @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile editedFile, final @NotNull FileType fileType) {
        final WebEditorOptions webEditorOptions = WebEditorOptions.getInstance();

        if (c == ':') {
            PsiDocumentManager.getInstance(project).commitAllDocuments();

            FileViewProvider provider = editedFile.getViewProvider();
            int offset = editor.getCaretModel().getOffset();

            PsiElement element = null;
            if (offset < editor.getDocument().getTextLength()) {
                element = provider.findElementAt(offset, MiniMessageLanguage.class);

                if (element == null && offset > 0) {
                    element = provider.findElementAt(offset - 1, MiniMessageLanguage.class);
                }
            }
            if (element == null) {
                return Result.CONTINUE;
            }
            IElementType tt = element.getNode().getElementType();
            if (!(tt == XmlTokenType.XML_NAME || tt == XmlTokenType.XML_TAG_NAME || tt == XmlTokenType.XML_TAG_END || tt == XmlTokenType.XML_WHITE_SPACE)) {
                return Result.CONTINUE;
            }
			EditorModificationUtil.insertStringAtCaret(editor, ">", true, true, 1);
			EditorModificationUtil.insertStringAtCaret(editor, ":", true, false, 0);
			selectRelatively(editor, -1, 0);
			EditorModificationUtil.deleteSelectedText(editor);
			EditorModificationUtil.moveCaretRelatively(editor, 1);

            PsiDocumentManager.getInstance(project).commitAllDocuments();

            ApplicationManager.getApplication().invokeLater(() -> {
                AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
            });
			return Result.STOP;
        }
        return Result.CONTINUE;
    }

	private void selectRelatively(Editor editor, int from, int to) {
		editor.getCaretModel().setCaretsAndSelections(editor.getCaretModel().getCaretsAndSelections().stream()
				.map(caretState -> {
					if (caretState.getCaretPosition() == null) {
						return caretState;
					}
					return new CaretState(caretState.getCaretPosition(),
							new LogicalPosition(caretState.getCaretPosition().line, caretState.getCaretPosition().column + from),
							new LogicalPosition(caretState.getCaretPosition().line, caretState.getCaretPosition().column + to)
					);
				})
				.toList());
	}
}
