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
import inf2b.algobench.main.AlgoBench.TaskView;
import inf2b.algobench.model.TaskMaster;
import inf2b.algobench.util.ListRenderer;
import inf2b.algobench.util.ITaskCompleteListener;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainWindow extends JFrame implements ITaskCompleteListener {

    private final DefaultListModel<TaskMaster> runListModel;
    private final DefaultListModel<String> archivedListModel;

    /**
     * Constructor: Creates new form MainWindow
     */
    public MainWindow() {
        // the logo icons for the app
        this.getLogoImages();
        initComponents();
        // get control over the list holding running tasks
        runListModel = new DefaultListModel<>();
        this.jListRuns.setModel(runListModel);
        ListRenderer renderer = new ListRenderer();
        this.jListRuns.setCellRenderer(renderer);
        // list for archived tasks
        archivedListModel = new DefaultListModel<>();
        this.jListArchivedTasks.setModel(archivedListModel);

        // load previously archived tasks if any
        File archives = new File(AlgoBench.JarDirectory + File.separator + "saved");
        if (archives.isDirectory()) {
            for (File f : archives.listFiles()) {
                String name = f.getName();
                if (f.isFile() && name.toLowerCase().endsWith(".ser")) {
                    archivedListModel.addElement(name.substring(0, name.lastIndexOf(".ser")));
                }
            }
        }
        
        
        jListRuns.addMouseListener(new MouseAdapter(){
            //click the blank space de-select jlist items
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                int bounds = runListModel.getSize()-1;
                if (!jListRuns.getCellBounds(0, bounds).contains(p)) {
                    jListRuns.clearSelection();
                }
            }
        });
        
        jListRuns.addMouseMotionListener(new MouseMotionAdapter(){
            //set up the tooltiptext for every cell in the jListRuns (pop up the algorithm name)
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int bounds = runListModel.getSize()-1;
                int index = jListRuns.locationToIndex(p);
                jListRuns.setToolTipText(null);
                if (index != -1 && jListRuns.getCellBounds(0, bounds).contains(p)) {
                    String text = runListModel.getElementAt(index).getTask().getAlgorithm();
                    jListRuns.setToolTipText(text);
                }
            }
        });

    }

    protected final void getLogoImages() {
        AlgoBench.iconImagesList = new ArrayList<>();
        String[] sizes = new String[]{"16x16", "24x24", "32x32", "64x64"};

        for (String s : sizes) {
            AlgoBench.iconImagesList.add(new ImageIcon(this.getClass().getResource(
                    "/inf2b/algobench/images/logo_" + s + ".png")).getImage());
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

        buttonGroupViews = new javax.swing.ButtonGroup();
        jPanelTopBar = new javax.swing.JPanel();
        jPanelCreateTask = new javax.swing.JPanel();
        jToolBarNewTask = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButtonCreateTask = new javax.swing.JButton();
        jButtonOpenTask = new javax.swing.JButton();
        jButtonArchiveTask = new javax.swing.JButton();
        jButtonDeleteTask = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButtonCompare = new javax.swing.JButton();
        jPanelExcution = new javax.swing.JPanel();
        jToolBarCompare = new javax.swing.JToolBar();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jButtonStart = new javax.swing.JButton();
        jButtonStop = new javax.swing.JButton();
        jButtonRestart = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jButtonTaskOverview = new javax.swing.JButton();
        jButtonViewChart = new javax.swing.JButton();
        jButtonViewTable = new javax.swing.JButton();
        jPanelBody = new javax.swing.JPanel();
        jSplitPaneBody = new javax.swing.JSplitPane();
        jPanelMain = new javax.swing.JPanel();
        jLabelStartPage = new javax.swing.JLabel();
        jLabelMultipleTasks = new javax.swing.JLabel();
        jSplitPaneLeft = new javax.swing.JSplitPane();
        jPanelTopLeft = new javax.swing.JPanel();
        jPanelRunListHeader = new javax.swing.JPanel();
        jLabelRunListHeader = new javax.swing.JLabel();
        jScrollPaneRunList = new javax.swing.JScrollPane();
        jListRuns = new javax.swing.JList();
        jPanelBottomLeft = new javax.swing.JPanel();
        jPanelSavedTasksHeader = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanelSavedTasksHeader1 = new javax.swing.JPanel();
        jButtonLoadArchive = new javax.swing.JButton();
        jButtonDeleteArchive = new javax.swing.JButton();
        jScrollPaneRunHistory = new javax.swing.JScrollPane();
        jListArchivedTasks = new javax.swing.JList();
        jPanelBottom = new javax.swing.JPanel();
        jLabelStatusInfo = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemCreate = new javax.swing.JMenuItem();
        jMenuItemLoad = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inf2B Algorithms Workbench");
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setIconImages(AlgoBench.iconImagesList);
        setMaximumSize(new java.awt.Dimension(3600, 2000));
        setMinimumSize(new java.awt.Dimension(1230, 750));
        setName("mainWindowFrame"); // NOI18N
        setPreferredSize(new java.awt.Dimension(1230, 770));
        setSize(new java.awt.Dimension(1230, 800));
        java.awt.GridBagLayout layout = new java.awt.GridBagLayout();
        layout.columnWeights = new double[] {1.0};
        getContentPane().setLayout(layout);

        jPanelTopBar.setBackground(new java.awt.Color(0, 0, 255));
        jPanelTopBar.setMaximumSize(new java.awt.Dimension(600, 65));
        jPanelTopBar.setMinimumSize(new java.awt.Dimension(400, 40));
        jPanelTopBar.setOpaque(false);
        jPanelTopBar.setPreferredSize(new java.awt.Dimension(400, 45));
        jPanelTopBar.setLayout(new java.awt.GridBagLayout());

        jPanelCreateTask.setBackground(new java.awt.Color(214, 225, 255));
        jPanelCreateTask.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelCreateTask.setMinimumSize(new java.awt.Dimension(200, 30));
        jPanelCreateTask.setPreferredSize(new java.awt.Dimension(260, 40));
        jPanelCreateTask.setLayout(new java.awt.BorderLayout());

        jToolBarNewTask.setBackground(new java.awt.Color(188, 215, 255));
        jToolBarNewTask.setFloatable(false);
        jToolBarNewTask.setRollover(true);
        jToolBarNewTask.setAlignmentX(0.0F);
        jToolBarNewTask.setMaximumSize(new java.awt.Dimension(91, 40));
        jToolBarNewTask.setMinimumSize(new java.awt.Dimension(91, 40));
        jToolBarNewTask.setName("New Task"); // NOI18N
        jToolBarNewTask.setOpaque(false);
        jToolBarNewTask.setPreferredSize(new java.awt.Dimension(91, 40));
        jToolBarNewTask.add(jSeparator2);

        jButtonCreateTask.setForeground(new java.awt.Color(54, 58, 73));
        jButtonCreateTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/task_new.png"))); // NOI18N
        jButtonCreateTask.setToolTipText("Create Task");
        jButtonCreateTask.setBorderPainted(false);
        jButtonCreateTask.setContentAreaFilled(false);
        jButtonCreateTask.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButtonCreateTask.setFocusPainted(false);
        jButtonCreateTask.setFocusable(false);
        jButtonCreateTask.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCreateTask.setIconTextGap(0);
        jButtonCreateTask.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonCreateTask.setMaximumSize(new java.awt.Dimension(45, 45));
        jButtonCreateTask.setMinimumSize(new java.awt.Dimension(60, 60));
        jButtonCreateTask.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCreateTask.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                MenuButtonPressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                MenuButtonReleased(evt);
            }
        });
        jButtonCreateTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateTaskActionPerformed(evt);
            }
        });
        jToolBarNewTask.add(jButtonCreateTask);

        jButtonOpenTask.setFont(jButtonCreateTask.getFont());
        jButtonOpenTask.setForeground(jButtonCreateTask.getForeground());
        jButtonOpenTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/task_load.png"))); // NOI18N
        jButtonOpenTask.setToolTipText("Load Saved Task");
        jButtonOpenTask.setBorderPainted(false);
        jButtonOpenTask.setContentAreaFilled(false);
        jButtonOpenTask.setFocusPainted(false);
        jButtonOpenTask.setFocusable(false);
        jButtonOpenTask.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonOpenTask.setHorizontalTextPosition(jButtonCreateTask.getHorizontalTextPosition());
        jButtonOpenTask.setIconTextGap(0);
        jButtonOpenTask.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonOpenTask.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonOpenTask.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonOpenTask.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonOpenTask.setVerticalTextPosition(jButtonCreateTask.getVerticalTextPosition());
        jButtonOpenTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenTaskActionPerformed(evt);
            }
        });
        jToolBarNewTask.add(jButtonOpenTask);

        jButtonArchiveTask.setFont(jButtonCreateTask.getFont());
        jButtonArchiveTask.setForeground(jButtonCreateTask.getForeground());
        jButtonArchiveTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/task_save.png"))); // NOI18N
        jButtonArchiveTask.setToolTipText("Save task");
        jButtonArchiveTask.setBorderPainted(false);
        jButtonArchiveTask.setContentAreaFilled(false);
        jButtonArchiveTask.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/task_save_disabled.png"))); // NOI18N
        jButtonArchiveTask.setEnabled(false);
        jButtonArchiveTask.setFocusPainted(false);
        jButtonArchiveTask.setFocusable(false);
        jButtonArchiveTask.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonArchiveTask.setHorizontalTextPosition(jButtonCreateTask.getHorizontalTextPosition());
        jButtonArchiveTask.setIconTextGap(0);
        jButtonArchiveTask.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonArchiveTask.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonArchiveTask.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonArchiveTask.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonArchiveTask.setVerticalTextPosition(jButtonCreateTask.getVerticalTextPosition());
        jButtonArchiveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonArchiveTaskActionPerformed(evt);
            }
        });
        jToolBarNewTask.add(jButtonArchiveTask);

        jButtonDeleteTask.setFont(jButtonCreateTask.getFont());
        jButtonDeleteTask.setForeground(jButtonCreateTask.getForeground());
        jButtonDeleteTask.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/task_delete.png"))); // NOI18N
        jButtonDeleteTask.setToolTipText("Delete task");
        jButtonDeleteTask.setBorderPainted(false);
        jButtonDeleteTask.setContentAreaFilled(false);
        jButtonDeleteTask.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/task_delete_disabled.png"))); // NOI18N
        jButtonDeleteTask.setEnabled(false);
        jButtonDeleteTask.setFocusPainted(false);
        jButtonDeleteTask.setFocusable(false);
        jButtonDeleteTask.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonDeleteTask.setHorizontalTextPosition(jButtonCreateTask.getHorizontalTextPosition());
        jButtonDeleteTask.setIconTextGap(0);
        jButtonDeleteTask.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonDeleteTask.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonDeleteTask.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonDeleteTask.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonDeleteTask.setVerticalTextPosition(jButtonCreateTask.getVerticalTextPosition());
        jButtonDeleteTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteTaskActionPerformed(evt);
            }
        });
        jToolBarNewTask.add(jButtonDeleteTask);

        jSeparator3.setMinimumSize(new java.awt.Dimension(11, 2));
        jSeparator3.setPreferredSize(new java.awt.Dimension(11, 2));
        jToolBarNewTask.add(jSeparator3);

        jButtonCompare.setForeground(new java.awt.Color(54, 58, 73));
        jButtonCompare.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/compare.png"))); // NOI18N
        jButtonCompare.setToolTipText("Compare tasks");
        jButtonCompare.setBorderPainted(false);
        jButtonCompare.setContentAreaFilled(false);
        jButtonCompare.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/compare._disabledpng.png"))); // NOI18N
        jButtonCompare.setEnabled(false);
        jButtonCompare.setFocusPainted(false);
        jButtonCompare.setFocusable(false);
        jButtonCompare.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCompare.setIconTextGap(0);
        jButtonCompare.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonCompare.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonCompare.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonCompare.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCompare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompareActionPerformed(evt);
            }
        });
        jToolBarNewTask.add(jButtonCompare);

        jPanelCreateTask.add(jToolBarNewTask, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanelTopBar.add(jPanelCreateTask, gridBagConstraints);

        jPanelExcution.setBackground(new java.awt.Color(214, 225, 255));
        jPanelExcution.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelExcution.setPreferredSize(new java.awt.Dimension(95, 40));
        jPanelExcution.setLayout(new java.awt.BorderLayout());

        jToolBarCompare.setBackground(new java.awt.Color(188, 215, 255));
        jToolBarCompare.setFloatable(false);
        jToolBarCompare.setRollover(true);
        jToolBarCompare.setAlignmentX(0.0F);
        jToolBarCompare.setMaximumSize(new java.awt.Dimension(91, 40));
        jToolBarCompare.setMinimumSize(new java.awt.Dimension(91, 40));
        jToolBarCompare.setName("New Task"); // NOI18N
        jToolBarCompare.setOpaque(false);
        jToolBarCompare.setPreferredSize(new java.awt.Dimension(91, 45));
        jToolBarCompare.add(jSeparator5);

        jButtonStart.setFont(jButtonCreateTask.getFont());
        jButtonStart.setForeground(jButtonCreateTask.getForeground());
        jButtonStart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/start.png"))); // NOI18N
        jButtonStart.setToolTipText("Start Execution");
        jButtonStart.setBorderPainted(false);
        jButtonStart.setContentAreaFilled(false);
        jButtonStart.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/start_disabled.png"))); // NOI18N
        jButtonStart.setEnabled(false);
        jButtonStart.setFocusPainted(false);
        jButtonStart.setFocusable(false);
        jButtonStart.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonStart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStart.setIconTextGap(0);
        jButtonStart.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonStart.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonStart.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonStart.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonStart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });
        jToolBarCompare.add(jButtonStart);

        jButtonStop.setFont(jButtonCreateTask.getFont());
        jButtonStop.setForeground(jButtonCreateTask.getForeground());
        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/stop.png"))); // NOI18N
        jButtonStop.setToolTipText("Stop Execution");
        jButtonStop.setBorderPainted(false);
        jButtonStop.setContentAreaFilled(false);
        jButtonStop.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/stop_disabled.png"))); // NOI18N
        jButtonStop.setEnabled(false);
        jButtonStop.setFocusPainted(false);
        jButtonStop.setFocusable(false);
        jButtonStop.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStop.setIconTextGap(0);
        jButtonStop.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonStop.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonStop.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonStop.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        jToolBarCompare.add(jButtonStop);

        jButtonRestart.setFont(jButtonCreateTask.getFont());
        jButtonRestart.setForeground(jButtonCreateTask.getForeground());
        jButtonRestart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/restart.png"))); // NOI18N
        jButtonRestart.setToolTipText("Restart execution");
        jButtonRestart.setBorderPainted(false);
        jButtonRestart.setContentAreaFilled(false);
        jButtonRestart.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/restart_disabled.png"))); // NOI18N
        jButtonRestart.setEnabled(false);
        jButtonRestart.setFocusPainted(false);
        jButtonRestart.setFocusable(false);
        jButtonRestart.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonRestart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRestart.setIconTextGap(0);
        jButtonRestart.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonRestart.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonRestart.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonRestart.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonRestart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRestart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRestartActionPerformed(evt);
            }
        });
        jToolBarCompare.add(jButtonRestart);
        jToolBarCompare.add(jSeparator4);

        jButtonTaskOverview.setFont(jButtonCreateTask.getFont());
        jButtonTaskOverview.setForeground(jButtonCreateTask.getForeground());
        jButtonTaskOverview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/overview.png"))); // NOI18N
        jButtonTaskOverview.setToolTipText("Task overview");
        jButtonTaskOverview.setActionCommand(TaskView.OVERVIEW.toString());
        jButtonTaskOverview.setBorderPainted(false);
        buttonGroupViews.add(jButtonTaskOverview);
        jButtonTaskOverview.setContentAreaFilled(false);
        jButtonTaskOverview.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/overview_disabled.png"))); // NOI18N
        jButtonTaskOverview.setEnabled(false);
        jButtonTaskOverview.setFocusPainted(false);
        jButtonTaskOverview.setFocusable(false);
        jButtonTaskOverview.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonTaskOverview.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTaskOverview.setIconTextGap(0);
        jButtonTaskOverview.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonTaskOverview.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonTaskOverview.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonTaskOverview.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonTaskOverview.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTaskOverview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTaskViewActionPerformed(evt);
            }
        });
        jToolBarCompare.add(jButtonTaskOverview);

        jButtonViewChart.setFont(jButtonCreateTask.getFont());
        jButtonViewChart.setForeground(jButtonCreateTask.getForeground());
        jButtonViewChart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/chart.png"))); // NOI18N
        jButtonViewChart.setToolTipText("View chart of runtimes");
        jButtonViewChart.setActionCommand(TaskView.CHART.toString());
        jButtonViewChart.setBorderPainted(false);
        buttonGroupViews.add(jButtonViewChart);
        jButtonViewChart.setContentAreaFilled(false);
        jButtonViewChart.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/chart_disabled.png"))); // NOI18N
        jButtonViewChart.setEnabled(false);
        jButtonViewChart.setFocusPainted(false);
        jButtonViewChart.setFocusable(false);
        jButtonViewChart.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonViewChart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonViewChart.setIconTextGap(0);
        jButtonViewChart.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonViewChart.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonViewChart.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonViewChart.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonViewChart.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonViewChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTaskViewActionPerformed(evt);
            }
        });
        jToolBarCompare.add(jButtonViewChart);

        jButtonViewTable.setFont(jButtonCreateTask.getFont());
        jButtonViewTable.setForeground(jButtonCreateTask.getForeground());
        jButtonViewTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/table.png"))); // NOI18N
        jButtonViewTable.setToolTipText("View results as table");
        jButtonViewTable.setActionCommand(TaskView.TABLE.toString());
        jButtonViewTable.setBorderPainted(false);
        buttonGroupViews.add(jButtonViewTable);
        jButtonViewTable.setContentAreaFilled(false);
        jButtonViewTable.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/table_disabled.png"))); // NOI18N
        jButtonViewTable.setEnabled(false);
        jButtonViewTable.setFocusPainted(false);
        jButtonViewTable.setFocusable(false);
        jButtonViewTable.setHorizontalAlignment(jButtonCreateTask.getHorizontalAlignment());
        jButtonViewTable.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonViewTable.setIconTextGap(0);
        jButtonViewTable.setMaximumSize(jButtonCreateTask.getMaximumSize());
        jButtonViewTable.setMinimumSize(jButtonCreateTask.getMinimumSize());
        jButtonViewTable.setPreferredSize(jButtonCreateTask.getPreferredSize());
        jButtonViewTable.setVerticalAlignment(jButtonCreateTask.getVerticalAlignment());
        jButtonViewTable.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonViewTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTaskViewActionPerformed(evt);
            }
        });
        jToolBarCompare.add(jButtonViewTable);

        jPanelExcution.add(jToolBarCompare, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanelTopBar.add(jPanelExcution, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        getContentPane().add(jPanelTopBar, gridBagConstraints);

        jPanelBody.setPreferredSize(new java.awt.Dimension(1200, 500));
        jPanelBody.setLayout(new java.awt.GridLayout(1, 0));

        jSplitPaneBody.setBorder(null);
        jSplitPaneBody.setDividerLocation(250);
        jSplitPaneBody.setMinimumSize(new java.awt.Dimension(100, 100));
        jSplitPaneBody.setOneTouchExpandable(true);

        jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
        jPanelMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelMain.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        jPanelMain.setLayout(new java.awt.CardLayout());

        jLabelStartPage.setBackground(new java.awt.Color(237, 237, 237));
        jLabelStartPage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelStartPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/awb_splash_screen_no_border.jpg"))); // NOI18N
        jLabelStartPage.setOpaque(true);
        jPanelMain.add(jLabelStartPage, "STARTPAGE");

        jLabelMultipleTasks.setFont(jLabelMultipleTasks.getFont().deriveFont((float)14));
        jLabelMultipleTasks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelMultipleTasks.setText("Multiple tasks selected");
        jLabelMultipleTasks.setToolTipText("");
        jLabelMultipleTasks.setOpaque(true);
        jPanelMain.add(jLabelMultipleTasks, "MULTIPLE");

        jSplitPaneBody.setRightComponent(jPanelMain);

        jSplitPaneLeft.setBorder(null);
        jSplitPaneLeft.setDividerLocation(300);
        jSplitPaneLeft.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        java.awt.GridBagLayout jPanelTopLeftLayout = new java.awt.GridBagLayout();
        jPanelTopLeftLayout.rowWeights = new double[] {0.0, 1.0};
        jPanelTopLeft.setLayout(jPanelTopLeftLayout);

        jPanelRunListHeader.setBackground(new java.awt.Color(77, 106, 130));
        jPanelRunListHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelRunListHeader.setForeground(new java.awt.Color(222, 222, 222));
        jPanelRunListHeader.setMinimumSize(new java.awt.Dimension(220, 28));
        jPanelRunListHeader.setPreferredSize(new java.awt.Dimension(220, 28));
        jPanelRunListHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelRunListHeader.setFont(jLabelRunListHeader.getFont().deriveFont((float)13));
        jLabelRunListHeader.setForeground(new java.awt.Color(242, 242, 242));
        jLabelRunListHeader.setLabelFor(jScrollPaneRunHistory);
        jLabelRunListHeader.setText("Active Tasks");
        jLabelRunListHeader.setMaximumSize(new java.awt.Dimension(68, 28));
        jLabelRunListHeader.setMinimumSize(new java.awt.Dimension(68, 28));
        jLabelRunListHeader.setPreferredSize(new java.awt.Dimension(68, 28));
        jPanelRunListHeader.add(jLabelRunListHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 2, 234, 23));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanelTopLeft.add(jPanelRunListHeader, gridBagConstraints);

        jScrollPaneRunList.setBorder(null);
        jScrollPaneRunList.setMinimumSize(new java.awt.Dimension(0, 0));

        jListRuns.setFont(jListRuns.getFont().deriveFont((float)13));
        jListRuns.setFixedCellHeight(28);
        jListRuns.setSelectionBackground(new java.awt.Color(102, 153, 204));
        jListRuns.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListRunsValueChanged(evt);
            }
        });
        jScrollPaneRunList.setViewportView(jListRuns);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelTopLeft.add(jScrollPaneRunList, gridBagConstraints);

        jSplitPaneLeft.setTopComponent(jPanelTopLeft);

        jPanelBottomLeft.setLayout(new java.awt.GridBagLayout());

        jPanelSavedTasksHeader.setBackground(new java.awt.Color(77, 106, 130));
        jPanelSavedTasksHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelSavedTasksHeader.setMaximumSize(new java.awt.Dimension(220, 28));
        jPanelSavedTasksHeader.setMinimumSize(new java.awt.Dimension(220, 28));
        jPanelSavedTasksHeader.setPreferredSize(new java.awt.Dimension(220, 28));
        jPanelSavedTasksHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(jLabel2.getFont().deriveFont((float)13));
        jLabel2.setForeground(new java.awt.Color(242, 242, 242));
        jLabel2.setText("Archived Tasks");
        jLabel2.setMaximumSize(new java.awt.Dimension(68, 28));
        jLabel2.setMinimumSize(new java.awt.Dimension(68, 28));
        jLabel2.setPreferredSize(new java.awt.Dimension(68, 28));
        jPanelSavedTasksHeader.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 2, 234, 23));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelBottomLeft.add(jPanelSavedTasksHeader, gridBagConstraints);

        jPanelSavedTasksHeader1.setBackground(new java.awt.Color(204, 204, 204));
        jPanelSavedTasksHeader1.setMaximumSize(new java.awt.Dimension(220, 28));
        jPanelSavedTasksHeader1.setMinimumSize(new java.awt.Dimension(220, 28));
        jPanelSavedTasksHeader1.setOpaque(false);
        jPanelSavedTasksHeader1.setPreferredSize(new java.awt.Dimension(220, 28));
        jPanelSavedTasksHeader1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 3));

        jButtonLoadArchive.setText("Load Task");
        jButtonLoadArchive.setEnabled(false);
        jButtonLoadArchive.setPreferredSize(new java.awt.Dimension(100, 20));
        jButtonLoadArchive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadArchiveActionPerformed(evt);
            }
        });
        jPanelSavedTasksHeader1.add(jButtonLoadArchive);

        jButtonDeleteArchive.setText("Delete Entry");
        jButtonDeleteArchive.setEnabled(false);
        jButtonDeleteArchive.setPreferredSize(new java.awt.Dimension(100, 20));
        jButtonDeleteArchive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteArchiveActionPerformed(evt);
            }
        });
        jPanelSavedTasksHeader1.add(jButtonDeleteArchive);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelBottomLeft.add(jPanelSavedTasksHeader1, gridBagConstraints);

        jScrollPaneRunHistory.setBorder(null);
        jScrollPaneRunHistory.setMinimumSize(new java.awt.Dimension(250, 150));
        jScrollPaneRunHistory.setPreferredSize(new java.awt.Dimension(250, 150));

        jListArchivedTasks.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jListArchivedTasks.setFont(jListArchivedTasks.getFont().deriveFont((float)13));
        jListArchivedTasks.setFixedCellHeight(25);
        jListArchivedTasks.setSelectionBackground(new java.awt.Color(102, 153, 204));
        jListArchivedTasks.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListArchivedTasksValueChanged(evt);
            }
        });
        jScrollPaneRunHistory.setViewportView(jListArchivedTasks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelBottomLeft.add(jScrollPaneRunHistory, gridBagConstraints);

        jSplitPaneLeft.setBottomComponent(jPanelBottomLeft);

        jSplitPaneBody.setLeftComponent(jSplitPaneLeft);

        jPanelBody.add(jSplitPaneBody);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelBody, gridBagConstraints);

        jPanelBottom.setBackground(new java.awt.Color(214, 225, 255));
        jPanelBottom.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelBottom.setMaximumSize(new java.awt.Dimension(500, 35));
        jPanelBottom.setMinimumSize(new java.awt.Dimension(500, 35));
        jPanelBottom.setPreferredSize(new java.awt.Dimension(500, 35));
        jPanelBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        jPanelBottom.add(jLabelStatusInfo);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(10, 20));
        jPanelBottom.add(jSeparator1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(jPanelBottom, gridBagConstraints);

        jMenuBar.setBackground(javax.swing.UIManager.getDefaults().getColor("Menu.background"));
        jMenuBar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jMenuBar.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); // NOI18N
        jMenuBar.setMinimumSize(new java.awt.Dimension(4, 25));
        jMenuBar.setPreferredSize(new java.awt.Dimension(56, 25));

        jMenuFile.setForeground(new java.awt.Color(51, 51, 51));
        jMenuFile.setMnemonic('F');
        jMenuFile.setText("File");
        jMenuFile.setFont(jMenuFile.getFont().deriveFont((float)13));
        jMenuFile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMenuFile.setMargin(new java.awt.Insets(10, 10, 10, 10));

        jMenuItemCreate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCreate.setFont(jMenuItemCreate.getFont().deriveFont((float)13));
        jMenuItemCreate.setForeground(java.awt.Color.darkGray);
        jMenuItemCreate.setMnemonic('c');
        jMenuItemCreate.setText("Create Task...");
        jMenuItemCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateTaskActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemCreate);

        jMenuItemLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemLoad.setFont(jMenuItemLoad.getFont().deriveFont((float)13));
        jMenuItemLoad.setForeground(java.awt.Color.darkGray);
        jMenuItemLoad.setMnemonic('l');
        jMenuItemLoad.setText("Load Saved Task...");
        jMenuItemLoad.setToolTipText("Load Saved Task...");
        jMenuItemLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenTaskActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemLoad);

        jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemExit.setFont(jMenuItemExit.getFont().deriveFont((float)13));
        jMenuItemExit.setForeground(java.awt.Color.darkGray);
        jMenuItemExit.setMnemonic('x');
        jMenuItemExit.setText("Exit");
        jMenuItemExit.setToolTipText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuHelp.setForeground(new java.awt.Color(51, 51, 51));
        jMenuHelp.setMnemonic('H');
        jMenuHelp.setText("Help");
        jMenuHelp.setFont(jMenuHelp.getFont().deriveFont((float)13));
        jMenuHelp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMenuHelp.setMargin(new java.awt.Insets(10, 10, 10, 10));

        jMenuItemHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemHelp.setFont(jMenuItemHelp.getFont().deriveFont((float)13));
        jMenuItemHelp.setMnemonic('e');
        jMenuItemHelp.setText("Help...");
        jMenuItemHelp.setToolTipText("Application Help");
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);

        jMenuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAbout.setFont(jMenuItemAbout.getFont().deriveFont((float)13));
        jMenuItemAbout.setMnemonic('a');
        jMenuItemAbout.setText("About...");
        jMenuItemAbout.setToolTipText("About the application");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar.add(jMenuHelp);

        setJMenuBar(jMenuBar);
        jMenuBar.getAccessibleContext().setAccessibleParent(this);

        getAccessibleContext().setAccessibleParent(this);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        jButtonStop.setEnabled(false);
        TaskMaster tm = this.runListModel.get(this.jListRuns.getSelectedIndex());
        tm.terminate();
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jButtonCreateTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateTaskActionPerformed
        this.displayMessage("Creating task...", false);
        NewTaskDialog newRun = new NewTaskDialog(new JFrame(), true);
        newRun.pack();
        TaskMaster tm = newRun.showDialog();
        if (tm != null) {
            createTask(tm);
        }
        this.clearMessage();
    }//GEN-LAST:event_jButtonCreateTaskActionPerformed

    private void jButtonOpenTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenTaskActionPerformed
        try {
            String taskFilename = chooseTaskFile();
            if (taskFilename.length() > 0) {
                loadArchive(taskFilename, true);
            }
        }
        catch (IOException ex) {
            displayTimedMessage(ex.getMessage(), true, 10000);
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonOpenTaskActionPerformed

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartActionPerformed
        TaskMaster tm = this.runListModel.get(this.jListRuns.getSelectedIndex());
        tm.setState(AlgoBench.TaskState.RUNNING);
        tm.addListener(this);
        setTaskView(tm, TaskView.OVERVIEW);
        jListRuns.repaint();
        Thread taskRunnerThread = new Thread(tm);
        taskRunnerThread.start();
    }//GEN-LAST:event_jButtonStartActionPerformed

    private void jButtonRestartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRestartActionPerformed
        TaskMaster tm = this.runListModel.get(this.jListRuns.getSelectedIndex());
        tm.setState(AlgoBench.TaskState.RUNNING);
        setTaskView(tm, TaskView.OVERVIEW);
        jListRuns.repaint();
        Thread taskRunnerThread = new Thread(tm);
        taskRunnerThread.start();
    }//GEN-LAST:event_jButtonRestartActionPerformed

    private void jListRunsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListRunsValueChanged
        // make changes only when the event fires for the destination of the selection
        if (!evt.getValueIsAdjusting()) {
            if (jListRuns.getSelectedIndex() != -1) { // something is selected
                if (jListRuns.getSelectedIndices().length > 1) {
                    setTaskView(null, TaskView.MULTIPLE);
                    resetTaskButtons();
                    jButtonDeleteTask.setEnabled(true);
                }
                else {
                    TaskMaster tm = (TaskMaster) jListRuns.getSelectedValue();
                    setTaskView(tm, tm.getLastState());
                }
            }
            else {
                setTaskView(null, TaskView.STARTPAGE);
            }
        }
    }//GEN-LAST:event_jListRunsValueChanged

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        JOptionPane.showMessageDialog(this, "Written by Eziama Ubachukwu and Yufen Wang\n"
                + "University of Edinburgh\n2016", "About AlgoBench", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jButtonDeleteTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteTaskActionPerformed

        if (jListRuns.getSelectedIndices().length > 0) {
            this.displayMessage("Delete task(s)...", false);
            String message;
            if (jListRuns.getSelectedIndices().length == 1) {
                message = "Delete this task?";
            }
            else {
                message = "Delete these tasks?";
            }
            message += " (Any running \ntask(s) will be terminated!)\n ";
            int result = JOptionPane.showConfirmDialog(this, message,
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                // to avoid array out of bounds error, adjust the deleted index with each
                // deletion, since the list shrinks in each round
                int[] indices = jListRuns.getSelectedIndices();
                for (int i = 0; i < indices.length; ++i) {
                    TaskMaster tm = runListModel.get(indices[i] - i);
                    if (tm.getState() == AlgoBench.TaskState.RUNNING) {
                        tm.terminate();
                    }
                    jButtonDeleteTask.setEnabled(false);
                    runListModel.remove(indices[i] - i);
                    jPanelMain.remove(tm.getTaskPanel());
                    if (tm.getResultChartPanel() != null) {
                        jPanelMain.remove(tm.getResultChartPanel());
                    }
                    if (tm.getTablePanel() != null) {
                        jPanelMain.remove(tm.getTablePanel());
                    }
                    jPanelMain.validate();
                    // if task is archived, re-enable the button if it's selected in archive
                    if (jListArchivedTasks.getSelectedIndices().length > 0
                            && jListArchivedTasks.getSelectedValue().toString().equals(tm.getTaskID())) {
                        jButtonLoadArchive.setEnabled(true);
                    }
                }
                this.displayTimedMessage("Task(s) deleted.", false, 3000);
                updateToolBarButtons();
            }
            else {
                this.clearMessage();
            }
        }
    }//GEN-LAST:event_jButtonDeleteTaskActionPerformed

    private void jButtonArchiveTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonArchiveTaskActionPerformed
        int rindex = jListRuns.getSelectedIndex();
        TaskMaster tm = runListModel.get(rindex);
        for (Object s : archivedListModel.toArray()) {
            if (s.toString().equals(tm.getTaskID())) {
                return;
            }
        }
        try (
            OutputStream file = new FileOutputStream(AlgoBench.JarDirectory + File.separator + "saved" + File.separator + tm.getTaskID() + ".ser");
            BufferedOutputStream buffer = new BufferedOutputStream(file);
            ObjectOutput output = new ObjectOutputStream(buffer);) {

            output.writeObject(tm);
            output.close();

            archivedListModel.addElement(tm.getTaskID());
            int sindex = archivedListModel.getSize() - 1;
            jListArchivedTasks.ensureIndexIsVisible(sindex);
            jListArchivedTasks.setSelectedIndex(sindex);
            jButtonArchiveTask.setEnabled(false);

//            dumpComponent(jPanelMain);
        }
        catch (IOException ex) {
            displayTimedMessage(ex.getMessage(), true, 10000);
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonArchiveTaskActionPerformed

    private void jButtonTaskViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTaskViewActionPerformed
        AbstractButton b = ((AbstractButton) evt.getSource());
        if (b.isSelected()) {
            return;
        }
        TaskMaster tm = (TaskMaster) jListRuns.getSelectedValue();
        String command = evt.getActionCommand();
        setTaskView(tm, TaskView.getView(command));
    }//GEN-LAST:event_jButtonTaskViewActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "Quit?", "Confirm Quit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            TaskMaster tm;
            for (int i = 0; i < runListModel.size(); ++i) {
                tm = runListModel.get(i);
                if (tm.getState() == AlgoBench.TaskState.RUNNING) {
                    tm.terminate();
                }
            }
            this.dispose();
            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jButtonLoadArchiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadArchiveActionPerformed
        loadArchive(jListArchivedTasks.getSelectedValue().toString(), false);
        jButtonLoadArchive.setEnabled(false);
    }//GEN-LAST:event_jButtonLoadArchiveActionPerformed

    private void jButtonDeleteArchiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteArchiveActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "Delete archived task?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        int[] indices = jListArchivedTasks.getSelectedIndices();
        // note the deleted records
        String[] fnames = new String[indices.length];
        for (int i = 0; i < indices.length; ++i) {
            String fname = archivedListModel.get(indices[i] - i);
            fnames[i] = fname;
            File f = new File(AlgoBench.JarDirectory + File.separator + "saved" + File.separator + fname + ".ser");
            if (f.exists()) {
                f.delete();
            }
            archivedListModel.remove(indices[i] - i);
        }
        // get last one to be deleted
        int index = indices[indices.length - 1];
        // select the next item newly in it's place it, or the last item
        index = index > archivedListModel.getSize() - 1
                ? archivedListModel.getSize() - 1 : index;
        jListArchivedTasks.setSelectedIndex(index);
        if (index < 0) {
            jButtonDeleteArchive.setEnabled(false);
            jButtonLoadArchive.setEnabled(false);
        }
        // if the currently selected queued task's archive was deleted, re-enable
        // its archive button
        if (jListRuns.getSelectedIndices().length == 1) {
            String selectedTask = jListRuns.getSelectedValue().toString();
            for (String f : fnames) {
                if (selectedTask.equals(f)) {
                    jButtonArchiveTask.setEnabled(true);
                }
            }
            // if currently selected archived task is same as the selected queued task,
            // disable the load button

            if (jListArchivedTasks.getSelectedIndex() >= 0
                    && jListArchivedTasks.getSelectedValue().toString().equals(selectedTask)) {
                jButtonLoadArchive.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jButtonDeleteArchiveActionPerformed

    private void jListArchivedTasksValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListArchivedTasksValueChanged
        if (!evt.getValueIsAdjusting() && jListArchivedTasks.getSelectedIndex() != -1) {
            if (jListArchivedTasks.getSelectedIndices().length == 1) {
                String taskID = jListArchivedTasks.getSelectedValue().toString();
                jButtonLoadArchive.setEnabled(true);
                for (Object m : runListModel.toArray()) {
                    if (((TaskMaster) m).getTaskID().equals(taskID)) {
                        jButtonLoadArchive.setEnabled(false);
                    }
                }
                jButtonDeleteArchive.setEnabled(true);
            }
            else {
                jButtonDeleteArchive.setEnabled(true);
                jButtonLoadArchive.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jListArchivedTasksValueChanged

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
//        Help h = new Help(null, true, "/inf2b/algobench/html/main_help.html");
        SimpleSwingBrowser browser = new SimpleSwingBrowser();
        browser.setVisible(true);
        browser.loadURL("/inf2b/algobench/html/main_help.html");
    }//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jButtonCompareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompareActionPerformed
        this.displayMessage("Creating comparison...", false);
        CompareDialog newCompare = new CompareDialog(new JFrame(), true, runListModel);
        newCompare.pack();
        CompareChartPanel comparePanel = newCompare.showDialog();
        if(comparePanel != null){
            JFrame compareWindow = new JFrame("Compare Window");
            compareWindow.add(comparePanel);
            compareWindow.pack();
            compareWindow.setVisible(true);
        }
        this.clearMessage();
    }//GEN-LAST:event_jButtonCompareActionPerformed

    private void MenuButtonPressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuButtonPressed
//        JButton button = (JButton)evt.getSource();
//        button.setOpaque(true);
//        button.setBackground(Color.GRAY);
    }//GEN-LAST:event_MenuButtonPressed

    private void MenuButtonReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_MenuButtonReleased
//        JButton button = (JButton)evt.getSource();
//        button.setOpaque(false);
    }//GEN-LAST:event_MenuButtonReleased

    private void loadArchive(String fileName, boolean absolutePath) {
        if (!absolutePath) {
            fileName = AlgoBench.JarDirectory + File.separator + "saved" + File.separator + fileName + ".ser";
        }
        try (
            InputStream file = new FileInputStream(fileName);
            BufferedInputStream buffer = new BufferedInputStream(file);
            ObjectInput output = new ObjectInputStream(buffer);) {
            TaskMaster tm = (TaskMaster) output.readObject();
            createTask(tm);
            notifyTaskComplete(tm);
            setTaskView(tm, TaskView.OVERVIEW);
            tm.addListener(this);
        }
        catch (IOException | ClassNotFoundException ex) {
            displayTimedMessage(ex.getMessage(), true, 10000);
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Displays a timed message at the bottom bar. For error the font color is
     * red.
     *
     * @param message The message to display
     * @param isError True for error messages
     * @param timeOut The delay before removing message in milliseconds
     */
    private void displayTimedMessage(String message, boolean isError, int timeOut) {
        if (isError) {
            jLabelStatusInfo.setForeground(Color.red);
        }
        jLabelStatusInfo.setText(message);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                jLabelStatusInfo.setText("");
                jLabelStatusInfo.setForeground(Color.black);
            }
        }, timeOut);
    }

    /**
     * Displays a message at the bottom bar. For error the font color is red.
     * Callers should clear the message afterwards, or else use
     * displayTimedMessage().
     *
     * @param message The message to display
     * @param isError True for error messages
     */
    private void displayMessage(String message, boolean isError) {
        if (isError) {
            jLabelStatusInfo.setForeground(Color.red);
        }
        jLabelStatusInfo.setText(message);
    }

    /**
     * Clears any messages on the bottom bar.
     */
    private void clearMessage() {
        jLabelStatusInfo.setText("");
        jLabelStatusInfo.setForeground(Color.black);
    }

    /**
     * Displays the open dialog for choosing a task file with .ser extension.
     *
     * @return The canonical path to the file, or the empty string if the user
     * cancelled the operation.
     * @throws IOException
     */
    private String chooseTaskFile() throws IOException {
        List<String[]> filters = new ArrayList<>();
        filters.add(new String[]{"Serialised task files (.ser)", "ser"});
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
        selectInputFileChooser.setAcceptAllFileFilterUsed(false);
        selectInputFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        selectInputFileChooser.setDialogTitle("Select Task File");
        for (String[] s : filters) {
            selectInputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter(s[0], s[1]));
        }
        selectInputFileChooser.setCurrentDirectory(new File(AlgoBench.JarDirectory + File.separator + "saved"));
        int result = selectInputFileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = selectInputFileChooser.getSelectedFile();
            return f.getCanonicalPath();
        }
        return "";
    }

    /**
     * Updates the state of the toolbar buttons to reflect the allowed actions
     * of the currently selected task in the running tasks list.
     */
    synchronized public void updateToolBarButtons() {// disable buttons
        this.resetTaskButtons();
        // enable the necessary ones
        TaskMaster tm = (TaskMaster) jListRuns.getSelectedValue();
        if (tm == null) {// nothing selected
            return;
        }
        jButtonTaskOverview.setEnabled(true);
        jButtonDeleteTask.setEnabled(true);
        jButtonArchiveTask.setEnabled(true);
        switch (tm.getState()) {
            case QUEUED:
                jButtonStart.setEnabled(true);
                break;
            case RUNNING:
                jButtonStop.setEnabled(true);
                jButtonArchiveTask.setEnabled(false);
                break;
            case STOPPED:
            case COMPLETED:
            case INCOMPLETE:
                jButtonRestart.setEnabled(true);
                if (tm.hasResults()) {
                    jButtonViewChart.setEnabled(true);
                    jButtonViewTable.setEnabled(true);
                }
            case FAILED:
                jButtonRestart.setEnabled(true);
                break;
            default:
                break;
        }
        // disable archiving when already archived
        for (Object s : archivedListModel.toArray()) {
            if (s.toString().equals(tm.getTaskID())) {
                jButtonArchiveTask.setEnabled(false);
            }
        }
    }

    /**
     * Displays the appropriate view of a task (chart, overview, table) or the
     * start page / multiple-tasks-selected page. Also handles the visual
     * persistence of the action, like keeping the selected button depressed.
     *
     * @param tm TaskMaster instance to consider. Pass in null if not needed,
     * but with one of "Start Page" or "Multiple Items" view.
     * @param view The view to select.
     */
    private void setTaskView(TaskMaster tm, TaskView view) {
        CardLayout cardLayout = (CardLayout) jPanelMain.getLayout();
        updateToolBarButtons();
        jButtonTaskOverview.setSelected(false);
        jButtonViewChart.setSelected(false);
        jButtonViewTable.setSelected(false);

        if (tm == null && view != TaskView.MULTIPLE && view != TaskView.STARTPAGE) {
            String message = "TaskMaster cannot be null for the supplied TaskView";
            displayTimedMessage(message, true, 3000);
            message += "\n\t... in setTaskView(): TaskMaster==null; TaskView==" + view.toString();
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, message);
            return;
        }

        // work with the currently selected task
        switch (view) {
            case OVERVIEW:
                jButtonTaskOverview.setSelected(true);
                cardLayout.show(jPanelMain, tm.getTaskPanelName());
                break;
            case CHART:
                jButtonViewChart.setSelected(true);
                cardLayout.show(jPanelMain, tm.getChartName());
                break;
            case TABLE:
                jButtonViewTable.setSelected(true);
                cardLayout.show(jPanelMain, tm.getTableName());
                break;
            case MULTIPLE:
                cardLayout.show(jPanelMain, TaskView.MULTIPLE.toString());
                break;
            case STARTPAGE:
                cardLayout.show(jPanelMain, TaskView.STARTPAGE.toString());
                break;
        }
    }

    private void resetTaskButtons() {
        this.jButtonStart.setEnabled(false);
        this.jButtonStop.setEnabled(false);
        this.jButtonRestart.setEnabled(false);
        this.jButtonTaskOverview.setEnabled(false);
        this.jButtonViewChart.setEnabled(false);
        this.jButtonViewTable.setEnabled(false);
        jButtonDeleteTask.setEnabled(false);
    }

    private void createTask(TaskMaster tm) {
        for (Object id : runListModel.toArray()) {
            if (((TaskMaster) id).getTaskID().equals(tm.getTaskID())) {
                JOptionPane.showMessageDialog(this, "Task already loaded",
                        "Duplicate Task", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        // add task overview panel
        this.jPanelMain.add(tm.getTaskPanel(), tm.getTaskPanelName());
        // draw
        this.jPanelMain.revalidate();
        this.runListModel.addElement(tm);
        this.jListRuns.setSelectedIndex(runListModel.size() - 1);
        this.jListRuns.ensureIndexIsVisible(runListModel.size() - 1);
        
        //only enable compare function when active tasks more than one
        if(runListModel.size()>1) this.jButtonCompare.setEnabled(true);
        else this.jButtonCompare.setEnabled(false);

        jButtonTaskOverview.setEnabled(true);
        jButtonTaskOverview.setSelected(true);
        // dumpComponent(jPanelMain);
    }

    private void dumpComponent(Component c) {
        if (c instanceof Container) {
            System.out.println(c.toString());
            System.out.println("-----Children-----");
            for (Component c2 : ((Container) c).getComponents()) {
                dumpComponent(c2);
            }
        }
        else {
            System.out.println(c.toString());
        }
    }

    @Override
    synchronized public void notifyTaskComplete(TaskMaster tm) {
        try {
            if (tm.hasResults()) {
                // add table panel
                this.jPanelMain.add(tm.getTablePanel(), tm.getTableName());
                // add chart panel
                this.jPanelMain.add(tm.getResultChartPanel(), tm.getChartName());
                this.jPanelMain.revalidate();
            }
            this.updateToolBarButtons();
            // trigger repaint
            jListRuns.repaint();
        }
        catch (Exception ex) {
            displayTimedMessage(ex.getMessage(), true, 10000);
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
////                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                    break;
//                }
//            }
//        }
//        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//        //</editor-fold>
//
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new MainWindow().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupViews;
    private javax.swing.JButton jButtonArchiveTask;
    private javax.swing.JButton jButtonCompare;
    private javax.swing.JButton jButtonCreateTask;
    private javax.swing.JButton jButtonDeleteArchive;
    private javax.swing.JButton jButtonDeleteTask;
    private javax.swing.JButton jButtonLoadArchive;
    private javax.swing.JButton jButtonOpenTask;
    private javax.swing.JButton jButtonRestart;
    private javax.swing.JButton jButtonStart;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JButton jButtonTaskOverview;
    private javax.swing.JButton jButtonViewChart;
    private javax.swing.JButton jButtonViewTable;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelMultipleTasks;
    private javax.swing.JLabel jLabelRunListHeader;
    private javax.swing.JLabel jLabelStartPage;
    private javax.swing.JLabel jLabelStatusInfo;
    private javax.swing.JList jListArchivedTasks;
    private javax.swing.JList jListRuns;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemCreate;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemLoad;
    private javax.swing.JPanel jPanelBody;
    private javax.swing.JPanel jPanelBottom;
    private javax.swing.JPanel jPanelBottomLeft;
    private javax.swing.JPanel jPanelCreateTask;
    private javax.swing.JPanel jPanelExcution;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelRunListHeader;
    private javax.swing.JPanel jPanelSavedTasksHeader;
    private javax.swing.JPanel jPanelSavedTasksHeader1;
    private javax.swing.JPanel jPanelTopBar;
    private javax.swing.JPanel jPanelTopLeft;
    private javax.swing.JScrollPane jScrollPaneRunHistory;
    private javax.swing.JScrollPane jScrollPaneRunList;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JSplitPane jSplitPaneBody;
    private javax.swing.JSplitPane jSplitPaneLeft;
    private javax.swing.JToolBar jToolBarCompare;
    private javax.swing.JToolBar jToolBarNewTask;
    // End of variables declaration//GEN-END:variables

}
