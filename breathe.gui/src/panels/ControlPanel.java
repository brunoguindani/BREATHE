package panels;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import app.App_temp;

public class ControlPanel {

    private JPanel mainPanel = new JPanel(); 

    public ControlPanel(App_temp app) {
    	//set up main panel
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setPreferredSize(new Dimension(150, 700));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        //set up button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.setPreferredSize(new Dimension(150, 700));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        Dimension buttonSize = new Dimension(130, 40); 

        //START FROM FILE BUTTON
        JButton startFromFileButton = new JButton("Start From File");
        startFromFileButton.setToolTipText("Start Simulation from Patient File");
        startFromFileButton.setPreferredSize(buttonSize);
        startFromFileButton.setMaximumSize(buttonSize);
        startFromFileButton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        startFromFileButton.setBackground(new Color(0, 122, 255));
        startFromFileButton.setForeground(Color.WHITE);
        startFromFileButton.setFocusPainted(false);

        //START FROM SCENARIO BUTTON
        JButton startFromScenarioButton = new JButton("Start From Scenario");
        startFromScenarioButton.setToolTipText("Start a Scenario");
        startFromScenarioButton.setPreferredSize(buttonSize);
        startFromScenarioButton.setMaximumSize(buttonSize);
        startFromScenarioButton.setAlignmentX(JButton.CENTER_ALIGNMENT); 
        startFromScenarioButton.setBackground(new Color(0, 122, 255));
        startFromScenarioButton.setForeground(Color.WHITE);
        startFromScenarioButton.setFocusPainted(false);

        //START SIMULATION BUTTON
        JButton startButton = new JButton("Start Simulation");
        startButton.setToolTipText("Start new Simulation");
        startButton.setPreferredSize(buttonSize);
        startButton.setMaximumSize(buttonSize);
        startButton.setAlignmentX(JButton.CENTER_ALIGNMENT); 
        startButton.setBackground(new Color(0, 122, 255));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        
        //STOP SIMULATION BUTTON
        JButton stopButton = new JButton("Stop Simulation");
        stopButton.setToolTipText("Stop Simulation");
        stopButton.setPreferredSize(buttonSize);
        stopButton.setMaximumSize(buttonSize);
        stopButton.setAlignmentX(JButton.CENTER_ALIGNMENT); 
        stopButton.setEnabled(false);
        stopButton.setBackground(new Color(255, 59, 48));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);

        //EXPORT BUTTON
        JButton exportButton = new JButton("Export Simulation");
        exportButton.setToolTipText("Export current patient state");
        exportButton.setPreferredSize(buttonSize);
        exportButton.setMaximumSize(buttonSize);
        exportButton.setAlignmentX(JButton.CENTER_ALIGNMENT); 
        exportButton.setEnabled(false);
        exportButton.setBackground(new Color(0, 128, 0));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);

        //Add buttons to buttonPanel
        buttonPanel.add(Box.createVerticalGlue()); 
        buttonPanel.add(startFromScenarioButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));  
        buttonPanel.add(startFromFileButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(stopButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exportButton);
        buttonPanel.add(Box.createVerticalGlue()); 
        
        //A few items for style
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(220,220,220));
        headerPanel.setPreferredSize(new Dimension(150, 33));  
        
        JPanel line = new JPanel();
        line.setBackground(new Color(185,206,225));
        line.setPreferredSize(new Dimension(150, 2));
        line.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.darkGray));
        
        //Add everything to mainPanel
        mainPanel.add(headerPanel);
        mainPanel.add(line);
        mainPanel.add(buttonPanel);
    }

    //method to return panel
    public JPanel getMainPanel() {
        return mainPanel;
    }
}
