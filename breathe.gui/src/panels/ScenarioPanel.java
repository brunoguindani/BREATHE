package panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import app.App_temp;

public class ScenarioPanel{
	
    private JPanel mainPanel = new JPanel();
    
    public ScenarioPanel(App_temp app) {
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	mainPanel.setPreferredSize(new Dimension(550, 700));
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
}
