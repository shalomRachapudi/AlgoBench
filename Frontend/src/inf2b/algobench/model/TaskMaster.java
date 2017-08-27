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
 *
 * Modified by Shalom
 * 2017
 */


package inf2b.algobench.model;

import inf2b.algobench.main.AlgoBench;
import inf2b.algobench.main.AlgoBench.TaskState;
import inf2b.algobench.main.AlgoBench.TaskView;
import inf2b.algobench.ui.ResultsChartPanel;
import inf2b.algobench.ui.ResultsTablePanel;
import inf2b.algobench.ui.TaskOverviewPanel;
import inf2b.algobench.util.ITaskCompleteListener;
import inf2b.algobench.util.ITaskCompleteNotifier;
import java.io.*;
import java.io.IOException;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.logging.*;
import javax.swing.JPanel;

/**
 * This class is the workhorse of the frontend. It is responsible for launching
 * the configured task, communicating with it, feeding the relevant GUI
 * components with progress updates from the backend, and notifying listeners
 * when a task is completed. It also supplies the results to the result-display
 * utilities of the workbench.
 *
 * @author eziama ubachukwu and Yufen Wand
 */
public class TaskMaster implements Runnable, ITaskCompleteNotifier, Serializable, Cloneable {

    protected final String SERVER_MODE = "0";
    protected final String CLIENT_MODE = "1";
    protected int serverPort;
    protected AtomicBoolean terminationRequested;
    protected boolean stateChanged;
    protected boolean successful;
    protected StringBuilder response;
    protected int responseLineCount;
    private Task task;
    protected TaskOverviewPanel taskPanel;
    // contains XChartPanel, a 3rd party library that's not serialisable
    // so skip during serialisation and reinitialise later
    transient protected ResultsChartPanel resultChartPanel;
    transient protected ProcessBuilder processBuilder;
    transient protected ResultsTablePanel resultTablePanel;
    transient protected List<ITaskCompleteListener> taskCompleteListeners;
    protected TaskState state; // queued, running, stopped, completed

    protected TaskView lastView;

    public TaskMaster(Task task) {
        this.response = new StringBuilder();
        this.setState(TaskState.QUEUED);
        this.task = task;
        this.lastView = TaskView.OVERVIEW;
        this.successful = false;
        this.responseLineCount = 0;
        init();
    }
    
    /**
     * Copy constructor to emulate C++ deep copy effect
     * @param tM the task master you want to copy
     */
    public TaskMaster(TaskMaster tM)
    {
        this.response = tM.response;
        this.setState(tM.state);
        this.task = new Task(tM.task);
        this.lastView = tM.lastView;
        this.successful = tM.successful;
        this.responseLineCount = tM.responseLineCount;
        init();
    }

    final public void init() {
        // after serialisation, these need to be reset, so they are placed in 
        // a separate method
        this.terminationRequested = new AtomicBoolean(false);
        this.taskPanel = new TaskOverviewPanel(task);
        this.stateChanged = false;

        // those who need to be told when this finishes
        this.taskCompleteListeners = new ArrayList<>();
    }

    @Override
    public void run() {
        Thread serverThread = null;
        try {
            // start listening and communicating with taskrunner
            serverThread = new Thread(new Server());
            serverThread.start();
            // start backend
            launchTaskRunner();
            serverThread.join();

            if (terminationRequested.get() && successful) {
                this.taskPanel.updateComponents("[UPDATE]\t=== Termination requested by user. ===");
                setState(TaskState.STOPPED);
            }
            else if (!successful) {
                if (responseLineCount <= 1) { // check if it was only results header that was sent
                    setState(TaskState.FAILED);
                }
                else {
                    setState(TaskState.INCOMPLETE);
                }
            }
            else {
                setState(TaskState.COMPLETED);
            }
            this.taskPanel.stopTimer(getState());
            // indicate something has changed, so that its jList container can update its records
            this.stateChanged = true;
            serverThread.join();
            getResultDisplays();

        }
        catch (IOException | InterruptedException ex) {
            Logger.getLogger(TaskMaster.class.getName()).log(Level.SEVERE, null, ex);
            setState(TaskState.FAILED);
            try {
                this.terminate();
                serverThread.join();
            }
            catch (InterruptedException ex1) {
                Logger.getLogger(TaskMaster.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        finally {
            this.terminationRequested.set(false);
            this.notifyListeners();
        }
    }

    /**
     * Populate the results display panels (table, chart, etc) with data
     * returned from the execution.
     */
    private void getResultDisplays() {
        if (!this.hasResults()) {
            // no more than just the headings
            return;
        }
        // instantiate and populate table and chart
        Plotter p;
        
        if ("HASH".equals(task.getAlgorithmGroup(true))) {
            p = new Plotter(this.task.getTaskID(), response.toString(), 2);
            p.setYAxisLabel("Bucket");
            p.setXAxisLabel("Bucket Size");
            // additional bar chart for Hash results
            resultChartPanel = new ResultsChartPanel(task.getTaskID());
            resultChartPanel.addResultChart(p.getBarChart());
            resultTablePanel = new ResultsTablePanel(response.toString(), this.getTaskID(), true, "");
            // show averages (for Hashing)
            this.taskPanel.updateComponents("[MINBUCKETSIZE]\t" + this.getTask().getMinBucketSize());
            this.taskPanel.updateComponents("[MAXBUCKETSIZE]\t" + this.getTask().getMaxBucketSize());
            DecimalFormat df = new DecimalFormat("#.00");
            this.taskPanel.updateComponents("[AVERAGE]\t" + df.format(p.getAverage()));
            this.taskPanel.updateComponents("[STDDEVIATION]\t" + df.format(p.getStandardDeviation()));
        }
        else {
            p = new Plotter(this.task.getTaskID(), response.toString(), 0);
            if(task.getAlgorithmGroup(true).equals("SEARCH") || task.getAlgorithmGroup(true).equals("TREE")){
                p.setYAxisLabel("Time (μs)");
            }
            else{
                p.setYAxisLabel("Time (ms)");
            }
            p.setXAxisLabel("Input Size");
            resultChartPanel = new ResultsChartPanel(task.getTaskID());
            resultChartPanel.addResultChart(p.getLineChart());
            resultChartPanel.sethasAverage(p.hasAverage());
            if(task.getAlgorithmGroup(true).equals("SEARCH") || task.getAlgorithmGroup(true).equals("TREE") ){
                resultTablePanel = new ResultsTablePanel(response.toString(), task.getTaskID(), false,"(μs)");
            }else{
                resultTablePanel = new ResultsTablePanel(response.toString(), task.getTaskID(), false,"(ms)");
            }
        }
        
        resultChartPanel.validate();
    }

    public ResultsChartPanel getResultsChartPanel()
    {
        return resultChartPanel;
    }
    /**
     * Start the backend task executor process, and connect to its output stream
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void launchTaskRunner() throws IOException, InterruptedException {

//        System.out.println(AlgoBench.PathToTaskRunner + " " + CLIENT_MODE + " " + serverPort);
        processBuilder = new ProcessBuilder(AlgoBench.PathToTaskRunner,
                CLIENT_MODE, Integer.toString(serverPort));
        processBuilder.redirectErrorStream(true);
        final Process taskRunner = processBuilder.start();
        // start timing
        this.taskPanel.startTimer();

        String line;
        InputStream is = taskRunner.getInputStream();
        InputStreamReader isReader = new InputStreamReader(is);
        BufferedReader bfReader = new BufferedReader(isReader);

        while ((line = bfReader.readLine()) != null) {
            this.taskPanel.updateComponents(line);
            this.getTask().updateAfterComplete(line);
        }
        // wait for the process to end
        taskRunner.waitFor();
    }

    /**
     * Opens a socket for communicating with the backend, and waits for
     * connections. Sends serialised instructions to the backend and receives
     * the execution results
     */
    private class Server implements Runnable {

        protected ServerSocket serverSocket;
        protected Socket connectionSocket;

        public Server() {

            for (int trials = 0; trials < 3; ++trials) {
                // try-with-resources won't work here, cos we need to decouple the calls
                // and save out resources to class members
                try {
                    serverSocket = new ServerSocket(0);
                    serverSocket.setSoTimeout(60 * 1000); // 1 minute
                    serverPort = serverSocket.getLocalPort();
                    break;
                }
                catch (IOException ex) {
                    serverPort = 0;
                    Logger.getLogger(TaskMaster.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (serverSocket.isClosed()) {
                System.out.println("Error: SERVER daemon failed to initiate.");
            }
        }

        private void startCommunication() {
            try {
                System.out.println("Server listening on port " + serverPort);
                serverSocket.setSoTimeout(20 * 1000);
                connectionSocket = serverSocket.accept();
                PrintWriter pOut = new PrintWriter(connectionSocket.getOutputStream(), true);
                BufferedReader buffIn = new BufferedReader(
                        new InputStreamReader(connectionSocket.getInputStream(), "UTF-8"));
                // server should write to other app giving instructions
                String instruction = task.getCommand();
                System.out.println("Writing to CLIENT...");
                pOut.write(instruction);
                pOut.flush();
                connectionSocket.shutdownOutput();
                connectionSocket.setSoTimeout(10 * 60 * 1000);   // 10 mins
                String responseLine;
                System.out.println("Waiting to receive from CLIENT...");

                // reset response in case this is a restart
                response = new StringBuilder();
                responseLineCount = 0;
                while (!terminationRequested.get() && (responseLine = buffIn.readLine()) != null) {
                    if (responseLine.length() == 1) {
                        // it's a heartbeat message
//                        System.out.println("-- heartbeat --");
                        continue;
                    }
                    switch (responseLine) {
                        case "BEGIN": // markers sent by client
                            break;
                        case "END":
                            break;
                        default:
                            response.append(responseLine.trim());
                            response.append("\n");
                            ++responseLineCount;
                            break;
                    }
                }
                System.out.println("SERVER: Done receiving from CLIENT");
                successful = true;
            }
            catch (IOException ex) {
                Logger.getLogger(TaskMaster.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                try {
                    // ensure to close in reverse order of creation
                    connectionSocket.close();
                    serverSocket.close();
                    System.out.println("[SERVER] Connection closed.");
                }
                catch (IOException ex) {
                    Logger.getLogger(TaskMaster.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        @Override
        public void run() {
            startCommunication();
        }
    }

    public String getTaskID() {
        return task.getTaskID();
    }

    public String getTaskPanelName() {
        lastView = TaskView.OVERVIEW;
        return task.getTaskID() + TaskView.OVERVIEW.toString();
    }

    public String getChartName() {
        lastView = TaskView.CHART;
        return task.getTaskID() + TaskView.CHART.toString();
    }

    public String getTableName() {
        lastView = TaskView.TABLE;
        return task.getTaskID() + TaskView.TABLE.toString();
    }

    public Task getTask() {
        return task;
    }

    public TaskOverviewPanel getTaskPanel() {
        return taskPanel;
    }

    public JPanel getResultChartPanel() {
        return resultChartPanel;
    }

    public ResultsTablePanel getTablePanel() {
        return resultTablePanel;
    }

    // keep state between transitions among tasks
    public TaskView getLastState() {
        return lastView;
    }

    public boolean getStateChanged() {
        return stateChanged;
    }

    public void clearStateChanged() {
        stateChanged = false;
    }

    public void terminate() {
        this.taskPanel.updateComponents("[TERMINATE]\t" + TaskState.STOPPING.toString() + "...");
        this.terminationRequested.set(true);
    }

    public AlgoBench.TaskState getState() {
        return this.state;
    }

    public final synchronized void setState(TaskState taskState) {
        this.state = taskState;
    }

    public boolean hasResults() {
        return responseLineCount > 1;
    }

    @Override
    public void addListener(ITaskCompleteListener listener) {
        this.taskCompleteListeners.add(listener);
    }

    @Override
    public void removeListener(ITaskCompleteListener listener) {
        this.taskCompleteListeners.remove(listener);
    }

    @Override
    public void notifyListeners() {
        for (ITaskCompleteListener l : taskCompleteListeners) {
            l.notifyTaskComplete(this);
        }
    }

    @Override
    public String toString() {
        return task.getTaskID();
    }

    /**
     * Custom implementation of the Serializable interface writeObject method
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Custom implementation of the Serializable interface readObject method
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.init();
        getResultDisplays();
    }
}
