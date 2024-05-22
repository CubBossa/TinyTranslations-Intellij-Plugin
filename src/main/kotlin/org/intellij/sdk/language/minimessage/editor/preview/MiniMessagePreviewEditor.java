package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Modifying;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewEditor;
import org.jetbrains.annotations.NotNull;

public class MiniMessagePreviewEditor extends AdventureComponentPreviewEditor {

  private static final MiniMessage miniMessage = MiniMessage.miniMessage();
  public static final Key<Supplier<List<Pair<String, String>>>> KEY_MM_REPLACEMENTS = Key.create("KEY_MM_REPLACEMENTS");

  public MiniMessagePlaceholderTable myPlaceholderTable;

  public MiniMessagePreviewEditor(Project project, PsiFile file) {
    super(project, file);
  }

  @Override
  public Component deserialize(String s, TagResolver... resolvers) {
    return miniMessage.deserialize(s, resolvers);
  }

  @Override
  public void addComponentsToRoot(Project project, PsiFile file, JPanel root) {

    this.myPlaceholderTable = new MiniMessagePlaceholderTable();

    var table = file.getUserData(KEY_MM_REPLACEMENTS);
    if (table == null) {
      table = ArrayList::new;
      file.putUserData(KEY_MM_REPLACEMENTS, myPlaceholderTable::getReplacements);
    }
    this.myPlaceholderTable.setReplacements(table.get());

    this.myPlaceholderTable.addTableModelListener(event -> {
      this.myPreviewComponent.forceUpdate(file);
    });

    var split = new JBSplitter(0.7f);
    split.setOrientation(true);
    root.setBorder(JBUI.Borders.empty(0, UIUtil.DEFAULT_HGAP));

    JComponent preview = new JPanel(new GridLayout());
    preview.add(myPreviewComponent.getComponent());
    JScrollPane scrollPane = new JBScrollPane(preview);

    JPanel previewPanel = new JPanel();
    previewPanel.add(scrollPane);

    split.setFirstComponent(scrollPane);
    split.setSecondComponent(myPlaceholderTable.getComponent());
    root.add(split);
  }

  public TagResolver getReplacementResolvers() {
    if (myPlaceholderTable == null) {
      return TagResolver.empty();
    }
    return TagResolver.builder()
        .resolvers(myPlaceholderTable.getReplacements().stream()
            .map(p -> TagResolver.resolver(p.first, (Modifying) (current, depth) -> {
              return depth == 0
                  ? deserialize(p.second, getReplacementResolvers(), Placeholder.component("slot", current))
                  : Component.empty();
            }))
            .collect(Collectors.toList()))
        .build();
  }
}
