/*
 * Created by Yufen Wang.
 * 2016
 */

package inf2b.algobench.util;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Yufen Wang
 */
public class CheckBoxListRenderer implements ListCellRenderer<JCheckBox> {

    @Override
    public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index, 
            boolean isSelected, boolean cellHasFocus) {
        JCheckBox checkbox = value;
        checkbox.setHorizontalAlignment(JCheckBox.LEFT);
        
        return checkbox;
    }
    
}
