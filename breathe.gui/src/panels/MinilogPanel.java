package panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import app.App_temp;

public class MinilogPanel{
	
    private JPanel mainPanel = new JPanel();
    
    public MinilogPanel(App_temp app) {
    	mainPanel.setBackground(Color.WHITE);
    	mainPanel.setPreferredSize(new Dimension(1250, 100));
    	mainPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY));
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
}
