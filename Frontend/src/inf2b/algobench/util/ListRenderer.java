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
package inf2b.algobench.util;

import inf2b.algobench.model.TaskMaster;
import java.awt.Color;
import java.awt.Component;
import java.util.*;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.MatteBorder;

/**
 *
 * @author eziama ubachukwu
 */
public class ListRenderer extends DefaultListCellRenderer {

    private Map<String, ImageIcon> imageMap;
    private JLabel label;

    public ListRenderer() {
        imageMap = new HashMap<>();
        label = new JLabel();
        label.setIconTextGap(10);
        label.setBorder(new MatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
        label.setOpaque(true);
    }

    protected ImageIcon getImageIcon(String imageName) {
        String fullName = "/inf2b/algobench/images/algo_icons/"
                + imageName.toLowerCase() + ".png";
        return new ImageIcon(getClass().getResource(fullName));
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus){

        TaskMaster tm = (TaskMaster) value;
        //set up the appearance of a cell
        label.setFont(list.getFont());
        if (isSelected) {
            label.setForeground(list.getSelectionForeground());
            label.setBackground(new Color(102, 153, 204));
        }
        else {
            label.setBackground(list.getBackground());
            label.setForeground(list.getForeground());
        }
        if (tm.getStateChanged()) {
            if (isSelected) {
                tm.clearStateChanged();
            }
            else{ // give visual cue to user that state has changed
                label.setBackground(new Color(255, 186, 0));
            }
        }
        
        label.setIcon(this.getImageIcon(tm.getTask().getAlgorithmShortName()));
        label.setText(value.toString() + " [" + tm.getState().toString().toLowerCase() + "]");
        
        Icon icon = new ImageIcon(this.getClass().getResource( "/inf2b/algobench/images/bordericon.png"));
        MatteBorder matteicon = new MatteBorder(0, 0, 4, 0, icon);
        label.setBorder(matteicon);
        
        return label;
    }
    
    
}
