/*
 * Created by Yufen Wang.
 * 2016
 */

package inf2b.algobench.util;

import inf2b.algobench.model.TaskMaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Yufen WANG
 */
public class CheckboxTableRenderer extends DefaultTableCellRenderer  {

    private Map<String, ImageIcon> imageMap;
    private JLabel label;
    private boolean[] selectability;
    private JCheckBox c;
    private JPanel panel;
    
    public CheckboxTableRenderer(boolean[] selectability) {
        imageMap = new HashMap<>();
        label = new JLabel();
        label.setIconTextGap(10);
        label.setBorder(new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        label.setOpaque(true);
        c = new JCheckBox();
        panel = new JPanel();
        this.selectability = selectability;
    }
    
    protected ImageIcon getImageIcon(String imageName) {
        String fullName = "/inf2b/algobench/images/algo_icons/"
                + imageName.toLowerCase() + ".png";
        return new ImageIcon(getClass().getResource(fullName));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        label.setFont(table.getFont());
        label.setBackground(table.getBackground());
        label.setForeground(table.getForeground());
        panel.setBackground(table.getBackground());
        panel.setForeground(table.getForeground());
        
        Icon icon = new ImageIcon(this.getClass().getResource( "/inf2b/algobench/images/bordericon.png"));
        MatteBorder matteicon = new MatteBorder(0, 0, 4, 0, icon);
        label.setBorder(matteicon);
        c.setEnabled(true);
        
        if(column == 0){
            if(!selectability[row]){
                label.setBackground(new Color(203,203,203));
                label.setForeground(Color.white);
            }
            label.setIcon(this.getImageIcon(((TaskMaster)value).getTask().getAlgorithmShortName()));
            label.setText(value.toString() + " [" + ((TaskMaster)value).getState().toString().toLowerCase() + "]");
            return label;
        }else{
            c.setHorizontalAlignment(JCheckBox.CENTER);
//            c.setVerticalAlignment(JCheckBox.CENTER);
            c.setSelected(Boolean.valueOf(value.toString()));
            if(!selectability[row]){
                panel.setBackground(new Color(203,203,203));
                c.setEnabled(false);
            }
            panel.setLayout(new BorderLayout());
            panel.add(c, BorderLayout.CENTER);
            panel.setBorder(matteicon);
            return panel;
        }
    }
    
}
