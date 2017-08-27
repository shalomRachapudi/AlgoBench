/*
 * Created by Yufen Wang.
 * 2016
 */

package inf2b.algobench.ui;

import com.xeiam.xchart.Series;
import com.xeiam.xchart.StyleManager;
import inf2b.algobench.main.AlgoBench;
import inf2b.algobench.model.MyChart;
import inf2b.algobench.model.MyChartBuilder;
import inf2b.algobench.model.Task;
import inf2b.algobench.model.TaskMaster;
import inf2b.algobench.util.CheckboxTableRenderer;
import inf2b.algobench.util.CompareDetailTableRenderer;
import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class CompareDialog extends JDialog {

    private static final int CHECK_COL = 1;
    private static final String[] COLUMNS = {"Tasks", "Choose"};
    private static Object[][] DATA;
    private DataModel dataModel;
    private boolean[] selectability;
    private int selectNum;
    private CompareChartPanel comparetChartPanel;
    private MyChart chart;
    boolean userCancelled;
    //detail table datas
    private Vector<Object> onerow;
    private DefaultTableModel algoModel;
    
    /**
     * Creates new form NewTaskDialog
     *
     * @param parent The main window of the application, whence this is called
     * @param modal Whether this should prevent access to the parent till closed
     * @param runListModel
     *
     */
    public CompareDialog(Frame parent, boolean modal, DefaultListModel<TaskMaster> runListModel) {
        super(parent, modal);
        initComponents();
        this.userCancelled = true;

        this.setLocationRelativeTo(parent);
        this.getRootPane().setDefaultButton(jButtonComfirm);
        
        selectability = new boolean[runListModel.size()];
        selectNum = 0;
        for(int j=0;j<selectability.length;j++){
            TaskMaster tm = (TaskMaster)runListModel.getElementAt(j);
            if(tm.getTask().getAlgorithmGroup(true).equals("HASH")){
                selectability[j] = false; //hash algo is not allowed to compare
            }else{
                selectability[j] = true;
            }
        }
        
        int size = runListModel.size();
        DATA = new Object[size][2];
        for(int i=0;i<runListModel.size();i++){
            DATA[i][0] = runListModel.elementAt(i);
            DATA[i][1] = Boolean.FALSE;
        }
        dataModel = new DataModel(DATA,COLUMNS);
        jTableTasks.setModel(dataModel);
        jTableTasks.getColumnModel().getColumn(0).setPreferredWidth(300);
        jTableTasks.setDefaultRenderer(Object.class, new CheckboxTableRenderer(selectability));
        
        //add listener at table to make diffrent algotype become gray
        jTableTasks.getModel().addTableModelListener(new TableModelListener(){
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                TaskMaster tm = (TaskMaster)jTableTasks.getValueAt(row, 0);
                Task ta = tm.getTask();
                String algoType = ta.getAlgorithmGroup(true);
                //when it is the first item user choose, gray out other type of algorithm.
                if(selectNum == 0){
                    selectNum++;
                    jLabelAlgogroup.setText(algoType);
                    for(int r=0; r<jTableTasks.getRowCount(); r++){
                        TaskMaster t = (TaskMaster)jTableTasks.getValueAt(r, 0);
                        if(t.getTask().getAlgorithmGroup(true).equals(algoType)){
                            selectability[r] = true;
                        }else{
                            selectability[r] = false;
                        }
                    }
                    jTableTasks.repaint();
                    
                    //update detail table
                    algoModel = new DefaultTableModel();
                    jTableDetails.setModel(algoModel);
                    algoModel.addColumn("Color");
                    algoModel.addColumn("TaskID");
                    algoModel.addColumn("Algorithm");
                    switch(algoType){
                        case "SORT":
                            algoModel.addColumn("Distribution");
                            algoModel.addColumn("Others");
                            algoModel.addColumn("InputFile");
                            break;
                        case "GRAPH":
                            algoModel.addColumn("Directed");
                            algoModel.addColumn("SelfLoop");
                            algoModel.addColumn("SimulateLongVisit");
                            algoModel.addColumn("FixedParam");
                            jTableDetails.getColumnModel().getColumn(4).setPreferredWidth(20);
                            break;
                        case "SEARCH":
                            algoModel.addColumn("Distribution");
                            algoModel.addColumn("Search key");
                            algoModel.addColumn("InputFile");
                            break;
                        case "TREE":
                            algoModel.addColumn("Tree Type");
                            algoModel.addColumn("Size");
                            algoModel.addColumn("Input Range");
                    }
                    addDetailTableRow(ta);
                    jTableDetails.getColumnModel().getColumn(0).setMinWidth(0);
                    jTableDetails.getColumnModel().getColumn(0).setMaxWidth(0);
                    jTableDetails.getColumnModel().getColumn(1).setMinWidth(0);
                    jTableDetails.getColumnModel().getColumn(1).setMaxWidth(100);
                    jTableDetails.getColumnModel().getColumn(0).setPreferredWidth(0);
                    jTableDetails.getColumnModel().getColumn(1).setPreferredWidth(65);
                    jTableDetails.getColumnModel().getColumn(3).setPreferredWidth(20);
                    jTableDetails.repaint();
                }
                //when user unchoose the last choosed-item, make all items chooseble. 
                else if(selectNum == 1 && (boolean)jTableTasks.getValueAt(row, 1)==false){ //this false means after clicked the checkbox is unchecked
                    jLabelAlgogroup.setText("---");
                    for(int j=0;j<selectability.length;j++){
                        if(algoType.equals("HASH")){
                            selectability[j] = false; //hash algo is not allowed to compare
                        }else{
                            selectability[j] = true;
                        }
                    }
                    removeDetailTableRow(ta);
                    selectNum--;
                    jTableTasks.repaint();
                }else if(!(boolean)jTableTasks.getValueAt(row, 1)){ //when choose one
                    removeDetailTableRow(ta);
                    selectNum--;
                }else if((boolean)jTableTasks.getValueAt(row, 1)){ //when unchoose one
                    addDetailTableRow(ta);
                    selectNum++;
                }
            }
        });
        
        jTableDetails.setDefaultRenderer(Object.class, new CompareDetailTableRenderer());
        
    }
    
    public void addDetailTableRow(Task ta){
        String algoType = ta.getAlgorithmGroup(true);
        String algo = ta.getAlgorithm();
        onerow = new Vector<>();
        onerow.add(Color.red);
        onerow.add(ta.getTaskID());
        onerow.add(algo);
        switch(algoType){
            case "SORT":
                if(ta.getInputFileName().equals("")){
                    onerow.add(ta.getInputDistribution(true));
                    if(algo.equals("QUICKSORT")) 
                        onerow.add("Pivot Positon = "+ta.getPivotPosition(true));
                    else if(algo.equals("EXTERNAL_MERGESORT"))
                        onerow.add("RAM = "+ta.getSortRam()+"KB");
                    else
                        onerow.add("--");
                    onerow.add("--");
                }else{
                    onerow.add("--");
                    onerow.add("--");
                    onerow.add(ta.getInputFileName());
                }
                break;
            case "GRAPH":
                onerow.add(ta.getIsDirectedGraph()?"√":"×");
                onerow.add(ta.getAllowSelfLoops()?"√":"×");
                onerow.add(ta.getGraphIsDelayed()?"√":"×");
                onerow.add(ta.getFixedGraphSize()+" "+ta.getFixedGraphParam(true));
                break;
            case "SEARCH":
                if(ta.getInputFileName()==null){
                    onerow.add(ta.getInputDistribution(true));
                    onerow.add(ta.getSearchKeyType(true));
                    onerow.add("--");
                }else{
                    onerow.add("--");
                    onerow.add("--");
                    onerow.add(ta.getInputFileName());
                }
                break;
            case "TREE":
                String tree = null;
                String treeType = ta.getTreeType();
                if (treeType.equals("0"))
                    tree = "Random Valued";
                else if (treeType.equals("1"))
                    tree = "Left Skewed Tree";
                else if (treeType.equals("2"))
                    tree = "Right Skewed Tree";
                
                onerow.add(tree);
                onerow.add(ta.getTreeSize());
                onerow.add(ta.getTreeRange());
        }
        algoModel.addRow(onerow);
    }
    
    public void removeDetailTableRow(Task ta){
        String taskid = ta.getTaskID();
        Vector data = algoModel.getDataVector();
        int row = 0;
        for(int i=0; i<data.size(); i++){
            String id = (String)((Vector)data.get(i)).get(1);
            if(id.endsWith(taskid)){
                row = i;
                break;
            }
        }
        algoModel.removeRow(row);
    }
    
    public CompareChartPanel showDialog(){
        this.revalidate();
        this.setVisible(true);
        
        if (userCancelled) {
            return null;
        }
        System.out.println("New Compare Created.");
        this.dispose();
        return comparetChartPanel;
    }
    
    private class DataModel extends DefaultTableModel {

        public DataModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
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
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroupGraphFix = new javax.swing.ButtonGroup();
        jPanelLeft = new javax.swing.JPanel();
        jPanelActiveTasks = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPaneRunList = new javax.swing.JScrollPane();
        jTableTasks = new javax.swing.JTable();
        jPanelMain = new javax.swing.JPanel();
        jLabelAlgogroup = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextAreaInfo = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDetails = new javax.swing.JTable();
        jPanelBottom = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButtonHelp = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButtonComfirm = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Compare Tasks");
        setIconImages(AlgoBench.iconImagesList);
        setMinimumSize(new java.awt.Dimension(800, 500));
        setModal(true);
        setName("newTaskDialog"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 500));
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWeights = new double[] {0.0, 1.0};
        layout.rowWeights = new double[] {1.0, 0.0};
        getContentPane().setLayout(layout);

        jPanelLeft.setBackground(new java.awt.Color(204, 204, 204));
        jPanelLeft.setMaximumSize(new java.awt.Dimension(250, 430));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(200, 430));
        jPanelLeft.setPreferredSize(new java.awt.Dimension(200, 430));
        jPanelLeft.setLayout(new java.awt.GridLayout(1, 1, 0, 2));

        jPanelActiveTasks.setBackground(new java.awt.Color(255, 255, 255));
        jPanelActiveTasks.setLayout(new java.awt.GridBagLayout());

        jLabel3.setBackground(new java.awt.Color(52, 108, 156));
        jLabel3.setFont(jLabel3.getFont().deriveFont((float)13));
        jLabel3.setForeground(new java.awt.Color(226, 226, 226));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/pixel.png"))); // NOI18N
        jLabel3.setText("Select tasks");
        jLabel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setIconTextGap(8);
        jLabel3.setMaximumSize(new java.awt.Dimension(3200, 28));
        jLabel3.setMinimumSize(new java.awt.Dimension(220, 28));
        jLabel3.setOpaque(true);
        jLabel3.setPreferredSize(new java.awt.Dimension(220, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanelActiveTasks.add(jLabel3, gridBagConstraints);

        jScrollPaneRunList.setBorder(null);

        jTableTasks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jTableTasks.setRowHeight(28);
        jTableTasks.setSelectionBackground(new java.awt.Color(102, 153, 204));
        jTableTasks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTasksMouseClicked(evt);
            }
        });
        jScrollPaneRunList.setViewportView(jTableTasks);
        if (jTableTasks.getColumnModel().getColumnCount() > 0) {
            jTableTasks.getColumnModel().getColumn(0).setPreferredWidth(300);
            jTableTasks.getColumnModel().getColumn(1).setPreferredWidth(80);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelActiveTasks.add(jScrollPaneRunList, gridBagConstraints);

        jPanelLeft.add(jPanelActiveTasks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 118;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        getContentPane().add(jPanelLeft, gridBagConstraints);

        jPanelMain.setMaximumSize(new java.awt.Dimension(550, 430));
        jPanelMain.setMinimumSize(new java.awt.Dimension(550, 430));
        jPanelMain.setPreferredSize(new java.awt.Dimension(500, 430));

        jLabelAlgogroup.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jLabelAlgogroup.setText("---");

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jLabel4.setText("Algorithm Group:");

        jTextAreaInfo.setEditable(false);
        jTextAreaInfo.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTextAreaInfo.setForeground(new java.awt.Color(147, 0, 0));
        jTextAreaInfo.setLineWrap(true);
        jTextAreaInfo.setTabSize(4);
        jTextAreaInfo.setWrapStyleWord(true);
        jTextAreaInfo.setBorder(null);
        jTextAreaInfo.setOpaque(false);
        jTextAreaInfo.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel2.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel2.setText("Comparing Details");

        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));

        jTableDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDetails.setEnabled(false);
        jTableDetails.setRowHeight(20);
        jTableDetails.setSelectionBackground(new java.awt.Color(102, 153, 204));
        jScrollPane1.setViewportView(jTableDetails);

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextAreaInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanelMainLayout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jLabelAlgogroup))
                        .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addComponent(jScrollPane1)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelAlgogroup))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextAreaInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanelMain, gridBagConstraints);

        jPanelBottom.setBackground(new java.awt.Color(219, 229, 244));
        jPanelBottom.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(153, 153, 153)));
        jPanelBottom.setMinimumSize(new java.awt.Dimension(800, 45));
        jPanelBottom.setPreferredSize(new java.awt.Dimension(850, 45));
        jPanelBottom.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setBackground(new java.awt.Color(219, 229, 244));
        jPanel1.setMinimumSize(new java.awt.Dimension(156, 70));
        jPanel1.setPreferredSize(new java.awt.Dimension(240, 60));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        jButtonHelp.setFont(jButtonHelp.getFont().deriveFont((float)12));
        jButtonHelp.setText("Help");
        jButtonHelp.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonHelp);

        jPanelBottom.add(jPanel1);

        jPanel2.setBackground(new java.awt.Color(219, 229, 244));
        jPanel2.setPreferredSize(new java.awt.Dimension(240, 60));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButtonComfirm.setFont(jButtonComfirm.getFont().deriveFont((float)12));
        jButtonComfirm.setText("Confirm");
        jButtonComfirm.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonComfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonComfirmActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonComfirm);

        jButtonCancel.setFont(jButtonCancel.getFont().deriveFont((float)12));
        jButtonCancel.setText("Cancel");
        jButtonCancel.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonCancel);

        jPanelBottom.add(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        getContentPane().add(jPanelBottom, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonComfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonComfirmActionPerformed
        //store choosed items
        ArrayList<TaskMaster> taskmasters = new ArrayList<TaskMaster>();
        for(int i=0; i<jTableTasks.getRowCount();i++){
            if((boolean)jTableTasks.getValueAt(i, 1)){
                TaskMaster tm = (TaskMaster)jTableTasks.getValueAt(i, 0);
                if(tm.getState()!=AlgoBench.TaskState.COMPLETED){
                    displayError(tm.getTaskID()+" have not completed, please complete it first.");
                    return;
                }
                taskmasters.add((TaskMaster)jTableTasks.getValueAt(i, 0));
            }
        }
        if(taskmasters.isEmpty()){
            displayError("You have not choose any task");
            return;
        }else if(taskmasters.size() == 1){
            displayError("Please choose more than one task");
            return;
        }
        //make a comparing chart
        String title;
        String XAxisTitle, YAxisTitle;
        switch(jLabelAlgogroup.getText()){
            case "SORT":
                title = "SORT algorithm compare chart";
                XAxisTitle = "Input Size";
                YAxisTitle = "Run Time(ms)";
                break;
            case "GRAPH":
                title = "GRAPH algorithm compare chart";
                XAxisTitle = "Input Size";
                YAxisTitle = "Time (ms)";
                break;
            case "SEARCH":
                title = "SEARCH algorithm compare chart";
                XAxisTitle = "Input Size";
                YAxisTitle = "Time (μs)";
                break;
            case "TREE":
                title = "TREE algorithm compare chart";
                XAxisTitle = "Input Size";
                YAxisTitle = "Time (μs)"; 
                break;
            default:
                displayError("Can not find algo group");
                return;
        }
        chart = new MyChartBuilder().chartType(StyleManager.ChartType.Line).theme(StyleManager.ChartTheme.Matlab)
                .title(title).xAxisTitle(XAxisTitle).yAxisTitle(YAxisTitle).width(800).height(600).build();
        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideNW);
        chart.getStyleManager().setAxisTitlesVisible(true);
        
        //add series to compareChart
        Map<String, Series> serieses = new LinkedHashMap<>();
        for(TaskMaster tma : taskmasters){
            ResultsChartPanel rcpanel = (ResultsChartPanel)tma.getResultChartPanel();
            Series tmps = rcpanel.getAverageSeries();
            Task t = tma.getTask();
            String seriesName = tma.getTaskID();
            
            serieses.put(seriesName, tmps);
        }
        Iterator<Map.Entry<String, Series>> entries = serieses.entrySet().iterator();
        while(entries.hasNext()){
            Map.Entry<String, Series> entry = entries.next();
            chart.addSeries(entry.getKey(),entry.getValue());
        }
        //add prepared chart to panel
        comparetChartPanel = new CompareChartPanel(jLabelAlgogroup.getText());
        comparetChartPanel.addCompareChart(chart);
        comparetChartPanel.addCompareTable(algoModel);
        this.userCancelled = false;
        this.setVisible(false);
    }//GEN-LAST:event_jButtonComfirmActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        Help h = new Help(null, true, "/inf2b/algobench/html/newtask_help.html");
        h.setVisible(true);
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void jTableTasksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTasksMouseClicked
        int r = jTableTasks.getSelectedRow();
        int c = jTableTasks.getSelectedColumn();
        if(c == 1 && selectability[r]){
            boolean oldValue = (boolean)jTableTasks.getValueAt(r, c);
            if(oldValue){
                jTableTasks.setValueAt(false, r, c);
            }else{
                jTableTasks.setValueAt(true, r, c);
            }
        }
    }//GEN-LAST:event_jTableTasksMouseClicked

    private void displayError(String message) {
        if (message.length() > 0) {
            message = "INFO: " + message;
        }
        jTextAreaInfo.setText(message);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupGraphFix;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonComfirm;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelAlgogroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelActiveTasks;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneRunList;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTableDetails;
    private javax.swing.JTable jTableTasks;
    private javax.swing.JTextArea jTextAreaInfo;
    // End of variables declaration//GEN-END:variables

}
