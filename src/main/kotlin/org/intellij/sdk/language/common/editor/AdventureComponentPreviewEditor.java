package org.intellij.sdk.language.common.editor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import de.cubbossa.tinytranslations.GlobalStyles;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Inserting;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.Tag;
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

  public static final Key<Supplier<List<Pair<String, String>>>> KEY_MM_REPLACEMENTS = Key.create("KEY_MM_REPLACEMENTS");

  private final JPanel myRootPanel;
  private final AdventureComponentPreviewComponent myPreviewComponent;
  private final MiniMessagePlaceholderTable myPlaceholderTable;

  private static File replacementsFileGenerated = null;

  public AdventureComponentPreviewEditor(Project project, PsiFile file) {

    boolean replacements = file.getLanguage().isKindOf(MiniMessageLanguage.INSTANCE);

    this.myPlaceholderTable = new MiniMessagePlaceholderTable();
    if (replacements) {
      var table = file.getUserData(KEY_MM_REPLACEMENTS);
      if (table == null) {
        table = ArrayList::new;
        file.putUserData(KEY_MM_REPLACEMENTS, myPlaceholderTable::getReplacements);
      }
      this.myPlaceholderTable.setReplacements(table.get());
    }

    ComponentSerializer<Component, ? extends Component, String> serializer;
    if (file.getLanguage().is(NanoMessageLanguage.INSTANCE)) {
      serializer = NanoMessage.nanoMessage();
      List<Pair<String, String>> repl = new ArrayList<>(this.myPlaceholderTable.getReplacements());

      try {
        MessageTranslator tr = null;
        if (replacementsFileGenerated == null) {
          replacementsFileGenerated = Files.createTempDirectory("temp_tiny_translations").toFile();
          tr = TinyTranslations.globalTranslator(replacementsFileGenerated);
        }
        if (tr != null) {
          tr.getStyleSet().forEach((string, style) -> {
            repl.add(new Pair<>(string, style.asString()));
          });
        }
        this.myPlaceholderTable.setReplacements(repl);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    } else if (file.getLanguage().isKindOf(MiniMessageLanguage.INSTANCE)) {
      serializer = MiniMessage.builder().strict(false).build();
    } else if (file.getLanguage().is(AmpersandLanguage.INSTANCE)) {
      serializer = LegacyComponentSerializer.legacy('&');
    } else if (file.getLanguage().isKindOf(LegacyLanguage.INSTANCE)) {
      serializer = LegacyComponentSerializer.legacy('\u00A7');
    } else {
      throw new IllegalStateException("Editor registered for unknown language " + file.getLanguage());
    }

    this.myPreviewComponent = new AdventureComponentPreviewComponent((ComponentSerializer<Component, Component, String>) serializer, file) {
      @Override
      public TagResolver[] getResolvers() {
        if (!replacements) {
          return new TagResolver[0];
        }
        return myPlaceholderTable.getReplacements().stream()
        .map(p -> TagResolver.resolver(p.first, (Modifying) (current, depth) -> {
          if (depth == 0) {
            String s = p.second.contains("<slot>") ? p.second : p.second.concat("<slot>");
            return serializer instanceof MiniMessage
            ? ((MiniMessage) serializer).deserialize(s, Placeholder.component("slot", current))
            : ((NanoMessage) serializer).deserialize(s, Placeholder.component("slot", current));
          }
          return Component.empty();
        }))
        .toArray(TagResolver[]::new);
      }
    };


    myRootPanel = new JPanel(new GridLayout());
    if (replacements) {
      var split = new JBSplitter(0.7f);
      split.setOrientation(true);
      myRootPanel.setBorder(JBUI.Borders.empty(UIUtil.DEFAULT_VGAP, UIUtil.DEFAULT_HGAP));

      JComponent preview = new JPanel(new GridLayout());
      preview.add(myPreviewComponent.getComponent());
      JScrollPane scrollPane = new JBScrollPane(preview);

      JPanel previewPanel = new JPanel();
      previewPanel.add(scrollPane);

      split.setFirstComponent(scrollPane);
      split.setSecondComponent(myPlaceholderTable.getComponent());
      myRootPanel.add(split);
    } else {
      myRootPanel.add(myPreviewComponent.getComponent());
    }

    PsiTreeChangeAdapter listener = new PsiTreeChangeAdapter() {
      @Override
      public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        if (event.getFile() != null && event.getFile().equals(file)) {
          myPreviewComponent.update(file);
        }
      }
    };
    PsiManager.getInstance(project).addPsiTreeChangeListener(listener, this);
    if (replacements) {
      this.myPlaceholderTable.addTableModelListener(event -> {
        this.myPreviewComponent.forceUpdate(file);
      });
    }
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
