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

package inf2b.algobench.main;

import inf2b.algobench.ui.MainWindow;
import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;


/**
 * The entry point of the application, and which holds static application-wide
 * data and undertakes some initialisation tasks
 * @author eziama ubachukwu and Yufen Wang
 */
public class AlgoBench {
    public static Properties properties = new Properties();
    public static String JarDirectory;
    public static String PathToTaskRunner;
    public static int WindowsOS = 1;
    public static int LinuxOS = 2;
    public static List<Image> iconImagesList;

    public static int taskNumber = 0;

    public static enum TaskState {

        QUEUED, RUNNING, STOPPED, COMPLETED, INCOMPLETE, FAILED, STOPPING;
    }

    public static enum TaskView {

        OVERVIEW, CHART, TABLE, MULTIPLE, STARTPAGE;

        public static TaskView getView(String view) {
            for (TaskView tv : values()) {
                if (tv.toString().equals(view)) {
                    return tv;
                }
            }
            return STARTPAGE;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                JarDirectory = args[0];
                PathToTaskRunner = args[1];
                if (!(new File(PathToTaskRunner).exists())) {
                    System.out.println("Task runner not valid: " + PathToTaskRunner);
                    return;
                }
            }
            else {
                System.out.println("Usage: java -jar /path/to/jar-file/algobench.jar /path/to/jar-file/ /path/to/algobench_b");
                return;
            }
            /* Set the Nimbus look and feel */
            //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
            /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
             * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
             */
            try {
//                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                    if ("Nimbus".equals(info.getName())) {
//                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                        break;
//                    }
//                }
                javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            //</editor-fold>
            //</editor-fold>
            //</editor-fold>

            String config = "/inf2b/algobench/config/config.properties";
            InputStream fis = AlgoBench.class.getResourceAsStream(config);
            properties.load(fis);
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(() -> {
                new MainWindow().setVisible(true);
            });
        }
        catch (Exception ex) {
            Logger.getLogger(AlgoBench.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
