package tests;

import javax.swing.*;
import java.awt.*;

public class AppTest extends JFrame {

	private JTabbedPane switchTabbedPane;
	
	public PatientPanelTest patient = new PatientPanelTest(this);
	public VentilatorPanelTest ventilator = new VentilatorPanelTest();
	public ActionPanelTest action = new ActionPanelTest();
	public LogPanelTest log = new LogPanelTest();
	public ChartsPanelTest charts = new ChartsPanelTest();
	public LineChartPanelTest[] chartPanels;
	

    
    public static JButton connectButton = new JButton("Connect");
   

    public AppTest() {
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
        JScrollPane scrollChartPane = charts.getChartsScrollPane();
        chartPanels = charts.getChartPanel();      
       
        // Aggiungi i pannelli al layout principale
        add(switchTabbedPane, BorderLayout.CENTER);  // Pannello input al centro
        add(scrollChartPane, BorderLayout.EAST);   // Pannello del grafico a destra
    }
}
