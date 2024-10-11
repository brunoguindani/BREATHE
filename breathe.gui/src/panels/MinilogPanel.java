package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import app.App;

public class MinilogPanel {
	
	/*
	 * SMALL LOG Always visibile to display most important messages 
	 */
	
    private JPanel mainPanel;
    private JScrollPane logScrollPane;
    static JTextArea logTextArea;
    private static JButton clearButton;

    public MinilogPanel(App app) {
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        logTextArea = new JTextArea(3, 20);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 11)); 
        logTextArea.setBackground(new Color(240, 240, 240));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);

        logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setPreferredSize(new Dimension(300, 80));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(logScrollPane, gbc);

        clearButton = new JButton("Clear Log");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });

        gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(clearButton, gbc);

        mainPanel.setPreferredSize(new Dimension(300, 210));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void append(String message) {
        logTextArea.append(message + "\n");
        SwingUtilities.invokeLater(() -> {
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    public void clear() {
        logTextArea.setText("");
    }
}