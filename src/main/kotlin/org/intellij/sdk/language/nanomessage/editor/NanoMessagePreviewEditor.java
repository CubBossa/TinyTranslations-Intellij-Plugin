package org.intellij.sdk.language.nanomessage.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import de.cubbossa.tinytranslations.StyleSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.sdk.language.minimessage.editor.preview.MiniMessagePreviewEditor;
import org.intellij.sdk.language.nanomessage.TinyTranslationsProject;

public class NanoMessagePreviewEditor extends MiniMessagePreviewEditor {

  public NanoMessagePreviewEditor(Project project, PsiFile file) {
    super(project, file);
    StyleSet styleSet = TinyTranslationsProject.getTranslator(file).getStyleSet();

    myPlaceholderTable.addTableModelListener(e -> {
      styleSet.clear();
      for (Pair<String, String> replacement : myPlaceholderTable.getReplacements()) {
        styleSet.put(replacement.first, replacement.second);
      }
    });
  }

  @Override
  public Component deserialize(String s, TagResolver... resolvers) {
    return TinyTranslationsProject.getTranslator(myPsiFile).translate(s, resolvers);
  }
}
