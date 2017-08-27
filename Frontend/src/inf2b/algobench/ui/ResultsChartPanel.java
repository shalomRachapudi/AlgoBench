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

/*
 * Modified by Yufen Wang.
 * 2016
 */

package inf2b.algobench.ui;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.BitmapEncoder.BitmapFormat;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.XChartPanel;
import inf2b.algobench.main.AlgoBench;
import inf2b.algobench.model.MyChart;
import inf2b.algobench.util.CheckBoxListRenderer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Holds and displays the charts of runtimes, with buttons for extra stuff like
 * saving and going between chart types
 * @author eziama ubachukwu and Yufen WANG
 */
public class ResultsChartPanel extends JPanel {

    MyChart chart;
    String taskID;
    ButtonGroup showbgroup;
    boolean hasAverage;
    private DefaultListModel<JCheckBox> seriesListModel;

    /**
     * Creates new form ResultsChartPanel
     *
     * @param taskID
     */
    public ResultsChartPanel(String taskID) {
        initComponents();
        this.taskID = taskID;
        this.seriesListModel = new DefaultListModel<>();
        
        showbgroup = new ButtonGroup();
        showbgroup.add(this.jRadioShowall);
        showbgroup.add(this.jRadioShowaverage);
        showbgroup.add(this.jRadioWithoutaverage);
        showbgroup.add(this.jRadioButtonCustomshows);
        
        this.jRadioShowall.setEnabled(false);
        this.jRadioShowaverage.setEnabled(false);
        this.jRadioWithoutaverage.setEnabled(false);
        this.jRadioButtonCustomshows.setEnabled(false);
        
        this.jLabel3.setVisible(false);
        this.jComboBoxBoundary1.setVisible(false);
        this.jLabel4.setVisible(false);
        this.jTextFieldParam1.setVisible(false);
        
        this.jTextFieldParam.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBoundary(1);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBoundary(1);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        this.jTextFieldParam1.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBoundary(2);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBoundary(2);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    public MyChart getChart()
    {
        return chart;
    }
    public void addResultChart(MyChart chart){
        this.chart = chart;
        jPanelChartHolder.add(new XChartPanel(chart),"chart");
        this.jRadioShowall.setEnabled(true);
        this.jRadioShowall.setSelected(true);
        
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
        setSeriesListEnable(false);
        
        if(chart.getStyleManager().getChartType().equals(ChartType.Bar)){
            this.jTextFieldParam.setVisible(false);
            this.jComboBoxBoundary.setVisible(false);
            this.jLabel1.setVisible(false);
            this.jLabel2.setVisible(false);
            this.jCheckBoxAdd.setVisible(false);
        }
        
    }
    
    public void sethasAverage(boolean b){
        this.hasAverage = b;
        if(b){
            this.jRadioShowaverage.setEnabled(true);
            this.jRadioWithoutaverage.setEnabled(true);
            this.jRadioButtonCustomshows.setEnabled(true);
        }else{
            this.jRadioShowaverage.setEnabled(false);
            this.jRadioWithoutaverage.setEnabled(false);
            this.jRadioButtonCustomshows.setEnabled(false);
        }
    }
    
    //return average series if no average ruturn run 1
    public Series getAverageSeries(){
        Series s;
        if(hasAverage){
            s = chart.getSeriesMap().get("Average");
        }else{
            s = chart.getSeriesMap().get("Run 1");
        }
        return s;
    }
    
    public void setSeriesListEnable(boolean b){
        this.jListSeries.setEnabled(b);
        for(int i=0; i<seriesListModel.size(); i++){
            JCheckBox c = (JCheckBox)seriesListModel.getElementAt(i);
            c.setEnabled(b);
        }
    }
    
    private void displayError(String message) {
        if (message.length() > 0) {
            message = "INFO: " + message;
        }
        jTextAreaInfo.setText(message);
    }
    
    private boolean validateNumberType(String number) {
        Pattern numPattern = Pattern.compile("^(\\d*\\.)?\\d+$");
        Matcher numMatcher = numPattern.matcher(number.trim());
        if (!numMatcher.find()) {
            displayError("Invalid number format supplied. Use only positive integers or 0.");
            return false;
        }
        return true;
    }
    
    private void updateBoundary(int Num){
        displayError("");
        double param;
        String func;
        if(Num == 1){
            if(!validateNumberType(this.jTextFieldParam.getText())) return;
            param = Double.parseDouble(this.jTextFieldParam.getText())*0.00001;
            func = (String)jComboBoxBoundary.getSelectedItem();
        }else{
            if(!validateNumberType(this.jTextFieldParam1.getText())) return;
            param = Double.parseDouble(this.jTextFieldParam1.getText())*0.00001;
            func = (String)jComboBoxBoundary1.getSelectedItem();
        }
        
        //add or update a Boundary series
        Vector<Double> xData;
        xData = new Vector(chart.getAllSeriesMap().get("Run 1").getXData());
        Vector<Double> yData = new Vector<>();
        double tmp;
        switch(func){
            case "None":
                chart.hideSeries("Standard"+Num);
                jPanelChartHolder.revalidate();
                jPanelChartHolder.repaint();
                return;
            case "1":
                for(double x : xData){
                    yData.add(param);
                }
                break;
            case "logN":
                for(double x : xData){
                    if(x!=0){
                        tmp = Math.log(x)/Math.log(2);
                    }else{
                        tmp = 0;
                    }
                    yData.add(param*tmp);
                }
                break;
            case "N":
                for(double x : xData){
                    yData.add(param*x);
                }
                break;
            case "NlogN":
                for(double x : xData){
                    if(x!=0){
                        tmp = Math.log(x)/Math.log(2);
                    }else{
                        tmp = 0;
                    }
                    yData.add(param*x*tmp);
                }
                break;
            case "N^2":
                for(double x : xData){
                    yData.add(param*0.0001*x*x);
                }
                break;
        }
        if(chart.getAllSeriesMap().get("Standard"+Num) != null){
            chart.getAllSeriesMap().get("Standard"+Num).replaceYData(yData);
        }else{
            chart.addSeries("Standard"+Num, xData, yData);
        }
        chart.showSeries("Standard"+Num);
        jPanelChartHolder.revalidate();
        jPanelChartHolder.repaint();
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
        jPanelSetting = new javax.swing.JPanel();
        jRadioShowall = new javax.swing.JRadioButton();
        jRadioShowaverage = new javax.swing.JRadioButton();
        jRadioWithoutaverage = new javax.swing.JRadioButton();
        jRadioButtonCustomshows = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListSeries = new javax.swing.JList<>();
        jComboBoxBoundary = new javax.swing.JComboBox<>();
        jTextFieldParam = new javax.swing.JTextField();
        jPanelInfo = new javax.swing.JPanel();
        jTextAreaInfo = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jCheckBoxAdd = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxBoundary1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldParam1 = new javax.swing.JTextField();

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

        jPanelSetting.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Chart Settings"));
        jPanelSetting.setMaximumSize(new java.awt.Dimension(200, 32767));

        jRadioShowall.setText("Show all");
        jRadioShowall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioShowallActionPerformed(evt);
            }
        });

        jRadioShowaverage.setText("Show only average");
        jRadioShowaverage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioShowaverageActionPerformed(evt);
            }
        });

        jRadioWithoutaverage.setText("Show w/o average");
        jRadioWithoutaverage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioWithoutaverageActionPerformed(evt);
            }
        });

        jRadioButtonCustomshows.setText("Custom");
        jRadioButtonCustomshows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCustomshowsActionPerformed(evt);
            }
        });

        jListSeries.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListSeriesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListSeries);

        jComboBoxBoundary.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "1", "logN", "N", "NlogN", "N^2" }));
        jComboBoxBoundary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxBoundaryActionPerformed(evt);
            }
        });

        jTextFieldParam.setText("1");

        jPanelInfo.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jPanelInfo.setMinimumSize(new java.awt.Dimension(400, 35));
        jPanelInfo.setPreferredSize(new java.awt.Dimension(400, 35));
        jPanelInfo.setLayout(new java.awt.BorderLayout());

        jTextAreaInfo.setEditable(false);
        jTextAreaInfo.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextAreaInfo.setForeground(new java.awt.Color(147, 0, 0));
        jTextAreaInfo.setLineWrap(true);
        jTextAreaInfo.setTabSize(4);
        jTextAreaInfo.setWrapStyleWord(true);
        jTextAreaInfo.setBorder(null);
        jTextAreaInfo.setOpaque(false);
        jTextAreaInfo.setPreferredSize(new java.awt.Dimension(100, 35));
        jPanelInfo.add(jTextAreaInfo, java.awt.BorderLayout.PAGE_START);

        jLabel1.setText("Standard function:");

        jLabel2.setText("Constant:0.00001*");

        jCheckBoxAdd.setText("Add another one");
        jCheckBoxAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAddActionPerformed(evt);
            }
        });

        jLabel3.setText("Standard function:");

        jComboBoxBoundary1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "1", "logN", "N", "NlogN", "N^2" }));
        jComboBoxBoundary1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxBoundary1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Constant:0.00001*");

        jTextFieldParam1.setText("1");

        javax.swing.GroupLayout jPanelSettingLayout = new javax.swing.GroupLayout(jPanelSetting);
        jPanelSetting.setLayout(jPanelSettingLayout);
        jPanelSettingLayout.setHorizontalGroup(
            jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxAdd)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSettingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(jPanelSettingLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSettingLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxBoundary1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelSettingLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxBoundary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelSettingLayout.createSequentialGroup()
                        .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSettingLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldParam, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelSettingLayout.createSequentialGroup()
                                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRadioButtonCustomshows)
                                    .addComponent(jRadioShowaverage)
                                    .addComponent(jRadioWithoutaverage)
                                    .addComponent(jRadioShowall))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanelSettingLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldParam1)))
                        .addContainerGap())))
        );
        jPanelSettingLayout.setVerticalGroup(
            jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSettingLayout.createSequentialGroup()
                .addComponent(jRadioShowall)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioWithoutaverage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioShowaverage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonCustomshows)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxBoundary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSettingLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBoxAdd))
                    .addComponent(jTextFieldParam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxBoundary1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSettingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldParam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelTableButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelChartHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 736, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSetting, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelChartHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelSetting, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelTableButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void saveChart()
    {
        try {
            String chartID = chart.getStyleManager().getChartType().toString()+"Chart" + this.taskID + ".jpg";
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
            Logger.getLogger(ResultsChartPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void jButtonSaveChartAsImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveChartAsImageActionPerformed
        saveChart();
    }//GEN-LAST:event_jButtonSaveChartAsImageActionPerformed

    private void jRadioShowaverageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioShowaverageActionPerformed
        setSeriesListEnable(false);
        chart.showOnlyAverage();
        jPanelChartHolder.revalidate();
        jPanelChartHolder.repaint();
    }//GEN-LAST:event_jRadioShowaverageActionPerformed

    private void jRadioShowallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioShowallActionPerformed
        setSeriesListEnable(false);
        chart.showAll();
        jPanelChartHolder.revalidate();
        jPanelChartHolder.repaint();
    }//GEN-LAST:event_jRadioShowallActionPerformed

    private void jRadioWithoutaverageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioWithoutaverageActionPerformed
        setSeriesListEnable(false);
        chart.showWithoutAverage();
        jPanelChartHolder.revalidate();
        jPanelChartHolder.repaint();
    }//GEN-LAST:event_jRadioWithoutaverageActionPerformed

    private void jListSeriesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListSeriesMouseClicked
        int index = jListSeries.locationToIndex(evt.getPoint());
        boolean b = jListSeries.isEnabled();
        if (index != -1 && b) {
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

    private void jRadioButtonCustomshowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCustomshowsActionPerformed
        setSeriesListEnable(true);
        chart.showAsModel(seriesListModel);
        jPanelChartHolder.revalidate();
        jPanelChartHolder.repaint();
    }//GEN-LAST:event_jRadioButtonCustomshowsActionPerformed

    private void jComboBoxBoundaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxBoundaryActionPerformed
        updateBoundary(1);
    }//GEN-LAST:event_jComboBoxBoundaryActionPerformed

    private void jCheckBoxAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAddActionPerformed
        if(this.jCheckBoxAdd.isSelected()){
            this.jLabel3.setVisible(true);
            this.jComboBoxBoundary1.setVisible(true);
            this.jLabel4.setVisible(true);
            this.jTextFieldParam1.setVisible(true);
            updateBoundary(2);
            
        }else{
            this.jLabel3.setVisible(false);
            this.jComboBoxBoundary1.setVisible(false);
            this.jLabel4.setVisible(false);
            this.jTextFieldParam1.setVisible(false);
            
            chart.hideSeries("Standard2");
            jPanelChartHolder.revalidate();
            jPanelChartHolder.repaint();
        }
    }//GEN-LAST:event_jCheckBoxAddActionPerformed

    private void jComboBoxBoundary1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxBoundary1ActionPerformed
        updateBoundary(2);
    }//GEN-LAST:event_jComboBoxBoundary1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSaveChartAsImage;
    private javax.swing.JCheckBox jCheckBoxAdd;
    private javax.swing.JComboBox<String> jComboBoxBoundary;
    private javax.swing.JComboBox<String> jComboBoxBoundary1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<JCheckBox> jListSeries;
    private javax.swing.JPanel jPanelChartHolder;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelSetting;
    private javax.swing.JPanel jPanelTableButtons;
    private javax.swing.JRadioButton jRadioButtonCustomshows;
    private javax.swing.JRadioButton jRadioShowall;
    private javax.swing.JRadioButton jRadioShowaverage;
    private javax.swing.JRadioButton jRadioWithoutaverage;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaInfo;
    private javax.swing.JTextField jTextFieldParam;
    private javax.swing.JTextField jTextFieldParam1;
    // End of variables declaration//GEN-END:variables
}
