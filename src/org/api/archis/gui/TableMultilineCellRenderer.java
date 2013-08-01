package org.api.archis.gui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;

public class TableMultilineCellRenderer implements TableCellRenderer
{
  private JTextArea ta;
  private EmptyBorder border;

  /**
   * Default constructor
   */
  public TableMultilineCellRenderer()
  {
    ta = new JTextArea();
    ta.setLineWrap(true);
    ta.setWrapStyleWord(true);
    ta.setOpaque(true);
    border = new EmptyBorder(1, 2, 1, 2);
  }

  public synchronized Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column)
  {
    if (isSelected)
      ta.setForeground(table.getSelectionForeground());
    else ta.setForeground(table.getForeground());
    if (isSelected)
      ta.setBackground(table.getSelectionBackground());
    else ta.setBackground(table.getBackground());
    ta.setFont(table.getFont());
    ta.setBorder(border);
    ta.setText((value == null) ? "" : value.toString());
    if (value != null) {
      int sw = ta.getFontMetrics(ta.getFont()).stringWidth(value.toString());
      int w = table.getCellRect(row,column,false).width;
      if (sw > w) {
        sw /= w;
        int rh = table.getRowHeight();
        sw *= rh;
        sw += rh; sw += rh;
        if (table.getRowHeight(row) != sw)
          table.setRowHeight(row,sw);
      }
    }
    ta.setSelectedTextColor(ta.getForeground());
    ta.setSelectionColor(ta.getBackground());

    return ta;
  }
}
