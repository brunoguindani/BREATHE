package panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import app.App_temp;

public class VentilatorsPanel{
	
    private JPanel mainPanel = new JPanel();
    
    public VentilatorsPanel(App_temp app) {
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	mainPanel.setPreferredSize(new Dimension(550, 650));
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
}
