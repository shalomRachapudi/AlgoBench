/*
 * Created by Yufen Wang.
 * 2016
 */


package inf2b.algobench.ui;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.XChartPanel;
import inf2b.algobench.main.AlgoBench;
import inf2b.algobench.model.MyChart;
import inf2b.algobench.util.CheckBoxListRenderer;
import inf2b.algobench.util.CompareDetailTableRenderer;
import java.awt.CardLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;



public class CompareChartPanel extends JPanel {

    MyChart chart;
    String compareID;
    private DefaultListModel<JCheckBox> seriesListModel;
    private DefaultTableModel tableModel;
    private CardLayout cardlayout;

    
    public CompareChartPanel(String algotype) {
        initComponents();
        this.compareID = algotype.toUpperCase()+"_" + (System.currentTimeMillis() % 1000);
        this.seriesListModel = new DefaultListModel<>();
        this.cardlayout = (CardLayout)jPanelChartHolder.getLayout();
        this.jButtonChart.setEnabled(false);
        this.jTableDetails.setDefaultRenderer(Object.class, new CompareDetailTableRenderer());
    }

    public void addCompareChart(MyChart chart){
        this.chart = chart;
        jPanelChartHolder.add(new XChartPanel(chart),"cardChart");
        cardlayout.show(jPanelChartHolder, "cardChart");
        
        Iterator<Map.Entry<String, Series>> entries = chart.getSeriesMap().entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String, Series> entry = entries.next();
            String s = entry.getKey();
            seriesListModel.addElement(new JCheckBox(s));
        }
        jListSeries.setModel(seriesListModel);
        jListSeries.setCellRenderer(new CheckBoxListRenderer());
        
        for(int i=0; i<seriesListModel.size(); i++){
            JCheckBox c = (JCheckBox)seriesListModel.getElementAt(i);
            c.setSelected(true);
        }
        
    }
    
    public void addCompareTable(DefaultTableModel model){
        this.tableModel = model;
        for(int i=0; i<model.getRowCount(); i++){
            Map<String, Series> seriesMap = new LinkedHashMap<>();
            seriesMap = chart.getSeriesMap();
            String seriesName = model.getValueAt(i, 1) + "";
            Series s = seriesMap.get(seriesName);
            model.setValueAt(s.getStrokeColor(), i, 0);
        }
        jTableDetails.setModel(this.tableModel);
        jTableDetails.getColumnModel().getColumn(0).setMinWidth(0);
        jTableDetails.getColumnModel().getColumn(0).setMaxWidth(20);
        jTableDetails.getColumnModel().getColumn(1).setMinWidth(0);
        jTableDetails.getColumnModel().getColumn(1).setMaxWidth(100);
        jTableDetails.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTableDetails.getColumnModel().getColumn(1).setPreferredWidth(65);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelTableButtons = new javax.swing.JPanel();
        jButtonSaveChartAsImage = new javax.swing.JButton();
        jPanelChartHolder = new javax.swing.JPanel();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTableDetails = new javax.swing.JTable();
        jPanelSetting = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListSeries = new javax.swing.JList<>();
        jButtonTable = new javax.swing.JButton();
        jButtonChart = new javax.swing.JButton();

        jPanelTableButtons.setBackground(new java.awt.Color(219, 229, 244));
        jPanelTableButtons.setMinimumSize(new java.awt.Dimension(50, 50));

        jButtonSaveChartAsImage.setText("Save as Image");
        jButtonSaveChartAsImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSaveChartAsImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveChartAsImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTableButtonsLayout = new javax.swing.GroupLayout(jPanelTableButtons);
        jPanelTableButtons.setLayout(jPanelTableButtonsLayout);
        jPanelTableButtonsLayout.setHorizontalGroup(
            jPanelTableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTableButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSaveChartAsImage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTableButtonsLayout.setVerticalGroup(
            jPanelTableButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTableButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSaveChartAsImage)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelChartHolder.setLayout(new java.awt.CardLayout());

        jTableDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDetails.setEnabled(false);
        jTableDetails.setRowHeight(20);
        jTableDetails.setSelectionBackground(new java.awt.Color(102, 153, 204));
        jTableDetails.setShowGrid(true);
        jScrollPaneTable.setViewportView(jTableDetails);

        jPanelChartHolder.add(jScrollPaneTable, "cardTable");

        jPanelSetting.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Chart Settings"));
        jPanelSetting.setMaximumSize(new java.awt.Dimension(200, 32767));

        jListSeries.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListSeriesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListSeries);

        jButtonTable.setText("Show Table");
        jButtonTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTableActionPerformed(evt);
            }
        });

        jButtonChart.setText("Show Chart");
        jButtonChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSettingLayout = new javax.swing.GroupLayout(jPanelSetting);
        jPanelSetting.setLayout(jPanelSettingLayout);
        jPanelSettingLayout.setHorizontalGroup(
            jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingLayout.createSequentialGroup()
                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSettingLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelSettingLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonChart)
                            .addComponent(jButtonTable))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanelSettingLayout.setVerticalGroup(
            jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonTable)
                .addGap(18, 18, 18)
                .addComponent(jButtonChart)
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTableButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelChartHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelChartHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                    .addComponent(jPanelSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTableButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSaveChartAsImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveChartAsImageActionPerformed
        try {
            String chartID = this.compareID + ".jpg";
            File saveFile = new File(chartID);
            // create file filters
            List<String[]> filters = new ArrayList<>();
            filters.add(new String[]{"JPEG Image (.jpg)", "jpg"});
            filters.add(new String[]{"PNG Image (.png)", "png"});
            filters.add(new String[]{"BMP Image (.bmp)", "bmp"});

            // ensure user doesn't inadvertently overwrite a file
            JFileChooser saveChartFileChooser = new JFileChooser() {
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
            saveChartFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            saveChartFileChooser.setDialogTitle("Save Chart As Image");
            saveChartFileChooser.setCurrentDirectory(new File(AlgoBench.JarDirectory + File.separator + "saved"));
            saveChartFileChooser.setSelectedFile(saveFile);
//            saveChartFileChooser.ensureFileIsVisible(saveFile);
            // filters
            saveChartFileChooser.setAcceptAllFileFilterUsed(false);
            for (String[] s : filters) {
                saveChartFileChooser.addChoosableFileFilter(new FileNameExtensionFilter(s[0], s[1]));
            }

            int result = saveChartFileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                saveFile = saveChartFileChooser.getSelectedFile();
                // saving chart
                String desc = saveChartFileChooser.getFileFilter().getDescription();
                String ext = "jpg";
                for (String[] ff : filters) {
                    if (ff[0].toLowerCase().equals(desc.toLowerCase())) {
                        ext = ff[1].toLowerCase();
                        break;
                    }
                }
                switch (ext) {
                    case "jpg":
                        BitmapEncoder.saveBitmap(chart, saveFile.getCanonicalPath(), BitmapFormat.JPG);
                        break;
                    case "png":
                        BitmapEncoder.saveBitmap(chart, saveFile.getCanonicalPath(), BitmapFormat.PNG);
                        break;
                    case "bmp":
                        BitmapEncoder.saveBitmap(chart, saveFile.getCanonicalPath(), BitmapFormat.BMP);
                        break;
                    default:
                        BitmapEncoder.saveBitmap(chart, saveFile.getCanonicalPath(), BitmapFormat.JPG);
                }
            }

//            BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300);
//            BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.JPG, 300);
//            BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.GIF, 300);
        }
        catch (IOException ex) {
            Logger.getLogger(CompareChartPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonSaveChartAsImageActionPerformed

    private void jListSeriesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListSeriesMouseClicked
        int index = jListSeries.locationToIndex(evt.getPoint());
        if (index != -1) {
           JCheckBox c = (JCheckBox)seriesListModel.getElementAt(index);
           
           if(chart.getCurrentSeriesNum()==1 && c.isSelected()==true){
               JOptionPane.showMessageDialog(this, "You have to choose at least one line.",
                "Notice", JOptionPane.INFORMATION_MESSAGE);
               c.setSelected(true);
               return;
           }
           
           c.setSelected(!c.isSelected());
           jListSeries.repaint();
           if(c.isSelected()) chart.showSeries(c.getText());
           else chart.hideSeries(c.getText());
           
           jPanelChartHolder.revalidate();
           jPanelChartHolder.repaint();
        }
    }//GEN-LAST:event_jListSeriesMouseClicked

    private void jButtonTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTableActionPerformed
        cardlayout.show(jPanelChartHolder, "cardTable");
        this.jButtonTable.setEnabled(false);
        this.jButtonChart.setEnabled(true);
    }//GEN-LAST:event_jButtonTableActionPerformed

    private void jButtonChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChartActionPerformed
        cardlayout.show(jPanelChartHolder, "cardChart");
        this.jButtonTable.setEnabled(true);
        this.jButtonChart.setEnabled(false);
    }//GEN-LAST:event_jButtonChartActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonChart;
    private javax.swing.JButton jButtonSaveChartAsImage;
    private javax.swing.JButton jButtonTable;
    private javax.swing.JList<JCheckBox> jListSeries;
    private javax.swing.JPanel jPanelChartHolder;
    private javax.swing.JPanel jPanelSetting;
    private javax.swing.JPanel jPanelTableButtons;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JTable jTableDetails;
    // End of variables declaration//GEN-END:variables
}
