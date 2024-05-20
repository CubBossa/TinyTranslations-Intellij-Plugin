package org.intellij.sdk.language.common.editor;

import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.TextEditorWithPreview;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewEditor;
import org.jetbrains.annotations.NotNull;

public class AdventureComponentSplitViewEditor extends TextEditorWithPreview {

    public AdventureComponentSplitViewEditor(@NotNull TextEditor editor, @NotNull AdventureComponentPreviewEditor preview) {
        super(
                editor,
                preview,
                "Preview Editor",
                Layout.SHOW_EDITOR_AND_PREVIEW,
                false
        );
    }
}
