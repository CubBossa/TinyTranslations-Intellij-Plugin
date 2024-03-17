package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.util.Pair;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;

public class MiniMessagePlaceholderTable {

    private JPanel myRootPanel;
    private PlaceholderTableModel myModel;
    private JBTable myTable;

    public MiniMessagePlaceholderTable() {

        myModel = new PlaceholderTableModel();
        myModel.setValueAt("placeholder", 0, 0);
        myModel.setValueAt("<green>replacement</green>", 0, 1);

        myTable = new JBTable(myModel);
        myTable.setShowGrid(true);
        myTable.setStriped(true);
        myTable.getEmptyText().setText("Insert placeholder");

        myRootPanel = ToolbarDecorator.createDecorator(myTable)
                .setAddAction(anActionButton -> {
                    myModel.insertRow(myTable.getSelectedRow() + 1, "", "");
                })
                .setRemoveAction(anActionButton -> {
                    myModel.deleteRow(myTable.getSelectedRow());
                })
                .disableUpDownActions()
                .createPanel();
    }

    public List<Pair<String, String>> getReplacements() {
        return myModel.values;
    }

    public JComponent getComponent() {
        return myRootPanel;
    }

    private static class PlaceholderTableModel implements TableModel {

        private final List<Pair<String, String>> values;

        public PlaceholderTableModel() {
            values = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                values.add(new Pair<>("", ""));
            }
        }

        public void insertRow(int index, String key, String value) {
            values.add(Integer.max(index, values.size()), new Pair<>(key, value));
        }

        public void deleteRow(int index) {
            if (values.size() > index) {
                values.remove(index);
            }
        }

        public List<Pair<String, String>> getValues() {
            return values;
        }

        @Override
        public int getRowCount() {
            return values.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Nls
        @Override
        public String getColumnName(int columnIndex) {
            return columnIndex == 0 ? "Tag Name" : "MiniMessage Replacement";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= values.size()) {
                return null;
            }
            var pair = values.get(rowIndex);
            return columnIndex == 0 ? pair.first : pair.second;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            for (int i = values.size(); i < rowIndex + 1; i++) {
                values.add(new Pair<>("", ""));
            }
            var pair = values.get(rowIndex);
            values.set(rowIndex, new Pair<>(
                    columnIndex == 0 ? aValue.toString() : pair.first,
                    columnIndex == 0 ? pair.second : aValue.toString()
            ));
        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }
}
