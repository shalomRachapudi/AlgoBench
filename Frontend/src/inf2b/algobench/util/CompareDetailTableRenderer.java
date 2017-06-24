/*
 * Created by Yufen Wang.
 * 2016
 */

package inf2b.algobench.util;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Yufen Wang
 */
public class CompareDetailTableRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column){
        if(row %2 == 1){
            this.setBackground(new Color(255, 255, 239));
        }else{
            this.setBackground(Color.WHITE);
        }
        if(column == 0){
            JLabel label = new JLabel();
            label.setText("co");
            label.setOpaque(true);
            label.setBackground((Color)value);
            label.setForeground((Color)value);
            return label;
        }
        
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
