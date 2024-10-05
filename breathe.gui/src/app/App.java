package app;

import javax.swing.*;

import com.kitware.pulse.utilities.JNIBridge;

import panels.ConditionsPanel;
import panels.ActionsPanel;
import panels.ChartsPanel;
import panels.LogPanel;
import panels.MinilogPanel;
import panels.PatientPanel;
import panels.ScenarioPanel;
import panels.VentilatorPanel;
import utils.ItemDisplay;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class App extends JFrame {
	
	/*
	 * Main Panel containing all the other Panels
	 */
	
    private static final long serialVersionUID = 1L;

    //tab to select other panels
    public JTabbedPane leftTabbedPane;
    public JTabbedPane rightTabbedPane; 

    //all available panels
    public PatientPanel patient = new PatientPanel(this);
    public VentilatorPanel ventilator = new VentilatorPanel();
    public ActionsPanel action = new ActionsPanel(this);
    public ConditionsPanel condition = new ConditionsPanel();
    public ScenarioPanel scenario = new ScenarioPanel();
    public LogPanel log = new LogPanel();
    public MinilogPanel mlog = new MinilogPanel();
    public ChartsPanel charts = new ChartsPanel();
    public HashMap<String, ItemDisplay> chartPanels;

    // Panel for switching between patient and condition
    private JPanel patientConditionPanel;
    private CardLayout cardLayout;
    
    public App() {
        JNIBridge.initialize();
    	//main panel
        setTitle("Pulse Simulation");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        getContentPane().setBackground(Color.LIGHT_GRAY);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SimulationWorker.requestStop();
            }
        });

        //retrieving all panels
        JPanel patientPanel = patient.getPatientPanel();
        JScrollPane scrollConditionPane = condition.getConditionScrollPane();
        JPanel ventilatorPanel = ventilator.getVentilatorPanel();
        JScrollPane scrollActionPane = action.getActionScrollPane();
        JPanel scenarioPanel = scenario.getScenarioPanel();
        JScrollPane scrollLogPane = log.getLogScrollPane();
        JPanel miniLogPanel = mlog.getMiniLogPanel();
        miniLogPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.DARK_GRAY));
        JPanel chartPanel = charts.getChartPanel(); 
        chartPanels = charts.getChartsPanel();
        
        //switch between patient and condition
        JToggleButton patientRadioButton = new JToggleButton("Patient");
        JToggleButton conditionRadioButton = new JToggleButton("Condition");
        Dimension buttonSize = new Dimension(150, 30); 
        patientRadioButton.setPreferredSize(buttonSize);
        conditionRadioButton.setPreferredSize(buttonSize);
        ButtonGroup group = new ButtonGroup();
        
        group.add(patientRadioButton);
        group.add(conditionRadioButton);
        patientRadioButton.setSelected(true);  
        
        JPanel radioPanel = new JPanel();
        radioPanel.setBackground(Color.LIGHT_GRAY); 
        radioPanel.add(patientRadioButton);
        radioPanel.add(conditionRadioButton);

        cardLayout = new CardLayout();
        patientConditionPanel = new JPanel(cardLayout);
        patientConditionPanel.add(patientPanel, "Patient");
        patientConditionPanel.add(scrollConditionPane, "Condition");

        patientRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Patient"));
        conditionRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Condition"));

        JPanel switchPanel = new JPanel(new BorderLayout());
        switchPanel.add(radioPanel, BorderLayout.NORTH);
        switchPanel.add(patientConditionPanel, BorderLayout.CENTER);
        
        //Panel on the right with outputs
        rightTabbedPane = new JTabbedPane();
        rightTabbedPane.addTab("Charts", chartPanel);
        rightTabbedPane.addTab("Scenario", scenarioPanel);
        rightTabbedPane.addTab("Log", scrollLogPane);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(rightTabbedPane, BorderLayout.CENTER);

        //Panels on the left with inputs
        leftTabbedPane = new JTabbedPane();
        leftTabbedPane.addTab("Patient", switchPanel);
        leftTabbedPane.addTab("Action", scrollActionPane);
        leftTabbedPane.addTab("Ventilator", ventilatorPanel);
        leftTabbedPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.DARK_GRAY));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(leftTabbedPane, BorderLayout.CENTER);  
        leftPanel.add(miniLogPanel, BorderLayout.SOUTH);          

        //splitting view
        add(leftPanel, BorderLayout.CENTER);  
        add(rightPanel, BorderLayout.EAST);  // Right TabbedPane with charts and scenario
    }
}
