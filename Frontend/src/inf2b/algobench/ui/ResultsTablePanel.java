/*
 * The MIT License
 *
 * Copyright 2015 Eziama Ubachukwu (eziama.ubachukwu@gmail.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package inf2b.algobench.ui;

import inf2b.algobench.main.AlgoBench;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Parses the returned execution results into a table and displays it. Hosts
 * options for saving the table and for copying it out.
 *
 * @author eziama ubachukwu
 */
public class ResultsTablePanel extends javax.swing.JPanel implements Serializable {

    String dataString;
    String taskID;
    boolean runAverage;

    /**
     * Creates new form ResultTablePanel
     *
     * @param dataString
     * @param taskID
     * @param resizeManually
     * @param unit
     */
    public ResultsTablePanel(String dataString, String taskID, boolean resizeManually, String unit) {
        initComponents();
        this.taskID = taskID;
        this.dataString = dataString;
        ResultsTableModel tableModel = new ResultsTableModel(this.dataString,unit);
        this.jTableResults.setModel(tableModel);
        this.runAverage = false;
        
//        this.jScrollPaneTable.setRowHeaderView(new JLabel("Hello RowHeader"));
        if (resizeManually) {
            this.jTableResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            // for adjusting column width to fit contents
            // courtesy of Rob Camick ( https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/ )
            int rowHeight;
            for (int column = 0; column < jTableResults.getColumnCount(); column++) {
                TableColumn tableColumn = jTableResults.getColumnModel().getColumn(column);
                tableColumn.setMinWidth(100);
                int preferredWidth = tableColumn.getMinWidth();
                int maxWidth = tableColumn.getMaxWidth();

                for (int row = 0; row < jTableResults.getRowCount(); row++) {
                    TableCellRenderer cellRenderer = jTableResults.getCellRenderer(row, column);
                    Component c = jTableResults.prepareRenderer(cellRenderer, row, column);
                    int width = c.getPreferredSize().width + jTableResults.getIntercellSpacing().width;
                    rowHeight = c.getPreferredSize().height + jTableResults.getIntercellSpacing().height;
                    preferredWidth = Math.max(preferredWidth, width);

                    //  We've exceeded the maximum width, no need to check other rows
                    if (preferredWidth >= maxWidth) {
                        preferredWidth = maxWidth;
                        break;
                    }
                }
                tableColumn.setPreferredWidth(preferredWidth);
            }
//            this.jTableResults.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        }
    }

    public void setDataString() {
    }
    

    private class ResultsTableModel extends AbstractTableModel {

        private final String[] columnNames;
        private final String[][] data;      
        private String unit;

        public ResultsTableModel(String dataString, String unit) {
            this.unit = unit;

            if (dataString.isEmpty()) {
                System.err.println("Empty values supplied to Table.");
            }
            String[] lines = dataString.split("\n");
            String[] tmp_columnNames = lines[0].split("\t"); // label line
            if(!tmp_columnNames[0].equals("BUCKET") && tmp_columnNames.length > 2){
                this.columnNames = new String[tmp_columnNames.length+1];
                System.arraycopy(tmp_columnNames, 0, this.columnNames, 0, tmp_columnNames.length);
                this.columnNames[tmp_columnNames.length] = "Average";
                runAverage = true;
            }else{
                this.columnNames = tmp_columnNames;
            }
            this.data = new String[lines.length - 1][this.columnNames.length];

            for (int i = 1; i < lines.length; ++i) {
                double sum = 0;
                String[] tmp_data = lines[i].split("\t");
                System.arraycopy(tmp_data, 0, this.data[i-1], 0, tmp_data.length);
                if(runAverage){
                    for(int j=1; j<tmp_data.length; j++){
                        sum += Double.parseDouble(tmp_data[j]);
                    }
                    this.data[i-1][this.columnNames.length-1] = sum/(this.columnNames.length-2)+"";
                }
            }
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }

        @Override
        public String getColumnName(int column) {
            if(column == 0)
                return columnNames[column];
            else
                return columnNames[column]+unit;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneTable = new javax.swing.JScrollPane();
        jTableResults = new javax.swing.JTable();
        jPanelTableButtons = new javax.swing.JPanel();
        jButtonCopyTable = new javax.swing.JButton();
        jButtonExportTableAsCSV = new javax.swing.JButton();

        setBackground(new java.awt.Color(219, 229, 244));
        setLayout(new java.awt.BorderLayout());

        jScrollPaneTable.setBorder(null);

        jTableResults.setFont(jTableResults.getFont().deriveFont((float)11));
        jTableResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"10", "20"},
                {"20", "45"},
                {null, null},
                {null, null}
            },
            new String [] {
                "Input Size", "Runtime (ms)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableResults.setColumnSelectionAllowed(true);
        jTableResults.setFillsViewportHeight(true);
        jTableResults.setGridColor(new java.awt.Color(204, 204, 204));
        jTableResults.setIntercellSpacing(new java.awt.Dimension(10, 0));
        jTableResults.setRowHeight(25);
        jTableResults.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPaneTable.setViewportView(jTableResults);
        jTableResults.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (jTableResults.getColumnModel().getColumnCount() > 0) {
            jTableResults.getColumnModel().getColumn(0).setHeaderValue("Input Size");
            jTableResults.getColumnModel().getColumn(1).setHeaderValue("Runtime (ms)");
        }

        add(jScrollPaneTable, java.awt.BorderLayout.CENTER);

        jPanelTableButtons.setBackground(new java.awt.Color(219, 229, 244));
        jPanelTableButtons.setMinimumSize(new java.awt.Dimension(50, 50));

        jButtonCopyTable.setText("Copy to Clipboard");
        jButtonCopyTable.setRequestFocusEnabled(false);
        jButtonCopyTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopyTableActionPerformed(evt);
            }
        });
        jPanelTableButtons.add(jButtonCopyTable);

        jButtonExportTableAsCSV.setText("Export as Tab-Separated Values");
        jButtonExportTableAsCSV.setRequestFocusEnabled(false);
        jButtonExportTableAsCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportTableAsCSVActionPerformed(evt);
            }
        });
        jPanelTableButtons.add(jButtonExportTableAsCSV);

        add(jPanelTableButtons, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCopyTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopyTableActionPerformed
        jButtonCopyTable.setEnabled(false);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(this.dataString);
        clipboard.setContents(stringSelection, stringSelection);
        jButtonCopyTable.setEnabled(true);
    }//GEN-LAST:event_jButtonCopyTableActionPerformed

    private void jButtonExportTableAsCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportTableAsCSVActionPerformed

        String fname = "table_" + this.taskID + ".txt";
        File saveFile = new File(fname);

        // ensure user doesn't inadvertently overwrite a file
        JFileChooser saveRunFileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File saveFile = this.getSelectedFile();
                if (saveFile.exists()) {
                    // confirm there's no overwrite, or it's intentional
                    int response = JOptionPane.showConfirmDialog(this,
                            "Overwrite existing file?", "Confirm Overwrite",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    switch (response) {
                        case JOptionPane.OK_OPTION:
                            super.approveSelection();
                            return;
                        default: // do nothing otherwise
                            return;
                    }
                }
                super.approveSelection(); //To change body of generated methods, choose Tools | Templates.
            }
        };
        saveRunFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        saveRunFileChooser.setDialogTitle("Export as Tab-Separated Values...");
        saveRunFileChooser.setAcceptAllFileFilterUsed(false);
        saveRunFileChooser.setFileFilter(new FileNameExtensionFilter("Text files (.txt)", "txt"));
        saveRunFileChooser.setCurrentDirectory(new File(AlgoBench.JarDirectory + File.separator + "saved"));
        saveRunFileChooser.setSelectedFile(saveFile);
        int result = saveRunFileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            saveFile = saveRunFileChooser.getSelectedFile();
            // write the file
            try (FileWriter fWriter = new FileWriter(saveFile)) {
                fWriter.write(this.dataString);
            }
            catch (IOException ex) {
                Logger.getLogger(NewTaskDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButtonExportTableAsCSVActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCopyTable;
    private javax.swing.JButton jButtonExportTableAsCSV;
    private javax.swing.JPanel jPanelTableButtons;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JTable jTableResults;
    // End of variables declaration//GEN-END:variables
}
