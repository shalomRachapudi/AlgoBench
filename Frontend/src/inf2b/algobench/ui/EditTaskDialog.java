/*
 * The MIT License
 *
 * Copyright 2017 Shalom.
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
import inf2b.algobench.model.Task;
import inf2b.algobench.model.TaskMaster;
import java.awt.CardLayout;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Shalom
 */
public class EditTaskDialog extends javax.swing.JDialog {
    
    Task task;
    boolean cancelEditTask;
    CardLayout inputSettingsCardLayout;
    CardLayout configSettingsCardLayout;
    int dataElement;
    private String error;
    private String taskName;

    /**
     * Creates new form EditTaskDialog
     */
    public EditTaskDialog(java.awt.Frame parent, boolean modal, TaskMaster tM) {
        super(parent, modal);
        this.setTitle("Edit Task");
                
        // initialize components
        initComponents();
        this.task = tM.getTask();
        this.inputSettingsCardLayout = (CardLayout) inputSettingsPanel.getLayout();
        this.configSettingsCardLayout = (CardLayout) configurationSettingsPanel.getLayout();
        
        setLabelsToExistingTaskValues();
        
        // auto update tree basic operations summary when a number is given
        jTextFieldDataElement.getDocument().addDocumentListener(new DocumentListener(){
            void updateSummary() {
                dataElement = 0;
                try {
                    dataElement = Integer.parseInt(jTextFieldDataElement.getText());
                    editTaskButton.setEnabled(true);
                }
                catch( java.lang.NumberFormatException ex)
                {
                    editTaskButton.setEnabled(false);
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
        
        //when jTextFieldRam is set, auto calculate number of elements (External Merge Sort)
        jTextFieldRam.getDocument().addDocumentListener( new DocumentListener()
        {
            double ram;
            @Override
            public void insertUpdate( DocumentEvent e )
            {
                if ( !jTextFieldRam.getText().equals( "" ) )
                {
                    if ( validateNumberType( jTextFieldRam.getText() ) ) 
                    {
                        try {
                            ram = Integer.parseInt( jTextFieldRam.getText() );
                        }
                        catch( Exception ex )
                        {
                            displayError( "Please enter a positve Integer" );
                        }
                        if( ram <= 0 ) 
                            displayError( "Input should be greater than ZERO" );
                        else
                        {
                            displayError( "" );
                            int count = ( int )( ram*1024 / 4 );
                            jLabelNumElements.setText( Integer.toString( count ) );
                        }
                    }
                }
            }

            @Override
            public void removeUpdate( DocumentEvent e )
            {
                insertUpdate( e );
            }

            @Override
            public void changedUpdate( DocumentEvent e ) 
            {
                insertUpdate( e );
            }
        
        });
        
        jTextFieldHasha.getDocument().addDocumentListener( new DocumentListener()
        {
            @Override
            public void insertUpdate( DocumentEvent e ) 
            {
                jLabelHashWarn.setText( "" );
                if( ( !jTextFieldHasha.getText().equals( "" ) )
                   && validateNumberType2( jTextFieldHasha.getText() ) )
                {
                    if( ( !jTextFieldHashn.getText().equals( "" ) )
                       && validateNumberType( jTextFieldHashn.getText() ) )
                    {
                        int a = Integer.parseInt(jTextFieldHasha.getText());
                        int n = Integer.parseInt(jTextFieldHashn.getText());
                        String displayMessage;
                        if( isCoprime( a, n ) )
                            displayMessage = "Constants a and N are coprime. It is likely to be a good hash function.";
                        else
                            displayMessage = "Constants a and N are not coprime. It is a bad hash function.";
                        jLabelHashWarn.setText( displayMessage );
                    }
                }
            }

            @Override
            public void removeUpdate( DocumentEvent e ) 
            {
                insertUpdate( e );
            }

            @Override
            public void changedUpdate( DocumentEvent e )
            {
                insertUpdate( e );
            }
        });
        
        jTextFieldHashn.getDocument().addDocumentListener( new DocumentListener() {
            @Override
            public void insertUpdate( DocumentEvent e ) 
            {
                jLabelHashWarn.setText( "" );
                if( ( !jTextFieldHasha.getText().equals( "" ) )
                   && validateNumberType2( jTextFieldHasha.getText() ) )
                {
                    if( ( !jTextFieldHashn.getText().equals( "" ) )
                       && validateNumberType( jTextFieldHashn.getText() ) )
                    {
                        int a = Integer.parseInt(jTextFieldHasha.getText());
                        int n = Integer.parseInt(jTextFieldHashn.getText());
                        String displayMessage;
                        if( isCoprime( a, n ) )
                            displayMessage = "Constants a and N are coprime. It is likely to be a good hash function.";
                        else
                            displayMessage = "Constants a and N are not coprime. It is a bad hash function.";
                        jLabelHashWarn.setText( displayMessage );
                    }
                }
            }

            @Override
            public void removeUpdate( DocumentEvent e )
            {
                insertUpdate( e );
            }

            @Override
            public void changedUpdate( DocumentEvent e ) 
            {
                insertUpdate( e );
            }
        });
    } // constructor
    
    // number can only be positive int or 0
    // Taken from NewTaskDialog.java
    private boolean validateNumberType(String number) {
        Pattern numPattern = Pattern.compile("^(\\d+)$");
        Matcher numMatcher = numPattern.matcher(number.trim());
        if (!numMatcher.find()) {
            displayError("Invalid number format supplied. Use only positive integers or 0.");
            return false;
        }
        return true;
    }
    
    // number can only be integers (positive or negtive)
    // Taken from NewTaskDialog.java
    private boolean validateNumberType2(String number) {
        Pattern numPattern = Pattern.compile("^-?\\d*$");
        Matcher numMatcher = numPattern.matcher(number.trim());
        if (!numMatcher.find()) {
            displayError("Invalid number format supplied. Use only integers.");
            return false;
        }
        return true;
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
    
    private void displayError(String message) {
        if (message.length() > 0) {
            message = "INFO: " + message;
        }
        jLabelTextInfo.setText( message );
    }
    
    /**
     * Set labels to the parameter values retrieved from the task object
     */
    private void setLabelsToExistingTaskValues()
    {
        String algoGroup = task.getAlgorithmGroup();
        String algoName = task.getAlgorithmShortName();
        
        /* set task information */
        algorithmGroupLabel.setText( algoGroup );
        algorithmLabel.setText( algoName );
        taskNameTextField.setText( task.getTaskID() );
        
        /* Set input and config settings values */
        switch( algoGroup.toLowerCase() )
        {
            case "sort":
                /* input settings */
                String inputDistribution = task.getInputDistribution();
                if ( algoName.equals( "QS" ) ) {
                    /* set and display inputSettingsPanel */
                    jComboBoxQSPivotPosition.setSelectedItem( task.getPivotPosition() );
                    jComboBoxQSDistribution.setSelectedItem( inputDistribution );
                    
                    /* show qsInputSettings card */ 
                    inputSettingsCardLayout.show( inputSettingsPanel, "qsInputSettings" );
                }
                else if ( algoName.equals( "EMS" ) ) {
                    int ram = task.getSortRam();
                    int numOfElements = ( ram * 1024 ) / 4;   // 4 because see ExternalMergeSort.h/.cpp
                    
                    jComboBoxEMSDistribution.setSelectedItem( inputDistribution );
                    jTextFieldRam.setText( Integer.toString( ram ) );
                    jLabelNumElements.setText( Integer.toString( numOfElements ) );
                    
                    /* show emsInputSettings card */ 
                    inputSettingsCardLayout.show( inputSettingsPanel, "emsInputSettings" );
                }
                else { // all other sorts
                    jComboBoxSortDistribution.setSelectedItem( inputDistribution );
                    
                    /* show otherSortsInputSettings card */ 
                    inputSettingsCardLayout.show( inputSettingsPanel, "otherSortsInputSettings" );
                }
                
                /* config settings */
                jComboBoxInitialSize.setSelectedItem( task.getInputStartSize() );
                jComboBoxFinalSize.setSelectedItem( task.getInputFinalSize() );
                jComboBoxStepSize.setSelectedItem( task.getInputStepSize() );
                jComboBoxNumRepeats.setSelectedItem( task.getNumRepeats() );
                
                /* show configSettings card */ 
                configSettingsCardLayout.show( configurationSettingsPanel, "sortAndSearchConfigSettings" );
                break;
            
            case "graph":
                
                /* set input settings */
                jCheckBoxSelfLoop.setSelected( task.getAllowSelfLoops() );
                jCheckBoxDirected.setSelected( task.getIsDirectedGraph() );
                jCheckBoxSimulateLongVisit.setSelected( task.getGraphIsDelayed() );
                
                /* show graphInputSettings card */ 
                inputSettingsCardLayout.show( inputSettingsPanel, "graphInputSettings" );
                
                /* set config settings */
                if ( task.getFixedGraphParam() == 1 ) { // if user specified edges?
                    jLabelFixedNumber.setText( "Edges: " );   // display "Edges" instead of "Vertices"
                    jLabelInitialSizeGraph.setText( "Initial no. of Vertices:" );
                    jLabelFinalSizeGraph.setText( "Final no. of Vertices:" );
                }
                jComboBoxFixedNumber.setSelectedItem( task.getFixedGraphSize() );
                jComboBoxInitialSizeGraph.setSelectedItem( task.getInputStartSize() );
                jComboBoxFinalSizeGraph.setSelectedItem( task.getInputFinalSize() );
                jComboBoxRepeats.setSelectedItem( task.getNumRepeats() );
                
                /* show config card */
                configSettingsCardLayout.show( configurationSettingsPanel, "graphConfigSettings" );
                break;
            
            case "search":
                /* set input settings */
                jComboBoxSearchInput.setSelectedItem( task.getInputDistribution() );
                jComboBoxSearchKey.setSelectedItem( task.getSearchKeyType() );
                
                inputSettingsCardLayout.show( inputSettingsPanel, "searchInputSettings" );
                
                /* set config settings */
                /* config settings */
                jComboBoxInitialSize.setSelectedItem( task.getInputStartSize() );
                jComboBoxFinalSize.setSelectedItem( task.getInputFinalSize() );
                jComboBoxStepSize.setSelectedItem( task.getInputStepSize() );
                jComboBoxNumRepeats.setSelectedItem( task.getNumRepeats() );
                
                /* show configSettings card */ 
                configSettingsCardLayout.show( configurationSettingsPanel, "sortAndSearchConfigSettings" );
                break;
            
            case "hash":
                /* set input settings */
                jComboBoxHashInputSize.setSelectedItem( task.getInputStartSize() );
                inputSettingsCardLayout.show( inputSettingsPanel, "hashInputSettings" );
                
                /* set config settings */
                jTextFieldHasha.setText( Integer.toString( task.getHashparameters( 'a' ) ) );
                jTextFieldHashb.setText( Integer.toString( task.getHashparameters( 'b' ) ) );
                jTextFieldHashn.setText( Integer.toString( task.getHashBucketSize() ) );
                configSettingsCardLayout.show( configurationSettingsPanel, "hashConfigSettings" );
                break;
            
            case "tree":
                /* set input settings */
                jComboBoxTreeSize.setSelectedItem(task.getTreeSize());
                jComboBoxTreeType.setSelectedIndex(Integer.parseInt(task.getTreeType()));
                jComboBoxLowerLimit.setSelectedItem(task.getTreeRangeLowerLimit());
                jComboBoxUpperLimit.setSelectedItem(task.getTreeRangeUpperLimit());
                
                inputSettingsCardLayout.show( inputSettingsPanel, "treeInputSettings" );
                
                /* set config settings */
                dataElement = Integer.parseInt(task.getDataElement());
                jTextFieldDataElement.setText(task.getDataElement());
                jRadioButtonCustomInput.setSelected(true);
                
                if (!task.getInsertOp()) {
                    if (dataElement < Integer.parseInt(task.getTreeRangeLowerLimit()))
                        jCheckBoxInsertOp.setEnabled(false);
                    jCheckBoxInsertOp.setSelected(false);
                }
                else
                    jCheckBoxInsertOp.setEnabled(true);
                
                if (!task.getSearchOp()) {
                    jCheckBoxSearchOp.setSelected(false);
                }
                
                if (!task.getDeleteOp()) {
                    jCheckBoxDeleteOp.setSelected(false);
                }
                    
                configSettingsCardLayout.show( configurationSettingsPanel, "treeConfigSettings" );
                break;
            default: 
                break;
        }
        
    }
    
    /**
     * Update task object parameters (values)
     */
    private void updateTaskValues()
    {
        String algoGroup = task.getAlgorithmGroup();
        String algoName = task.getAlgorithmShortName();
        
        // update task ID if user doesn't want to override existing task
        if (!task.getOverrideFlag()) 
            task.setTaskID(taskName);
        
        /* Set input and config settings values */
        switch( algoGroup.toLowerCase() )
        {
            case "sort":
                if ( algoName.equals( "QS" ) ) {
                    /* set and display inputSettingsPanel */
                    task.setPivotPosition( jComboBoxQSPivotPosition.getSelectedItem().toString() );
                    task.setInputDistribution( jComboBoxQSDistribution.getSelectedItem().toString() );
                }
                else if ( algoName.equals( "EMS" ) ) {
                    
                    task.setSortRam( jTextFieldRam.getText() );
                    task.setInputDistribution( jComboBoxEMSDistribution.getSelectedItem().toString() );
                }
                else { // all other sorts
                    task.setInputDistribution( jComboBoxSortDistribution.getSelectedItem().toString() );
                }
                
                /* config settings */
                task.setInputStartSize( Long.parseLong( jComboBoxInitialSize.getSelectedItem().toString() ) );
                task.setInputFinalSize( Long.parseLong( jComboBoxFinalSize.getSelectedItem().toString() ) );
                task.setInputStepSize( Long.parseLong( jComboBoxStepSize.getSelectedItem().toString() ) );
                task.setNumRepeats( Integer.parseInt( jComboBoxNumRepeats.getSelectedItem().toString() ) );
                break;
                
            case "search":
                /* set input settings */
                task.setInputDistribution( jComboBoxSearchInput.getSelectedItem().toString() );
                task.setSearchKeyType( jComboBoxSearchKey.getSelectedItem().toString() );
                
                task.setInputStartSize( Long.parseLong( jComboBoxInitialSize.getSelectedItem().toString() ) );
                task.setInputFinalSize( Long.parseLong( jComboBoxFinalSize.getSelectedItem().toString() ) );
                task.setInputStepSize( Long.parseLong( jComboBoxStepSize.getSelectedItem().toString() ) );
                task.setNumRepeats( Integer.parseInt( jComboBoxNumRepeats.getSelectedItem().toString() ) );
                break;
                
            case "graph":
                /* update graph's input & config settings */
                task.setAllowSelfLoops( jCheckBoxSelfLoop.isSelected() );
                task.setIsDirectedGraph( jCheckBoxDirected.isSelected() );
                task.setGraphIsDelayed( jCheckBoxSimulateLongVisit.isSelected() );
                
                task.setFixedGraphSize(Long.parseLong( jComboBoxFixedNumber.getSelectedItem().toString() ) );
                task.setInputStartSize( Long.parseLong( jComboBoxInitialSizeGraph.getSelectedItem().toString() ) );
                task.setInputFinalSize( Long.parseLong( jComboBoxFinalSizeGraph.getSelectedItem().toString() ) );
                task.setNumRepeats( Integer.parseInt( jComboBoxRepeats.getSelectedItem().toString() ) );
                break;
                
            case "hash":
                /* set input settings */
                task.setInputStartSize( Long.parseLong( jComboBoxHashInputSize.getSelectedItem().toString() ) );
                task.setHashparameters(
                                Integer.parseInt( jTextFieldHasha.getText() ),
                                Integer.parseInt( jTextFieldHashb.getText() )
                             );
                task.setHashBucketSize( Integer.parseInt( jTextFieldHashn.getText() ) );
                break;
            
            case "tree":
                /* update tree's input & config settings */
                task.setTreeSize(this.jComboBoxTreeSize.getSelectedItem().toString());
                task.setTreeRangeLowerLimit(this.jComboBoxLowerLimit.getSelectedItem().toString());
                task.setTreeRangeUpperLimit(this.jComboBoxUpperLimit.getSelectedItem().toString());
                task.setTreeType(this.jComboBoxTreeType.getSelectedItem().toString());
                    
                // basic operations
                task.setDataElement(Integer.toString(dataElement));
                task.setInsertOp(jCheckBoxInsertOp.isSelected());
                task.setSearchOp(jCheckBoxSearchOp.isSelected());
                task.setDeleteOp(jCheckBoxDeleteOp.isSelected());
                break;
            default:
                break;
        }
    }
    
    /**
     * Show EditTask GUI dialog
     * @return TaskMaster on success or null if editing a task is cancelled
     */
    public TaskMaster showDialog()
    {
        this.revalidate();
        //storeDefaultValues(jPanelMain);
        this.setVisible(true);

        if ( cancelEditTask ) {
            return null;
        }

        this.dispose();
        return new TaskMaster( task );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupBasicOp = new javax.swing.ButtonGroup();
        jPanelMain = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        algorithmGroupLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        algorithmLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        cancelButton = new javax.swing.JButton();
        editTaskButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        inputSettingsPanel = new javax.swing.JPanel();
        jPanelSearchInputSettings = new javax.swing.JPanel();
        jComboBoxSearchInput = new javax.swing.JComboBox();
        jLabelSearchInput = new javax.swing.JLabel();
        jLabelSearchKey = new javax.swing.JLabel();
        jComboBoxSearchKey = new javax.swing.JComboBox();
        jPanelGraph = new javax.swing.JPanel();
        jCheckBoxSimulateLongVisit = new javax.swing.JCheckBox();
        jCheckBoxSelfLoop = new javax.swing.JCheckBox();
        jCheckBoxDirected = new javax.swing.JCheckBox();
        jLabelGraphStructure = new javax.swing.JLabel();
        jComboBoxGraphStructure = new javax.swing.JComboBox();
        jPanelOtherSorts = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jComboBoxSortDistribution = new javax.swing.JComboBox<>();
        jPanelHashInputSettings = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jComboBoxHashInputSize = new javax.swing.JComboBox();
        jPanelQSInputSettings = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxQSPivotPosition = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxQSDistribution = new javax.swing.JComboBox<>();
        jPanelEMSInputSettings = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jComboBoxEMSDistribution = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldRam = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabelNumElements = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabelTextInfo = new javax.swing.JLabel();
        jPanelTreeInputSettings = new javax.swing.JPanel();
        jComboBoxTreeSize = new javax.swing.JComboBox();
        jLabelSearchInput1 = new javax.swing.JLabel();
        jLabelSearchKey1 = new javax.swing.JLabel();
        jComboBoxLowerLimit = new javax.swing.JComboBox();
        jLabelSearchInput2 = new javax.swing.JLabel();
        jComboBoxTreeType = new javax.swing.JComboBox();
        jLabelSearchInput3 = new javax.swing.JLabel();
        jComboBoxUpperLimit = new javax.swing.JComboBox();
        jLabelError = new javax.swing.JLabel();
        configurationSettingsPanel = new javax.swing.JPanel();
        jPanelHashConfigSettings = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jTextFieldHashb = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jTextFieldHasha = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jTextFieldHashn = new javax.swing.JTextField();
        jLabelHashWarn = new javax.swing.JLabel();
        jPanelGraphConfigSettings = new javax.swing.JPanel();
        jLabelFixedNumber = new javax.swing.JLabel();
        jComboBoxFixedNumber = new javax.swing.JComboBox();
        jLabelInitialSizeGraph = new javax.swing.JLabel();
        jComboBoxInitialSizeGraph = new javax.swing.JComboBox();
        jCheckBoxUseLowerLimit = new javax.swing.JCheckBox();
        jLabelFinalSizeGraph = new javax.swing.JLabel();
        jComboBoxFinalSizeGraph = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jComboBoxRepeats = new javax.swing.JComboBox();
        jPanelSortAndSearchConfigSettings = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jComboBoxInitialSize = new javax.swing.JComboBox();
        jComboBoxFinalSize = new javax.swing.JComboBox();
        jComboBoxStepSize = new javax.swing.JComboBox();
        jLabel31 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jComboBoxNumRepeats = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        jPanelTreeConfigSettings = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jRadioButtonAlgoGen = new javax.swing.JRadioButton();
        jRadioButtonCustomInput = new javax.swing.JRadioButton();
        jToggleTestCase = new javax.swing.JToggleButton();
        jToggleTestCase.setVisible(false);
        jTextFieldDataElement = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jCheckBoxInsertOp = new javax.swing.JCheckBox();
        jCheckBoxSearchOp = new javax.swing.JCheckBox();
        jCheckBoxDeleteOp = new javax.swing.JCheckBox();
        taskNameTextField = new javax.swing.JTextField();

        buttonGroupBasicOp.add(jRadioButtonAlgoGen);
        buttonGroupBasicOp.add(jRadioButtonCustomInput);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(580, 480));
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setName("jDialogEditTask"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelMain.setMaximumSize(new java.awt.Dimension(550, 480));
        jPanelMain.setMinimumSize(new java.awt.Dimension(550, 480));
        jPanelMain.setPreferredSize(new java.awt.Dimension(500, 450));

        jLabel1.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel1.setText("Task Information");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Algorithm Group: ");

        algorithmGroupLabel.setText("NA");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Algorithm:");

        algorithmLabel.setText("NA");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Task Name:");

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel3.setText("Input Settings");

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        jSeparator3.setForeground(new java.awt.Color(0, 0, 0));

        cancelButton.setText("Cancel");
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelButtonMouseClicked(evt);
            }
        });
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        editTaskButton.setText("Done");
        editTaskButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editTaskButtonMouseClicked(evt);
            }
        });
        editTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTaskButtonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Yu Gothic", 1, 18)); // NOI18N
        jLabel5.setText("Configuration Settings");

        inputSettingsPanel.setLayout(new java.awt.CardLayout());

        jComboBoxSearchInput.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabelSearchInput.setText("Input Array:");

        jLabelSearchKey.setText("Search Key:");

        jComboBoxSearchKey.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Always in array", "Not in array", "Random" }));
        jComboBoxSearchKey.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxSearchKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSearchKeyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSearchInputSettingsLayout = new javax.swing.GroupLayout(jPanelSearchInputSettings);
        jPanelSearchInputSettings.setLayout(jPanelSearchInputSettingsLayout);
        jPanelSearchInputSettingsLayout.setHorizontalGroup(
            jPanelSearchInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchInputSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelSearchInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelSearchInput)
                    .addComponent(jLabelSearchKey))
                .addGap(28, 28, 28)
                .addGroup(jPanelSearchInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jComboBoxSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
<<<<<<< HEAD
                .addContainerGap(344, Short.MAX_VALUE))
=======
                .addContainerGap(304, Short.MAX_VALUE))
>>>>>>> master
        );
        jPanelSearchInputSettingsLayout.setVerticalGroup(
            jPanelSearchInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchInputSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSearchInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearchInput)
                    .addComponent(jComboBoxSearchInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSearchInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSearchKey))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        inputSettingsPanel.add(jPanelSearchInputSettings, "searchInputSettings");

        jCheckBoxSimulateLongVisit.setText("Simulate longer vertex visits");

        jCheckBoxSelfLoop.setText("Allow self loops");

        jCheckBoxDirected.setText("Use directed edges");

        jLabelGraphStructure.setText("Graph representation:");

        jComboBoxGraphStructure.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Adjacency List" }));
        jComboBoxGraphStructure.setPreferredSize(new java.awt.Dimension(120, 25));

        javax.swing.GroupLayout jPanelGraphLayout = new javax.swing.GroupLayout(jPanelGraph);
        jPanelGraph.setLayout(jPanelGraphLayout);
        jPanelGraphLayout.setHorizontalGroup(
            jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxSimulateLongVisit)
                    .addComponent(jCheckBoxDirected)
                    .addGroup(jPanelGraphLayout.createSequentialGroup()
                        .addGroup(jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelGraphStructure)
                            .addComponent(jCheckBoxSelfLoop))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
<<<<<<< HEAD
                .addContainerGap(315, Short.MAX_VALUE))
=======
                .addContainerGap(275, Short.MAX_VALUE))
>>>>>>> master
        );
        jPanelGraphLayout.setVerticalGroup(
            jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphLayout.createSequentialGroup()
                .addGroup(jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGraphStructure))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxSelfLoop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDirected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxSimulateLongVisit)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        inputSettingsPanel.add(jPanelGraph, "graphInputSettings");

        jLabel13.setText("Distribution:");

        jComboBoxSortDistribution.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Random", "Sorted", "Reverse Sorted" }));
        jComboBoxSortDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxSortDistributionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelOtherSortsLayout = new javax.swing.GroupLayout(jPanelOtherSorts);
        jPanelOtherSorts.setLayout(jPanelOtherSortsLayout);
        jPanelOtherSortsLayout.setHorizontalGroup(
            jPanelOtherSortsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelOtherSortsLayout.createSequentialGroup()
<<<<<<< HEAD
                .addContainerGap(78, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxSortDistribution, 0, 129, Short.MAX_VALUE)
=======
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxSortDistribution, 0, 109, Short.MAX_VALUE)
>>>>>>> master
                .addGap(325, 325, 325))
        );
        jPanelOtherSortsLayout.setVerticalGroup(
            jPanelOtherSortsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelOtherSortsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelOtherSortsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBoxSortDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inputSettingsPanel.add(jPanelOtherSorts, "otherSortsInputSettings");

        jLabel25.setText("Input size:");

        jComboBoxHashInputSize.setEditable(true);
        jComboBoxHashInputSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1000", "10000", "500000", "1000000" }));
        jComboBoxHashInputSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxHashInputSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxHashInputSizeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelHashInputSettingsLayout = new javax.swing.GroupLayout(jPanelHashInputSettings);
        jPanelHashInputSettings.setLayout(jPanelHashInputSettingsLayout);
        jPanelHashInputSettingsLayout.setHorizontalGroup(
            jPanelHashInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashInputSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxHashInputSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
<<<<<<< HEAD
                .addContainerGap(371, Short.MAX_VALUE))
=======
                .addContainerGap(331, Short.MAX_VALUE))
>>>>>>> master
        );
        jPanelHashInputSettingsLayout.setVerticalGroup(
            jPanelHashInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashInputSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHashInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jComboBoxHashInputSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

        inputSettingsPanel.add(jPanelHashInputSettings, "hashInputSettings");

        jLabel7.setText("Pivot Position:");

        jComboBoxQSPivotPosition.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Left", "Center" }));
        jComboBoxQSPivotPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxQSPivotPositionActionPerformed(evt);
            }
        });

        jLabel8.setText("Distribution:");

        jComboBoxQSDistribution.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Random", "Sorted", "Reverse-sorted", "Worst-case" }));

        javax.swing.GroupLayout jPanelQSInputSettingsLayout = new javax.swing.GroupLayout(jPanelQSInputSettings);
        jPanelQSInputSettings.setLayout(jPanelQSInputSettingsLayout);
        jPanelQSInputSettingsLayout.setHorizontalGroup(
            jPanelQSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQSInputSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelQSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelQSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBoxQSDistribution, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxQSPivotPosition, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
<<<<<<< HEAD
                .addContainerGap(374, Short.MAX_VALUE))
=======
                .addContainerGap(334, Short.MAX_VALUE))
>>>>>>> master
        );
        jPanelQSInputSettingsLayout.setVerticalGroup(
            jPanelQSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelQSInputSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelQSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBoxQSPivotPosition, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(jPanelQSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jComboBoxQSDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inputSettingsPanel.add(jPanelQSInputSettings, "qsInputSettings");

        jLabel9.setText("Distribution:");

        jComboBoxEMSDistribution.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Random", "Sorted", "Reverse-sorted" }));
        jComboBoxEMSDistribution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxEMSDistributionActionPerformed(evt);
            }
        });

        jLabel12.setText("RAM :");

        jTextFieldRam.setText("16");

        jLabel14.setText("KB");

        jLabel10.setForeground(new java.awt.Color(255, 0, 0));
        jLabel10.setText("Please notice that the unit of RAM is using KB, and ");

        jLabel15.setForeground(new java.awt.Color(255, 0, 0));
        jLabel15.setText("the RAM you have set equals to");

        jLabelNumElements.setForeground(new java.awt.Color(255, 0, 0));
        jLabelNumElements.setText("4096");

        jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        jLabel11.setText("of input elements");

        jLabelTextInfo.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        javax.swing.GroupLayout jPanelEMSInputSettingsLayout = new javax.swing.GroupLayout(jPanelEMSInputSettings);
        jPanelEMSInputSettings.setLayout(jPanelEMSInputSettingsLayout);
        jPanelEMSInputSettingsLayout.setHorizontalGroup(
            jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEMSInputSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEMSInputSettingsLayout.createSequentialGroup()
                        .addGroup(jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanelEMSInputSettingsLayout.createSequentialGroup()
                                .addGroup(jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanelEMSInputSettingsLayout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBoxEMSDistribution, 0, 1, Short.MAX_VALUE))
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelNumElements)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)))
<<<<<<< HEAD
                        .addGap(0, 279, Short.MAX_VALUE))
=======
                        .addGap(0, 239, Short.MAX_VALUE))
>>>>>>> master
                    .addGroup(jPanelEMSInputSettingsLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(41, 41, 41)
                        .addComponent(jTextFieldRam, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelTextInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanelEMSInputSettingsLayout.setVerticalGroup(
            jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEMSInputSettingsLayout.createSequentialGroup()
                .addGroup(jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextFieldRam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabelTextInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabelNumElements)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelEMSInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jComboBoxEMSDistribution, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inputSettingsPanel.add(jPanelEMSInputSettings, "emsInputSettings");

        jComboBoxTreeSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "25000", "50000", "100000" }));
        jComboBoxTreeSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxTreeSize.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTreeSizeItemStateChanged(evt);
            }
        });

        jLabelSearchInput1.setText("Tree Size (N):");

        jLabelSearchKey1.setText("Lower Limit:");

        jComboBoxLowerLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "500", "1000", "10000", "25000", "50000", "75000" }));
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

        jLabelSearchInput2.setText("Tree Type:");

        jComboBoxTreeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rooted Tree", "Left Skewed Tree", "Right Skewed Tree" }));
        jComboBoxTreeType.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabelSearchInput3.setText("Upper Limit:");

        jComboBoxUpperLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "25000", "50000", "75000", "100000" }));
        jComboBoxUpperLimit.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxUpperLimit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxUpperLimitItemStateChanged(evt);
            }
        });

        jLabelError.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        javax.swing.GroupLayout jPanelTreeInputSettingsLayout = new javax.swing.GroupLayout(jPanelTreeInputSettings);
        jPanelTreeInputSettings.setLayout(jPanelTreeInputSettingsLayout);
        jPanelTreeInputSettingsLayout.setHorizontalGroup(
            jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeInputSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelTreeInputSettingsLayout.createSequentialGroup()
                        .addGroup(jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSearchInput1)
                            .addComponent(jLabelSearchKey1))
                        .addGap(28, 28, 28)
                        .addGroup(jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBoxLowerLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxTreeSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelTreeInputSettingsLayout.createSequentialGroup()
                                .addComponent(jLabelSearchInput2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBoxTreeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelTreeInputSettingsLayout.createSequentialGroup()
                                .addComponent(jLabelSearchInput3)
                                .addGap(28, 28, 28)
                                .addComponent(jComboBoxUpperLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(123, Short.MAX_VALUE))
        );
        jPanelTreeInputSettingsLayout.setVerticalGroup(
            jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeInputSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearchInput1)
                    .addComponent(jComboBoxTreeSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSearchInput2)
                    .addComponent(jComboBoxTreeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanelTreeInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelSearchInput3)
                    .addComponent(jComboBoxUpperLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxLowerLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSearchKey1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                .addContainerGap())
        );

        inputSettingsPanel.add(jPanelTreeInputSettings, "treeInputSettings");

        configurationSettingsPanel.setLayout(new java.awt.CardLayout());

        jLabel43.setText("b =");

        jLabel42.setText("a = ");

        jLabel41.setText("Hash Function:");

        jLabel44.setText("|aK + b| mod N");

        jLabel45.setText("N =");

        jLabelHashWarn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabelHashWarn.setForeground(new java.awt.Color(204, 0, 51));

        javax.swing.GroupLayout jPanelHashConfigSettingsLayout = new javax.swing.GroupLayout(jPanelHashConfigSettings);
        jPanelHashConfigSettings.setLayout(jPanelHashConfigSettingsLayout);
        jPanelHashConfigSettingsLayout.setHorizontalGroup(
            jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                        .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldHashn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldHashb, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                        .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldHasha, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
<<<<<<< HEAD
                .addContainerGap(364, Short.MAX_VALUE))
=======
                .addContainerGap(324, Short.MAX_VALUE))
>>>>>>> master
            .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                    .addGap(178, 178, 178)
                    .addComponent(jLabelHashWarn, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanelHashConfigSettingsLayout.setVerticalGroup(
            jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHasha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHashb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHashn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                    .addGap(49, 49, 49)
                    .addComponent(jLabelHashWarn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(58, Short.MAX_VALUE)))
        );

        configurationSettingsPanel.add(jPanelHashConfigSettings, "hashConfigSettings");

        jLabelFixedNumber.setText("Vertices:");

        jComboBoxFixedNumber.setEditable(true);
        jComboBoxFixedNumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100000", "500000", "1000000", "5000000" }));
        jComboBoxFixedNumber.setSelectedIndex(1);
        jComboBoxFixedNumber.setName("STARTSIZE"); // NOI18N
        jComboBoxFixedNumber.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFixedNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFixedNumberActionPerformed(evt);
            }
        });

        jLabelInitialSizeGraph.setText("Initial no. of Edges:");

        jComboBoxInitialSizeGraph.setEditable(true);
        jComboBoxInitialSizeGraph.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0" }));
        jComboBoxInitialSizeGraph.setName("STARTSIZE"); // NOI18N
        jComboBoxInitialSizeGraph.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxInitialSizeGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInitialSizeGraphActionPerformed(evt);
            }
        });

        jCheckBoxUseLowerLimit.setText("Use Lower Limit");
        jCheckBoxUseLowerLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxUseLowerLimitActionPerformed(evt);
            }
        });

        jLabelFinalSizeGraph.setText("Final no. of Edges:");

        jComboBoxFinalSizeGraph.setEditable(true);
        jComboBoxFinalSizeGraph.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100000", "200000", "500000", "1000000", "2000000", "5000000" }));
        jComboBoxFinalSizeGraph.setName("NUMSTEPS"); // NOI18N
        jComboBoxFinalSizeGraph.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFinalSizeGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFinalSizeGraphActionPerformed(evt);
            }
        });

        jLabel16.setText("No. of Experiments per Step:");

        jComboBoxRepeats.setEditable(true);
        jComboBoxRepeats.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        jComboBoxRepeats.setName("NUMSTEPS"); // NOI18N
        jComboBoxRepeats.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxRepeats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRepeatsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGraphConfigSettingsLayout = new javax.swing.GroupLayout(jPanelGraphConfigSettings);
        jPanelGraphConfigSettings.setLayout(jPanelGraphConfigSettingsLayout);
        jPanelGraphConfigSettingsLayout.setHorizontalGroup(
            jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphConfigSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGraphConfigSettingsLayout.createSequentialGroup()
                        .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelFinalSizeGraph)
                            .addComponent(jLabelInitialSizeGraph))
                        .addGap(206, 206, 206)
                        .addComponent(jCheckBoxUseLowerLimit))
                    .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanelGraphConfigSettingsLayout.createSequentialGroup()
                            .addComponent(jLabel16)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxRepeats, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBoxInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelGraphConfigSettingsLayout.createSequentialGroup()
                                .addComponent(jLabelFixedNumber)
                                .addGap(120, 120, 120)
                                .addComponent(jComboBoxFixedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jComboBoxFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
<<<<<<< HEAD
                .addContainerGap(153, Short.MAX_VALUE))
=======
                .addContainerGap(113, Short.MAX_VALUE))
>>>>>>> master
        );
        jPanelGraphConfigSettingsLayout.setVerticalGroup(
            jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphConfigSettingsLayout.createSequentialGroup()
                .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxFixedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFixedNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxUseLowerLimit)
                    .addComponent(jLabelInitialSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelFinalSizeGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelGraphConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jComboBoxRepeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        configurationSettingsPanel.add(jPanelGraphConfigSettings, "graphConfigSettings");

        jLabel27.setText("Initial Input Size:");

        jComboBoxInitialSize.setEditable(true);
        jComboBoxInitialSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "20000", "50000", "100000" }));
        jComboBoxInitialSize.setName("STARTSIZE"); // NOI18N
        jComboBoxInitialSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxInitialSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInitialSizeActionPerformed(evt);
            }
        });

        jComboBoxFinalSize.setEditable(true);
        jComboBoxFinalSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "500000", "1000000", "2000000", "5000000", "10000000" }));
        jComboBoxFinalSize.setName("NUMSTEPS"); // NOI18N
        jComboBoxFinalSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxFinalSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFinalSizeActionPerformed(evt);
            }
        });

        jComboBoxStepSize.setEditable(true);
        jComboBoxStepSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10000", "20000", "50000", "100000" }));
        jComboBoxStepSize.setName("STEPSIZE"); // NOI18N
        jComboBoxStepSize.setPreferredSize(new java.awt.Dimension(120, 25));
        jComboBoxStepSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStepSizeActionPerformed(evt);
            }
        });

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Number of Experiments per Input Size:");

        jLabel29.setText("Step Size:");

        jLabel28.setText("Final Input Size:");

        jComboBoxNumRepeats.setEditable(true);
        jComboBoxNumRepeats.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5" }));
        jComboBoxNumRepeats.setName("NUMREPEATS"); // NOI18N
        jComboBoxNumRepeats.setPreferredSize(new java.awt.Dimension(120, 25));

        jLabel39.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel39.setText("If >1, Algobench will calculate average runtime ");

        javax.swing.GroupLayout jPanelSortAndSearchConfigSettingsLayout = new javax.swing.GroupLayout(jPanelSortAndSearchConfigSettings);
        jPanelSortAndSearchConfigSettings.setLayout(jPanelSortAndSearchConfigSettingsLayout);
        jPanelSortAndSearchConfigSettingsLayout.setHorizontalGroup(
            jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxStepSize, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                        .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxInitialSize, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFinalSize, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxNumRepeats, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel39))))
<<<<<<< HEAD
                .addContainerGap(88, Short.MAX_VALUE))
=======
                .addContainerGap(48, Short.MAX_VALUE))
            .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                    .addGap(268, 268, 268)
                    .addComponent(jLabel38)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
>>>>>>> master
        );
        jPanelSortAndSearchConfigSettingsLayout.setVerticalGroup(
            jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxInitialSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxNumRepeats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxFinalSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxStepSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        configurationSettingsPanel.add(jPanelSortAndSearchConfigSettings, "sortAndSearchConfigSettings");

        jLabel30.setText("Data Element:");

        jRadioButtonAlgoGen.setText("Use Algobench-generated data element");
        jRadioButtonAlgoGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonAlgoGenActionPerformed(evt);
            }
        });

        jRadioButtonCustomInput.setText("Use custom input element");
        jRadioButtonCustomInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCustomInputActionPerformed(evt);
            }
        });

        jToggleTestCase.setSelected(true);
        jToggleTestCase.setText("Positive Case");
        jToggleTestCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleTestCaseActionPerformed(evt);
            }
        });

        jLabel17.setText("Summary: The following operations will be performed");

        jCheckBoxInsertOp.setSelected(true);
        jCheckBoxInsertOp.setText("insert()");
        jCheckBoxInsertOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxInsertOpActionPerformed(evt);
            }
        });

        jCheckBoxSearchOp.setSelected(true);
        jCheckBoxSearchOp.setText("search()");
        jCheckBoxSearchOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxSearchOpActionPerformed(evt);
            }
        });

        jCheckBoxDeleteOp.setSelected(true);
        jCheckBoxDeleteOp.setText("delete()");
        jCheckBoxDeleteOp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxDeleteOpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTreeConfigSettingsLayout = new javax.swing.GroupLayout(jPanelTreeConfigSettings);
        jPanelTreeConfigSettings.setLayout(jPanelTreeConfigSettingsLayout);
        jPanelTreeConfigSettingsLayout.setHorizontalGroup(
            jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeConfigSettingsLayout.createSequentialGroup()
                .addGroup(jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTreeConfigSettingsLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addGroup(jPanelTreeConfigSettingsLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDataElement, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelTreeConfigSettingsLayout.createSequentialGroup()
                                .addGroup(jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButtonCustomInput)
                                    .addComponent(jRadioButtonAlgoGen))
                                .addGap(38, 38, 38)
                                .addComponent(jToggleTestCase))))
                    .addGroup(jPanelTreeConfigSettingsLayout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jCheckBoxInsertOp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jCheckBoxSearchOp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66)
                        .addComponent(jCheckBoxDeleteOp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        jPanelTreeConfigSettingsLayout.setVerticalGroup(
            jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTreeConfigSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonAlgoGen)
                    .addComponent(jToggleTestCase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonCustomInput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDataElement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelTreeConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxInsertOp)
                    .addComponent(jCheckBoxSearchOp)
                    .addComponent(jCheckBoxDeleteOp))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        configurationSettingsPanel.add(jPanelTreeConfigSettings, "treeConfigSettings");

        taskNameTextField.setToolTipText("Edit to Change Task Name");
        taskNameTextField.setPreferredSize(new java.awt.Dimension(132, 20));
        taskNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskNameTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMainLayout = new javax.swing.GroupLayout(jPanelMain);
        jPanelMain.setLayout(jPanelMainLayout);
        jPanelMainLayout.setHorizontalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMainLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editTaskButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator3)
                    .addComponent(inputSettingsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(configurationSettingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelMainLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(algorithmGroupLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(algorithmLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(taskNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelMainLayout.setVerticalGroup(
            jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(algorithmGroupLabel)
                    .addComponent(jLabel4)
                    .addComponent(algorithmLabel)
                    .addComponent(jLabel6)
                    .addComponent(taskNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(configurationSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(editTaskButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
<<<<<<< HEAD
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
=======
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
>>>>>>> master
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
        );

        jPanelMain.getAccessibleContext().setAccessibleName("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxQSPivotPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxQSPivotPositionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxQSPivotPositionActionPerformed

    private void jComboBoxEMSDistributionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxEMSDistributionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxEMSDistributionActionPerformed

    private void jComboBoxSortDistributionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSortDistributionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxSortDistributionActionPerformed

    private void jComboBoxHashInputSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxHashInputSizeActionPerformed

    }//GEN-LAST:event_jComboBoxHashInputSizeActionPerformed

    private void jComboBoxInitialSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInitialSizeActionPerformed
    }//GEN-LAST:event_jComboBoxInitialSizeActionPerformed

    private void jComboBoxFinalSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFinalSizeActionPerformed

    }//GEN-LAST:event_jComboBoxFinalSizeActionPerformed

    private void jComboBoxStepSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStepSizeActionPerformed

    }//GEN-LAST:event_jComboBoxStepSizeActionPerformed

    private void jComboBoxSearchKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxSearchKeyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxSearchKeyActionPerformed

    private void jComboBoxFixedNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFixedNumberActionPerformed
        
    }//GEN-LAST:event_jComboBoxFixedNumberActionPerformed

    private void jComboBoxInitialSizeGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInitialSizeGraphActionPerformed
        
    }//GEN-LAST:event_jComboBoxInitialSizeGraphActionPerformed

    private void jCheckBoxUseLowerLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxUseLowerLimitActionPerformed
   
    }//GEN-LAST:event_jCheckBoxUseLowerLimitActionPerformed

    private void jComboBoxFinalSizeGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFinalSizeGraphActionPerformed
        
    }//GEN-LAST:event_jComboBoxFinalSizeGraphActionPerformed

    private void jComboBoxRepeatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxRepeatsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxRepeatsActionPerformed

    private void editTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTaskButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editTaskButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void editTaskButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editTaskButtonMouseClicked
        // TODO add your handling code here:
        /* Update task values on editTask button */
        
        if ( cancelEditTask == true )
            return;
        cancelEditTask = false;
        
        String taskName = task.getTaskID();
        if (taskNameTextField.getText().length() <= 0) {
            String message = "Oops! Task Name cannot be empty.";
            JOptionPane.showMessageDialog(this, message, "Error: Task Name", JOptionPane.ERROR_MESSAGE);
            taskNameTextField.setText(task.getTaskID()); // set prev task name in case user forgets
            return;
        }
        else if (taskNameTextField.getText().equals(taskName)) { // override task if task name is not changed?    
            String message = "Override the existing task? If no, please click 'No', and change the 'Task Name' on Edit Task Dialog";
            String[] options = {"Yes", "No", "Cancel"};
            int result = JOptionPane.showOptionDialog(this, message,
                    "Confirm Edit Task", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            if (result == 0) {
                task.setOverrideFlag(true);
            }
            else if (result == 1) { //generate a new name
               task.setOverrideFlag(false);
               
               // task ID or task name
                long tmp = System.currentTimeMillis() % 1000;
                String id = "";
                if (id.equals("")) {
                    if(tmp < 100)
                        id = "0" + tmp;
                    else 
                        id = "" + tmp;
                    this.taskName = task.getAlgorithm().toLowerCase() + "_" + id;
                }
            }
            else {
                task.setOverrideFlag(false);
                return;
            }
        }
        else   {
            this.taskName = taskNameTextField.getText().trim();
            task.setOverrideFlag(false);
        }
            
        
        updateTaskValues();
        this.setVisible( false );
    }//GEN-LAST:event_editTaskButtonMouseClicked

    private void cancelButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseClicked
        // TODO add your handling code here:
        cancelEditTask = true;
        this.setVisible( false );
    }//GEN-LAST:event_cancelButtonMouseClicked

    private void taskNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_taskNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_taskNameTextFieldActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        cancelEditTask = true;
    }//GEN-LAST:event_formWindowClosing

    private void jComboBoxLowerLimitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLowerLimitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxLowerLimitActionPerformed

    private void jRadioButtonAlgoGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonAlgoGenActionPerformed
        // TODO add your handling code here:
                // TODO add your handling code here:
         if (jRadioButtonAlgoGen.isSelected()) {
            jToggleTestCase.setVisible(true);
            jTextFieldDataElement.setText("");
            jTextFieldDataElement.setEnabled(false);
            editTaskButton.setEnabled(true);
            
            // get a random integer 
            dataElement = generateDataElement(jToggleTestCase.isSelected());
            
            // update summary and data element?
            updateBasicOpSummary(dataElement);
        }

    }//GEN-LAST:event_jRadioButtonAlgoGenActionPerformed

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
    
    private void jCheckBoxInsertOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxInsertOpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxInsertOpActionPerformed

    private void jCheckBoxSearchOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxSearchOpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxSearchOpActionPerformed

    private void jCheckBoxDeleteOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxDeleteOpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxDeleteOpActionPerformed

    private void jRadioButtonCustomInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCustomInputActionPerformed
        // TODO add your handling code here:
        if (jRadioButtonCustomInput.isSelected()) {
            jToggleTestCase.setVisible(false);
            jTextFieldDataElement.setEnabled(true);
            
            // enable and check jCheckBoxInsertOp
            jCheckBoxInsertOp.setEnabled(true);
            jCheckBoxInsertOp.setSelected(true);
            
            
            if (jTextFieldDataElement.getText().equals(""))
                editTaskButton.setEnabled(false);
            
            resetSummary();            
        }
    }//GEN-LAST:event_jRadioButtonCustomInputActionPerformed

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

    private void jComboBoxLowerLimitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLowerLimitItemStateChanged
        // TODO add your handling code here:
        validateTreeParams();      
    }//GEN-LAST:event_jComboBoxLowerLimitItemStateChanged

    private void jComboBoxUpperLimitItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxUpperLimitItemStateChanged
        // TODO add your handling code here:
        validateTreeParams();        
    }//GEN-LAST:event_jComboBoxUpperLimitItemStateChanged

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
            editTaskButton.setEnabled(false);
        }
        else {
            jLabelError.setText("");
            error = "";
            editTaskButton.setEnabled(true);
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
            error += "<html>Number of unique elements generated by the above Range is lower than the specified Tree Size.<br>Please increase the"
                  + " range or decrease the tree size</html>";
            jLabelError.setText(error);
            editTaskButton.setEnabled(false);
        }
        else {
            jLabelError.setText("");
            error = "";
            editTaskButton.setEnabled(true);
        }   
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel algorithmGroupLabel;
    private javax.swing.JLabel algorithmLabel;
    private javax.swing.ButtonGroup buttonGroupBasicOp;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel configurationSettingsPanel;
    private javax.swing.JButton editTaskButton;
    private javax.swing.JPanel inputSettingsPanel;
    private javax.swing.JCheckBox jCheckBoxDeleteOp;
    private javax.swing.JCheckBox jCheckBoxDirected;
    private javax.swing.JCheckBox jCheckBoxInsertOp;
    private javax.swing.JCheckBox jCheckBoxSearchOp;
    private javax.swing.JCheckBox jCheckBoxSelfLoop;
    private javax.swing.JCheckBox jCheckBoxSimulateLongVisit;
    private javax.swing.JCheckBox jCheckBoxUseLowerLimit;
    private javax.swing.JComboBox<String> jComboBoxEMSDistribution;
    private javax.swing.JComboBox jComboBoxFinalSize;
    private javax.swing.JComboBox jComboBoxFinalSizeGraph;
    private javax.swing.JComboBox jComboBoxFixedNumber;
    private javax.swing.JComboBox jComboBoxGraphStructure;
    private javax.swing.JComboBox jComboBoxHashInputSize;
    private javax.swing.JComboBox jComboBoxInitialSize;
    private javax.swing.JComboBox jComboBoxInitialSizeGraph;
    private javax.swing.JComboBox jComboBoxLowerLimit;
    private javax.swing.JComboBox jComboBoxNumRepeats;
    private javax.swing.JComboBox<String> jComboBoxQSDistribution;
    private javax.swing.JComboBox<String> jComboBoxQSPivotPosition;
    private javax.swing.JComboBox jComboBoxRepeats;
    private javax.swing.JComboBox jComboBoxSearchInput;
    private javax.swing.JComboBox jComboBoxSearchKey;
    private javax.swing.JComboBox<String> jComboBoxSortDistribution;
    private javax.swing.JComboBox jComboBoxStepSize;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelFinalSizeGraph;
    private javax.swing.JLabel jLabelFixedNumber;
    private javax.swing.JLabel jLabelGraphStructure;
    private javax.swing.JLabel jLabelHashWarn;
    private javax.swing.JLabel jLabelInitialSizeGraph;
    private javax.swing.JLabel jLabelNumElements;
    private javax.swing.JLabel jLabelSearchInput;
    private javax.swing.JLabel jLabelSearchInput1;
    private javax.swing.JLabel jLabelSearchInput2;
    private javax.swing.JLabel jLabelSearchInput3;
    private javax.swing.JLabel jLabelSearchKey;
    private javax.swing.JLabel jLabelSearchKey1;
    private javax.swing.JLabel jLabelTextInfo;
    private javax.swing.JPanel jPanelEMSInputSettings;
    private javax.swing.JPanel jPanelGraph;
    private javax.swing.JPanel jPanelGraphConfigSettings;
    private javax.swing.JPanel jPanelHashConfigSettings;
    private javax.swing.JPanel jPanelHashInputSettings;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelOtherSorts;
    private javax.swing.JPanel jPanelQSInputSettings;
    private javax.swing.JPanel jPanelSearchInputSettings;
    private javax.swing.JPanel jPanelSortAndSearchConfigSettings;
    private javax.swing.JPanel jPanelTreeConfigSettings;
    private javax.swing.JPanel jPanelTreeInputSettings;
    private javax.swing.JRadioButton jRadioButtonAlgoGen;
    private javax.swing.JRadioButton jRadioButtonCustomInput;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldDataElement;
    private javax.swing.JTextField jTextFieldHasha;
    private javax.swing.JTextField jTextFieldHashb;
    private javax.swing.JTextField jTextFieldHashn;
    private javax.swing.JTextField jTextFieldRam;
    private javax.swing.JToggleButton jToggleTestCase;
    private javax.swing.JTextField taskNameTextField;
    // End of variables declaration//GEN-END:variables
}
