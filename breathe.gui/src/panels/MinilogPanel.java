package panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import app.App;

public class MinilogPanel{
	
    private JPanel mainPanel = new JPanel();
    
    public MinilogPanel(App app) {
    	mainPanel.setBackground(Color.WHITE);
    	mainPanel.setPreferredSize(new Dimension(1100, 100));
    	mainPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY));
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
}
