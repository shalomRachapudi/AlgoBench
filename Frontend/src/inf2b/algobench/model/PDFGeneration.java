/***********************************************************************************
 *  AlgoBench is a learning aid directed at students taking the Inf2B course to
 *  better understand the theoretical concepts, experiment with algorithms using 
 *  various inputs, and interpret the results.
 * 
 *  Copyright (C) 2017  Shalom <shalomray7@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  version 2 any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************************
 *  This program's developed as part of MSc project, University of Edinburgh (2017)
 *  Project: AlgoBench
 *  Supervisor: Kyriakos Kalorkoti
 *  School: School of Informatics
 *  Previous Contributor(s): None
 ***********************************************************************************/

package inf2b.algobench.model;

import com.xeiam.xchart.BitmapEncoder;
import inf2b.algobench.main.AlgoBench;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;

import inf2b.algobench.model.Task;
import inf2b.algobench.model.TaskMaster;
import inf2b.algobench.model.MyChart;
import inf2b.algobench.ui.ResultsChartPanel;

//JAXP
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;
 
//FOP
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

public class PDFGeneration {
    
    /**
     * Generates PDF for selected task
     * @param t : Task t
     * @return PDF Absolute file name on success. Otherwise, null
     */
    /*
     * The following method is based on 
     * https://cuppajavamattiz.wordpress.com/2006/02/23/practical-example-using-fop-for-pdf-generation-updated/
     */
    public String generatePDF(TaskMaster tM)
    {
        Task task = tM.getTask();
        MyChart chart = tM.getResultsChartPanel().getChart();
        try {
            System.out.println("Generating PDF for " + task.getAlgorithm());
            
            File baseDir = new File(".");
            File outDir = new File(baseDir, "out");
            outDir.mkdirs();
            System.out.println("Base directory: " + baseDir);
            // setup input & output files
            String pdfFileName = task.getTaskID() + ".pdf";
            System.out.println(pdfFileName);
            File xsltFile = new File(baseDir, "xml/xslt/task.xsl");
            File pdfFile = new File(outDir, pdfFileName);
            
            // generate runtime chart
            String chartID = "image.jpg";
            File saveFile = new File(baseDir, "images/" + chartID);
            System.out.println(saveFile.getCanonicalPath());
            BitmapEncoder.saveBitmap(chart, saveFile.getCanonicalPath(), BitmapEncoder.BitmapFormat.JPG);
            
            
            //get xml source code
            String xmlSrc = getXMLSourceCode(task);
            
            System.out.println("Transforming xml and xsl to PDF");
            
            //configure fopFactory as desired
            FopFactory fopFactory = FopFactory.newInstance();
            
            //configure foUserAgent
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            
            // setup output
            OutputStream out = new java.io.FileOutputStream(pdfFile);
            out = new java.io.BufferedOutputStream(out);
 
            try {
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                
                // set up XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer( new StreamSource(xsltFile));
                transformer.setParameter("versionParam", "2.0");
                
                // Setup input for XSLT transformation
                Source src = new StreamSource(new StringReader(xmlSrc));
                
                // Resulting SAX events (the generated FO) must be piped through
                // to FOP
                Result res = new SAXResult(fop.getDefaultHandler());
                
                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);
            }
            finally {
                out.close();
            }
            return pdfFile.getAbsolutePath();
        }
        catch(Exception ex)
        {
            System.out.print("Error");
            ex.printStackTrace(System.err);
            return null;
        }
    }
    private String getXMLSourceCode(Task t)
    {
        if (t.getAlgorithmGroup().equals("TREE")) {
            return generateXMLForTreeBasedAlgorithms(t);
        }
        return generateXMLForTask(t);
    }
    
    private String generateXMLForTreeBasedAlgorithms(Task t)
    {
        return null;
    }
    
    private String generateXMLForTask(Task t)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "\n<data>"
                + "\n<task-name>" + t.getAlgorithm() + "</task-name>"
                + "\n<input-size>" + t.getInputFinalSize() + "</input-size>"
                + "\n<memory-footprint>" + t.getMemoryFootprint() + "</memory-footprint>"
                + "\n<scheduled-tasks>" + t.getNumRuns() + "</scheduled-tasks>"
                + "\n<completed-tasks>" + t.getNumCompletedRuns()+ "</completed-tasks>"  
                + "\n<initial-size>" + t.getInputStartSize() + "</initial-size>"
                + "\n<final-size>" + t.getInputFinalSize() + "</final-size>"
                + "\n<step-size>" + t.getInputStepSize() + "</step-size>"
                + "\n<notes>" + t.getNotes() + "</notes>"
            );
        
        if (t.getAlgorithmGroup().equals("GRAPH")) {
            sb.append("\n<graph-fixed-size>" + t.getFixedGraphSize() + "</graph-fixed-size>"
                    + "\n<graph-fixed-edges>" + t.getFixedGraphParam(false) + "</graph-fixed-edges"
                    + "\n<graph-allow-self-loop>" + t.getAllowSelfLoops() + "</graph-allow-self-loop>"
                    + "\n<graph-is-directed>" + t.getIsDirectedGraph() + "</graph-is-directed>"
                    + "\n<graph-is-delayed" + t.getGraphIsDelayed() + "</graph-is-delayed"
                );
        }
        else if (t.getAlgorithmGroup().equals("HASH")) {
            sb.append("\n<hash-bucket-array-size>" + Integer.toString(t.getHashBucketSize()) + "</hash-bucket-array-size>"
                    + "\n<hash-key-type>" + t.getHashKeyType(false) + "</hash-key-type>"
                    + "\n<hash-function-a" + t.getHashparameters('a') + "</hash-function-a>"
                    + "\n<hash-function-b>" + t.getHashparameters('b') + "</hash-function-b>"
                );
        }
        else if (t.getAlgorithmGroup().equals("SORT")) {
            sb.append("\n<input-range>[" + t.getInputMinValue() + ", " + t.getInputMaxValue() + "]</input-range>"
                    + "\n<input-distribution>" + t.getInputDistribution(true) + "</input-distribution>"
                );

            if (t.getAlgorithmCode().equals(AlgoBench.properties.getProperty("QUICKSORT"))) {
                sb.append("\n<pivot-position>" + t.getPivotPosition(true) +"</pivot-position>"
                    + "\n<max-recursion-depth>" + t.getMaxRecursionDepth() + "</max-recursion-depth>"
                );
            }
            else if (t.getAlgorithmCode().equals(AlgoBench.properties.getProperty("EXTERNAL_MERGESORT"))) {
                sb.append("\n<sort-ram-ems>" + t.getSortRam() + "</sort-ram-ems>");
            }
        }
        else if (t.getAlgorithmGroup().equals("SEARCH")) {
            sb.append("\n<input-range>[" + t.getInputMinValue() + ", " + t.getInputMaxValue() + "]</input-range>"
                    + "\n<input-distribution>" + t.getInputDistribution(true) + "</input-distribution>"
                    + "\n<search-key-type>" + t.getSearchKeyType() + "</search-key-type>"
                );
        }
        
        sb.append("\n</data>");
        
        System.out.println(sb.toString());
        return sb.toString();
    }
}
