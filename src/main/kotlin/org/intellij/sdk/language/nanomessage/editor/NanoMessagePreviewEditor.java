package org.intellij.sdk.language.nanomessage.editor;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import de.cubbossa.tinytranslations.MessageTranslator;
import de.cubbossa.tinytranslations.StyleSet;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.intellij.sdk.language.minimessage.editor.preview.MiniMessagePreviewEditor;
import org.intellij.sdk.language.nanomessage.TinyTranslationsProject;
import org.jetbrains.annotations.NotNull;

public class NanoMessagePreviewEditor extends MiniMessagePreviewEditor {

  public NanoMessagePreviewEditor(Project project, PsiFile file) {
    super(project, file, new NanoMessageSerializer(file));

    StyleSet styleSet = ((NanoMessageSerializer) mySerializer).translator.getStyleSet();

    myPlaceholderTable.addTableModelListener(e -> {
      styleSet.clear();
      for (Pair<String, String> replacement : myPlaceholderTable.getReplacements()) {
        styleSet.put(replacement.first, replacement.second);
      }
    });
  }

  static class NanoMessageSerializer implements ComponentSerializer<Component, Component, String> {

    private final MessageTranslator translator;

    public NanoMessageSerializer(PsiFile file) {
      Module module = ProjectRootManager.getInstance(file.getProject()).getFileIndex().getModuleForFile(file.getVirtualFile());
      translator = TinyTranslationsProject.getTranslator(module);
    }

    @Override
    public @NotNull Component deserialize(@NotNull String input) {
      return translator.translate(input);
    }

    @Override
    public @NotNull String serialize(@NotNull Component component) {
      return null;
    }
  }
}
