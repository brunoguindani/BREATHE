package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import app.App_temp;

public class LogPanel{
	
    private JPanel mainPanel = new JPanel();
    JTextArea resultArea = new JTextArea();
    
    public LogPanel(App_temp app) {
    	
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	mainPanel.setPreferredSize(new Dimension(550, 500));
    	
		// Text area that updates with every new data retrieved from the engine
	    resultArea.setEditable(false);
	    resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
	    resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    resultArea.setBackground(Color.WHITE);
	    
	    JScrollPane scrollLogPane = new JScrollPane(resultArea);
	    scrollLogPane.setPreferredSize(new Dimension(550, 500));
	    scrollLogPane.setBackground(Color.LIGHT_GRAY);
	    
	    mainPanel.add(scrollLogPane);
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
    
    public void append(String log) {
    	resultArea.append(log);
    	resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
}
