package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MiniLogPanel {
    private JPanel miniLogPanel;
    private JScrollPane logScrollPane;
    static JTextArea logTextArea;
    private static JButton clearButton;
    
    public MiniLogPanel() {
        miniLogPanel = new JPanel();
        miniLogPanel.setBackground(Color.LIGHT_GRAY);  
        miniLogPanel.setLayout(new BorderLayout());
        
        logTextArea = new JTextArea(3, 20);  
        logTextArea.setEditable(false);
        
        logScrollPane = new JScrollPane(logTextArea);
        miniLogPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // Create and configure the clear button
        clearButton = new JButton("Clear Log");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        
        // Add the button to the bottom of the panel
        miniLogPanel.add(clearButton, BorderLayout.SOUTH);
        
        miniLogPanel.setPreferredSize(new Dimension(1000, 100)); 
    }
    
    public JPanel getMiniLogPanel() {
        return miniLogPanel;
    }
    
    public static void append(String message) {
        logTextArea.append(message + "\n");
        SwingUtilities.invokeLater(() -> {
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }
    
    public void clear() {
        logTextArea.setText("");
    }
    
}