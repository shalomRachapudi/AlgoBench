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

package inf2b.algobench.model;

import inf2b.algobench.main.AlgoBench;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * Represents a task to be given to the backend. TODO: Make Task abstract or an
 * interface, and create sub-classes: SortTask, GraphTask, SearchTask, HashTask
 *
 * @author eziama ubachukwu, Yufen WANG, and Shalom
 */
public class Task implements Serializable {

    private String runTitle;
    private String algorithmCode;
    private String algorithm;
    private String algorithmGroup; // graph, sort, hash, search
    private String taskID;
    private Long inputStartSize;
    private Long inputStepSize;
    private Long inputFinalSize;
    private Long inputMinValue;
    private Long inputMaxValue;
    private Integer numRuns;
    private Integer numRepeats;
    private Integer numCompletedRuns;
    private String inputDistribution;
    private String error;
    private String pivotPosition;
    private String inputFileName; // full path to custom input
    private String dataStructure;
    private String fixedGraphParam; // 0-vertices, 1-edges
    private Long fixedGraphSize;
    private Boolean isDirectedGraph;
    private Boolean allowSelfLoops;
    private Integer hashBucketSize;
    private String hashKeyType;
    private int hashFunction_a;
    private int hashFunction_b;
    private int maxBucketSize;
    private int minBucketSize;
    private boolean graphIsDelayed;
    private String searchKeyType;//custom,always-in,not-in,random
    private int ram;
    private boolean overrideFlag; // override existing task if (when) edited?
    private String totalProgress;
    
    // For Tree based tasks
    private String treeSize;
    private String treeRangeLowerLimit;
    private String treeRangeUpperLimit;
    private String treeType;
    private String dataElement;
    private boolean insertElement;
    private boolean searchElement;
    private boolean deleteElement;  
    private String memoryFootprint;
    private String maxRecursionDepth;
    
    private String notes;   // for notes to remember - not implemented yet as a functionality
    protected Integer status;
    // make it able to fire property changed events
    PropertyChangeSupport taskPcs;

    public Task() {
        this.status = 0;
        this.taskPcs = new PropertyChangeSupport(this);
        this.error = "";
        this.overrideFlag = false;
        
        memoryFootprint = "--";
        maxRecursionDepth = "--";
        totalProgress = "0";
    }
    
    /**
     * Copy constructor to emulate C++ deep copy effect
     * @param other the task you want to copy
     */
    public Task(Task other) {
        this.status = 0;
        this.taskPcs = new PropertyChangeSupport(this);
        this.error = "";
        this.overrideFlag = false;
        
        this.runTitle = other.runTitle;
        this.algorithmCode = other.algorithmCode;
        this.algorithm = other.algorithm;
        this.algorithmGroup = other.algorithmGroup;
        this.taskID = other.taskID;
        this.inputStartSize = other.inputStartSize;
        this.inputStepSize = other.inputStepSize;
        this.inputFinalSize = other.inputFinalSize;
        this.inputMinValue = other.inputMinValue;
        this.inputMaxValue = other.inputMaxValue;
        this.numRuns = other.numRuns;
        this.numRepeats = other.numRepeats;
        this.numCompletedRuns = other.numCompletedRuns;
        this.inputDistribution = other.inputDistribution;
        this.error = other.error;
        this.pivotPosition = other.pivotPosition;
        this.inputFileName = other.inputFileName;
        this.dataStructure = other.dataStructure;
        this.fixedGraphParam = other.fixedGraphParam;
        this.fixedGraphSize = other.fixedGraphSize;
        this.isDirectedGraph = other.isDirectedGraph;
        this.allowSelfLoops = other.allowSelfLoops;
        this.hashBucketSize = other.hashBucketSize;
        this.hashKeyType = other.hashKeyType;
        this.hashFunction_a = other.hashFunction_a;
        this.hashFunction_b = other.hashFunction_b;
        this.maxBucketSize = other.maxBucketSize;
        this.minBucketSize = other.minBucketSize;
        this.graphIsDelayed = other.graphIsDelayed;
        this.searchKeyType = other.searchKeyType;
        this.ram = other.ram;
        this.overrideFlag = other.overrideFlag;
        
        this.treeSize = other.treeSize;
        this.treeRangeLowerLimit = other.treeRangeLowerLimit;
        this.treeRangeUpperLimit = other.treeRangeUpperLimit;
        this.treeType = other.treeType;
        this.dataElement = other.dataElement;
        this.insertElement = other.insertElement;
        this.searchElement = other.searchElement;
        this.deleteElement = other.deleteElement;
        
        this.maxRecursionDepth = other.maxRecursionDepth;
        this.memoryFootprint = other.memoryFootprint;
        this.totalProgress = other.totalProgress;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskID() {
        return taskID;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getInputFinalSize() {
        return inputFinalSize;
    }

    public void setInputFinalSize(Long inputFinalSize) {
        this.inputFinalSize = inputFinalSize;
    }

    public PropertyChangeSupport getTaskPcs() {
        return taskPcs;
    }

    public void setTaskPcs(PropertyChangeSupport taskPcs) {
        this.taskPcs = taskPcs;
    }
    
    public void setNotes(String s)
    {
        notes = s;
    }
    
    public String getNotes()
    {
        return notes == null ? "Not implemented ATM" : notes;
    }

    public String getAlgorithmCode() {
        return algorithmCode;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getAlgorithmShortName() {
        return AlgoBench.properties.getProperty(algorithm.toUpperCase() + "_SHORT");
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm.replaceAll("\\s+", "_").toUpperCase();
        this.algorithmCode = AlgoBench.properties.getProperty(this.algorithm);
    }

    public Long getInputStartSize() {
        return inputStartSize;
    }

    public Boolean setInputStartSize(Long inputStartSize) {
        if (inputStartSize < 0) {
            this.error += "Please use only positive values.\n";
            return false;
        }
        this.inputStartSize = inputStartSize;
        return true;
    }

    public Long getInputStepSize() {
        return inputStepSize;
    }

    public String getMaxRecursionDepth()
    {
        return maxRecursionDepth;
    }
    public Integer getNumRepeats() {
        return numRepeats;
    }

    public void setNumRepeats(Integer numRepeats) {
        this.numRepeats = numRepeats;
    }

    public Integer getNumCompletedRuns() {
        return numCompletedRuns;
    }

    public void setNumCompletedRuns(Integer numCompletedRuns) {
        Integer oldValue = this.numCompletedRuns;
        this.numCompletedRuns = numCompletedRuns;
        taskPcs.firePropertyChange("numCompletedRuns",
                oldValue, numCompletedRuns);
    }

    public boolean getGraphIsDelayed() {
        return graphIsDelayed;
    }

    public void setGraphIsDelayed(boolean graphIsDelayed) {
        this.graphIsDelayed = graphIsDelayed;
    }

    public Boolean getIsDirectedGraph() {
        return isDirectedGraph;
    }

    public void setIsDirectedGraph(Boolean isDirectedGraph) {
        this.isDirectedGraph = isDirectedGraph;
    }

    public Boolean getAllowSelfLoops() {
        return allowSelfLoops;
    }

    public void setAllowSelfLoops(Boolean allowSelfLoops) {
        this.allowSelfLoops = allowSelfLoops;
    }

    public int getFixedGraphParam() {
        return Integer.parseInt( getFixedGraphParam(true) );
    }
    
    public String getFixedGraphParam(Boolean asString) {
        if (!asString) {
            return AlgoBench.properties.getProperty(fixedGraphParam.toUpperCase());
        }
        return fixedGraphParam;
    }

    public void setFixedGraphParam(String fixedGraphParam) {
        this.fixedGraphParam = fixedGraphParam;
    }

    public Long getFixedGraphSize() {
        return fixedGraphSize;
    }

    public String getAlgorithmGroup(Boolean asString) {
        if (!asString) {
            return AlgoBench.properties.getProperty(algorithmGroup.toUpperCase());
        }
        return algorithmGroup;
    }
    public String getAlgorithmGroup()
    {
        return getAlgorithmGroup(true);
    }
    public void setAlgorithmGroup(String algorithmGroup) {
        this.algorithmGroup = algorithmGroup;
    }

    public void setFixedGraphSize(Long fixedGraphSize) {
        this.fixedGraphSize = fixedGraphSize;
    }

    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    public String getDataStructure(Boolean asString) {
        if (!asString) {
            String s = dataStructure.toUpperCase().replace(" ", "_");
            return AlgoBench.properties.getProperty("GRAPH_" + s);
        }
        return dataStructure;
    }

    public void setOverrideFlag(boolean flag) {
        this.overrideFlag = flag;
    }

    public boolean getOverrideFlag() {
        return overrideFlag;
    }
    
    public void setInputStepSize(Long inputStepSize) {
        if (inputStepSize == 0) {
            this.error += "Please use only non-zero values for step size.\n";
            return;
        }
        this.inputStepSize = inputStepSize;
    }

    public Long getInputMinValue() {
        return inputMinValue;
    }

    public void setInputMinValue(String inputMinValue) {
        this.inputMinValue = Long.parseLong(inputMinValue);
        if (this.inputMinValue > Long.MAX_VALUE
                || this.inputMinValue < 0) {
            this.error += "Invalid min value\n";
        }
    }

    public Long getInputMaxValue() {
        return inputMaxValue;
    }

    public void setInputMaxValue(String inputMaxValue) {
        this.inputMaxValue = Long.parseLong(inputMaxValue);
        if (this.inputMaxValue < this.inputMinValue) {
            this.error += "Max value is less than min value.\n";
        }
        if (this.inputMaxValue == 0) {
            this.error += "Invalid max value\n";
        }
    }

    public Integer getNumRuns() {
        return numRuns;
    }

    public void setNumRuns(Integer numRuns) {
        this.numRuns = numRuns;
    }

    public void setNumRuns() {
        if(numRuns!=null){
            return;
        }
        if (this.inputStepSize == 0) {
            this.numRuns = 1;
            return;
        }
        Long temp = (this.inputFinalSize - this.inputStartSize) / this.inputStepSize;
        this.numRuns= temp.intValue();
        this.numRuns++; // add 1 for the running with the initial size
    }

    public String getInputFileName() {
        if (inputFileName == null) {
            return "";
        }
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getInputDistribution() {
        return getInputDistribution(true);
    }
    
    public String getInputDistribution(boolean asString) {
        if(!asString){
            return AlgoBench.properties.getProperty("INPUT_" + inputDistribution.toUpperCase());
        }else{
            return this.inputDistribution;
        }
    }

    public void setInputDistribution(String inputDistribution) {
        this.inputDistribution = inputDistribution;
    }

    public void setRunTitle(String runTitle) {
        this.runTitle = runTitle;
    }

    public String getPivotPosition()
    {
        return getPivotPosition(true);
    }
    public String getPivotPosition(boolean asString) {
        if (!asString) {
            return AlgoBench.properties.getProperty(
                    "PIVOT_" + pivotPosition.toUpperCase(), "1");
        }
        return pivotPosition;
    }

    public void setPivotPosition(String pivotPosition) {
        this.pivotPosition = pivotPosition;
    }

    public Integer getHashBucketSize() {
        return hashBucketSize;
    }

    public void setHashBucketSize(Integer hashBucketSize) {
        this.hashBucketSize = hashBucketSize;
    }
    
    

    public void setMaxBucketSize(String maxBucketSize) {
        this.maxBucketSize = Integer.parseInt(maxBucketSize);
    }

    public void setMinBucketSize(String minBucketSize) {
        this.minBucketSize = Integer.parseInt(minBucketSize);
    }

    public int getMaxBucketSize() {
        return maxBucketSize;
    }

    public int getMinBucketSize() {
        return minBucketSize;
    }

//    public String getHashType(Boolean asString) {
//        if (!asString) {
//            return AlgoBench.properties.getProperty("HASHING_FUNCTION_" + hashFunctionType.toUpperCase());
//        }
//        return hashFunctionType;
//    }
//
//    public void setHashFunctionType(String hashFunctionType) {
//        this.hashFunctionType = hashFunctionType;
//    }

    public String getHashKeyType(Boolean asString) {
        if (!asString) {
            return AlgoBench.properties.getProperty("HASHING_KEY_" + hashKeyType.toUpperCase());
        }
        return hashKeyType;
    }

    public void setHashKeyType(String hashKeyType) {
        this.hashKeyType = hashKeyType;
    }
    
    public int getHashparameters(char s){
        switch(s){
            case 'a':
                return this.hashFunction_a;
            case 'b':
                return this.hashFunction_b;
            default:
                return this.hashFunction_a;
        }
    }
    
    public void setHashparameters(int a, int b){
        this.hashFunction_a = a;
        this.hashFunction_b = b;
    }
    
    public String getHashFunction(){
        String a = this.getHashparameters('a')+"";
        String b = this.getHashparameters('b')+"";
        String n = this.getHashBucketSize()+"";
        String result = "|"+a+"K+"+b+"| mod "+n;
        
        return result;
    }
    
    public void setTotalProgress(String progress)
    {
        totalProgress = progress;
    }
    public String getTotalProgress()
    {
        return totalProgress;
    }
    public String getSearchKeyType()
    {
        return getSearchKeyType(true);
    }
    public String getSearchKeyType(Boolean asString) {
        if (!asString) {
            return AlgoBench.properties.getProperty("SEARCH_KEY_" + searchKeyType.toUpperCase());
        }
        return searchKeyType;
    }

    public void setSearchKeyType(String searchKeyType) {
        this.searchKeyType = searchKeyType.replaceAll("\\s+", "_").toUpperCase();;
    }
    
    public int getSortRam(){
        return this.ram;
    }
    
    public String getMemoryFootprint()
    {
        return memoryFootprint;
    }
    
    public void setSortRam(String ram){
        this.ram = Integer.parseInt(ram);
    }

    public String getRunTitle() {
        return runTitle;
    }

    public String getError() {
        return this.error;
    }

    public void logError(String message) {
        this.error += message;
    }

    public void clearErrorLog() {
        this.error = "";
    }
   
    public void setTreeSize(String treeSize)
    {
        this.treeSize = treeSize;
    }
    
    public void setTreeRangeLowerLimit(String treeRangeLowerLimit)
    {
        this.treeRangeLowerLimit = treeRangeLowerLimit;
    }
    
    public void setTreeRangeUpperLimit(String treeRangeUpperLimit)
    {
        this.treeRangeUpperLimit = treeRangeUpperLimit;
    }
    
    public void setTreeType(String treeType)
    {
        String temp = treeType.replaceAll("\\s+", "_").toUpperCase();
        this.treeType = AlgoBench.properties.getProperty(temp);
    }
    
    public String getTreeSize()
    {
        return treeSize;
    }
    
    public String getTreeRangeLowerLimit()
    {
        return treeRangeLowerLimit;
    }
    
    public String getTreeRangeUpperLimit()
    {
        return treeRangeUpperLimit;
    }
    
    public String getTreeRange()
    {
        String range = "[" + getTreeRangeLowerLimit() + ", " + getTreeRangeUpperLimit() + "]";
        return range;
    }
    
    public String getTreeType()
    {
        return treeType;
    }

    @Override
    public String toString() {
        return runTitle;
    }

    public String getCommand() {
        String r = "ALGORITHM:" + getAlgorithmCode();
        r += "\nALGORITHM-GROUP:" + getAlgorithmGroup(false);
        r += "\nINPUT-STARTSIZE:" + getInputStartSize();
        r += "\nINPUT-STEPSIZE:" + getInputStepSize();
        r += "\nINPUT-FILENAME:" + getInputFileName();
        r += "\nNUMRUNS:" + getNumRuns();
        r += "\nNUMREPEATS:" + getNumRepeats();
        if (getAlgorithmGroup(true).equals("GRAPH")) {
            r += "\nGRAPH-STRUCTURE:" + getDataStructure(false);
            r += "\nGRAPH-FIXED-SIZE:" + getFixedGraphSize();
            r += "\nGRAPH-FIXED-EDGES:" + getFixedGraphParam(false);
            r += "\nGRAPH-ALLOW-SELF-LOOP:" + (getAllowSelfLoops() ? "1" : "0");
            r += "\nGRAPH-IS-DIRECTED:" + (getIsDirectedGraph() ? "1" : "0");
            r += "\nGRAPH-IS-DELAYED:" + (getGraphIsDelayed() ? "1" : "0");
        }
        else if (getAlgorithmGroup(true).equals("HASH")) {
            r += "\nHASH-BUCKET-ARRAY-SIZE:" + Integer.toString(getHashBucketSize());
            //r += "\nHASH-FUNCTION-TYPE:" + getHashType(false);
            r += "\nHASH-KEY-TYPE:" + getHashKeyType(false);
            r += "\nHASH-FUNCTION-A:" + getHashparameters('a');
            r += "\nHASH-FUNCTION-B:" + getHashparameters('b');
        }
        else if (getAlgorithmGroup(true).equals("SORT")) {
            r += "\nINPUT-MINVALUE:" + getInputMinValue();
            r += "\nINPUT-MAXVALUE:" + getInputMaxValue();
            r += "\nINPUT-DISTRIBUTION:" + getInputDistribution(false);

            if (getAlgorithmCode().equals(AlgoBench.properties.getProperty("QUICKSORT"))) {
                r += "\nQUICKSORT-PIVOT-POSITION:" + getPivotPosition(false);
            }
            else if (getAlgorithmCode().equals(AlgoBench.properties.getProperty("EXTERNAL_MERGESORT"))) {
                r += "\nEXTERNAL-MERGESORT-RAM:" + getSortRam();
            }
        }
        else if (getAlgorithmGroup(true).equals("SEARCH")) {
            r += "\nINPUT-MINVALUE:" + getInputMinValue();
            r += "\nINPUT-MAXVALUE:" + getInputMaxValue();
            r += "\nINPUT-DISTRIBUTION:" + getInputDistribution(false);
            r += "\nSEARCH-KEY-TYPE:" + getSearchKeyType(false);
        }
        else if (getAlgorithmGroup().equals("TREE")) {
            r = "ALGORITHM:" + getAlgorithmCode();
            r += "\nALGORITHM-GROUP:" + getAlgorithmGroup(false);
            r += "\nTREE-SIZE:" + getTreeSize();
            r += "\nTREE-RANGE-LOWER-LIMIT:" + getTreeRangeLowerLimit();
            r += "\nTREE-RANGE-UPPER-LIMIT:" + getTreeRangeUpperLimit();
            r += "\nTREE-TYPE:" + getTreeType();
            r += "\nDATA-ELEMENT:" + getDataElement();
            r += getInsertOp() ? "\nINSERT-OP:1" : "\nINSERT-OP:0";
            r += getSearchOp() ? "\nSEARCH-OP:1" : "\nSEARCH-OP:0";
            r += getDeleteOp() ? "\nDELETE-OP:1" : "\nDELETE-OP:0";
                
        }
        r += "\n";
        return r;
    }

    public void update(String response) {
        // parse the response
        String[] parts = response.split(":");
        switch (parts[0]) {
            case "NUMCOMPLETEDRUNS":
                setNumCompletedRuns(Integer.parseInt(parts[1]));
                break;
        }
    }
    
    public void updateAfterComplete(String newUpdate){
        String[] parts = newUpdate.split("\t");
        switch (parts[0].toUpperCase()) {
            case "[MINBUCKETSIZE]":
                this.setMinBucketSize(parts[1]);
                break;
            case "[MAXBUCKETSIZE]":
                this.setMaxBucketSize(parts[1]);
                break;
            default:
                break;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        taskPcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        taskPcs.removePropertyChangeListener(listener);
    }
    
     public String getDataElement()
    {
        return dataElement;
    }
    
    public boolean getInsertOp()
    {
        return insertElement;
    }
    
    public boolean getSearchOp()
    {
        return searchElement;
    }
    
    public boolean getDeleteOp()
    {
        return deleteElement;
    }
    
    public void setDataElement(String element)
    {
        dataElement = element;
    }
    
    public void setInsertOp(boolean flag) 
    {
        insertElement = flag;
    }
    
    public void setSearchOp(boolean flag)
    {
        searchElement = flag;
    }
    
    public void setDeleteOp(boolean flag)
    {
        deleteElement = flag;
    }
    
    public void setMemoryFootprint(String s)
    {
        memoryFootprint = s;
    }
    
    public void setMaxRecursionDepth(String s)
    {
        maxRecursionDepth = s;
    }
}
