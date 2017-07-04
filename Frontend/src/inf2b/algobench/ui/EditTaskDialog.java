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
            task.setTaskID(taskNameTextField.getText().trim());
        
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
        jLabel38 = new javax.swing.JLabel();
        jComboBoxNumRepeats = new javax.swing.JComboBox();
        jLabel39 = new javax.swing.JLabel();
        taskNameTextField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(550, 480));
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
                .addContainerGap(274, Short.MAX_VALUE))
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
                .addContainerGap(42, Short.MAX_VALUE))
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
                .addContainerGap(245, Short.MAX_VALUE))
        );
        jPanelGraphLayout.setVerticalGroup(
            jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGraphLayout.createSequentialGroup()
                .addGroup(jPanelGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxGraphStructure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelGraphStructure))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxSelfLoop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxDirected)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxSimulateLongVisit))
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
                .addContainerGap(43, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxSortDistribution, 0, 94, Short.MAX_VALUE)
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
                .addContainerGap(301, Short.MAX_VALUE))
        );
        jPanelHashInputSettingsLayout.setVerticalGroup(
            jPanelHashInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHashInputSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHashInputSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jComboBoxHashInputSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
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
                .addContainerGap(304, Short.MAX_VALUE))
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
                        .addGap(0, 209, Short.MAX_VALUE))
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
                .addContainerGap(294, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldHashn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanelHashConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelHashConfigSettingsLayout.createSequentialGroup()
                    .addGap(49, 49, 49)
                    .addComponent(jLabelHashWarn, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(24, Short.MAX_VALUE)))
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
                .addContainerGap(83, Short.MAX_VALUE))
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

        jLabel38.setFont(new java.awt.Font("Lucida Grande", 2, 13)); // NOI18N
        jLabel38.setText("If >1, Algobench will calculate average runtime ");

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
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                    .addGap(268, 268, 268)
                    .addComponent(jLabel38)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addContainerGap(70, Short.MAX_VALUE))
            .addGroup(jPanelSortAndSearchConfigSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanelSortAndSearchConfigSettingsLayout.createSequentialGroup()
                    .addGap(139, 139, 139)
                    .addComponent(jLabel38)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        configurationSettingsPanel.add(jPanelSortAndSearchConfigSettings, "sortAndSearchConfigSettings");

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
                    .addComponent(configurationSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addGap(4, 4, 4)
                .addComponent(inputSettingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(configurationSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
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
        if (taskNameTextField.getText().equals(taskName)) { // override task if task name is not changed?    
            String message = "Override the existing task? If no, please click 'No', and change the 'Task Name' on Edit Task Dialog";
            int result = JOptionPane.showConfirmDialog(this, message,
                    "Confirm Edit Task", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                task.setOverrideFlag(true);
            } else {
                task.setOverrideFlag(false);
                return;
            }
        }
        else 
            task.setOverrideFlag(false);
        
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

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel algorithmGroupLabel;
    private javax.swing.JLabel algorithmLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel configurationSettingsPanel;
    private javax.swing.JButton editTaskButton;
    private javax.swing.JPanel inputSettingsPanel;
    private javax.swing.JCheckBox jCheckBoxDirected;
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
    private javax.swing.JComboBox jComboBoxNumRepeats;
    private javax.swing.JComboBox<String> jComboBoxQSDistribution;
    private javax.swing.JComboBox<String> jComboBoxQSPivotPosition;
    private javax.swing.JComboBox jComboBoxRepeats;
    private javax.swing.JComboBox jComboBoxSearchInput;
    private javax.swing.JComboBox jComboBoxSearchKey;
    private javax.swing.JComboBox<String> jComboBoxSortDistribution;
    private javax.swing.JComboBox jComboBoxStepSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel38;
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
    private javax.swing.JLabel jLabelFinalSizeGraph;
    private javax.swing.JLabel jLabelFixedNumber;
    private javax.swing.JLabel jLabelGraphStructure;
    private javax.swing.JLabel jLabelHashWarn;
    private javax.swing.JLabel jLabelInitialSizeGraph;
    private javax.swing.JLabel jLabelNumElements;
    private javax.swing.JLabel jLabelSearchInput;
    private javax.swing.JLabel jLabelSearchKey;
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
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextFieldHasha;
    private javax.swing.JTextField jTextFieldHashb;
    private javax.swing.JTextField jTextFieldHashn;
    private javax.swing.JTextField jTextFieldRam;
    private javax.swing.JTextField taskNameTextField;
    // End of variables declaration//GEN-END:variables
}
