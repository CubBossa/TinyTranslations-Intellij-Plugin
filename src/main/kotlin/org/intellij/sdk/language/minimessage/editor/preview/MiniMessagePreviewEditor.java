package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.intellij.sdk.language.common.editor.AdventureComponentPreviewEditor;

public class MiniMessagePreviewEditor extends AdventureComponentPreviewEditor {

  public static final Key<Supplier<List<Pair<String, String>>>> KEY_MM_REPLACEMENTS = Key.create("KEY_MM_REPLACEMENTS");

  public MiniMessagePlaceholderTable myPlaceholderTable;

  public MiniMessagePreviewEditor(Project project, PsiFile file) {
    this(project, file, MiniMessage.miniMessage());
  }

  public MiniMessagePreviewEditor(Project project, PsiFile file, ComponentSerializer<Component, Component, String> serializer) {
    super(project, file, serializer);
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
    JBTabbedPane tabs = new JBTabbedPane();
    populateTabs(project, file, tabs);
    if (tabs.getTabCount() > 0) {
      tabs.insertTab("Dynamic Replacements", null, myPlaceholderTable.getComponent(), null, 0);
      split.setSecondComponent(tabs);
    } else {
      split.setSecondComponent(myPlaceholderTable.getComponent());
    }
    root.add(split);
  }

  public void populateTabs(Project project, PsiFile file, JBTabbedPane tabs) {

  }
}
