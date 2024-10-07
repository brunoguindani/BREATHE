package toRemove;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class LogPanel {
	
	/*
	 * Panel to read log
	 */
	
    private JTextArea resultArea;
    private JScrollPane scrollLogPane;
    
	public LogPanel() {
		// Text area that updates with every new data retrieved from the engine
	    resultArea = new JTextArea();
	    resultArea.setEditable(false);
	    resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
	    resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    resultArea.setBackground(Color.WHITE);
	    scrollLogPane = new JScrollPane(resultArea);
	    scrollLogPane.setPreferredSize(new Dimension(450, 0));
	    scrollLogPane.setBackground(Color.LIGHT_GRAY);
	}
	
	 public JScrollPane getLogScrollPane() {
	    	return scrollLogPane;
	    }

	 public JTextArea getResultArea() {
	        return resultArea;
	    }
}
