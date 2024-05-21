package org.intellij.sdk.language.common.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import de.cubbossa.tinytranslations.MessageTranslator;
import de.cubbossa.tinytranslations.TinyTranslations;
import de.cubbossa.tinytranslations.nanomessage.NanoMessage;
import java.awt.GridLayout;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.sdk.language.legacy.ampersand.AmpersandLanguage;
import org.intellij.sdk.language.legacy.common.LegacyLanguage;
import org.intellij.sdk.language.minimessage.MiniMessageLanguage;
import org.intellij.sdk.language.minimessage.editor.preview.MiniMessagePlaceholderTable;
import org.intellij.sdk.language.nanomessage.NanoMessageLanguage;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdventureComponentPreviewEditor extends UserDataHolderBase implements UserDataHolder, FileEditor {

  private final Project project;
  @Getter
  private final PsiFile myPsiFile;
  public final JPanel myRootPanel;
  public final AdventureComponentPreviewComponent myPreviewComponent;
  public final ComponentSerializer<Component, ? extends Component, String> mySerializer;

  public AdventureComponentPreviewEditor(Project project, PsiFile file, ComponentSerializer<Component, ? extends Component, String> serializer) {
    this.myPreviewComponent = new AdventureComponentPreviewComponent(serializer, file);
    this.mySerializer = serializer;
    this.project = project;
    this.myPsiFile = file;

    myRootPanel = new JPanel(new GridLayout());
    addComponentsToRoot(project, file, myRootPanel);

    PsiTreeChangeAdapter listener = new PsiTreeChangeAdapter() {
      @Override
      public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        if (event.getFile() != null && event.getFile().equals(file)) {
          myPreviewComponent.update(file);
        }
      }
    };
    PsiManager.getInstance(project).addPsiTreeChangeListener(listener, this);
  }

  public void updateView() {
    myPreviewComponent.forceUpdate(myPsiFile);
  }

  public void addComponentsToRoot(Project project, PsiFile psiFile, JPanel root) {
    root.add(myPreviewComponent.getComponent());
  }

  @Override
  public @NotNull JComponent getComponent() {
    return myRootPanel;
  }

  @Override
  public @Nullable JComponent getPreferredFocusedComponent() {
    return myRootPanel;
  }

  @Override
  public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
    return "MiniMessage Preview";
  }

  @Override
  public void setState(@NotNull FileEditorState state) {
  }

  @Override
  public boolean isModified() {
    return false;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
  }

  @Override
  public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

  }

  @Override
  public void dispose() {
  }
}
