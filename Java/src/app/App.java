package app;

import javax.swing.*;

import panels.ConditionPanel;
import panels.ActionPanel;
import panels.ChartsPanel;
import panels.LogPanel;
import panels.MiniLogPanel;
import panels.PatientPanel;
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
    public LogPanel log = new LogPanel();
    public MiniLogPanel mlog = new MiniLogPanel();
    public ChartsPanel charts = new ChartsPanel();
    public LineChart[] chartPanels;
   

    public App() {
    	
    	//main panel
        setTitle("Pulse Simulation");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        JScrollPane scrollLogPane = log.getLogScrollPane();
        JPanel miniLogPanel = mlog.getMiniLogPanel();

        //adding to the switch
        switchTabbedPane = new JTabbedPane();
        switchTabbedPane.setBackground(Color.LIGHT_GRAY);
        switchTabbedPane.addTab("Patient", patientPanel);
        switchTabbedPane.addTab("Condition", scrollConditionPane);
        switchTabbedPane.addTab("Action", scrollActionPane);
        switchTabbedPane.addTab("Ventilator", ventilatorPanel);
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
