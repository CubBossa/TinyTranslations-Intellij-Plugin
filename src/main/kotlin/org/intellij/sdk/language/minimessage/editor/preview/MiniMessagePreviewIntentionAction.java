package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.codeInsight.intention.impl.QuickEditAction;
import com.intellij.codeInsight.intention.impl.QuickEditHandler;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import javax.swing.Icon;
import javax.swing.JComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.intellij.sdk.language.TinyTranslationsIcons;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewComponent;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiniMessagePreviewIntentionAction extends QuickEditAction implements Iconable {

  @Override
  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    if (editor.getUserData(AdventureComponentPreviewComponent.KEY_MM_PREVIEW_EDITOR) != null) {
      // to disable intention inside AdventureComponentPreviewComponent itself
      return false;
    }
    Pair<PsiElement, TextRange> pair = getRangePair(file, editor);
    if (pair != null && pair.first != null) {
      Language language = pair.first.getLanguage();
      return language.isKindOf(MiniMessageLanguage.INSTANCE);
    }
    PsiFile baseFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
    return baseFile != null && baseFile.getLanguage().isKindOf(MiniMessageLanguage.INSTANCE);
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    PsiFile baseFile = InjectedLanguageManager.getInstance(project).getTopLevelFile(file);
    if (baseFile == null || !baseFile.getLanguage().isKindOf(MiniMessageLanguage.INSTANCE)) {
      super.invoke(project, editor, file);
      return;
    }
    JComponent component = createBalloonComponent(file);
    if (component != null) QuickEditHandler.showBalloon(editor, file, component);
  }

  @Override
  protected @Nullable JComponent createBalloonComponent(@NotNull PsiFile file) {
    final Project project = file.getProject();
    final Document document = PsiDocumentManager.getInstance(project).getDocument(file);
    if (document != null) {
      return new AdventureComponentPreviewComponent(MiniMessage.miniMessage(), file).getComponent();
    }
    return null;
  }

  @Override
  public @NotNull String getText() {
    return "Preview MiniMessage Text";
  }

  @Override
  protected boolean isShowInBalloon() {
    return true;
  }

  @Override
  public Icon getIcon(int flags) {
    return TinyTranslationsIcons.Tag;
  }
}
