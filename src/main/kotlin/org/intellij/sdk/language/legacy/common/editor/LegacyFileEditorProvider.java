package org.intellij.sdk.language.legacy.common.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewEditor;
import org.intellij.sdk.language.common.editor.AdventureComponentSplitViewEditor;
import org.intellij.sdk.language.legacy.common.LegacyFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LegacyFileEditorProvider implements FileEditorProvider, DumbAware {

  @Override
  public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
    return file.getFileType().equals(LegacyFileType.INSTANCE);
  }

  @Override
  public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {

    PsiFile f = PsiManager.getInstance(project).findFile(file);
    var serializer = LegacyComponentSerializer.legacy('\u00A7');
    return new AdventureComponentSplitViewEditor((TextEditor) TextEditorProvider.getInstance().createEditor(project, file),
        new AdventureComponentPreviewEditor(project, f) {
          @Override
          public Component deserialize(String s, TagResolver... resolvers) {
            return serializer.deserialize(s);
          }
        });
  }

  @Override
  public @NotNull @NonNls String getEditorTypeId() {
    return "AdventureComponentSplitViewEditor";
  }

  @Override
  public @NotNull FileEditorPolicy getPolicy() {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }
}
