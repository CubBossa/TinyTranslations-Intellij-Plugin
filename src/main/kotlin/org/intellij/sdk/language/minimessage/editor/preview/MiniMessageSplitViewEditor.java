package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.TextEditorWithPreview;
import org.jetbrains.annotations.NotNull;

public class MiniMessageSplitViewEditor extends TextEditorWithPreview {

    public MiniMessageSplitViewEditor(@NotNull TextEditor editor, @NotNull MiniMessagePreviewEditor preview) {
        super(
                editor,
                preview,
                "MiniMessage Preview",
                Layout.SHOW_EDITOR_AND_PREVIEW,
                false
        );
    }
}
