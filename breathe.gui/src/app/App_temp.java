package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

import panels.*;

public class App_temp extends JFrame {
	
	/*
	 * Main Panel containing all the other Panels
	 */
    private static final long serialVersionUID = 1L;
    
    //All panels
    public ControlPanel controlPanel = new ControlPanel(this);
    public PatientPanel patientPanel = new PatientPanel(this);
    public ConditionsPanel conditionsPanel = new ConditionsPanel(this);
    public ActionsPanel actionsPanel = new ActionsPanel(this);
    public VentilatorsPanel ventilatorsPanel = new VentilatorsPanel(this);
    public OutputPanel outputPanel = new OutputPanel(this);
    public LogPanel logPanel = new LogPanel(this);
    public ScenarioPanel scenarioPanel = new ScenarioPanel(this);
    public MinilogPanel minilogPanel = new MinilogPanel(this);
    
    //Left and Right Panel
    public JTabbedPane leftTabbedPane;
    public JTabbedPane rightTabbedPane; 
    
    //Panel for switching between patient and condition during setUp
    private JPanel patientConditionPanel;
    private CardLayout cardLayout;
    
    public App_temp() {
    	
    	//Main Panel Style
        setTitle("Breathe Simulation");
        setSize(1250, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.LIGHT_GRAY);

        /*
         * Set up switch between Patient and Condition
         */
        JToggleButton patientRadioButton = new JToggleButton("Patient");
        JToggleButton conditionsRadioButton = new JToggleButton("Conditions");
        patientRadioButton.setPreferredSize(new Dimension(150, 30));
        conditionsRadioButton.setPreferredSize(new Dimension(150, 30));
        
        ButtonGroup group = new ButtonGroup();
        group.add(patientRadioButton);
        group.add(conditionsRadioButton);
        patientRadioButton.setSelected(true);  
        
        JPanel radioPanel = new JPanel();
        radioPanel.setBackground(Color.LIGHT_GRAY); 
        radioPanel.add(patientRadioButton);
        radioPanel.add(conditionsRadioButton);

        cardLayout = new CardLayout();
        patientConditionPanel = new JPanel(cardLayout);
        patientConditionPanel.add(patientPanel.getMainPanel(), "Patient");
        patientConditionPanel.add(conditionsPanel.getMainPanel(), "Condition");

        patientRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Patient"));
        conditionsRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Condition"));

        JPanel switchPanel = new JPanel(new BorderLayout());
        switchPanel.add(radioPanel, BorderLayout.NORTH);
        switchPanel.add(patientConditionPanel, BorderLayout.CENTER);

        /*
         * Left side of the main panel
         */
        leftTabbedPane = new JTabbedPane();
        leftTabbedPane.addTab("Patient", switchPanel);
        leftTabbedPane.addTab("Actions", actionsPanel.getMainPanel());
        leftTabbedPane.addTab("Ventilators", ventilatorsPanel.getMainPanel());
        leftTabbedPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.DARK_GRAY));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(leftTabbedPane, BorderLayout.CENTER);        
        
        /*
         * Right side of the main panel
         */
        rightTabbedPane = new JTabbedPane();
        rightTabbedPane.addTab("Output", outputPanel.getMainPanel());
        rightTabbedPane.addTab("Scenario", scenarioPanel.getMainPanel());
        rightTabbedPane.addTab("Log", logPanel.getMainPanel());
        rightTabbedPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.DARK_GRAY));
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(rightTabbedPane, BorderLayout.CENTER);

        /*
         * Splitting View
         */
        add(controlPanel.getMainPanel(), BorderLayout.WEST);  
        add(leftPanel, BorderLayout.CENTER);  
        add(rightPanel, BorderLayout.EAST); 
        add(minilogPanel.getMainPanel(), BorderLayout.SOUTH);
    }
}
