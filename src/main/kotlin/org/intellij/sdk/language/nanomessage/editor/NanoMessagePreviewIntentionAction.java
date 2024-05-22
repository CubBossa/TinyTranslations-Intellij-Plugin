package org.intellij.sdk.language.nanomessage.editor;

import com.intellij.codeInsight.intention.impl.QuickEditHandler;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import javax.swing.JComponent;
import net.kyori.adventure.text.Component;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewComponent;
import org.intellij.sdk.language.minimessage.editor.preview.MiniMessagePreviewIntentionAction;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.intellij.sdk.language.nanomessage.TinyTranslationsProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NanoMessagePreviewIntentionAction extends MiniMessagePreviewIntentionAction {

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {

    if (editor.getUserData(AdventureComponentPreviewComponent.KEY_MM_PREVIEW_EDITOR) != null) {
      // to disable intention inside AdventureComponentPreviewComponent itself
      return false;
    }
    Pair<PsiElement, TextRange> pair = getRangePair(file, editor);
    if (pair != null && pair.first != null) {
      Language language = pair.first.getLanguage();
      return language.is(NanoMessageLanguage.INSTANCE);
    }
    PsiFile baseFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
    return baseFile != null && baseFile.getLanguage().is(NanoMessageLanguage.INSTANCE);
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    PsiFile baseFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
    if (baseFile == null || !baseFile.getLanguage().is(NanoMessageLanguage.INSTANCE)) {
      super.invoke(project, editor, file);
      return;
    }
    JComponent component = createBalloonComponent(file);
    if (component != null) QuickEditHandler.showBalloon(editor, file, component);
  }

  @Override
  protected @Nullable JComponent createBalloonComponent(final @NotNull PsiFile file) {
    var a = new AdventureComponentPreviewComponent() {
      @Override
      public Component deserialize(String s) {
        return TinyTranslationsProject.getTranslator(file).translate(s);
      }
    };
    a.update(file);
    return a.getComponent();
  }

  @Override
  public @NotNull String getText() {
    return "Preview NanoMessage Text";
  }
}
