package org.intellij.sdk.language.minimessage.editor.preview;

import com.intellij.openapi.util.Pair;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.jetbrains.annotations.Nls;

public class MiniMessagePlaceholderTable {

  private JPanel myRootPanel;
  private PlaceholderTableModel myModel;
  private JBTable myTable;

  public MiniMessagePlaceholderTable() {

    myModel = new PlaceholderTableModel();

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

  public void setReplacements(List<Pair<String, String>> values) {
    myModel.values = new ArrayList<>(values);
  }

  public JComponent getComponent() {
    return myRootPanel;
  }

  public void addTableModelListener(TableModelListener listener) {
    myModel.addTableModelListener(listener);
  }

  private static class PlaceholderTableModel extends AbstractTableModel implements TableModel {

    private List<Pair<String, String>> values = new ArrayList<>();

    public PlaceholderTableModel() {
    }

    public void insertRow(int index, String key, String value) {
      values.add(Integer.max(index, values.size()), new Pair<>(key, value));
      fireTableRowsInserted(index, index);
    }

    public void deleteRow(int index) {
      if (values.size() > index) {
        values.remove(index);
      }
      fireTableRowsDeleted(index, index);
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
      fireTableCellUpdated(rowIndex, columnIndex);
    }
  }
}
