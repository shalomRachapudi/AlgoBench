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

import inf2b.algobench.ui.components.HashTaskSubPanel;
import inf2b.algobench.ui.components.SortTaskSubPanel;
import inf2b.algobench.ui.components.GraphTaskSubPanel;
import inf2b.algobench.main.AlgoBench;
import inf2b.algobench.model.Task;
import inf2b.algobench.ui.components.SearchTaskSubPanel;
import inf2b.algobench.util.ITaskSubPanel;
import inf2b.algobench.util.SmartScroller;
import inf2b.algobench.util.Util;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The panel that displays the details of a configured task, and gets updated as 
 * the task executes.
 * @author eziama ubachukwu and Yufen Wang
 */
public class TaskOverviewPanel extends JPanel implements Serializable {

    protected Task task;
    protected Timer timer;
    protected GraphTaskSubPanel graphSubPanel;
    protected SortTaskSubPanel sortSubPanel;
    protected HashTaskSubPanel hashSubPanel;
    protected SearchTaskSubPanel searchSubPanel;
    protected ITaskSubPanel taskSubPanel;

    /**
     * Creates new form TaskPanel
     *
     * @param task
     */
    public TaskOverviewPanel(Task task) {
        initComponents();

        SmartScroller smartScroller = new SmartScroller(this.jScrollPaneOutput);
        smartScroller.initListener();

        this.task = task;
        this.jLabelAlgorithm.setText(" (" + this.task.getAlgorithm() + ")");
        this.jLabelCurrentStatus.setText(AlgoBench.TaskState.QUEUED.toString());
        this.jLabelTaskID.setText(task.getTaskID());

        switch (task.getAlgorithmGroup(true)) {
            case "GRAPH":
                taskSubPanel = new GraphTaskSubPanel();
                if (task.getInputFileName().length() == 0) {

                }
                ((GraphTaskSubPanel) taskSubPanel).setHasDirectedEdges(task.getIsDirectedGraph().toString().toUpperCase());
                ((GraphTaskSubPanel) taskSubPanel).setAllowSelfLoop(task.getAllowSelfLoops().toString().toUpperCase());
                ((GraphTaskSubPanel) taskSubPanel).setGraphFixedParam(task.getFixedGraphParam(true).toLowerCase());
                ((GraphTaskSubPanel) taskSubPanel).setGraphFixedValue(task.getFixedGraphSize().toString());
                ((GraphTaskSubPanel) taskSubPanel).setGraphIsDelayed(task.getGraphIsDelayed());
                ((GraphTaskSubPanel) taskSubPanel).setGraphRepresentation("Adjacency List");
                break;
            case "HASH":
                taskSubPanel = new HashTaskSubPanel();
                if (task.getInputFileName().length() == 0) {
                    ((HashTaskSubPanel) taskSubPanel).setBucketArraySize(task.getHashBucketSize().toString());
                    ((HashTaskSubPanel) taskSubPanel).setHashFunctionType(task.getHashFunction());
                    ((HashTaskSubPanel) taskSubPanel).setHashKeyType(task.getHashKeyType(true));
                }
                break;
            case "SORT":
                taskSubPanel = new SortTaskSubPanel();
                if (task.getInputFileName().length() == 0) {
                    ((SortTaskSubPanel) taskSubPanel).setMinInputValue(task.getInputMinValue().toString());
                    ((SortTaskSubPanel) taskSubPanel).setMaxInputValue(task.getInputMaxValue().toString());
                    ((SortTaskSubPanel) taskSubPanel).setInputDistribution(this.task.getInputDistribution(true));
                }
                if (task.getAlgorithmCode().equals(AlgoBench.properties.getProperty("QUICKSORT"))) {
                    ((SortTaskSubPanel) taskSubPanel).setLimitsLabel("Max. recursion depth:");
                    ((SortTaskSubPanel) taskSubPanel).setPivot(task.getPivotPosition(true));
                }
                else if (task.getAlgorithmCode().equals(AlgoBench.properties.getProperty("EXTERNAL_MERGESORT"))) {
                    ((SortTaskSubPanel) taskSubPanel).setPivotLabel("Custom RAM:");
                    ((SortTaskSubPanel) taskSubPanel).setPivot(task.getSortRam()+"KB");
                    ((SortTaskSubPanel) taskSubPanel).setLimitsLabel("Tree height:");
                }
                else {
                    ((SortTaskSubPanel) taskSubPanel).setLimitsLabel("Tree height:");
                }
                break;
            case "SEARCH":
                taskSubPanel = new SearchTaskSubPanel();
                if (task.getInputFileName().length() == 0) {
                    ((SearchTaskSubPanel) taskSubPanel).setMinInputValue(task.getInputMinValue().toString());
                    ((SearchTaskSubPanel) taskSubPanel).setMaxInputValue(task.getInputMaxValue().toString());
                    ((SearchTaskSubPanel) taskSubPanel).setInputDistribution(this.task.getInputDistribution(true));
                }
                if (task.getAlgorithmCode().equals(AlgoBench.properties.getProperty("LINEAR_SEARCH"))) {
                    ((SearchTaskSubPanel) taskSubPanel).setLimitsLabel("Max. recursion depth:");
                }
                else {
                    ((SearchTaskSubPanel) taskSubPanel).setLimitsLabel("Tree height:");
                }
                break;
            default:
                break;
        }
        // no custom input for GRAPH for now
        if (task.getInputFileName().length() == 0 || task.getAlgorithmGroup(true).equals("GRAPH")) {
            this.taskSubPanel.setCurrentSize(task.getInputStartSize().toString());
            this.taskSubPanel.setInitialSize(task.getInputStartSize().toString());
            this.taskSubPanel.setFinalSize(task.getInputFinalSize().toString());
            this.taskSubPanel.setStepSize(task.getInputStepSize().toString());
        }
        this.taskSubPanel.setNumTasks(task.getNumRuns().toString());
        this.taskSubPanel.setMemUsage("0");
        this.jPanelTaskDetails.add((Component) taskSubPanel);
    }

    public void startTimer() {
        jLabelCurrentStatus.setText(AlgoBench.TaskState.RUNNING.toString());
        jLabelCurrentStatus.setForeground(new Color(157, 129, 0));
        jLabelElaspedTime.setText("0");
        final long startTime = System.currentTimeMillis();
        timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
            // set the time on this form from parent form
            String duration = Util.getElapsedTime(startTime, true, true);
            jLabelElaspedTime.setText(duration);
        });

        if (!timer.isRunning()) {
            timer.start();
        }
    }

    public void stopTimer(AlgoBench.TaskState taskState) {
        jLabelCurrentStatus.setText(taskState.toString());
        switch (taskState) {
            case FAILED:
                jLabelCurrentStatus.setForeground(new Color(240, 0, 0));
                break;
            case COMPLETED:
            case STOPPED:
                jLabelCurrentStatus.setForeground(new Color(5, 145, 5));
                break;
            default:
                jLabelCurrentStatus.setForeground(Color.black);
        }
        this.jLabelLiveUpdate.setText("--");
        this.timer.stop();
    }

    synchronized public void updateComponents(String newUpdate) {
        String[] parts = newUpdate.split("\t");
        switch (parts[0].toUpperCase()) {
            case "[NUMCOMPLETEDRUNS]":
                this.taskSubPanel.setNumCompletedTasks(parts[1]);
                break;
            case "[MAXRECURSION]":
                ((SortTaskSubPanel) taskSubPanel).setLimits(parts[1]);
                break;
            case "[TREEHEIGHT]":
                ((SortTaskSubPanel) taskSubPanel).setLimits(parts[1]);
                break;
            case "[STATUS]":
                this.jLabelLiveUpdate.setText(parts[1]);
                break;
            case "[TERMINATE]":
                this.jLabelCurrentStatus.setText(parts[1]);
                break;
            case "[CURRENTMEMUSAGE]":
                Double memUsage = Double.parseDouble(parts[1]) / 1024; //KB
                DecimalFormat df = new DecimalFormat("#");
                String unit = " KB";
                if (memUsage > 1024) {
                    memUsage /= 1024;
                    df = new DecimalFormat("#.0");
                    unit = " MB";
                }
                taskSubPanel.setMemUsage(df.format(memUsage) + unit);
                break;
            case "[CURRENTINPUTSIZE]":
                taskSubPanel.setCurrentSize(parts[1]);
                break;
            case "[NUMRUNS]":
                taskSubPanel.setNumTasks(parts[1]);
                break;
            case "[MINBUCKETSIZE]":
                ((HashTaskSubPanel) taskSubPanel).setMinBucketSize(parts[1]);
                break;
            case "[MAXBUCKETSIZE]":
                ((HashTaskSubPanel) taskSubPanel).setMaxBucketSize(parts[1]);
                break;
            case "[AVERAGE]":
                ((HashTaskSubPanel) taskSubPanel).setAverageBucketSize(parts[1]);
                break;
            case "[STDDEVIATION]":
                ((HashTaskSubPanel) taskSubPanel).setBucketSizeSD(parts[1]);
                break;
            case "[ERROR]":
                this.jLabelLiveUpdate.setText(parts[1]);
                break;
            case "[UPDATE]":
                this.jTextAreaStreamingOutput.append(parts[1] + "\n");
                break;
            default:
                this.jTextAreaStreamingOutput.append(newUpdate + "\n");
                break;
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

        jPanelRunDetails = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelTop = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabelTaskID = new javax.swing.JLabel();
        jLabelAlgorithm = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabelElaspedTime = new javax.swing.JLabel();
        jPanelTaskDetails = new javax.swing.JPanel();
        jPanelStatus = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabelCurrentStatus = new javax.swing.JLabel();
        jPanelRunUpdates = new javax.swing.JPanel();
        jPanelLiveUpdates = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabelLiveUpdate = new javax.swing.JLabel();
        jScrollPaneOutput = new javax.swing.JScrollPane();
        jTextAreaStreamingOutput = new javax.swing.JTextArea();
        jButtonExpandBottom = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(500, 300));
        setPreferredSize(new java.awt.Dimension(750, 500));
        setLayout(new java.awt.BorderLayout());

        jPanelRunDetails.setBackground(new java.awt.Color(244, 247, 255));
        jPanelRunDetails.setMaximumSize(new java.awt.Dimension(944, 418));
        jPanelRunDetails.setMinimumSize(new java.awt.Dimension(944, 418));
        jPanelRunDetails.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 599;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 8);
        jPanelRunDetails.add(jSeparator1, gridBagConstraints);

        jPanelTop.setFont(jPanelTop.getFont().deriveFont((float)13));
        jPanelTop.setOpaque(false);
        jPanelTop.setPreferredSize(new java.awt.Dimension(919, 40));
        jPanelTop.setLayout(new java.awt.GridLayout(1, 0));

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabelTaskID.setFont(jLabelTaskID.getFont().deriveFont(jLabelTaskID.getFont().getStyle() | java.awt.Font.BOLD, 13));
        jLabelTaskID.setText("--");
        jPanel10.add(jLabelTaskID);

        jLabelAlgorithm.setFont(jLabelAlgorithm.getFont().deriveFont((float)13));
        jLabelAlgorithm.setText("--");
        jPanel10.add(jLabelAlgorithm);

        jPanelTop.add(jPanel10);

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jLabel10.setFont(jLabel10.getFont().deriveFont((float)13));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Running time:");
        jPanel11.add(jLabel10);

        jLabelElaspedTime.setFont(jLabelElaspedTime.getFont().deriveFont((float)13));
        jLabelElaspedTime.setText("--");
        jPanel11.add(jLabelElaspedTime);

        jPanelTop.add(jPanel11);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 15);
        jPanelRunDetails.add(jPanelTop, gridBagConstraints);

        jPanelTaskDetails.setMaximumSize(new java.awt.Dimension(2000, 1000));
        jPanelTaskDetails.setMinimumSize(new java.awt.Dimension(200, 200));
        jPanelTaskDetails.setOpaque(false);
        jPanelTaskDetails.setPreferredSize(new java.awt.Dimension(300, 300));
        jPanelTaskDetails.setLayout(new java.awt.CardLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 8);
        jPanelRunDetails.add(jPanelTaskDetails, gridBagConstraints);

        jPanelStatus.setOpaque(false);
        jPanelStatus.setLayout(new javax.swing.BoxLayout(jPanelStatus, javax.swing.BoxLayout.LINE_AXIS));

        jLabel14.setFont(jLabel14.getFont().deriveFont((float)13));
        jLabel14.setText("Status:  ");
        jPanelStatus.add(jLabel14);

        jLabelCurrentStatus.setFont(jLabelCurrentStatus.getFont().deriveFont(jLabelCurrentStatus.getFont().getStyle() | java.awt.Font.BOLD, 13));
        jLabelCurrentStatus.setText("--");
        jPanelStatus.add(jLabelCurrentStatus);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 20, 10);
        jPanelRunDetails.add(jPanelStatus, gridBagConstraints);

        add(jPanelRunDetails, java.awt.BorderLayout.NORTH);

        jPanelRunUpdates.setBackground(new java.awt.Color(244, 247, 255));
        jPanelRunUpdates.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 0, 0, 0, new java.awt.Color(135, 143, 155)));
        jPanelRunUpdates.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanelRunUpdates.setPreferredSize(new java.awt.Dimension(100, 150));
        java.awt.GridBagLayout jPanelRunUpdatesLayout = new java.awt.GridBagLayout();
        jPanelRunUpdatesLayout.columnWeights = new double[] {1.0};
        jPanelRunUpdatesLayout.rowWeights = new double[] {0.0, 1.0};
        jPanelRunUpdates.setLayout(jPanelRunUpdatesLayout);

        jPanelLiveUpdates.setOpaque(false);
        jPanelLiveUpdates.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel11.setFont(jLabel11.getFont().deriveFont((float)13));
        jLabel11.setText("Live updates:");
        jPanelLiveUpdates.add(jLabel11);

        jLabelLiveUpdate.setFont(jLabelLiveUpdate.getFont().deriveFont((float)13));
        jPanelLiveUpdates.add(jLabelLiveUpdate);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 0);
        jPanelRunUpdates.add(jPanelLiveUpdates, gridBagConstraints);

        jScrollPaneOutput.setMinimumSize(new java.awt.Dimension(0, 0));
        jScrollPaneOutput.setPreferredSize(new java.awt.Dimension(0, 0));

        jTextAreaStreamingOutput.setEditable(false);
        jTextAreaStreamingOutput.setBackground(new java.awt.Color(249, 249, 249));
        jTextAreaStreamingOutput.setFont(jTextAreaStreamingOutput.getFont().deriveFont((float)12));
        jTextAreaStreamingOutput.setRows(5);
        jTextAreaStreamingOutput.setTabSize(4);
        jTextAreaStreamingOutput.setMargin(new java.awt.Insets(10, 10, 0, 10));
        jScrollPaneOutput.setViewportView(jTextAreaStreamingOutput);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 10, 15);
        jPanelRunUpdates.add(jScrollPaneOutput, gridBagConstraints);

        jButtonExpandBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/up.png"))); // NOI18N
        jButtonExpandBottom.setFocusable(false);
        jButtonExpandBottom.setMaximumSize(new java.awt.Dimension(20, 20));
        jButtonExpandBottom.setMinimumSize(new java.awt.Dimension(20, 20));
        jButtonExpandBottom.setPreferredSize(new java.awt.Dimension(20, 20));
        jButtonExpandBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExpandBottomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 15);
        jPanelRunUpdates.add(jButtonExpandBottom, gridBagConstraints);

        add(jPanelRunUpdates, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExpandBottomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExpandBottomActionPerformed
        if (jPanelRunDetails.isVisible()) {
            jPanelRunDetails.setVisible(false);
            this.remove(jPanelRunDetails);
            jButtonExpandBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/down.png")));
            revalidate();
        }
        else {
            this.add(jPanelRunDetails, BorderLayout.NORTH);
            jPanelRunDetails.setVisible(true);
            jButtonExpandBottom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/inf2b/algobench/images/up.png")));
            revalidate();
        }
    }//GEN-LAST:event_jButtonExpandBottomActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExpandBottom;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabelAlgorithm;
    private javax.swing.JLabel jLabelCurrentStatus;
    private javax.swing.JLabel jLabelElaspedTime;
    private javax.swing.JLabel jLabelLiveUpdate;
    private javax.swing.JLabel jLabelTaskID;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanelLiveUpdates;
    private javax.swing.JPanel jPanelRunDetails;
    private javax.swing.JPanel jPanelRunUpdates;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelTaskDetails;
    private javax.swing.JPanel jPanelTop;
    private javax.swing.JScrollPane jScrollPaneOutput;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaStreamingOutput;
    // End of variables declaration//GEN-END:variables

}
