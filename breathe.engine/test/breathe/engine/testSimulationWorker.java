package breathe.engine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import app.Initializer;
import app.SimulationWorker;
import data.Action;
import data.Condition;
import data.Patient;
import data.Ventilator;
import interfaces.GuiCallback;
import utils.Pair;
import utils.VentilationMode;

public class testSimulationWorker {

	@Test
	public void testSimulation() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
		
		/*
		 * PATIENT
		 */
        Map<String, Double> patientParameters = new HashMap<>();
        patientParameters.put("Age", 30.0); 
        patientParameters.put("BodyFatFraction", 0.18); 
        patientParameters.put("HeartRateBaseline", 70.0); 
        patientParameters.put("DiastolicArterialPressureBaseline", 80.0); 
        patientParameters.put("SystolicArterialPressureBaseline", 120.0); 
        patientParameters.put("RespirationRateBaseline", 16.0); 
        patientParameters.put("BasalMetabolicRate", 1800.0); 
        patientParameters.put("Weight", 75.0); 
        patientParameters.put("Height", 175.0); 
        List<Condition> conditions = new ArrayList<>();
        Patient patient = new Patient("Gionatha", 'F', patientParameters, conditions);
        
        /*
         * SIMULATION
         */
        sim.simulation(patient);
        
	}
	
	@Test
	public void testFromFile() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/Malannus.json");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.exportSimulation(" ");
        sim.exportSimulation("../breathe.engine/states/exported/MalannusExported.json");
        sim.stopSimulation();
	}
	
	@Test
	public void testAction() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/Malannus.json");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	   	Map<String, Double> actionParameters = new HashMap<>();
	    actionParameters.put("BronchitisSeverity", 0.5); // Severità della bronchite (0-1)
	    actionParameters.put("LeftLungEmphysemaSeverity", 0.6); // Severità dell'enfisema polmonare (polmone sinistro) (0-1)
	    actionParameters.put("RightLungEmphysemaSeverity", 0.4); // Severità dell'enfisema polmonare (polmone destro) (0-1)
	
	    Action copdExacerbationAction = new Action("COPD Exacerbation", actionParameters);
	    sim.applyAction(copdExacerbationAction);
        sim.stopSimulation();
	}
	
	@Test
	public void testCreateScenario() {
		Initializer i = new Initializer();
		Initializer.initilizeJNI();
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
	   	Map<String, Double> actionParameters = new HashMap<>();
	    actionParameters.put("BronchitisSeverity", 0.5); // Severità della bronchite (0-1)
	    actionParameters.put("LeftLungEmphysemaSeverity", 0.6); // Severità dell'enfisema polmonare (polmone sinistro) (0-1)
	    actionParameters.put("RightLungEmphysemaSeverity", 0.4); // Severità dell'enfisema polmonare (polmone destro) (0-1)
	    Action copdExacerbationAction = new Action("COPD Exacerbation", actionParameters);
	    ArrayList<Pair<Action, Integer>> actions = new ArrayList<>();
		Pair<Action, Integer> p = new Pair<>(copdExacerbationAction, 1);
		actions.add(p);
	    sim.createScenario("../breathe.engine/states/exported/Malannus.json","Giornatha", actions);
	    sim.createScenario("../breathe.engine/states/exported/Malannus.json","Giornatha", actions);
	}
	
	@Test
	public void testVentilator() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/Malannus.json");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        Map<String, Number> ventilatorParameters = new HashMap<>();
        ventilatorParameters.put("AssistedMode", 1); // 0 = AssistedControl, 1 = ContinuousMandatoryVentilation
        ventilatorParameters.put("Flow", 60); // Flusso in L/min
        ventilatorParameters.put("FractionInspiredOxygen", 0.5); // 50% ossigeno
        ventilatorParameters.put("InspiratoryPeriod", 1.2); // Durata inspiratoria in secondi
        ventilatorParameters.put("PositiveEndExpiratoryPressure", 5); // PEEP in cmH2O
        ventilatorParameters.put("RespirationRate", 14); // Frequenza respiratoria in respiri al minuto
        ventilatorParameters.put("TidalVolume", 500); // Volume corrente in mL

        // Creazione del ventilatore
        Ventilator ventilator = new Ventilator(VentilationMode.VC, ventilatorParameters);
        sim.connectVentilator(ventilator);
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.disconnectVentilator(ventilator);
	}
	
	@Test
	public void testFromScenario() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromScenario("../breathe.engine/scenario/exported/Malannus test.json");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
