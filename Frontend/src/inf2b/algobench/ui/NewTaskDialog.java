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

import inf2b.algobench.main.AlgoBench;
import inf2b.algobench.model.TaskMaster;
import inf2b.algobench.model.Task;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class NewTaskDialog extends JDialog {

    Task task;
    boolean userCancelled;
    List<DefaultComboBoxModel<String>> algoModels;
    DefaultComboBoxModel<String> sortDistributionModel;
    DefaultComboBoxModel<String> searchDistributionModel;
    //DefaultComboBoxModel<String> searchKeyModel;
    Map<Component, String> defaultValues;
    CardLayout cardsteps;
    String[] steps;
    int current_step;
    int fileline;
    
    int rangeLowerLimit;
    int rangeUpperLimit;
    int dataElement;
    private String error;

    /**
     * Creates new form NewTaskDialog
     *
     * @param parent The main window of the application, whence this is called
     * @param modal Whether this should prevent access to the parent till closed
     *
     */
    public NewTaskDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.userCancelled = true;
        this.task = new Task();
        this.setLocationRelativeTo(parent);
        this.getRootPane().setDefaultButton(jButtonCreateTask);
        this.cardsteps = (CardLayout) jPanelSetupContainer.getLayout();
        this.steps = new String[]{"cardStep1", "cardStep2", "cardStep3"};
        this.current_step = 0;
        
        // initialize range
        this.rangeLowerLimit = Integer.parseInt(jComboBoxLowerLimit.getSelectedItem().toString());
        this.rangeUpperLimit = Integer.parseInt(jComboBoxUpperLimit.getSelectedItem().toString());
        
        //initial settings of step1 panel
        algoModels = new ArrayList<>();
        algoModels.add(new DefaultComboBoxModel(new String[]{"Quicksort", "Heapsort", "Insertsort","External Mergesort", "Internal Mergesort"}));
        algoModels.add(new DefaultComboBoxModel(new String[]{"Breadth-First Search", "Depth-First Search"}));
        algoModels.add(new DefaultComboBoxModel(new String[]{"Hashing"}));
        algoModels.add(new DefaultComboBoxModel(new String[]{"Linear Search", "Binary Search"}));
        algoModels.add(new DefaultComboBoxModel(new String[]{"Binary Search Tree", "AVL Tree"}));
        
        //default select quicksort algorithm when launch
        jListAlgoType.setSelectedIndex(0);
        cardsteps.show(jPanelSetupContainer, steps[0]);
        
        jComboBoxAlgo.setModel(algoModels.get(0));
        CardLayout cardstep1 = (CardLayout)jPanelOptions1.getLayout();
        cardstep1.show(jPanelOptions1,"cardQuicksort");
        jButtonBack.setEnabled(false);
        jButtonCreateTask.setEnabled(false);
        
        
        /*    public void insertUpdate(java.awt.event.ActionEvent e) {
                
            }*/
        
        // auto update tree basic operations summary when a number is given
        jTextFieldDataElement.getDocument().addDocumentListener(new DocumentListener(){
            void updateSummary() {
                dataElement = 0;
                try {
                    dataElement = Integer.parseInt(jTextFieldDataElement.getText());
                    jButtonCreateTask.setEnabled(true);
                }
                catch( java.lang.NumberFormatException ex)
                {
                    jButtonCreateTask.setEnabled(false);
                }
                int min = Integer.parseInt(jComboBoxLowerLimit.getSelectedItem().toString());
                int max = Integer.parseInt(jComboBoxUpperLimit.getSelectedItem().toString());
        
                enableInsertOp( (dataElement < min || dataElement > max) ? false : true );
                updateBasicOpSummary(dataElement);
            }
            @Override
            public void insertUpdate(DocumentEvent e) {                
                updateSummary();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSummary();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSummary();
            }
        
        });
        
        //when set RAM, auto calculate number of elements
        jTextFieldRam.getDocument().addDocumentListener(new DocumentListener(){
            double ram;
            @Override
            public void insertUpdate(DocumentEvent e) {
                displayError("");
                if(!jTextFieldRam.getText().equals("")){
                    if(!validateNumberType(jTextFieldRam.getText()));
                    else{
                        try{
                            ram = Integer.parseInt(jTextFieldRam.getText());
                        }catch(Exception ex){
                            displayError("Invalid RAM, please only enter Integer");
                        }
                        if(ram <= 0) 
                            displayError("Invalid RAM, it should be larger than ZERO");
                        else{
                            displayError("");
                            int count = (int)(ram*1024/4);
                            jLabelNumEle.setText(count+"");
                            jButtonNext.setEnabled(true);
                        }
                    }
                }
                else{
                    jLabelNumEle.setText("0");
                    jButtonNext.setEnabled(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                displayError("");
                if(!jTextFieldRam.getText().equals("")){
                    if(!validateNumberType(jTextFieldRam.getText()));
                    else{
                        try{
                            ram = Integer.parseInt(jTextFieldRam.getText());
                        }catch(Exception ex){
                            displayError("Invalid RAM, please only enter Integer");
                        }
                        if(ram <= 0) 
                            displayError("Invalid RAM, it should be larger than ZERO");
                        else{
                            displayError("");
                            int count = (int)(ram*1024/4);
                            jLabelNumEle.setText(count+"");
                            jButtonNext.setEnabled(true);
                        }
                    }
                }
                else{
                    jLabelNumEle.setText("0");
                    jButtonNext.setEnabled(false);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        
        });
        
        //initial settings of step2 panel
        ButtonGroup inputbgroup = new ButtonGroup();
        inputbgroup.add(jCheckBoxUseCustomInput);
        inputbgroup.add(jRadioButton2);
        jRadioButton2.setSelected(true);
        jButtonChooseInputFile.setEnabled(true);
        jLabelInputFilename.setEnabled(true);
        
        sortDistributionModel = new DefaultComboBoxModel(new String[]{
            "Random",
            "Sorted",
            "Reverse-sorted",
            "Worst-case"
        });
        jComboBoxInputDistribution.setModel(sortDistributionModel);
        
        searchDistributionModel = new DefaultComboBoxModel(new String[]{
            "Sorted",
            "Random"
        });
        jComboBoxSearchInput.setModel(searchDistributionModel);
        
        // hide min and max values since not really in use and confuses users
        jLabelMinValue.setVisible(false);
        jLabelMaxValue.setVisible(false);
        jComboBoxMinValue.setVisible(false);
        jComboBoxMaxValue.setVisible(false);
        //hide min and max in search card
        jLabelMinValue1.setVisible(false);
        jLabelMaxValue1.setVisible(false);
        jComboBoxMinValue1.setVisible(false);
        jComboBoxMaxValue1.setVisible(false);
        
        //initial settings of step1 panel
        jTextFieldHasha.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                displayError("");
                jLabelHashWarn.setText("");
                if((!jTextFieldHasha.getText().equals(""))&&validateNumberType2(jTextFieldHasha.getText())){
//                    if(validateHashParams()){
//                        jButtonCreateTask.setEnabled(true);
//                    }else{
//                        jButtonCreateTask.setEnabled(false);
//                    }
                    if((!jTextFieldHashn.getText().equals(""))&&validateNumberType(jTextFieldHashn.getText())){
                        int a = Integer.parseInt(jTextFieldHasha.getText());
                        int n = Integer.parseInt(jTextFieldHashn.getText());
                        if(isCoprime(a,n)) jLabelHashWarn.setText("Constant a and N are coprime. It is likely to be a good hash function.");
                        else jLabelHashWarn.setText("Constant a and N are not coprime. It is a bad hash function.");
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                displayError("");
                jLabelHashWarn.setText("");
                if((!jTextFieldHasha.getText().equals(""))&&validateNumberType2(jTextFieldHasha.getText())){
//                    if(validateHashParams()){
//                        jButtonCreateTask.setEnabled(true);
//                    }else{
//                        jButtonCreateTask.setEnabled(false);
//                    }
                    if((!jTextFieldHashn.getText().equals(""))&&validateNumberType(jTextFieldHashn.getText())){
                        int a = Integer.parseInt(jTextFieldHasha.getText());
                        int n = Integer.parseInt(jTextFieldHashn.getText());
                        if(isCoprime(a,n)) jLabelHashWarn.setText("Contant a and N are coprime. It is a good hash function.");
                        else jLabelHashWarn.setText("Contant a and N are not coprime. It is a bad hash function.");
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        jTextFieldHashb.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                displayError("");
                jLabelHashWarn.setText("");
                if((!jTextFieldHashb.getText().equals(""))&&validateNumberType2(jTextFieldHashb.getText())){
//                    if(validateHashParams()){
//                        jButtonCreateTask.setEnabled(true);
//                    }else{
//                        jButtonCreateTask.setEnabled(false);
//                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                displayError("");
                jLabelHashWarn.setText("");
                if((!jTextFieldHashb.getText().equals(""))&&validateNumberType2(jTextFieldHashb.getText())){
//                    if(validateHashParams()){
//                        jButtonCreateTask.setEnabled(true);
//                    }else{
//                        jButtonCreateTask.setEnabled(false);
//                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        jTextFieldHashn.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                displayError("");
                jLabelHashWarn.setText("");
                if((!jTextFieldHashn.getText().equals(""))&&validateNumberType(jTextFieldHashn.getText())){
                    if(validateHashParams()){
                        jButtonCreateTask.setEnabled(true);
                    }else{
                        jButtonCreateTask.setEnabled(false);
                    }
                    if((!jTextFieldHasha.getText().equals(""))&&validateNumberType2(jTextFieldHasha.getText())){
                        int a = Integer.parseInt(jTextFieldHasha.getText());
                        int n = Integer.parseInt(jTextFieldHashn.getText());
                        if(isCoprime(a,n)) jLabelHashWarn.setText("Notice: Contant a and N are coprime. It is likely to be a good hash function.");
                        else jLabelHashWarn.setText("Notice: Contant a and N are not coprime. It is a bad hash function.");
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                displayError("");
                jLabelHashWarn.setText("");
                if((!jTextFieldHashn.getText().equals(""))&&validateNumberType(jTextFieldHashn.getText())){
                    if(validateHashParams()){
                        jButtonCreateTask.setEnabled(true);
                    }else{
                        jButtonCreateTask.setEnabled(false);
                    }
                    if((!jTextFieldHasha.getText().equals(""))&&validateNumberType(jTextFieldHasha.getText())){
                        int a = Integer.parseInt(jTextFieldHasha.getText());
                        int n = Integer.parseInt(jTextFieldHashn.getText());
                        if(isCoprime(a,n)) jLabelHashWarn.setText("Contant a and N are coprime. It is a good hash function.");
                        else jLabelHashWarn.setText("Contant a and N are not coprime. It is a bad hash function.");
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        
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
        buttonGroupBasicOp = new javax.swing.ButtonGroup();
        jPanelLeft = new javax.swing.JPanel();
        jPanelAlgorithmGroup = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPaneAlgoTypeList = new javax.swing.JScrollPane();
        jListAlgoType = new javax.swing.JList();
        jPanelMain = new javax.swing.JPanel();
        jPanelSetupContainer = new javax.swing.JPanel();
        jPanelStep1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxAlgo = new javax.swing.JComboBox<>();
        jPanelOptions1 = new javax.swing.JPanel();
        jPanelQuicksort = new javax.swing.JPanel();
        jComboBoxPivotElement = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jPanelEms = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldRam = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabelNumEle = new javax.swing.JLabel();
        jPanelOthers = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jTextFieldTaskName = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jPanelStep2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JSeparator();
        jCheckBoxUseCustomInput = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanelOptions2 = new javax.swing.JPanel();
        jPanelCustominput = new javax.swing.JPanel();
        jButtonChooseInputFile = new javax.swing.JButton();
        jLabelInputFilename = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButtonGraphConfigHelp1 = new javax.swing.JButton();
        jLabelfileinfo = new javax.swing.JLabel();
        jPanelSearch = new javax.swing.JPanel();
        jLabelSearchInput = new javax.swing.JLabel();
        jComboBoxSearchInput = new javax.swing.JComboBox();
        jLabelSearchKey = new javax.swing.JLabel();
        jComboBoxSearchKey = new javax.swing.JComboBox();
        jLabelMinValue1 = new javax.swing.JLabel();
        jComboBoxMinValue1 = new javax.swing.JComboBox();
        jLabelMaxValue1 = new javax.swing.JLabel();
        jComboBoxMaxValue1 = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        jPanelSortinput = new javax.swing.JPanel();
        jLabelInputDistribution = new javax.swing.JLabel();
        jComboBoxInputDistribution = new javax.swing.JComboBox();
        jLabelMinValue = new javax.swing.JLabel();
        jComboBoxMinValue = new javax.swing.JComboBox();
        jLabelMaxValue = new javax.swing.JLabel();
        jComboBoxMaxValue = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jPanelGraphinput = new javax.swing.JPanel();
        jCheckBoxSelfLoop = new javax.swing.JCheckBox();
        jCheckBoxDirected = new javax.swing.JCheckBox();
        jLabelGraphStructure = new javax.swing.JLabel();
        jComboBoxGraphStructure = new javax.swing.JComboBox();
        jCheckBoxSimulateLongVisit = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jButtonGraphConfigHelp2 = new javax.swing.JButton();
        jPanelTreeInput = new javax.swing.JPanel();
        jComboBoxTreeSize = new javax.swing.JComboBox();
        jLabel53 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jComboBoxLowerLimit = new javax.swing.JComboBox();
        jLabel55 = new javax.swing.JLabel();
        jComboBoxUpperLimit = new javax.swing.JComboBox();
        jLabel56 = new javax.swing.JLabel();
        jComboBoxTreeType = new javax.swing.JComboBox();
        jLabelError = new javax.swing.JLabel();
        jPanelHashinput = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jComboBoxHashInputSize = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanelStep3 = new javax.swing.JPanel();
        jPanelOptions3 = new javax.swing.JPanel();
        jPanelGraphInput = new javax.swing.JPanel();
        jLabelFixedNumber = new javax.swing.JLabel();
        jComboBoxInitialSizeGraph = new javax.swing.JComboBox();
        jComboBoxFinalSizeGraph = new javax.swing.JComboBox();
        jLabelFinalSizeGraph = new javax.swing.JLabel();
        jComboBoxIncrement = new javax.swing.JComboBox();
        jLabel34 = new javax.swing.JLabel();
        jComboBoxNumRepeatsGraph = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        jRadioButtonEdge = new javax.swing.JRadioButton();
        jRadioButtonVertex = new javax.swing.JRadioButton();
        jLabelInitialSizeGraph = new javax.swing.JLabel();
        jComboBoxFixedNumber = new javax.swing.JComboBox();
        jCheckBoxUseLowerLimit = new javax.swing.JCheckBox();
        jButtonGraphConfigHelp = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel37 = new javax.swing.JLabel();
        jLabelTotalExecutionsGraph = new javax.swing.JLabel();
        jLabelStartSizeGraph2 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel26 = new javax.swing.JLabel();
        jPanelHashInput = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jTextFieldHasha = new javax.swing.JTextField();
        jTextFieldHashn = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jTextFieldHashb = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabelHashWarn = new javax.swing.JLabel();
        jPanelSearchInput = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        jComboBoxInitialSizeSearch = new javax.swing.JComboBox();
        jComboBoxFinalSizeSearch = new javax.swing.JComboBox();
        jLabel46 = new javax.swing.JLabel();
        jComboBoxStepSizeSearch = new javax.swing.JComboBox();
        jLabel47 = new javax.swing.JLabel();
        jComboBoxNumRepeatsSearch = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabelTotalExecutionsSearch = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel33 = new javax.swing.JLabel();
        jPanelSortInput = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jComboBoxInitialSize = new javax.swing.JComboBox();
        jComboBoxFinalSize = new javax.swing.JComboBox();
        jLabel28 = new javax.swing.JLabel();
        jComboBoxStepSize = new javax.swing.JComboBox();
        jLabel29 = new javax.swing.JLabel();
        jComboBoxNumRepeats = new javax.swing.JComboBox();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabelTotalExecutions = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel38 = new javax.swing.JLabel();
        jPanelTreeInputFinalStep = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jLabelRange = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabelNumOfNodes = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jRadioButtonCustomInput = new javax.swing.JRadioButton();
        jRadioButtonAlgoGen = new javax.swing.JRadioButton();
        jToggleTestCase = new javax.swing.JToggleButton();
        jToggleTestCase.setVisible(true);
        jLabel61 = new javax.swing.JLabel();
        jTextFieldDataElement = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jCheckBoxInsertOp = new javax.swing.JCheckBox();
        jCheckBoxSearchOp = new javax.swing.JCheckBox();
        jCheckBoxDeleteOp = new javax.swing.JCheckBox();
        jLabel21 = new javax.swing.JLabel();
        jPanelInfo = new javax.swing.JPanel();
        jTextAreaInfo = new javax.swing.JTextArea();
        jPanelBottom = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonHelp = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();
        jButtonNext = new javax.swing.JButton();
        jButtonCreateTask = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        buttonGroupBasicOp.add(jRadioButtonAlgoGen);
        buttonGroupBasicOp.add(jRadioButtonCustomInput);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Task...");
        setIconImages(AlgoBench.iconImagesList);
        setMinimumSize(new java.awt.Dimension(800, 500));
        setModal(true);
        setName("newTaskDialog"); // NOI18N
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWeights = new double[] {0.0, 1.0};
        layout.rowWeights = new double[] {1.0, 0.0};
        getContentPane().setLayout(layout);

        jPanelLeft.setBackground(new java.awt.Color(204, 204, 204));
        jPanelLeft.setMaximumSize(new java.awt.Dimension(250, 430));
        jPanelLeft.setMinimumSize(new java.awt.Dimension(200, 430));
        jPanelLeft.setPreferredSize(new java.awt.Dimension(200, 430));
        jPanelLeft.setLayout(new java.awt.GridLayout(1, 1, 0, 2));

        jPanelAlgorithmGroup.setBackground(new java.awt.Color(255, 255, 255));
        jPanelAlgorithmGroup.setLayout(new java.awt.GridBagLayout());

        jLabel3.setBackground(new java.awt.Color(52, 108, 156));
        jLabel3.setFont(jLabel3.getFont().deriveFont((float)13));
        jLabel3.setForeground(new java.awt.Color(226, 226, 226));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/pixel.png"))); // NOI18N
        jLabel3.setText("Select algorithm type");
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
        jPanelAlgorithmGroup.add(jLabel3, gridBagConstraints);

        jScrollPaneAlgoTypeList.setBorder(null);

        jListAlgoType.setFont(jListAlgoType.getFont().deriveFont((float)13));
        jListAlgoType.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Sort", "Graph", "Hash", "Search", "Tree" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListAlgoType.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListAlgoType.setFixedCellHeight(25);
        jListAlgoType.setMaximumSize(new java.awt.Dimension(0, 0));
        jListAlgoType.setMinimumSize(new java.awt.Dimension(0, 0));
        jListAlgoType.setName("algoGroupList"); // NOI18N
        jListAlgoType.setPreferredSize(new java.awt.Dimension(0, 0));
        jListAlgoType.setValueIsAdjusting(true);
        jListAlgoType.setVisibleRowCount(3);
        jListAlgoType.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListAlgoTypeValueChanged(evt);
            }
        });
        jScrollPaneAlgoTypeList.setViewportView(jListAlgoType);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelAlgorithmGroup.add(jScrollPaneAlgoTypeList, gridBagConstraints);

        jPanelLeft.add(jPanelAlgorithmGroup);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanelLeft, gridBagConstraints);

        jPanelMain.setMaximumSize(new java.awt.Dimension(550, 450));
        jPanelMain.setMinimumSize(new java.awt.Dimension(550, 450));
        jPanelMain.setPreferredSize(new java.awt.Dimension(500, 450));
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelSetupContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jPanelSetupContainer.setName("MINVALUE"); // NOI18N
        jPanelSetupContainer.setPreferredSize(new java.awt.Dimension(550, 420));
        jPanelSetupContainer.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel2.setText("Step 1 : Choose Algorithm");

        jComboBoxAlgo.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxAlgo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxAlgoActionPerformed(evt);
            }
        });

        jPanelOptions1.setLayout(new java.awt.CardLayout());

        jComboBoxPivotElement.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxPivotElement.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Left", "Centre" }));
        jComboBoxPivotElement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPivotElementActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Yu Gothic", 0, 14)); // NOI18N
        jLabel6.setText("Pivot position:");

        javax.swing.GroupLayout jPanelQuicksortLayout = new javax.swing.GroupLayout(jPanelQuicksort);
        jPanelQuicksort.setLayout(jPanelQuicksortLayout);
        jPanelQuicksortLayout.setHorizontalGroup(
            jPanelQuicksortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQuicksortLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxPivotElement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(287, Short.MAX_VALUE))
        );
        jPanelQuicksortLayout.setVerticalGroup(
            jPanelQuicksortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQuicksortLayout.createSequentialGroup()
                .addGroup(jPanelQuicksortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxPivotElement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(190, 190, 190))
        );

        jPanelOptions1.add(jPanelQuicksort, "cardQuicksort");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel12.setText("RAM :");

        jTextFieldRam.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jTextFieldRam.setText("16");

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel14.setText("KB");

        jLabel8.setForeground(new java.awt.Color(255, 0, 0));
        jLabel8.setText("Please notice that the unit of RAM is using KB, and ");

        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("of input elements");

        jLabel15.setForeground(new java.awt.Color(255, 0, 0));
        jLabel15.setText("the RAM you have set equals to");

        jLabelNumEle.setForeground(new java.awt.Color(255, 0, 0));
        jLabelNumEle.setText("4096");

        javax.swing.GroupLayout jPanelEmsLayout = new javax.swing.GroupLayout(jPanelEms);
        jPanelEms.setLayout(jPanelEmsLayout);
        jPanelEmsLayout.setHorizontalGroup(
            jPanelEmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEmsLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(41, 41, 41)
                        .addComponent(jTextFieldRam, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14))
                    .addComponent(jLabel8)
                    .addGroup(jPanelEmsLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelNumEle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        jPanelEmsLayout.setVerticalGroup(
            jPanelEmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEmsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextFieldRam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEmsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabelNumEle)
                    .addComponent(jLabel9))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanelOptions1.add(jPanelEms, "cardEms");

        javax.swing.GroupLayout jPanelOthersLayout = new javax.swing.GroupLayout(jPanelOthers);
        jPanelOthers.setLayout(jPanelOthersLayout);
        jPanelOthersLayout.setHorizontalGroup(
            jPanelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
        );
        jPanelOthersLayout.setVerticalGroup(
            jPanelOthersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 92, Short.MAX_VALUE)
        );

        jPanelOptions1.add(jPanelOthers, "cardOthers");

        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel1.setText("Algorithm:");

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel4.setText("Click Next to Continue");

        jLabel44.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel44.setText("Task Name:");
        jLabel44.setMaximumSize(new java.awt.Dimension(63, 19));
        jLabel44.setMinimumSize(new java.awt.Dimension(63, 19));

        jTextFieldTaskName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jTextFieldTaskName.setMaximumSize(new java.awt.Dimension(150, 20));
        jTextFieldTaskName.setMinimumSize(new java.awt.Dimension(150, 20));
        jTextFieldTaskName.setPreferredSize(new java.awt.Dimension(150, 20));
        jTextFieldTaskName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTaskNameActionPerformed(evt);
            }
        });

        jLabel51.setText("Note: If the above field is left blank, AlgoBench will generate a unique name for the task");

        javax.swing.GroupLayout jPanelStep1Layout = new javax.swing.GroupLayout(jPanelStep1);
        jPanelStep1.setLayout(jPanelStep1Layout);
        jPanelStep1Layout.setHorizontalGroup(
            jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStep1Layout.createSequentialGroup()
                .addGroup(jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(jPanelStep1Layout.createSequentialGroup()
                        .addGroup(jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelOptions1, javax.swing.GroupLayout.PREFERRED_SIZE, 504, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelStep1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(jPanelStep1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxAlgo, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel4)
                                    .addGroup(jPanelStep1Layout.createSequentialGroup()
                                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextFieldTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel51))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelStep1Layout.setVerticalGroup(
            jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStep1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBoxAlgo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanelOptions1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanelStep1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel51)
                .addGap(27, 27, 27)
                .addComponent(jLabel4))
        );

        jPanelSetupContainer.add(jPanelStep1, "cardStep1");

        jLabel5.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel5.setText("Step 2 : Input settings");

        jSeparator4.setForeground(new java.awt.Color(0, 0, 0));

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel7.setText("Input Source:");

        jCheckBoxUseCustomInput.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N
        jCheckBoxUseCustomInput.setText("Use custom input file");
        jCheckBoxUseCustomInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUseCustomInputActionPerformed(evt);
            }
        });

        jRadioButton2.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N
        jRadioButton2.setText("Use Algobench-generated input");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jPanelOptions2.setLayout(new java.awt.CardLayout());

        jButtonChooseInputFile.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jButtonChooseInputFile.setText("Choose file...");
        jButtonChooseInputFile.setEnabled(false);
        jButtonChooseInputFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseInputFileActionPerformed(evt);
            }
        });

        jLabelInputFilename.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelInputFilename.setName("INPUTFILE"); // NOI18N

        jLabel18.setFont(new java.awt.Font("Trebuchet MS", 2, 14)); // NOI18N
        jLabel18.setText("Input File Format Help");

        jLabel11.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel11.setText("Choose file:");

        jButtonGraphConfigHelp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/help.png"))); // NOI18N
        jButtonGraphConfigHelp1.setToolTipText("help");
        jButtonGraphConfigHelp1.setBorder(null);
        jButtonGraphConfigHelp1.setBorderPainted(false);
        jButtonGraphConfigHelp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGraphConfigHelp1ActionPerformed(evt);
            }
        });

        jLabelfileinfo.setForeground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelCustominputLayout = new javax.swing.GroupLayout(jPanelCustominput);
        jPanelCustominput.setLayout(jPanelCustominputLayout);
        jPanelCustominputLayout.setHorizontalGroup(
            jPanelCustominputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCustominputLayout.createSequentialGroup()
                .addGroup(jPanelCustominputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCustominputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11))
                    .addGroup(jPanelCustominputLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonGraphConfigHelp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCustominputLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jButtonChooseInputFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelInputFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelCustominputLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabelfileinfo, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanelCustominputLayout.setVerticalGroup(
            jPanelCustominputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelCustominputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(9, 9, 9)
                .addGroup(jPanelCustominputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelInputFilename, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonChooseInputFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelfileinfo, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCustominputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonGraphConfigHelp1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(43, 43, 43))
        );

        jPanelOptions2.add(jPanelCustominput, "cardCustom");

        jPanelSearch.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N

        jLabelSearchInput.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelSearchInput.setText("Input array:");

        jComboBoxSearchInput.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxSearchInput.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxSearchInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSearchInputActionPerformed(evt);
            }
        });

        jLabelSearchKey.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelSearchKey.setText("Search key:");

        jComboBoxSearchKey.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxSearchKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Always in array", "Not in array", "Random" }));
        jComboBoxSearchKey.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxSearchKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSearchKeyActionPerformed(evt);
            }
        });

        jLabelMinValue1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelMinValue1.setText("Min. value:");
        jLabelMinValue1.setEnabled(false);

        jComboBoxMinValue1.setEditable(true);
        jComboBoxMinValue1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxMinValue1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        jComboBoxMinValue1.setEnabled(false);
        jComboBoxMinValue1.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabelMaxValue1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelMaxValue1.setText("Max. value:");
        jLabelMaxValue1.setEnabled(false);

        jComboBoxMaxValue1.setEditable(true);
        jComboBoxMaxValue1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxMaxValue1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100000000" }));
        jComboBoxMaxValue1.setEnabled(false);
        jComboBoxMaxValue1.setName("MAXVALUE"); // NOI18N
        jComboBoxMaxValue1.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel13.setText("Input Settings:");

        javax.swing.GroupLayout jPanelSearchLayout = new javax.swing.GroupLayout(jPanelSearch);
        jPanelSearch.setLayout(jPanelSearchLayout);
        jPanelSearchLayout.setHorizontalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSearchLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13))
                    .addGroup(jPanelSearchLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSearchLayout.createSequentialGroup()
                                .addComponent(jLabelSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelSearchLayout.createSequentialGroup()
                                .addComponent(jLabelSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelSearchLayout.createSequentialGroup()
                                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelMinValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelMaxValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBoxMaxValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxMinValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(217, Short.MAX_VALUE))
        );
        jPanelSearchLayout.setVerticalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSearchLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel13)
                .addGap(12, 12, 12)
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelMinValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMinValue1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMaxValue1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMaxValue1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanelOptions2.add(jPanelSearch, "cardSearch");

        jPanelSortinput.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N

        jLabelInputDistribution.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelInputDistribution.setText("Distribution:");

        jComboBoxInputDistribution.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxInputDistribution.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxInputDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInputDistributionActionPerformed(evt);
            }
        });

        jLabelMinValue.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelMinValue.setText("Min. value:");
        jLabelMinValue.setEnabled(false);

        jComboBoxMinValue.setEditable(true);
        jComboBoxMinValue.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxMinValue.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        jComboBoxMinValue.setEnabled(false);
        jComboBoxMinValue.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabelMaxValue.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelMaxValue.setText("Max. value:");
        jLabelMaxValue.setEnabled(false);

        jComboBoxMaxValue.setEditable(true);
        jComboBoxMaxValue.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxMaxValue.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100000000" }));
        jComboBoxMaxValue.setEnabled(false);
        jComboBoxMaxValue.setName("MAXVALUE"); // NOI18N
        jComboBoxMaxValue.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabel16.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel16.setText("Input Settings:");

        javax.swing.GroupLayout jPanelSortinputLayout = new javax.swing.GroupLayout(jPanelSortinput);
        jPanelSortinput.setLayout(jPanelSortinputLayout);
        jPanelSortinputLayout.setHorizontalGroup(
            jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSortinputLayout.createSequentialGroup()
                .addGroup(jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSortinputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16))
                    .addGroup(jPanelSortinputLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelMaxValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelMinValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelInputDistribution, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBoxInputDistribution, 0, 133, Short.MAX_VALUE)
                            .addComponent(jComboBoxMinValue, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxMaxValue, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(256, Short.MAX_VALUE))
        );
        jPanelSortinputLayout.setVerticalGroup(
            jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSortinputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addGap(15, 15, 15)
                .addGroup(jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxInputDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelInputDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxMinValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSortinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jPanelOptions2.add(jPanelSortinput, "cardSort");

        jCheckBoxSelfLoop.setFont(new java.awt.Font("Yu Gothic UI", 0, 13)); // NOI18N
        jCheckBoxSelfLoop.setText("Allow self loops");
        jCheckBoxSelfLoop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSelfLoopActionPerformed(evt);
            }
        });

        jCheckBoxDirected.setFont(new java.awt.Font("Yu Gothic UI", 0, 13)); // NOI18N
        jCheckBoxDirected.setText("Use directed edges");
        jCheckBoxDirected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDirectedActionPerformed(evt);
            }
        });

        jLabelGraphStructure.setFont(new java.awt.Font("Yu Gothic UI", 0, 13)); // NOI18N
        jLabelGraphStructure.setText("Graph representation:");

        jComboBoxGraphStructure.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N
        jComboBoxGraphStructure.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adjacency List" }));
        jComboBoxGraphStructure.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxGraphStructure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxGraphStructureActionPerformed(evt);
            }
        });

        jCheckBoxSimulateLongVisit.setFont(new java.awt.Font("Yu Gothic UI", 0, 13)); // NOI18N
        jCheckBoxSimulateLongVisit.setText("Simulate longer vertex visits");

        jLabel17.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel17.setText("Input Settings:");

        jButtonGraphConfigHelp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/help.png"))); // NOI18N
        jButtonGraphConfigHelp2.setToolTipText("help");
        jButtonGraphConfigHelp2.setBorder(null);
        jButtonGraphConfigHelp2.setBorderPainted(false);
        jButtonGraphConfigHelp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGraphConfigHelp2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGraphinputLayout = new javax.swing.GroupLayout(jPanelGraphinput);
        jPanelGraphinput.setLayout(jPanelGraphinputLayout);
        jPanelGraphinputLayout.setHorizontalGroup(
            jPanelGraphinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphinputLayout.createSequentialGroup()
                .addGroup(jPanelGraphinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGraphinputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17))
                    .addGroup(jPanelGraphinputLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanelGraphinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxDirected, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelGraphinputLayout.createSequentialGroup()
                                .addComponent(jLabelGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckBoxSelfLoop, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelGraphinputLayout.createSequentialGroup()
                                .addComponent(jCheckBoxSimulateLongVisit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonGraphConfigHelp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(192, Short.MAX_VALUE))
        );
        jPanelGraphinputLayout.setVerticalGroup(
            jPanelGraphinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphinputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addGap(11, 11, 11)
                .addGroup(jPanelGraphinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDirected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxSelfLoop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelGraphinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxSimulateLongVisit)
                    .addComponent(jButtonGraphConfigHelp2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        jPanelOptions2.add(jPanelGraphinput, "cardGraph");

        jPanelTreeInput.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N

        jComboBoxTreeSize.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxTreeSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "25000", "50000", "100000" }));
        jComboBoxTreeSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxTreeSize.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTreeSizeItemStateChanged(evt);
            }
        });
        jComboBoxTreeSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTreeSizeActionPerformed(evt);
            }
        });

        jLabel53.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel53.setText("Input Settings:");

        jLabel57.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel57.setText("Tree Size (N):");

        jLabel54.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel54.setText("Lower Limit:");

        jComboBoxLowerLimit.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxLowerLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "500", "1000", "10000", "25000", "50000", "75000" }));
        jComboBoxLowerLimit.setName("STARTSIZE"); // NOI18N
        jComboBoxLowerLimit.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxLowerLimit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLowerLimitItemStateChanged(evt);
            }
        });
        jComboBoxLowerLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLowerLimitActionPerformed(evt);
            }
        });

        jLabel55.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel55.setText("Upper Limit:");

        jComboBoxUpperLimit.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxUpperLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "25000", "50000", "75000", "100000" }));
        jComboBoxUpperLimit.setName("NUMSTEPS"); // NOI18N
        jComboBoxUpperLimit.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxUpperLimit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxUpperLimitItemStateChanged(evt);
            }
        });
        jComboBoxUpperLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUpperLimitActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel56.setText("Tree Type:");

        jComboBoxTreeType.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxTreeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rooted Tree", "Left Skewed Tree", "Right Skewed Tree" }));
        jComboBoxTreeType.setName("STEPSIZE"); // NOI18N
        jComboBoxTreeType.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxTreeType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTreeTypeActionPerformed(evt);
            }
        });

        jLabelError.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanelTreeInputLayout = new javax.swing.GroupLayout(jPanelTreeInput);
        jPanelTreeInput.setLayout(jPanelTreeInputLayout);
        jPanelTreeInputLayout.setHorizontalGroup(
            jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                .addGroup(jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel53))
                    .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel57)
                            .addComponent(jLabel54))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBoxTreeSize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxLowerLimit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                                .addComponent(jLabel55)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxUpperLimit, 0, 137, Short.MAX_VALUE))
                            .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                                .addComponent(jLabel56)
                                .addGap(23, 23, 23)
                                .addComponent(jComboBoxTreeType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelTreeInputLayout.setVerticalGroup(
            jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeInputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxTreeSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxTreeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanelTreeInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxLowerLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxUpperLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelOptions2.add(jPanelTreeInput, "cardTree");

        jPanelHashinput.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel25.setText("Tree Size:");

        jComboBoxHashInputSize.setEditable(true);
        jComboBoxHashInputSize.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxHashInputSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1000", "10000", "50000", "100000" }));
        jComboBoxHashInputSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxHashInputSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxHashInputSizeActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel19.setText("Input Settings:");

        javax.swing.GroupLayout jPanelHashinputLayout = new javax.swing.GroupLayout(jPanelHashinput);
        jPanelHashinput.setLayout(jPanelHashinputLayout);
        jPanelHashinputLayout.setHorizontalGroup(
            jPanelHashinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashinputLayout.createSequentialGroup()
                .addGroup(jPanelHashinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelHashinputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19))
                    .addGroup(jPanelHashinputLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxHashInputSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(279, Short.MAX_VALUE))
        );
        jPanelHashinputLayout.setVerticalGroup(
            jPanelHashinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashinputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelHashinputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxHashInputSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(100, Short.MAX_VALUE))
        );

        jPanelOptions2.add(jPanelHashinput, "cardHash");

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel10.setText("Click Next to continue");

        javax.swing.GroupLayout jPanelStep2Layout = new javax.swing.GroupLayout(jPanelStep2);
        jPanelStep2.setLayout(jPanelStep2Layout);
        jPanelStep2Layout.setHorizontalGroup(
            jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStep2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanelStep2Layout.createSequentialGroup()
                .addGroup(jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelStep2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator4)
                            .addGroup(jPanelStep2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanelStep2Layout.createSequentialGroup()
                        .addGroup(jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 501, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelStep2Layout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addGroup(jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jRadioButton2)
                                        .addComponent(jCheckBoxUseCustomInput)))
                                .addGroup(jPanelStep2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(jLabel10))
                                .addComponent(jPanelOptions2, javax.swing.GroupLayout.PREFERRED_SIZE, 522, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelStep2Layout.setVerticalGroup(
            jPanelStep2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStep2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxUseCustomInput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelOptions2, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addContainerGap())
        );

        jPanelSetupContainer.add(jPanelStep2, "cardStep2");

        jPanelStep3.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N

        jPanelOptions3.setMinimumSize(new java.awt.Dimension(547, 280));
        jPanelOptions3.setPreferredSize(new java.awt.Dimension(550, 280));
        jPanelOptions3.setLayout(new java.awt.CardLayout());

        jPanelGraphInput.setBackground(new java.awt.Color(252, 252, 255));
        jPanelGraphInput.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        jPanelGraphInput.setOpaque(false);
        jPanelGraphInput.setPreferredSize(new java.awt.Dimension(550, 255));

        jLabelFixedNumber.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelFixedNumber.setText("Size of fixed parameter (Vertices):");

        jComboBoxInitialSizeGraph.setEditable(true);
        jComboBoxInitialSizeGraph.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxInitialSizeGraph.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        jComboBoxInitialSizeGraph.setName("STARTSIZE"); // NOI18N
        jComboBoxInitialSizeGraph.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxInitialSizeGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInitialSizeGraphActionPerformed(evt);
            }
        });

        jComboBoxFinalSizeGraph.setEditable(true);
        jComboBoxFinalSizeGraph.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxFinalSizeGraph.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100000", "200000", "500000", "1000000", "2000000", "5000000" }));
        jComboBoxFinalSizeGraph.setName("NUMSTEPS"); // NOI18N
        jComboBoxFinalSizeGraph.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFinalSizeGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFinalSizeGraphActionPerformed(evt);
            }
        });

        jLabelFinalSizeGraph.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelFinalSizeGraph.setText("Final no. of Edges:");

        jComboBoxIncrement.setEditable(true);
        jComboBoxIncrement.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxIncrement.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "50000", "100000", "500000" }));
        jComboBoxIncrement.setName("STEPSIZE"); // NOI18N
        jComboBoxIncrement.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxIncrement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxIncrementActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel34.setText("Increment by:");

        jComboBoxNumRepeatsGraph.setEditable(true);
        jComboBoxNumRepeatsGraph.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxNumRepeatsGraph.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        jComboBoxNumRepeatsGraph.setName("NUMREPEATS"); // NOI18N
        jComboBoxNumRepeatsGraph.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxNumRepeatsGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxNumRepeatsGraphActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("No. of experiments per step:");

        jRadioButtonEdge.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jRadioButtonEdge.setText("No. of Edges");
        jRadioButtonEdge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonEdgeActionPerformed(evt);
            }
        });

        jRadioButtonVertex.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jRadioButtonVertex.setSelected(true);
        jRadioButtonVertex.setText("No. of Vertices");
        jRadioButtonVertex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVertexActionPerformed(evt);
            }
        });

        jLabelInitialSizeGraph.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelInitialSizeGraph.setText("Initial no. of Edges:");

        jComboBoxFixedNumber.setEditable(true);
        jComboBoxFixedNumber.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxFixedNumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100000", "500000", "1000000", "5000000" }));
        jComboBoxFixedNumber.setSelectedIndex(1);
        jComboBoxFixedNumber.setName("STARTSIZE"); // NOI18N
        jComboBoxFixedNumber.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFixedNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFixedNumberActionPerformed(evt);
            }
        });

        jCheckBoxUseLowerLimit.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jCheckBoxUseLowerLimit.setText("Use lower limit");
        jCheckBoxUseLowerLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUseLowerLimitActionPerformed(evt);
            }
        });

        jButtonGraphConfigHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/help.png"))); // NOI18N
        jButtonGraphConfigHelp.setToolTipText("help");
        jButtonGraphConfigHelp.setBorder(null);
        jButtonGraphConfigHelp.setBorderPainted(false);
        jButtonGraphConfigHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGraphConfigHelpActionPerformed(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel36.setText("Varied parameter:");

        jSeparator2.setForeground(new java.awt.Color(201, 201, 201));

        jLabel37.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel37.setText("Fixed parameter:");

        jLabelTotalExecutionsGraph.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelTotalExecutionsGraph.setText("--");

        jLabelStartSizeGraph2.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelStartSizeGraph2.setText("Total no. of executions:");

        jLabel24.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel24.setText("Step 3 : Graph configuration");

        jSeparator9.setForeground(new java.awt.Color(0, 0, 0));

        jLabel26.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        jLabel26.setText("If >1, Algobench will calculate average runtime ");

        javax.swing.GroupLayout jPanelGraphInputLayout = new javax.swing.GroupLayout(jPanelGraphInput);
        jPanelGraphInput.setLayout(jPanelGraphInputLayout);
        jPanelGraphInputLayout.setHorizontalGroup(
            jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButtonVertex)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButtonEdge))
                            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                .addComponent(jLabelFixedNumber)
                                .addGap(12, 12, 12)
                                .addComponent(jComboBoxFixedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                .addComponent(jLabelStartSizeGraph2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelTotalExecutionsGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                .addGap(196, 196, 196)
                                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBoxFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                        .addComponent(jComboBoxInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(jCheckBoxUseLowerLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonGraphConfigHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                                        .addComponent(jLabel35)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxNumRepeatsGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel26)))))))
                .addGap(182, 182, 182))
        );
        jPanelGraphInputLayout.setVerticalGroup(
            jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphInputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24)
                    .addComponent(jButtonGraphConfigHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jRadioButtonVertex)
                    .addComponent(jRadioButtonEdge))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFixedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxFixedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxUseLowerLimit))
                .addGap(2, 2, 2)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxNumRepeatsGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addGap(7, 7, 7)
                .addGroup(jPanelGraphInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelStartSizeGraph2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTotalExecutionsGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jPanelOptions3.add(jPanelGraphInput, "cardGraph");

        jPanelHashInput.setBackground(new java.awt.Color(252, 252, 255));
        jPanelHashInput.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N
        jPanelHashInput.setOpaque(false);
        jPanelHashInput.setPreferredSize(new java.awt.Dimension(530, 160));

        jLabel39.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel39.setText("N =");

        jTextFieldHasha.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N

        jTextFieldHashn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel40.setText("My Hash function:");

        jLabel41.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel41.setText("|aK + b| mod N");

        jLabel42.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel42.setText("a = ");

        jTextFieldHashb.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N

        jLabel43.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel43.setText("b =");

        jLabel23.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel23.setText("Step 3 : Hash function");

        jSeparator8.setForeground(new java.awt.Color(0, 0, 0));

        jLabelHashWarn.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabelHashWarn.setForeground(new java.awt.Color(204, 0, 51));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelHashWarn, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelHashWarn, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelHashInputLayout = new javax.swing.GroupLayout(jPanelHashInput);
        jPanelHashInput.setLayout(jPanelHashInputLayout);
        jPanelHashInputLayout.setHorizontalGroup(
            jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashInputLayout.createSequentialGroup()
                .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelHashInputLayout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanelHashInputLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel23)))
                    .addGroup(jPanelHashInputLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelHashInputLayout.createSequentialGroup()
                                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jTextFieldHasha, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel40)
                            .addGroup(jPanelHashInputLayout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jTextFieldHashb, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelHashInputLayout.createSequentialGroup()
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jTextFieldHashn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelHashInputLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelHashInputLayout.setVerticalGroup(
            jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashInputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHasha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHashb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanelHashInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHashn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanelOptions3.add(jPanelHashInput, "cardHash");

        jPanelSearchInput.setBackground(new java.awt.Color(252, 252, 255));
        jPanelSearchInput.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N
        jPanelSearchInput.setOpaque(false);
        jPanelSearchInput.setPreferredSize(new java.awt.Dimension(530, 160));

        jLabel45.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel45.setText("Initial input size:");

        jComboBoxInitialSizeSearch.setEditable(true);
        jComboBoxInitialSizeSearch.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxInitialSizeSearch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5000000", "10000000", "20000000", "50000000" }));
        jComboBoxInitialSizeSearch.setName("STARTSIZE"); // NOI18N
        jComboBoxInitialSizeSearch.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxInitialSizeSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInitialSizeSearchActionPerformed(evt);
            }
        });

        jComboBoxFinalSizeSearch.setEditable(true);
        jComboBoxFinalSizeSearch.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxFinalSizeSearch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "50000000", "100000000", "200000000", "500000000" }));
        jComboBoxFinalSizeSearch.setName("NUMSTEPS"); // NOI18N
        jComboBoxFinalSizeSearch.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFinalSizeSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFinalSizeSearchActionPerformed(evt);
            }
        });

        jLabel46.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel46.setText("Final input size:");

        jComboBoxStepSizeSearch.setEditable(true);
        jComboBoxStepSizeSearch.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxStepSizeSearch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "5000000", "10000000", "20000000", "50000000" }));
        jComboBoxStepSizeSearch.setName("STEPSIZE"); // NOI18N
        jComboBoxStepSizeSearch.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxStepSizeSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStepSizeSearchActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel47.setText("Step size:");

        jComboBoxNumRepeatsSearch.setEditable(true);
        jComboBoxNumRepeatsSearch.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxNumRepeatsSearch.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        jComboBoxNumRepeatsSearch.setName("NUMREPEATS"); // NOI18N
        jComboBoxNumRepeatsSearch.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxNumRepeatsSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxNumRepeatsSearchActionPerformed(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel48.setText("per input size:");

        jLabel49.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel49.setText("Number of experiments");

        jLabelTotalExecutionsSearch.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelTotalExecutionsSearch.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelTotalExecutionsSearch.setText("--");

        jLabel50.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel50.setText("Total no. of executions:");

        jLabel22.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel22.setText("Step 3 : Run configuration");

        jSeparator7.setForeground(new java.awt.Color(0, 0, 0));

        jLabel33.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        jLabel33.setText("If >1, Algobench will calculate average runtime ");

        javax.swing.GroupLayout jPanelSearchInputLayout = new javax.swing.GroupLayout(jPanelSearchInput);
        jPanelSearchInput.setLayout(jPanelSearchInputLayout);
        jPanelSearchInputLayout.setHorizontalGroup(
            jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchInputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelSearchInputLayout.createSequentialGroup()
                        .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(66, 66, 66)
                        .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxStepSizeSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jComboBoxFinalSizeSearch, 0, 130, Short.MAX_VALUE)
                                .addComponent(jComboBoxInitialSizeSearch, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelSearchInputLayout.createSequentialGroup()
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxNumRepeatsSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel33))
                    .addGroup(jPanelSearchInputLayout.createSequentialGroup()
                        .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelTotalExecutionsSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelSearchInputLayout.setVerticalGroup(
            jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchInputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxInitialSizeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxFinalSizeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxStepSizeSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxNumRepeatsSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33))
                .addGap(18, 18, 18)
                .addGroup(jPanelSearchInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTotalExecutionsSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelOptions3.add(jPanelSearchInput, "cardSearch");

        jPanelSortInput.setBackground(new java.awt.Color(252, 252, 255));
        jPanelSortInput.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        jPanelSortInput.setOpaque(false);
        jPanelSortInput.setPreferredSize(new java.awt.Dimension(530, 160));

        jLabel27.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel27.setText("Initial input size:");

        jComboBoxInitialSize.setEditable(true);
        jComboBoxInitialSize.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxInitialSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "20000", "50000", "100000" }));
        jComboBoxInitialSize.setName("STARTSIZE"); // NOI18N
        jComboBoxInitialSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxInitialSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInitialSizeActionPerformed(evt);
            }
        });

        jComboBoxFinalSize.setEditable(true);
        jComboBoxFinalSize.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxFinalSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "500000", "1000000", "2000000", "5000000", "10000000" }));
        jComboBoxFinalSize.setName("NUMSTEPS"); // NOI18N
        jComboBoxFinalSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFinalSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFinalSizeActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel28.setText("Final input size:");

        jComboBoxStepSize.setEditable(true);
        jComboBoxStepSize.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxStepSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "20000", "50000", "100000" }));
        jComboBoxStepSize.setName("STEPSIZE"); // NOI18N
        jComboBoxStepSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxStepSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStepSizeActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel29.setText("Step size:");

        jComboBoxNumRepeats.setEditable(true);
        jComboBoxNumRepeats.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jComboBoxNumRepeats.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        jComboBoxNumRepeats.setName("NUMREPEATS"); // NOI18N
        jComboBoxNumRepeats.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxNumRepeats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxNumRepeatsActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("per input size:");

        jLabel31.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Number of experiments");

        jLabelTotalExecutions.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelTotalExecutions.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelTotalExecutions.setText("--");

        jLabel32.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Total no. of executions:");

        jLabel20.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel20.setText("Step 3 : Run configuration");

        jSeparator6.setForeground(new java.awt.Color(0, 0, 0));

        jLabel38.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        jLabel38.setText("If >1, Algobench will calculate average runtime ");

        javax.swing.GroupLayout jPanelSortInputLayout = new javax.swing.GroupLayout(jPanelSortInput);
        jPanelSortInput.setLayout(jPanelSortInputLayout);
        jPanelSortInputLayout.setHorizontalGroup(
            jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSortInputLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSortInputLayout.createSequentialGroup()
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSortInputLayout.createSequentialGroup()
                                .addComponent(jComboBoxNumRepeats, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38))
                            .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBoxInitialSize, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBoxFinalSize, 0, 130, Short.MAX_VALUE)
                                    .addComponent(jComboBoxStepSize, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addComponent(jLabel20)
                    .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addGroup(jPanelSortInputLayout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelTotalExecutions, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelSortInputLayout.setVerticalGroup(
            jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSortInputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxInitialSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxFinalSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxStepSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxNumRepeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38))
                .addGap(18, 18, 18)
                .addGroup(jPanelSortInputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTotalExecutions, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(108, 108, 108))
        );

        jPanelOptions3.add(jPanelSortInput, "cardSort");

        jPanelTreeInputFinalStep.setBackground(new java.awt.Color(252, 252, 255));
        jPanelTreeInputFinalStep.setFont(new java.awt.Font("Yu Gothic", 0, 12)); // NOI18N
        jPanelTreeInputFinalStep.setOpaque(false);
        jPanelTreeInputFinalStep.setPreferredSize(new java.awt.Dimension(530, 160));

        jLabel60.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel60.setText("Step 3 : Run configuration");

        jSeparator10.setForeground(new java.awt.Color(0, 0, 0));

        jLabelRange.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabelRange.setText("[0, 10000]");

        jLabel58.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel58.setText("Range:");

        jLabel59.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel59.setText("Number of Nodes:");

        jLabelNumOfNodes.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N

        jLabel52.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel52.setText("Basic Operations:");

        jRadioButtonCustomInput.setText("Use custom data element");
        jRadioButtonCustomInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCustomInputActionPerformed(evt);
            }
        });

        jRadioButtonAlgoGen.setText("Use Algobench-generated data element");
        jRadioButtonAlgoGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonAlgoGenActionPerformed(evt);
            }
        });

        jToggleTestCase.setSelected(true);
        jToggleTestCase.setText("Positive Case");
        jToggleTestCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleTestCaseActionPerformed(evt);
            }
        });

        jLabel61.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel61.setText("Data Element:");

        jLabel62.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel62.setText("Summary:");

        jLabel63.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel63.setText("The following operations will be performed");

        jCheckBoxInsertOp.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCheckBoxInsertOp.setSelected(true);
        jCheckBoxInsertOp.setText("insert();");
        jCheckBoxInsertOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxInsertOpActionPerformed(evt);
            }
        });

        jCheckBoxSearchOp.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCheckBoxSearchOp.setSelected(true);
        jCheckBoxSearchOp.setText("search();");
        jCheckBoxSearchOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSearchOpActionPerformed(evt);
            }
        });

        jCheckBoxDeleteOp.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCheckBoxDeleteOp.setSelected(true);
        jCheckBoxDeleteOp.setText("delete();");
        jCheckBoxDeleteOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDeleteOpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTreeInputFinalStepLayout = new javax.swing.GroupLayout(jPanelTreeInputFinalStep);
        jPanelTreeInputFinalStep.setLayout(jPanelTreeInputFinalStepLayout);
        jPanelTreeInputFinalStepLayout.setHorizontalGroup(
            jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel60)
                            .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTreeInputFinalStepLayout.createSequentialGroup()
                                .addComponent(jLabel59)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelNumOfNodes, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel58)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelRange, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(118, 118, 118))))
                    .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                                .addComponent(jRadioButtonAlgoGen)
                                .addGap(18, 18, 18)
                                .addComponent(jToggleTestCase))
                            .addComponent(jRadioButtonCustomInput)))
                    .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel52))
                    .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel61)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDataElement, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel62)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel63)
                            .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jCheckBoxInsertOp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jCheckBoxSearchOp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                                .addComponent(jCheckBoxDeleteOp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTreeInputFinalStepLayout.setVerticalGroup(
            jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeInputFinalStepLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRange, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelNumOfNodes, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonAlgoGen)
                    .addComponent(jToggleTestCase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonCustomInput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDataElement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTreeInputFinalStepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxInsertOp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxSearchOp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDeleteOp)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanelOptions3.add(jPanelTreeInputFinalStep, "cardTree");

        jLabel21.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel21.setText("Click \"Create Task\" button below to finish.");

        javax.swing.GroupLayout jPanelStep3Layout = new javax.swing.GroupLayout(jPanelStep3);
        jPanelStep3.setLayout(jPanelStep3Layout);
        jPanelStep3Layout.setHorizontalGroup(
            jPanelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelStep3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(306, Short.MAX_VALUE))
            .addGroup(jPanelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanelOptions3, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE))
        );
        jPanelStep3Layout.setVerticalGroup(
            jPanelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStep3Layout.createSequentialGroup()
                .addGap(0, 336, Short.MAX_VALUE)
                .addComponent(jLabel21))
            .addGroup(jPanelStep3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelStep3Layout.createSequentialGroup()
                    .addComponent(jPanelOptions3, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 24, Short.MAX_VALUE)))
        );

        jPanelSetupContainer.add(jPanelStep3, "cardStep3");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelMain.add(jPanelSetupContainer, gridBagConstraints);

        jPanelInfo.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N
        jPanelInfo.setMinimumSize(new java.awt.Dimension(400, 35));
        jPanelInfo.setPreferredSize(new java.awt.Dimension(400, 35));
        jPanelInfo.setLayout(new java.awt.BorderLayout());

        jTextAreaInfo.setEditable(false);
        jTextAreaInfo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jTextAreaInfo.setForeground(new java.awt.Color(255, 0, 51));
        jTextAreaInfo.setLineWrap(true);
        jTextAreaInfo.setTabSize(4);
        jTextAreaInfo.setWrapStyleWord(true);
        jTextAreaInfo.setBorder(null);
        jTextAreaInfo.setOpaque(false);
        jTextAreaInfo.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanelInfo.add(jTextAreaInfo, java.awt.BorderLayout.PAGE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        jPanelMain.add(jPanelInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jPanelMain, gridBagConstraints);

        jPanelBottom.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(153, 153, 153)));
        jPanelBottom.setMinimumSize(new java.awt.Dimension(800, 45));
        jPanelBottom.setPreferredSize(new java.awt.Dimension(850, 45));
        jPanelBottom.setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setBackground(new java.awt.Color(219, 229, 244));
        jPanel2.setPreferredSize(new java.awt.Dimension(240, 60));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));

        jButtonHelp.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jButtonHelp.setText("Help");
        jButtonHelp.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonHelp);

        jButtonBack.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jButtonBack.setText("Back");
        jButtonBack.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonBack);

        jButtonNext.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jButtonNext.setText("Next");
        jButtonNext.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonNext);

        jButtonCreateTask.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jButtonCreateTask.setText("Create Task");
        jButtonCreateTask.setPreferredSize(new java.awt.Dimension(105, 25));
        jButtonCreateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateTaskActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonCreateTask);

        jButtonCancel.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
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

    private void jButtonCreateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateTaskActionPerformed
        prepareTask();
        this.setVisible(false);
        this.userCancelled = false;
    }//GEN-LAST:event_jButtonCreateTaskActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jListAlgoTypeValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListAlgoTypeValueChanged
        if (!evt.getValueIsAdjusting()) {
            int index = jListAlgoType.getSelectedIndex();
            if (index != -1) { // something is selected
                current_step = 0;
                jComboBoxAlgo.setModel(algoModels.get(index));
                cardsteps.show(jPanelSetupContainer, steps[current_step]);
                CardLayout cardstep1 = (CardLayout)jPanelOptions1.getLayout();
                if(index != 0){
                    cardstep1.show(jPanelOptions1,"cardOthers");
                }else{
                    cardstep1.show(jPanelOptions1,"cardQuicksort");
                }
                jButtonBack.setEnabled(false);
                jButtonNext.setEnabled(true);
                jButtonCreateTask.setEnabled(false);
                displayError("");
            }
            else {
                jListAlgoType.setSelectedIndex(-1);
            }
        }
    }//GEN-LAST:event_jListAlgoTypeValueChanged

    private void jButtonNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextActionPerformed
        current_step++;
        cardsteps.show(jPanelSetupContainer,steps[current_step]);
        if(current_step>0) jButtonBack.setEnabled(true);
        int index = jListAlgoType.getSelectedIndex();
        
        if(current_step == 1){ //step 2 input setting
            CardLayout cardstep2 = (CardLayout)jPanelOptions2.getLayout();
            if(index == 1 || index == 4){ //Graph or Tree? disable custom input
               jCheckBoxUseCustomInput.setEnabled(false);
               jRadioButton2.setSelected(true);
            }else{
               jCheckBoxUseCustomInput.setEnabled(true);
            }
            if(jRadioButton2.isSelected()){ //non-custom input
                switch(index){
                    case 0: //SORT
                        cardstep2.show(jPanelOptions2, "cardSort");
                        break;
                    case 1: //GRAPH
                        cardstep2.show(jPanelOptions2, "cardGraph");
                        break;
                    case 2: //HASH
                        cardstep2.show(jPanelOptions2, "cardHash");
                        break;
                    case 3: //SEARCH
                        cardstep2.show(jPanelOptions2, "cardSearch");
                        break;
                    case 4: //Tree
                        cardstep2.show(jPanelOptions2, "cardTree");
                        break;
                    default:
                        break;
                }
            }else{
                cardstep2.show(jPanelOptions2, "cardCustom");
            }
        }
        else if(current_step == 2){ //step3 run config or hash function settings
            jButtonNext.setEnabled(false);
            CardLayout cardstep3 = (CardLayout)jPanelOptions3.getLayout();
            switch(index){
                case 0:
                    cardstep3.show(jPanelOptions3, "cardSort");
                    jButtonCreateTask.setEnabled(true);
                    break;
                case 1:
                    cardstep3.show(jPanelOptions3, "cardGraph");
                    jButtonCreateTask.setEnabled(true);
                    break;
                case 2:
                    cardstep3.show(jPanelOptions3, "cardHash");
                    jButtonCreateTask.setEnabled(false);
                    break;
                case 3:
                    cardstep3.show(jPanelOptions3, "cardSearch");
                    jButtonCreateTask.setEnabled(true);
                    break;
                case 4:
                    cardstep3.show(jPanelOptions3, "cardTree");
                    jLabelNumOfNodes.setText(jComboBoxTreeSize.getSelectedItem().toString());
                    jButtonCreateTask.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    }//GEN-LAST:event_jButtonNextActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        current_step--;
        cardsteps.show(jPanelSetupContainer,steps[current_step]);
        if(current_step == 0) jButtonBack.setEnabled(false);
        jButtonNext.setEnabled(true);
        jButtonCreateTask.setEnabled(false);
    }//GEN-LAST:event_jButtonBackActionPerformed

    private void jComboBoxAlgoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxAlgoActionPerformed
       CardLayout cardstep1 = (CardLayout)jPanelOptions1.getLayout();
       if (jListAlgoType.getSelectedIndex() == 0) { //Sort algorithm
            switch (jComboBoxAlgo.getSelectedIndex()) {
               case 0:
                   //Quicksort
                   cardstep1.show(jPanelOptions1,"cardQuicksort");
                   break;
               case 3:
                   //external mergesort
                   cardstep1.show(jPanelOptions1,"cardEms");
                   break;
               default:
                   cardstep1.show(jPanelOptions1,"cardOthers");
                   jButtonNext.setEnabled(true);
                   break;
            }
            //only quicksort have worst-case distribution
            boolean b = jComboBoxAlgo.getSelectedIndex() == 0; //Quicksort
            if(b && sortDistributionModel.getSize() == 3){
                sortDistributionModel.addElement("Worst-case");
            }
            else if(!b && sortDistributionModel.getSize() == 4){
                sortDistributionModel.removeElementAt(3);
            }
        }
        else if (jListAlgoType.getSelectedIndex() == 3){
            boolean b = jComboBoxAlgo.getSelectedIndex() == 0;
            System.out.println("selected=0? " +b);
            if(b && searchDistributionModel.getSize() == 1){
                searchDistributionModel.addElement("Random");
            }
            else if(!b && searchDistributionModel.getSize() == 2){
                searchDistributionModel.removeElementAt(1);
            }
            cardstep1.show(jPanelOptions1,"cardOthers");
            jButtonNext.setEnabled(true);
        }
        else{
            cardstep1.show(jPanelOptions1,"cardOthers");
            jButtonNext.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBoxAlgoActionPerformed

    private void jComboBoxPivotElementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPivotElementActionPerformed
        int index = jComboBoxPivotElement.getSelectedIndex();
        if (index != -1) { // something is selected
            jButtonNext.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBoxPivotElementActionPerformed

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        Help h = new Help(null, true, "/inf2b/algobench/html/newtask_help.html");
        h.setVisible(true);
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void jButtonChooseInputFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseInputFileActionPerformed
        chooseInputFile();
    }//GEN-LAST:event_jButtonChooseInputFileActionPerformed

    private void jCheckBoxSelfLoopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSelfLoopActionPerformed
        //validateGraphParams();
    }//GEN-LAST:event_jCheckBoxSelfLoopActionPerformed

    private void jCheckBoxDirectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDirectedActionPerformed
       //validateGraphParams();
    }//GEN-LAST:event_jCheckBoxDirectedActionPerformed

    //use custom input file
    private void jCheckBoxUseCustomInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxUseCustomInputActionPerformed
        CardLayout cardstep2 = (CardLayout)jPanelOptions2.getLayout();
        cardstep2.show(jPanelOptions2, "cardCustom");
        this.jButtonChooseInputFile.setEnabled(true);
        this.jLabelInputFilename.setEnabled(true);
        int index = jListAlgoType.getSelectedIndex();
        if(isStep2Complete()&&index == 3){
            jLabel10.setText("Click \"Next\" button to step 2.");
            jButtonNext.setEnabled(true);
        }else{
            jLabel10.setText("Click \"Create Task\" button to finish.");
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBoxUseCustomInputActionPerformed

    //use algobench input
    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        jButtonNext.setEnabled(false);
        jButtonCreateTask.setEnabled(false);
        displayError("");
        CardLayout cardstep2 = (CardLayout)jPanelOptions2.getLayout();
        int index = jListAlgoType.getSelectedIndex();
        switch(index){
            case 0:
                cardstep2.show(jPanelOptions2, "cardSort");
                break;
            case 1:
                cardstep2.show(jPanelOptions2, "cardGraph");
                break;
            case 2:
                cardstep2.show(jPanelOptions2, "cardHash");
                break;
            case 3:
                cardstep2.show(jPanelOptions2, "cardSearch");
                break;
            default:
                break;
        }
        if(isStep2Complete()){
            jButtonNext.setEnabled(true);
            jLabel10.setText("Click \"Next\" button to step 2.");
        }else{
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jComboBoxInputDistributionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInputDistributionActionPerformed
        if(isStep2Complete()){
            jButtonNext.setEnabled(true);
        }else{
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxInputDistributionActionPerformed

    private void jComboBoxGraphStructureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxGraphStructureActionPerformed
       if(isStep2Complete()){
            jButtonNext.setEnabled(true);
        }else{
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxGraphStructureActionPerformed

    private void jComboBoxHashInputSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxHashInputSizeActionPerformed
        if(isStep2Complete()){
            jButtonNext.setEnabled(true);
        }else{
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxHashInputSizeActionPerformed

    private void jComboBoxSearchInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSearchInputActionPerformed
        if(isStep2Complete()){
            jButtonNext.setEnabled(true);
        }else{
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxSearchInputActionPerformed

    private void jComboBoxSearchKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSearchKeyActionPerformed
        if(isStep2Complete()){
            jButtonNext.setEnabled(true);
        }else{
            jButtonNext.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxSearchKeyActionPerformed

    private void jComboBoxInitialSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInitialSizeActionPerformed
        if (validateNumberType(jComboBoxInitialSize.getSelectedItem().toString())
            && validateSortParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxInitialSizeActionPerformed

    private void jComboBoxFinalSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFinalSizeActionPerformed
        if (validateNumberType(jComboBoxFinalSize.getSelectedItem().toString())
            && validateSortParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxFinalSizeActionPerformed

    private void jComboBoxStepSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStepSizeActionPerformed
        if (validateNumberType(jComboBoxStepSize.getSelectedItem().toString())
            && validateSortParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxStepSizeActionPerformed

    private void jComboBoxNumRepeatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxNumRepeatsActionPerformed
        String n = jComboBoxNumRepeats.getSelectedItem().toString();
        if (validateNumberType(n)) {
            if (Integer.parseInt(n) > 10) {
                displayError("Max. for number of repeats is 10. Resetting...");
                jComboBoxNumRepeats.setSelectedItem("10");
            }
        }
    }//GEN-LAST:event_jComboBoxNumRepeatsActionPerformed

    private void jComboBoxInitialSizeGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInitialSizeGraphActionPerformed
        if (validateNumberType(jComboBoxIncrement.getSelectedItem().toString())
            && validateGraphParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxInitialSizeGraphActionPerformed

    private void jComboBoxFinalSizeGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFinalSizeGraphActionPerformed
        if (validateNumberType(jComboBoxFinalSizeGraph.getSelectedItem().toString())
            && validateGraphParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxFinalSizeGraphActionPerformed

    private void jComboBoxIncrementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxIncrementActionPerformed
        if (validateNumberType(jComboBoxIncrement.getSelectedItem().toString())
            && validateGraphParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxIncrementActionPerformed

    private void jComboBoxNumRepeatsGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxNumRepeatsGraphActionPerformed
        String n = jComboBoxNumRepeatsGraph.getSelectedItem().toString();
        if (validateNumberType(n)) {
            if (Integer.parseInt(n) > 10) {
                displayError("Max. for number of repeats is 10. Resetting...");
                jComboBoxNumRepeatsGraph.setSelectedItem("10");
            }
        }
    }//GEN-LAST:event_jComboBoxNumRepeatsGraphActionPerformed

    private void jRadioButtonEdgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonEdgeActionPerformed
        jRadioButtonVertexActionPerformed(evt);
    }//GEN-LAST:event_jRadioButtonEdgeActionPerformed

    private void jRadioButtonVertexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVertexActionPerformed
        String[] param = new String[2];
        if (jRadioButtonVertex.isSelected()) {
            param[0] = "Vertices";
            param[1] = "Edges";
        }
        else {
            param[0] = "Edges";
            param[1] = "Vertices";
        }
        jLabelFixedNumber.setText("Size of fixed parameter (" + param[0] + "):");
        jLabelInitialSizeGraph.setText("Initial no. of " + param[1] + ":");
        jLabelFinalSizeGraph.setText("Final no. of " + param[1] + ":");
        validateGraphParams();
    }//GEN-LAST:event_jRadioButtonVertexActionPerformed

    private void jComboBoxFixedNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFixedNumberActionPerformed
        if (validateNumberType(jComboBoxFixedNumber.getSelectedItem().toString())&& validateGraphParams()){
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxFixedNumberActionPerformed

    private void jCheckBoxUseLowerLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxUseLowerLimitActionPerformed
        jComboBoxInitialSizeGraph.setEnabled(!jCheckBoxUseLowerLimit.isSelected());
        validateGraphParams();
    }//GEN-LAST:event_jCheckBoxUseLowerLimitActionPerformed

    private void jButtonGraphConfigHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGraphConfigHelpActionPerformed
        String inputFormat = "For graph experiment, either the no. of vertices\n"
        + "is fixed, while the no. of edges is varied starting \n"
        + "from an Initial Value and increased by an Increment\n"
        + "value for the given Number of Steps; or the other way\n"
        + "round: the no. of edges fixed, and no. of vertices is\n"
        + "varied";
        displayHelpDialog(inputFormat);
    }//GEN-LAST:event_jButtonGraphConfigHelpActionPerformed

    private void jComboBoxInitialSizeSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInitialSizeSearchActionPerformed
        if (validateNumberType(jComboBoxInitialSizeSearch.getSelectedItem().toString())
            && validateSearchParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxInitialSizeSearchActionPerformed

    private void jComboBoxFinalSizeSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFinalSizeSearchActionPerformed
        if (validateNumberType(jComboBoxFinalSizeSearch.getSelectedItem().toString())
            && validateSearchParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxFinalSizeSearchActionPerformed

    private void jComboBoxStepSizeSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStepSizeSearchActionPerformed
        if (validateNumberType(jComboBoxStepSizeSearch.getSelectedItem().toString())
            && validateSearchParams()) {
            setNumExecutions();
            jButtonCreateTask.setEnabled(true);
        }else{
            jButtonCreateTask.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxStepSizeSearchActionPerformed

    private void jComboBoxNumRepeatsSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxNumRepeatsSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxNumRepeatsSearchActionPerformed

    private void jButtonGraphConfigHelp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGraphConfigHelp1ActionPerformed
       String inputFormat = "Custum input file format: \n"
        + "1. Input can olny contain  Integers. \n"
        + "2. Integers seperated by commas. \n"
        + "3. New line demarcates new input.\n"
        + "4. For search algorithms, the first number every is search key.\n"
        + "5. Example: \n"
        + "   1,2,3,4,5 \n"
        + "   10,11,12,13,14,15\n"
        + "   20,21,22,23,24,25,26\n";
        displayHelpDialog(inputFormat);
    }//GEN-LAST:event_jButtonGraphConfigHelp1ActionPerformed

    private void jButtonGraphConfigHelp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGraphConfigHelp2ActionPerformed
        String s = "Simulate longer vertex visits: \n"
        + "Enabling this option adds a little delay when the vertices of the graph\n"
        + "are visited, so that smaller graph sizes can yield more reasonable runtime\n"
        + "values. See the New Task help (click \"Help\" button below) for details.\n";
        displayHelpDialog(s);
    }//GEN-LAST:event_jButtonGraphConfigHelp2ActionPerformed

    private void jTextFieldTaskNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTaskNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTaskNameActionPerformed

    private void jComboBoxTreeSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTreeSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxTreeSizeActionPerformed

    private void jComboBoxLowerLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLowerLimitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxLowerLimitActionPerformed

    private void jComboBoxUpperLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUpperLimitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxUpperLimitActionPerformed

    private void jComboBoxTreeTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTreeTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxTreeTypeActionPerformed
    
    private void jComboBoxLowerLimitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLowerLimitItemStateChanged
        // TODO add your handling code here:
        validateTreeParams();
    }//GEN-LAST:event_jComboBoxLowerLimitItemStateChanged

    private void jComboBoxUpperLimitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxUpperLimitItemStateChanged
        // TODO add your handling code here:
        validateTreeParams();
    }//GEN-LAST:event_jComboBoxUpperLimitItemStateChanged

    private void jRadioButtonCustomInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCustomInputActionPerformed
        // TODO add your handling code here:
        if (jRadioButtonCustomInput.isSelected()) {
            jToggleTestCase.setVisible(false);
            jTextFieldDataElement.setEnabled(true);
            
            // enable and check jCheckBoxInsertOp
            jCheckBoxInsertOp.setEnabled(true);
            jCheckBoxInsertOp.setSelected(true);
            
            if (jTextFieldDataElement.getText().equals(""))
                jButtonCreateTask.setEnabled(false);
            resetSummary();            
        }
    }//GEN-LAST:event_jRadioButtonCustomInputActionPerformed

    private void jRadioButtonAlgoGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonAlgoGenActionPerformed
        // TODO add your handling code here:
         generateDataElementAndUpdateOpSummary();
    }//GEN-LAST:event_jRadioButtonAlgoGenActionPerformed

    private void generateDataElementAndUpdateOpSummary()
    {
        if (jRadioButtonAlgoGen.isSelected()) {
            jToggleTestCase.setVisible(true);
            jTextFieldDataElement.setText("");
            jTextFieldDataElement.setEnabled(false);
            jButtonCreateTask.setEnabled(true);
            
            // get a random integer 
            dataElement = generateDataElement(jToggleTestCase.isSelected());
            
            // update summary and data element?
            updateBasicOpSummary(dataElement);
        }        
    }
    private void jCheckBoxInsertOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxInsertOpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxInsertOpActionPerformed

    private void jCheckBoxSearchOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSearchOpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxSearchOpActionPerformed

    private void jCheckBoxDeleteOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDeleteOpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxDeleteOpActionPerformed

    private void jToggleTestCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleTestCaseActionPerformed
        // TODO add your handling code here:
        if (jToggleTestCase.isSelected()) {
            jToggleTestCase.setText("Positive Case");
        }
        else {
            jToggleTestCase.setText("Negative Case");
            jCheckBoxInsertOp.setSelected(false);
            jCheckBoxInsertOp.setEnabled(false);
        }
        
        dataElement = generateDataElement(jToggleTestCase.isSelected());
        updateBasicOpSummary(dataElement);
    }//GEN-LAST:event_jToggleTestCaseActionPerformed

    private void jComboBoxTreeSizeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTreeSizeItemStateChanged
        // TODO add your handling code here:
        validateTreeParams();
    }//GEN-LAST:event_jComboBoxTreeSizeItemStateChanged

    /**
     * Validates tree parameters (treeSize, lowerLimit, upperLimit)
     */
    private void validateTreeParams()
    {
        int lowerLimit = Integer.parseInt(jComboBoxLowerLimit.getSelectedItem().toString());
        int upperLimit = Integer.parseInt(jComboBoxUpperLimit.getSelectedItem().toString());
        
        /* validate range */
        rangeCheck(lowerLimit, upperLimit);
        /* number of unique elements generated by given range should be greater than treeSize (N) */
        rangeElementsCheck(lowerLimit, upperLimit);        
    }
    
    private int generateDataElement(boolean flag)
    {
        // handle insertOp case: disable it if flag == false
        enableInsertOp(flag);
        Random r = new Random();
        int min = Integer.parseInt(jComboBoxLowerLimit.getSelectedItem().toString());
        int max = Integer.parseInt(jComboBoxUpperLimit.getSelectedItem().toString());
        
        if (!flag) { // negative test case
            return min - r.nextInt(100);
        }
        return r.nextInt((max - min) + 1) + min; // positive test case
    }
    
    /**
     * Enables jCheckBoxInsertOp based on given boolean flag
     * 
     * This method enables/disables insert operation based on +ve/-ve test case.
     * insert operation is not supported for negative test case
     *
     * @param flag true if positive test case. False otherwise
     */
    private void enableInsertOp(boolean flag)
    {
        jCheckBoxInsertOp.setSelected(flag);
        jCheckBoxInsertOp.setEnabled(flag);
    }
    private void updateBasicOpSummary(int val)
    {
        jCheckBoxInsertOp.setText("insert( " + val + " );");
        jCheckBoxSearchOp.setText("search( " + val + " );");
        jCheckBoxDeleteOp.setText("delete( " + val + " );");
    }
    
    private void resetSummary()
    {
        jCheckBoxInsertOp.setText("insert();");
        jCheckBoxSearchOp.setText("search();");
        jCheckBoxDeleteOp.setText("delete();");
    }
    /**
     * Validate Range
     * @param lowerLimit lower limit of given range
     * @param upperLimit upper limit of given range
     */
    private void rangeCheck(int lowerLimit, int upperLimit)
    {
        if (lowerLimit > upperLimit) {
            error = "<html>Error: Upper Limit should be greater than Lower Limit. </html>";
            jLabelError.setText(error);
            jButtonNext.setEnabled(false);
        }
        else {
            jLabelError.setText("");
            error = "";
            jButtonNext.setEnabled(true);
            String range = "[" + lowerLimit + ", " + upperLimit + "]";
            jLabelRange.setText(range);
        }        
    }
    
    /**
     * Validate number of unique elements generated given the range
     * 
     * In order to have N unique elements, |lower - upper| >= N
     * @param lower lower limit of given range
     * @param upper upper limit of given range
     */
    private void rangeElementsCheck(int lower, int upper)
    {
        int treeSize = Integer.parseInt(jComboBoxTreeSize.getSelectedItem().toString());
        if ((upper - lower) < treeSize) {
            error += "<html>Number of unique elements generated by the above Range is lower than the specified Tree Size. Please increase the"
                  + " range or decrease the tree size</html>";
            jLabelError.setText(error);
            jButtonNext.setEnabled(false);
        }
        else {
            jLabelError.setText("");
            error = "";
            jButtonNext.setEnabled(true);
        }   
    }
    
    private static boolean isCoprime(int u, int v) {
    // If both numbers are even, then they are not coprime.
    if (((u | v) & 1) == 0) return false;

    // Now at least one number is odd. Eliminate all the factors of 2 from u.
    while ((u & 1) == 0) u >>= 1;

    // One is coprime with everything else by definition.
    if (u == 1) return true;

    do {
        // Eliminate all the factors of 2 from v, because we know that u and v do not have any 2's in common.
        while ((v & 1) == 0) v >>= 1;

        // One is coprime with everything else by definition.
        if (v == 1) return true;

        // Swap if necessary to ensure that v >= u.
        if (u > v) {
            int t = v;
            v = u;
            u = t;
        }

        // We know that GCD(u, v) = GCD(u, v - u).
        v -= u;
    } while (v != 0);

    // When we reach here, we have v = 0 and GCD(u, v) = current value of u, which is greater than 1.
    return false;
}
    
    private boolean isStep2Complete(){
        if(jCheckBoxUseCustomInput.isSelected()){
            if(jLabelInputFilename.getText().equals("")){
                return false;
            }else{
                return true;
            }
        }
        int index = jListAlgoType.getSelectedIndex();
        switch(index){
            case 0:
                boolean b1 = jComboBoxInputDistribution.getSelectedIndex() == -1;
                boolean b2 = jComboBoxMinValue.getSelectedIndex() == -1;
                boolean b3 = jComboBoxMaxValue.getSelectedIndex() == -1;
                if(b1||b2||b3){
                    return false;
                }else{
                    return true;
                }
            case 1:
                return true;
            case 2:
                if(jComboBoxHashInputSize.getSelectedIndex() == -1){
                    return false;
                }else{
                    return true;
                }
            case 3:
                boolean b4 = jComboBoxSearchInput.getSelectedIndex() == -1;
                boolean b5 = jComboBoxSearchKey.getSelectedIndex() == -1;
                boolean b6 = jComboBoxMinValue1.getSelectedIndex() == -1;
                boolean b7 = jComboBoxMaxValue1.getSelectedIndex() == -1;
                if(b4||b5||b6||b7){
                    return false;
                }else{
                    return true;
                }
            default:
                return false;
        }
    }
    
    private boolean validateSortParams() {
        long initialSize = Long.parseLong(jComboBoxInitialSize.getSelectedItem().toString());
        long finalSize = Long.parseLong(jComboBoxFinalSize.getSelectedItem().toString());
        long stepSize = Long.parseLong(jComboBoxStepSize.getSelectedItem().toString());
        if (finalSize < initialSize) {
            displayError("Final size cannot be less than initial size.");
            return false;
        }
        if (stepSize > (finalSize - initialSize)&&(finalSize != initialSize)) {
            displayError("Step size must be less than the difference between initial and final sizes.");
            return false;
        }
        displayError("");
        return true;
    }
    
    private boolean validateSearchParams() {
        long initialSize = Long.parseLong(jComboBoxInitialSizeSearch.getSelectedItem().toString());
        long finalSize = Long.parseLong(jComboBoxFinalSizeSearch.getSelectedItem().toString());
        long stepSize = Long.parseLong(jComboBoxStepSizeSearch.getSelectedItem().toString());
        if (finalSize < initialSize) {
            displayError("Final size cannot be less than initial size.");
            return false;
        }
        if (stepSize > (finalSize - initialSize)&&(finalSize != initialSize)) {
            displayError("Step size must be less than the difference between initial and final sizes.");
            return false;
        }
        displayError("");
        return true;
    }
    
    private boolean validateHashParams() {
//        int inputsize = Integer.parseInt(jComboBoxHashInputSize.getSelectedItem().toString());
        if(jTextFieldHasha.getText().equals("") || jTextFieldHashb.getText().equals("") || jTextFieldHashn.getText().equals("")){
            displayError("a, b, and N cannot be null, please enter a number.");
            return false;
        }
        if(!jTextFieldHashn.getText().equals("")){
            int n = Integer.parseInt(jTextFieldHashn.getText());
            if(n==0){
                displayError("N cannot be ZERO.");
                return false;
            }
//            if(n>inputsize){
//                displayError("N cannot be larger than Input size.");
//                return false;
//            }
        }
        return true;
    }

    private boolean validateGraphParams() {
        if (!validateNumbers()) {
            return false;
        }
        // clear warning
        displayError("");
        // calculate what the edges should be
        long initialSize = Long.parseLong(jComboBoxInitialSizeGraph.getSelectedItem().toString());
        long stepSize = Long.parseLong(jComboBoxIncrement.getSelectedItem().toString());
        long finalSize = Long.parseLong(jComboBoxFinalSizeGraph.getSelectedItem().toString());
        if (finalSize < initialSize) {
            displayError("Final size cannot be less than initial size.");
            return false;
        }
        if (stepSize > (finalSize - initialSize)) {
            displayError("Step size must be less than the difference between initial and final sizes.");
            return false;
        }
        long fixedSize = Long.parseLong(jComboBoxFixedNumber.getSelectedItem().toString());
        boolean useLimits = jCheckBoxUseLowerLimit.isSelected();

        // numbers have been validated already so only positive values can be had at
        // this point
        if ((initialSize + stepSize + finalSize) == 0) {
            displayError("All run sizes cannot be zero!");
            return false;
        }

        if (jRadioButtonVertex.isSelected()) { // fixed vertices
            long numEdgesLimit;
            // if |V| = N, 
            // undirected with self-loops: 0 <= |E| <= N(N+1)/2
            if (!jCheckBoxDirected.isSelected() && jCheckBoxSelfLoop.isSelected()) {
                numEdgesLimit = (long) (0.5 * fixedSize * (fixedSize + 1));
            } // undirected without self-loops: 0 <= |E| <= N(N-1)/2
            else if (!jCheckBoxDirected.isSelected() && !jCheckBoxSelfLoop.isSelected()) {
                numEdgesLimit = (long) (0.5 * fixedSize * (fixedSize - 1));
            } // directed with self-loops: 0 <= |E| <= N^2
            else if (jCheckBoxDirected.isSelected() && jCheckBoxSelfLoop.isSelected()) {
                numEdgesLimit = fixedSize * fixedSize;
            }
            else {// directed without self-loops: 0 <= |E| <= N(N-1)
                numEdgesLimit = fixedSize * (fixedSize - 1);
            }
            if (useLimits) {
                initialSize = 0;
                jComboBoxInitialSizeGraph.setSelectedItem(initialSize);
            }
            if (finalSize > numEdgesLimit) {
                displayError("Hint: Number of edges cannot exceed " + numEdgesLimit
                        + ". Can you tell why?");
                return false;
            }
        }
        else { // fixed edges
            long numVerticesLowerBound;
            // undirected with self-loops: N >= (sqrt(1+8*|E|) - 1)/2
            if (!jCheckBoxDirected.isSelected() && jCheckBoxSelfLoop.isSelected()) {
                numVerticesLowerBound = (long) Math.ceil(0.5 * (Math.sqrt(8 * fixedSize + 1) - 1));
            } // undirected without self-loops: N >= (sqrt(1+8*|E|) + 1)/2
            else if (!jCheckBoxDirected.isSelected() && !jCheckBoxSelfLoop.isSelected()) {
                numVerticesLowerBound = (long) Math.ceil(0.5 * (Math.sqrt(8 * fixedSize + 1) + 1));
            } // directed with self-loops: N >= sqrt(|E|)
            else if (jCheckBoxDirected.isSelected() && jCheckBoxSelfLoop.isSelected()) {
                numVerticesLowerBound = (long) Math.ceil(Math.sqrt(fixedSize));
            } // directed without self-loops: N >= (sqrt(1+4*|E|) + 1)/2
            else {
                numVerticesLowerBound = (long) Math.ceil(0.5 * (Math.sqrt(4 * fixedSize + 1) + 1));
            }
            if (useLimits) {
                initialSize = numVerticesLowerBound;
                jComboBoxInitialSizeGraph.setSelectedItem(initialSize);
            }
            if (initialSize < numVerticesLowerBound) {
                displayError("Hint: Number of vertices cannot be less than "
                        + numVerticesLowerBound + ". Can you tell why?");
                return false;
            }
        }
        return true;
    }
    
    private boolean validateNumbers() {
        List<JPanel> panels = new ArrayList<>();
        panels.add(jPanelSortInput);
        panels.add(jPanelGraphInput);
        panels.add(jPanelHashinput);
        panels.add(jPanelSortinput);

        for (JPanel p : panels) {
            for (Component c : p.getComponents()) {
                if (c instanceof JComboBox && ((JComboBox) c).isEditable()) {
                    String number = ((JComboBox) c).getSelectedItem().toString().trim();
                    long maxUnsignedInt = (long) (Math.pow(2, 32)) - 1L;
                    if (!validateNumberType(number)
                            || !validateNumberSize(Long.parseLong(number), maxUnsignedInt)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    //number can only be positive int or 0
    private boolean validateNumberType(String number) {
        Pattern numPattern = Pattern.compile("^(\\d+)$");
        Matcher numMatcher = numPattern.matcher(number.trim());
        if (!numMatcher.find()) {
            displayError("Invalid number format supplied. Use only positive integers or 0.");
            return false;
        }
        return true;
    }
    
    //number can only be integers (positive or negtive)
    private boolean validateNumberType2(String number) {
        Pattern numPattern = Pattern.compile("^-?\\d*$");
        Matcher numMatcher = numPattern.matcher(number.trim());
        if (!numMatcher.find()) {
            displayError("Invalid number format supplied. Use only integers.");
            return false;
        }
        return true;
    }

    private boolean validateNumberSize(Long number) {
        return validateNumberSize(number, Long.MAX_VALUE);
    }
    
    private boolean validateFileNumberType(String number) {
        Pattern numPattern = Pattern.compile("^(\\d+)$");
        Matcher numMatcher = numPattern.matcher(number.trim());
        if (!numMatcher.find()) {
            return false;
        }
        return true;
    }

    private boolean validateNumberSize(Long number, Long limit) {
        if (number > limit) {
            displayError("One or more numbers too large: must be less than " + limit);
            return false;
        }
        return true;
    }
    
    private void setNumExecutions() {

        // just duplicate... no time
        switch (jListAlgoType.getSelectedIndex()) {
            case 0:// sorting
                {
                    Long initialSize = Long.parseLong(jComboBoxInitialSize.getSelectedItem().toString());
                    Long finalSize = Long.parseLong(jComboBoxFinalSize.getSelectedItem().toString());
                    Long stepSize = Long.parseLong(jComboBoxStepSize.getSelectedItem().toString());
                    if (stepSize == 0) {
                        jLabelTotalExecutions.setText("1");
                        return;
                    }       
                    jLabelTotalExecutions.setText(Long.toString(((finalSize - initialSize) / stepSize) + 1));
                    break;
                }
            case 1:// graph
                {
                    Long initialSize = Long.parseLong(jComboBoxInitialSizeGraph.getSelectedItem().toString());
                    Long finalSize = Long.parseLong(jComboBoxFinalSizeGraph.getSelectedItem().toString());
                    Long stepSize = Long.parseLong(jComboBoxIncrement.getSelectedItem().toString());
                    if (stepSize == 0) {
                        jLabelTotalExecutionsGraph.setText("1");
                        return;
                    }       
                    jLabelTotalExecutionsGraph.setText(Long.toString(((finalSize - initialSize) / stepSize) + 1));
                    break;
                }
            case 3:// searching
                {
                    Long initialSize = Long.parseLong(jComboBoxInitialSizeSearch.getSelectedItem().toString());
                    Long finalSize = Long.parseLong(jComboBoxFinalSizeSearch.getSelectedItem().toString());
                    Long stepSize = Long.parseLong(jComboBoxStepSizeSearch.getSelectedItem().toString());
                    if (stepSize == 0) {
                        jLabelTotalExecutionsSearch.setText("1");
                        return;
                    }      
                    jLabelTotalExecutionsSearch.setText(Long.toString(((finalSize - initialSize) / stepSize) + 1));
                    break;
                }
            default:
                break;
        }
    }

    final void storeDefaultValues(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JComboBox) {
                defaultValues.put(c, ((JComboBox) c).getSelectedItem().toString());
            }
            else if (c instanceof JCheckBox || c instanceof JRadioButton) { // radiobutton and checkbox
                defaultValues.put(c, ((Boolean) ((AbstractButton) c).isSelected()).toString());
            }
            else if (c instanceof JButton) { // radiobutton and checkbox
                defaultValues.put(c, ((Boolean) c.isEnabled()).toString());
            }
            else if (c instanceof JLabel) {
                defaultValues.put(c, ((JLabel) c).getText());
            }
            else if (c instanceof Container) {
                storeDefaultValues((Container) c);
            }
        }
    }

    private void displayError(String message) {
        if (message.length() > 0) {
            message = "INFO: " + message;
        }
        jTextAreaInfo.setText(message);
    }

    private void displayHelpDialog(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Input Format", JOptionPane.INFORMATION_MESSAGE);
    }

    private void chooseInputFile() {
        displayError("");
        this.jLabelfileinfo.setText("");
        List<String[]> filters = new ArrayList<>();
        String inputType = jListAlgoType.getSelectedValue().toString().toUpperCase();
        switch (inputType) {
            case "SORT":
                filters.add(new String[]{"Text files (.txt)", "txt"});
                //filters.add(new String[]{"Comma-separated values (.csv)", "csv"});
                break;
            default:
                filters.add(new String[]{"Text files (.txt)", "txt"});
                break;
        }
        // launch filechooser
        JFileChooser selectInputFileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File openFile = this.getSelectedFile();
                if (openFile.exists()) {
                    super.approveSelection();
                }
                else {// confirm there's no overwrite, or it's intentional
                    JOptionPane.showConfirmDialog(this,
                            "Couldn't find the specified file.", "File Not Found",
                            JOptionPane.OK_OPTION,
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        selectInputFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        selectInputFileChooser.setAcceptAllFileFilterUsed(false);
        selectInputFileChooser.setDialogTitle("Select Input File");
        for (String[] s : filters) {
            selectInputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter(s[0], s[1]));
        }
        File dir = new File(AlgoBench.JarDirectory + File.separator + "input");
        if (!dir.exists()) {
            dir = new File(".");
        }
        selectInputFileChooser.setCurrentDirectory(dir);
        int result = selectInputFileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = selectInputFileChooser.getSelectedFile();
            if(!validateFile(f)){
                this.jLabelInputFilename.setText("Invalid File!");
                return;
            }
            this.jLabelInputFilename.setText(f.getName());
            String path = f.getAbsolutePath().replace("\\", "\\\\");
            this.task.setInputFileName(path);
            this.task.setNumRuns(fileline);
            int index = jListAlgoType.getSelectedIndex();
            if(index == 3){
                jButtonNext.setEnabled(true);
            }else{
                jButtonCreateTask.setEnabled(true);
            }
        }
    }
    
    private boolean  validateFile(File file){
        System.out.println("validating file...");
        BufferedReader reader = null;
        fileline = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 0;
            while ((tempString = reader.readLine()) != null) {
                String[] numbers = tempString.split(",");
                if(numbers.length == 1 && numbers[0].trim().equals("")) continue;
                for(int i=0; i<numbers.length; i++){
                    if(!validateFileNumberType(numbers[i].trim())){
                        this.jLabelfileinfo.setText("Invalid file content at line "+(line+1)+" and index "+i);
                        //displayError("Invalid file content at line "+(line+1)+" and index "+i);
                        return false;
                    }else if(!validateNumberSize(Long.parseLong(numbers[i].trim()))){
                        this.jLabelfileinfo.setText("number too big at line "+(line+1)+" and index "+i);
                        //displayError("number too big at line "+(line+1)+" and index "+i);
                        return false;
                    }
                }
                line++;
            }
            if(line == 0){
                this.jLabelfileinfo.setText("The file is empty!");
                return false;
            }else{
                fileline = line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * Displays the dialog and returns a Task instance representing everything
     * about the task to be run
     *
     * @return Task
     */
    public TaskMaster showDialog() {
        this.revalidate();
        //storeDefaultValues(jPanelMain);
        this.setVisible(true);

        if (userCancelled) {
            return null;
        }
        
        TaskMaster tm = new TaskMaster(task);
        System.out.println(tm.getTask().getCommand());
        this.dispose();
        return tm;
    }
    
    private boolean prepareTask() {
        String algoGroup = jListAlgoType.getSelectedValue().toString().toUpperCase().trim();
               
        this.task.setAlgorithm(this.jComboBoxAlgo.getSelectedItem().toString());
        this.task.setRunTitle(task.getTaskID() + " (" + task.getAlgorithm() + ")");
        this.task.setAlgorithmGroup(algoGroup);

        if (!jCheckBoxUseCustomInput.isSelected()) {
            // clear input file if it had been specified and then removed
            this.task.setInputFileName("");
        }

        if (null != algoGroup) {
            switch (algoGroup) {
                case "GRAPH":
                    this.task.setDataStructure(this.jComboBoxGraphStructure.getSelectedItem().toString());
                    String fixedParam;
                    if (jRadioButtonVertex.isSelected()) {
                        fixedParam = "VERTEX";
                    }
                    else {
                        fixedParam = "EDGE";
                    }
                    this.task.setFixedGraphParam(fixedParam);
                    this.task.setFixedGraphSize(Long.parseLong(jComboBoxFixedNumber.getSelectedItem().toString()));
                    this.task.setInputStartSize(Long.parseLong(this.jComboBoxInitialSizeGraph.getSelectedItem().toString()));
                    this.task.setInputStepSize(Long.parseLong(this.jComboBoxIncrement.getSelectedItem().toString()));
                    this.task.setInputFinalSize(Long.parseLong(this.jComboBoxFinalSizeGraph.getSelectedItem().toString()));
                    this.task.setNumRuns();
                    this.task.setNumRepeats(Integer.parseInt(this.jComboBoxNumRepeatsGraph.getSelectedItem().toString()));
                    this.task.setAllowSelfLoops(this.jCheckBoxSelfLoop.isSelected());
                    this.task.setIsDirectedGraph(this.jCheckBoxDirected.isSelected());
                    this.task.setGraphIsDelayed(this.jCheckBoxSimulateLongVisit.isSelected());
                    if (!this.validateGraphParams()) {
                        this.task.logError("Graph parameters are incorrect. Please recheck the run configuration.\n");
                    }
                    break;
                case "HASH":
                    if(!validateHashParams()) return false;
                    this.task.setHashBucketSize(Integer.parseInt(jTextFieldHashn.getText()));
                    //this.task.setHashFunctionType(jComboBoxHashFunctionType.getSelectedItem().toString());
                    this.task.setInputStartSize(Long.parseLong(jComboBoxHashInputSize.getSelectedItem().toString()));
                    this.task.setHashKeyType("Numbers");
                    int a = Integer.parseInt(jTextFieldHasha.getText());
                    int b = Integer.parseInt(jTextFieldHashb.getText());
                    this.task.setHashparameters(a,b);
                    this.task.setNumRuns(1);
                    this.task.setNumRepeats(1);
                    this.task.setInputStepSize(1L);
                    this.task.setInputFinalSize(1L);
                    break;
                case "SORT":
                    this.task.setPivotPosition(this.jComboBoxPivotElement.getSelectedItem().toString());
                    if(this.task.getAlgorithm().equals("EXTERNAL_MERGESORT" ) ){
                        this.task.setSortRam(this.jTextFieldRam.getText());
                    }
                    this.task.setInputMinValue(this.jComboBoxMinValue.getSelectedItem().toString());
                    this.task.setInputMaxValue(this.jComboBoxMaxValue.getSelectedItem().toString());
                    this.task.setInputStartSize(Long.parseLong(this.jComboBoxInitialSize.getSelectedItem().toString()));
                    this.task.setInputFinalSize(Long.parseLong(this.jComboBoxFinalSize.getSelectedItem().toString()));
                    this.task.setInputStepSize(Long.parseLong(this.jComboBoxStepSize.getSelectedItem().toString()));
                    this.task.setInputDistribution(this.jComboBoxInputDistribution.getSelectedItem().toString());
                    this.task.setNumRuns();
                    this.task.setNumRepeats(Integer.parseInt(this.jComboBoxNumRepeats.getSelectedItem().toString()));
                    break;
                case "SEARCH":
                    this.task.setInputMinValue(this.jComboBoxMinValue1.getSelectedItem().toString());
                    this.task.setInputMaxValue(this.jComboBoxMaxValue1.getSelectedItem().toString());
                    this.task.setInputStartSize(Long.parseLong(this.jComboBoxInitialSizeSearch.getSelectedItem().toString()));
                    this.task.setInputFinalSize(Long.parseLong(this.jComboBoxFinalSizeSearch.getSelectedItem().toString()));
                    this.task.setInputStepSize(Long.parseLong(this.jComboBoxStepSizeSearch.getSelectedItem().toString()));
                    this.task.setInputDistribution(this.jComboBoxSearchInput.getSelectedItem().toString());
                    this.task.setSearchKeyType(this.jComboBoxSearchKey.getSelectedItem().toString());
                    this.task.setNumRuns();
                    this.task.setNumRepeats(Integer.parseInt(this.jComboBoxNumRepeatsSearch.getSelectedItem().toString()));
                    break;
                case "TREE":
                    this.task.setTreeSize(this.jComboBoxTreeSize.getSelectedItem().toString());
                    this.task.setTreeRangeLowerLimit(this.jComboBoxLowerLimit.getSelectedItem().toString());
                    this.task.setTreeRangeUpperLimit(this.jComboBoxUpperLimit.getSelectedItem().toString());
                    this.task.setTreeType(this.jComboBoxTreeType.getSelectedItem().toString());
                    
                    // basic operations
                    this.task.setDataElement(Integer.toString(dataElement));
                    this.task.setInsertOp(jCheckBoxInsertOp.isSelected());
                    this.task.setSearchOp(jCheckBoxSearchOp.isSelected());
                    this.task.setDeleteOp(jCheckBoxDeleteOp.isSelected());
                    break;
                default:
                    break;
            }
        }
        
        // task ID or task name
        long tmp = System.currentTimeMillis() % 1000;
        String id = jTextFieldTaskName.getText().trim();
        if (id.equals("")) {
            if(tmp < 100)
                id = "0" + tmp;
            else 
                id = "" + tmp;
            id = task.getAlgorithm().toLowerCase() + "_" + id;
        }
        
        this.task.setTaskID(id);
        if (this.task.getError().length() > 0) {
            System.out.println("Waiting for option pane");
            JOptionPane.showMessageDialog(this, "Please fix the following errors:\n" + this.task.getError(),
                    "New Task Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("After waiting for option pane");
            this.task.clearErrorLog();
            return false;
        }
        return true;
    }


    /**
     * @param args the task line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Windows".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//
//                }
//            }
//        }
//        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(NewTaskDialog.class
//                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                NewTaskDialog dialog = new NewTaskDialog(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupBasicOp;
    private javax.swing.ButtonGroup buttonGroupGraphFix;
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChooseInputFile;
    private javax.swing.JButton jButtonCreateTask;
    private javax.swing.JButton jButtonGraphConfigHelp;
    private javax.swing.JButton jButtonGraphConfigHelp1;
    private javax.swing.JButton jButtonGraphConfigHelp2;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonNext;
    private javax.swing.JCheckBox jCheckBoxDeleteOp;
    private javax.swing.JCheckBox jCheckBoxDirected;
    private javax.swing.JCheckBox jCheckBoxInsertOp;
    private javax.swing.JCheckBox jCheckBoxSearchOp;
    private javax.swing.JCheckBox jCheckBoxSelfLoop;
    private javax.swing.JCheckBox jCheckBoxSimulateLongVisit;
    private javax.swing.JRadioButton jCheckBoxUseCustomInput;
    private javax.swing.JCheckBox jCheckBoxUseLowerLimit;
    private javax.swing.JComboBox<String> jComboBoxAlgo;
    private javax.swing.JComboBox jComboBoxFinalSize;
    private javax.swing.JComboBox jComboBoxFinalSizeGraph;
    private javax.swing.JComboBox jComboBoxFinalSizeSearch;
    private javax.swing.JComboBox jComboBoxFixedNumber;
    private javax.swing.JComboBox jComboBoxGraphStructure;
    private javax.swing.JComboBox jComboBoxHashInputSize;
    private javax.swing.JComboBox jComboBoxIncrement;
    private javax.swing.JComboBox jComboBoxInitialSize;
    private javax.swing.JComboBox jComboBoxInitialSizeGraph;
    private javax.swing.JComboBox jComboBoxInitialSizeSearch;
    private javax.swing.JComboBox jComboBoxInputDistribution;
    private javax.swing.JComboBox jComboBoxLowerLimit;
    private javax.swing.JComboBox jComboBoxMaxValue;
    private javax.swing.JComboBox jComboBoxMaxValue1;
    private javax.swing.JComboBox jComboBoxMinValue;
    private javax.swing.JComboBox jComboBoxMinValue1;
    private javax.swing.JComboBox jComboBoxNumRepeats;
    private javax.swing.JComboBox jComboBoxNumRepeatsGraph;
    private javax.swing.JComboBox jComboBoxNumRepeatsSearch;
    private javax.swing.JComboBox<String> jComboBoxPivotElement;
    private javax.swing.JComboBox jComboBoxSearchInput;
    private javax.swing.JComboBox jComboBoxSearchKey;
    private javax.swing.JComboBox jComboBoxStepSize;
    private javax.swing.JComboBox jComboBoxStepSizeSearch;
    private javax.swing.JComboBox jComboBoxTreeSize;
    private javax.swing.JComboBox jComboBoxTreeType;
    private javax.swing.JComboBox jComboBoxUpperLimit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelFinalSizeGraph;
    private javax.swing.JLabel jLabelFixedNumber;
    private javax.swing.JLabel jLabelGraphStructure;
    private javax.swing.JLabel jLabelHashWarn;
    private javax.swing.JLabel jLabelInitialSizeGraph;
    private javax.swing.JLabel jLabelInputDistribution;
    private javax.swing.JLabel jLabelInputFilename;
    private javax.swing.JLabel jLabelMaxValue;
    private javax.swing.JLabel jLabelMaxValue1;
    private javax.swing.JLabel jLabelMinValue;
    private javax.swing.JLabel jLabelMinValue1;
    private javax.swing.JLabel jLabelNumEle;
    private javax.swing.JLabel jLabelNumOfNodes;
    private javax.swing.JLabel jLabelRange;
    private javax.swing.JLabel jLabelSearchInput;
    private javax.swing.JLabel jLabelSearchKey;
    private javax.swing.JLabel jLabelStartSizeGraph2;
    private javax.swing.JLabel jLabelTotalExecutions;
    private javax.swing.JLabel jLabelTotalExecutionsGraph;
    private javax.swing.JLabel jLabelTotalExecutionsSearch;
    private javax.swing.JLabel jLabelfileinfo;
    private javax.swing.JList jListAlgoType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelAlgorithmGroup;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelCustominput;
    private javax.swing.JPanel jPanelEms;
    private javax.swing.JPanel jPanelGraphInput;
    private javax.swing.JPanel jPanelGraphinput;
    private javax.swing.JPanel jPanelHashInput;
    private javax.swing.JPanel jPanelHashinput;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelOptions1;
    private javax.swing.JPanel jPanelOptions2;
    private javax.swing.JPanel jPanelOptions3;
    private javax.swing.JPanel jPanelOthers;
    private javax.swing.JPanel jPanelQuicksort;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelSearchInput;
    private javax.swing.JPanel jPanelSetupContainer;
    private javax.swing.JPanel jPanelSortInput;
    private javax.swing.JPanel jPanelSortinput;
    private javax.swing.JPanel jPanelStep1;
    private javax.swing.JPanel jPanelStep2;
    private javax.swing.JPanel jPanelStep3;
    private javax.swing.JPanel jPanelTreeInput;
    private javax.swing.JPanel jPanelTreeInputFinalStep;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButtonAlgoGen;
    private javax.swing.JRadioButton jRadioButtonCustomInput;
    private javax.swing.JRadioButton jRadioButtonEdge;
    private javax.swing.JRadioButton jRadioButtonVertex;
    private javax.swing.JScrollPane jScrollPaneAlgoTypeList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextArea jTextAreaInfo;
    private javax.swing.JTextField jTextFieldDataElement;
    private javax.swing.JTextField jTextFieldHasha;
    private javax.swing.JTextField jTextFieldHashb;
    private javax.swing.JTextField jTextFieldHashn;
    private javax.swing.JTextField jTextFieldRam;
    private javax.swing.JTextField jTextFieldTaskName;
    private javax.swing.JToggleButton jToggleTestCase;
    // End of variables declaration//GEN-END:variables

}
