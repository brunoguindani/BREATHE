package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import panels.*;
import utils.Pair;
import data.Action;
import data.Condition;
import data.Patient;
import data.Ventilator;
import interfaces.GuiCallback;

public class App extends JFrame implements GuiCallback {
	
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
    public OutputButtonPanel outputButtonPanel = new OutputButtonPanel(this);
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
    
    //create a simulationWorker
    private SimulationWorker s;
   
    public App() {
    	
    	Initializer.initilizeJNI();
		s = new SimulationWorker(this);
    	
    	//Main Panel Style
        setTitle("Breathe Simulation");
        setSize(1100, 700);
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
        leftTabbedPane.addTab("Output Settings", outputButtonPanel.getMainPanel());
        
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
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(rightTabbedPane, BorderLayout.CENTER);

        /*
         * Splitting View
         */     
        JPanel leftView = new JPanel();
        leftView.setLayout(new BoxLayout(leftView, BoxLayout.Y_AXIS));
        leftView.add(leftPanel);
        leftView.add(Box.createVerticalGlue());  
        leftView.add(controlPanel.getMainPanel());
        leftView.add(minilogPanel.getMainPanel());
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftView, rightPanel);
        splitPane.setDividerLocation(550); 
        splitPane.setDividerSize(5);

        add(splitPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST); 
        
    }
    /*
     * GUI PANEL to another GUI PANEL
     */
	public void addOutputButton(String outputName) {
		outputButtonPanel.addOutputButton(outputName);
	}
    
	public void updateOutputDisplay(List<JToggleButton> buttons) {
		outputPanel.updateItemDisplay(buttons);
	}
	
    public void addActiontoScenario(Action action, int seconds) {
    	scenarioPanel.addAction(action, seconds);
    }
    
	public void applyCondition(Condition condition) {
		conditionsPanel.addCondition(condition);
	}
	
	public void removeCondition(String title) {
		conditionsPanel.removeCondition(title);
	}
	
	public List<Condition> getActiveConditions() {
		return conditionsPanel.getActiveConditions();
	}
    public boolean loadPatientData(String selectedPatientFilePath) {
    	return patientPanel.loadPatientData(selectedPatientFilePath);
    }
    
	public void clearOutputDisplay() {
		outputPanel.clearOutputDisplay();
	}
	
	public void resetVentilatorsButton() {
		ventilatorsPanel.resetButton();
	}
	
	public String getPatientName() {
		return patientPanel.getName();
	}
	

    
    /*
     * SIMULATIONWORKER METHODS CALLS FROM GUI
     */
    public boolean startSimulation() {
    	Patient new_patient = patientPanel.generateInitialPatient(getActiveConditions());
    	if(new_patient != null) {
    		s.simulation(new_patient);	
        	conditionsPanel.enableButtons(false);
    		ventilatorsPanel.setEnableConnectButton(true);
        	patientPanel.enableComponents(false);
    		return true;
    	}
    	return false;
    }
    
    public boolean startFromFileSimulation(String file) {
    	if(file != null) {
    		s.simulationFromFile(file);	
        	conditionsPanel.enableButtons(false);
    		ventilatorsPanel.setEnableConnectButton(true);
        	patientPanel.enableComponents(false);
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public boolean startFromScenarioSimulation(String scenarioFile) {
    	if(scenarioFile != null) {
    		s.simulationFromScenario(scenarioFile);	
    		ventilatorsPanel.setEnableConnectButton(true);
        	patientPanel.enableComponents(false);
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public void stopSimulation() {
    	s.stopSimulation();	
    	conditionsPanel.enableButtons(true);
    	patientPanel.enableComponents(true);
    	actionsPanel.enableButtons(false);
    }
    
    public void connectVentilator() {
    	Ventilator v = ventilatorsPanel.getCurrentVentilator();
    	if(v != null)
    		s.connectVentilator(v);	
    }
    
    public void disconnectVentilator() {
    	Ventilator v = ventilatorsPanel.getCurrentVentilator();
    	if(v != null)
    		s.disconnectVentilator(v);
    }
    
	public void applyAction(Action action) {
		s.applyAction(action);
	}
    
    /*
     * GUI METHODS CALLBACKS FROM SIMULATIONWORKER
     */
	@Override
	public void stabilizationComplete(boolean enable) {
		controlPanel.enableControlStartButton(!enable);
		controlPanel.showControlStartButton(!enable);
		actionsPanel.enableButtons(enable);
		conditionsPanel.enableButtons(!enable);
	}
    
	@Override
	public void logStringData(String data) {
		logPanel.append(data);
	}
	
	public void minilogStringData(String data){
		minilogPanel.append(data);
	}
	
	@Override
	public void logItemDisplayData(String data, double x, double y) {
		outputPanel.addValueToItemDisplay(data, x, y);
	}

	@Override
	public void logPressureExternalVentilatorData(double pressure) {
		ventilatorsPanel.setEXTPressureLabel(pressure);
	}

	@Override
	public void logVolumeExternalVentilatorData(double volume) {
		ventilatorsPanel.setEXTVolumeLabel(volume);
	}
	
	@Override
	public void setInitialCondition(List<Condition> list) {	
    	conditionsPanel.setInitialCondition(list);
	}
	
	public void exportSimulation(String exportFilePath) {
		s.exportSimulation(exportFilePath);
	}
	public void createScenario(String patientFile, String scenarioName, ArrayList<Pair<Action, Integer>> actions) {
		s.createScenario(patientFile, scenarioName, actions);
	}

}
