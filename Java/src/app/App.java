package app;

import javax.swing.*;

import panels.ConditionPanel;
import panels.ActionPanel;
import panels.ChartsPanel;
import panels.LogPanel;
import panels.MiniLogPanel;
import panels.PatientPanel;
import panels.ScenarioPanel;
import panels.VentilatorPanel;
import utils.LineChart;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App extends JFrame {
	
	/*
	 * Main Panel containing all the other Panels
	 */
	
    private static final long serialVersionUID = 1L;

    //tab to select other panels
    private JTabbedPane switchTabbedPane;

    //all available panels
    public PatientPanel patient = new PatientPanel(this);
    public VentilatorPanel ventilator = new VentilatorPanel();
    public ActionPanel action = new ActionPanel();
    public ConditionPanel condition = new ConditionPanel();
    public ScenarioPanel scenario = new ScenarioPanel();
    public LogPanel log = new LogPanel();
    public MiniLogPanel mlog = new MiniLogPanel();
    public ChartsPanel charts = new ChartsPanel();
    public LineChart[] chartPanels;

    // Panel for switching between patient and condition
    private JPanel patientConditionPanel;
    private CardLayout cardLayout;
    private JRadioButton patientRadioButton;
    private JRadioButton conditionRadioButton;

    public App() {
    	
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

        //retrieving panels
        JPanel patientPanel = patient.getPatientPanel();
        JScrollPane scrollConditionPane = condition.getConditionScrollPane();
        JPanel ventilatorPanel = ventilator.getVentilatorPanel();
        JScrollPane scrollActionPane = action.getActionScrollPane();
        JPanel scenarioPanel = scenario.getScenarioPanel();
        JScrollPane scrollLogPane = log.getLogScrollPane();
        JPanel miniLogPanel = mlog.getMiniLogPanel();

        // Creare i RadioButton per cambiare tra paziente e condizione
        patientRadioButton = new JRadioButton("Patient");
        conditionRadioButton = new JRadioButton("Condition");
        ButtonGroup group = new ButtonGroup();
        group.add(patientRadioButton);
        group.add(conditionRadioButton);
        patientRadioButton.setSelected(true);  // Di default, il pannello del paziente è selezionato
        
        // Aggiungere i RadioButton a un pannello
        JPanel radioPanel = new JPanel();
        radioPanel.setBackground(Color.LIGHT_GRAY); // Cambia il colore di sfondo
        radioPanel.add(patientRadioButton);
        radioPanel.add(conditionRadioButton);

        // Creare il pannello con CardLayout per contenere entrambi i pannelli
        cardLayout = new CardLayout();
        patientConditionPanel = new JPanel(cardLayout);
        patientConditionPanel.add(patientPanel, "Patient");
        patientConditionPanel.add(scrollConditionPane, "Condition");

        // Aggiungere un ActionListener ai RadioButton per cambiare pannello
        patientRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Patient"));
        conditionRadioButton.addActionListener(e -> cardLayout.show(patientConditionPanel, "Condition"));

        // Creare un pannello per includere i RadioButton e il pannello con CardLayout
        JPanel switchPanel = new JPanel(new BorderLayout());
        switchPanel.add(radioPanel, BorderLayout.NORTH);
        switchPanel.add(patientConditionPanel, BorderLayout.CENTER);

        //adding to the switch
        switchTabbedPane = new JTabbedPane();
        switchTabbedPane.setBackground(Color.LIGHT_GRAY);
        switchTabbedPane.addTab("Patient", switchPanel);
        switchTabbedPane.addTab("Action", scrollActionPane);
        switchTabbedPane.addTab("Ventilator", ventilatorPanel);
        switchTabbedPane.addTab("Scenario", scenarioPanel);
        switchTabbedPane.addTab("Log", scrollLogPane);
        switchTabbedPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.DARK_GRAY));

        //panel for the charts
        JPanel chartPanel = charts.getChartPanel();
        chartPanels = charts.getChartsPanel();

        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BorderLayout());
        centralPanel.add(switchTabbedPane, BorderLayout.CENTER);  
        centralPanel.add(miniLogPanel, BorderLayout.SOUTH);        

        //splitting view
        add(centralPanel, BorderLayout.CENTER);  
        add(chartPanel, BorderLayout.EAST);      
    }
}
