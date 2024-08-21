package app;

import javax.swing.*;

import panels.ActionPanel;
import panels.ChartsPanel;
import panels.LogPanel;
import panels.PatientPanel;
import panels.VentilatorPanel;
import utils.LineChart;

import java.awt.*;

public class App extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTabbedPane switchTabbedPane;
	
	public PatientPanel patient = new PatientPanel(this);
	public VentilatorPanel ventilator = new VentilatorPanel();
	public ActionPanel action = new ActionPanel();
	public LogPanel log = new LogPanel();
	public ChartsPanel charts = new ChartsPanel();
	public LineChart[] chartPanels;
	

    
    public static JButton connectButton = new JButton("Connect");
   

    public App() {
        setTitle("Pulse Simulation");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Imposta il colore di sfondo della finestra principale
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Pannello per gli input (centro)
        JPanel patientPanel = patient.getPatientPanel();
        JPanel ventilatorPanel = ventilator.getVentilatorPanel();	
        JPanel actionPanel = action.getActionPanel();
        JScrollPane scrollLogPane = log.getLogScrollPane();
        
        switchTabbedPane = new JTabbedPane();
        switchTabbedPane.setBackground(Color.LIGHT_GRAY);
        switchTabbedPane.addTab("Patient", patientPanel);
        switchTabbedPane.addTab("Action", actionPanel);
        switchTabbedPane.addTab("Ventilator", ventilatorPanel);
        switchTabbedPane.addTab("Log", scrollLogPane);
        
        // Pannello per il grafico (destra)
        JPanel chartPanel = charts.getChartPanel();
        chartPanels = charts.getChartsPanel();      
       
        // Aggiungi i pannelli al layout principale
        add(switchTabbedPane, BorderLayout.CENTER);  // Pannello input al centro
        add(chartPanel, BorderLayout.EAST);   // Pannello del grafico a destra
    }
}
