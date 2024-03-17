package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.sdk.language.minimessage.MiniMessageFileType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MiniMessagePreviewEditor extends UserDataHolderBase implements UserDataHolder, FileEditor {

    private final JPanel myRootPanel;
    private final MiniMessagePreviewComponent myPreviewComponent;
    private final MiniMessagePlaceholderTable myPlaceholderTable;

    public MiniMessagePreviewEditor(Project project, PsiFile file) {

        this.myPlaceholderTable = new MiniMessagePlaceholderTable();

        this.myPreviewComponent = new MiniMessagePreviewComponent(file) {
            @Override
            public TagResolver[] getResolvers() {
                return myPlaceholderTable.getReplacements().stream()
                        .map(p -> Placeholder.parsed(p.first, p.second))
                        .toArray(TagResolver[]::new);
            }
        };
        myRootPanel = new JPanel(new GridLayout(2 ,1));
        myRootPanel.setBorder(JBUI.Borders.empty(UIUtil.DEFAULT_VGAP, UIUtil.DEFAULT_HGAP));
        myRootPanel.add(myPreviewComponent.getComponent());
        myRootPanel.add(myPlaceholderTable.getComponent());


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
