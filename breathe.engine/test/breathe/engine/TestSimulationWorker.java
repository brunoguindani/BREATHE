package breathe.engine;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
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

public class TestSimulationWorker {

	@Test
	public void testStandardSimulation() {
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
        Map<String, Double> anemiaParams1 = new HashMap<>();
        anemiaParams1.put("ReductionFactor", 0.5);
        Condition anemiaCondition = new Condition("Chronic Anemia", anemiaParams1);
        List<Condition> conditions = new ArrayList<>();
        conditions.add(anemiaCondition);
        Patient patient = new Patient("Testing", 'F', patientParameters, conditions);
        patient.getPatient();
        patient.getName();
        
        /*
         * SIMULATION
         */
        
        //Add condition
        Map<String, Double> anemiaParams = new HashMap<>();
        anemiaParams.put("ReductionFactor", 0.8); 

        Condition chronicAnemiaCondition = new Condition("Chronic Anemia", anemiaParams);

        patient.addCondition(chronicAnemiaCondition);
        /*
        sim.simulation(patient);
        while(!sim.isStable()) {
            try {
    			Thread.sleep(2000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        }
        sim.stopSimulation();
        
        sim.simulation(patient);
        while(!sim.isStable()) {
            try {
    			Thread.sleep(2000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        }
        sim.stopSimulation();  
        */
        
	}
	
	@Test
	public void testPatient() {
		Initializer.initilizeJNI();
		Patient p = new Patient("../breathe.engine/states/exported/Malannus.json");
	}
	
	@Test
	public void testSimulationFromFile() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/PC_AC.json");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.stopSimulation();
        sim.simulationFromFile("../breathe.engine/states/exported/PC_CMV.json");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.stopSimulation();
        sim.simulationFromFile("../breathe.engine/states/exported/VC_AC.json");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.stopSimulation();
        sim.simulationFromFile("../breathe.engine/states/exported/VC_CMV.json");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.stopSimulation();
        sim.simulationFromFile("../breathe.engine/states/exported/CPAP.json");
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.exportSimulation(" ");
        sim.exportSimulation("../breathe.engine/states/exported/ExportTest.json");
        sim.stopSimulation();
	}
	
	@Test
	public void testSimulationFromScenarioVent() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromScenario("../breathe.engine/scenario/exported/TestPC_AC.json");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        sim.stopSimulation();
	}
	
	@Test
	public void testErrorSimulationFromScenario() {
	    GuiCallback gui = new TempGUI();
	    SimulationWorker sim = new SimulationWorker(gui);

	    try {
	        sim.simulationFromScenario("../breathe.engine/scenario/exported/ErrorFile.json");
	    } catch (Exception e) {}
	}
	
	@Test
	public void testSimulationFromScenario_extVent() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromScenario("../breathe.engine/scenario/exported/TestEXT.json");
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testAction() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/EXT.json");

	   	Map<String, Double> actionParameters = new HashMap<>();
	    actionParameters.put("BronchitisSeverity", 0.5);
	    actionParameters.put("LeftLungEmphysemaSeverity", 0.6);
	    actionParameters.put("RightLungEmphysemaSeverity", 0.4); 
	
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
	    actionParameters.put("BronchitisSeverity", 0.5); 
	    actionParameters.put("LeftLungEmphysemaSeverity", 0.6); 
	    actionParameters.put("RightLungEmphysemaSeverity", 0.4); 
	    Action copdExacerbationAction = new Action("COPD Exacerbation", actionParameters);
	    ArrayList<Pair<Action, Integer>> actions = new ArrayList<>();
		Pair<Action, Integer> p = new Pair<>(copdExacerbationAction, 1);
		actions.add(p);
	    sim.createScenario("../breathe.engine/states/exported/Malannus.json","TestScenario", actions);
	    sim.createScenario("../breathe.engine/states/exported/Malannus.json","TestScenario", actions);
	}
	
	@Test
	public void testVentilator() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/Malannus.json");
        
        // VC - Volume Control
        Map<String, Number> vcParameters = new HashMap<>();
        vcParameters.put("AssistedMode", 1); 
        vcParameters.put("Flow", 60); 
        vcParameters.put("FractionInspiredOxygen", 0.5); 
        vcParameters.put("InspiratoryPeriod", 1.2);
        vcParameters.put("PositiveEndExpiratoryPressure", 5); 
        vcParameters.put("RespirationRate", 14); 
        vcParameters.put("TidalVolume", 500); 

        Ventilator ventilatorVC = new Ventilator(VentilationMode.VC, vcParameters);
        ventilatorVC.getParameters();
        sim.connectVentilator(ventilatorVC);
        sim.connectVentilator(ventilatorVC);
        sim.disconnectVentilator(ventilatorVC);

        // PC - Pressure Control
        Map<String, Number> pcParameters = new HashMap<>();
        pcParameters.put("AssistedMode", 1); 
        pcParameters.put("InspiratoryPressure", 15); 
        pcParameters.put("FractionInspiredOxygen", 0.5); 
        pcParameters.put("InspiratoryPeriod", 1.2);
        pcParameters.put("PositiveEndExpiratoryPressure", 5); 
        pcParameters.put("RespirationRate", 14); 
        pcParameters.put("Slope", 0.3);

        Ventilator ventilatorPC = new Ventilator(VentilationMode.PC, pcParameters);
        sim.connectVentilator(ventilatorPC);
        sim.connectVentilator(ventilatorPC);
        sim.disconnectVentilator(ventilatorPC);

        // CPAP - Continuous Positive Airway Pressure
        Map<String, Number> cpapParameters = new HashMap<>();
        cpapParameters.put("FractionInspiredOxygen", 0.5); 
        cpapParameters.put("DeltaPressureSupport", 8); 
        cpapParameters.put("PositiveEndExpiratoryPressure", 5); 
        cpapParameters.put("Slope", 0.3);

        Ventilator ventilatorCPAP = new Ventilator(VentilationMode.CPAP, cpapParameters);
        sim.connectVentilator(ventilatorCPAP);
        sim.connectVentilator(ventilatorCPAP);
        sim.disconnectVentilator(ventilatorCPAP);

        // EXTERNAL
        Ventilator externalVentilator = new Ventilator(VentilationMode.EXT);
        sim.connectVentilator(externalVentilator);
        TempClient client = new TempClient();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.changetoVolume();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.disconnectFromServer();
        sim.disconnectVentilator(externalVentilator);
	}
	
	@Test
	public void testErrorsPC() {
		GuiCallback gui = new TempGUI();
		SimulationWorker sim = new SimulationWorker(gui);
        sim.simulationFromFile("../breathe.engine/states/exported/Malannus.json");
        
        // PC - Pressure Control
        Map<String, Number> pcParameters = new HashMap<>();
        pcParameters.put("AssistedMode", 1); 
        pcParameters.put("InspiratoryPressure", 15); 
        pcParameters.put("FractionInspiredOxygen", 0.5); 
        pcParameters.put("InspiratoryPeriod", 1.2);
        pcParameters.put("PositiveEndExpiratoryPressure", 25); 
        pcParameters.put("RespirationRate", 14); 
        pcParameters.put("Slope", 0.3);

        Ventilator ventilatorPC = new Ventilator(VentilationMode.PC, pcParameters);
        sim.connectVentilator(ventilatorPC);
        sim.disconnectVentilator(ventilatorPC);  
	}
	
	@Test
	public void testAllActions() {
		// ARDS Exacerbation
        Map<String, Double> ardsParams = new HashMap<>();
        ardsParams.put("LeftLungSeverity", 0.7);
        ardsParams.put("RightLungSeverity", 0.8);
        Action ardsAction = new Action("ARDS Exacerbation", ardsParams);

        // Acute Stress
        Map<String, Double> stressParams = new HashMap<>();
        stressParams.put("Severity", 0.5);
        Action stressAction = new Action("Acute Stress", stressParams);

        // Airway Obstruction
        Map<String, Double> obstructionParams = new HashMap<>();
        obstructionParams.put("Severity", 0.6);
        Action obstructionAction = new Action("Airway Obstruction", obstructionParams);

        // Asthma Attack
        Map<String, Double> asthmaParams = new HashMap<>();
        asthmaParams.put("Severity", 0.4);
        Action asthmaAction = new Action("Asthma Attack", asthmaParams);

        // Brain Injury
        Map<String, Double> brainInjuryParams = new HashMap<>();
        brainInjuryParams.put("Severity", 0.3);
        Action brainInjuryAction = new Action("Brain Injury", brainInjuryParams);

        // Bronchoconstriction
        Map<String, Double> bronchoconstrictionParams = new HashMap<>();
        bronchoconstrictionParams.put("Severity", 0.2);
        Action bronchoconstrictionAction = new Action("Bronchoconstriction", bronchoconstrictionParams);

        // COPD Exacerbation
        Map<String, Double> copdParams = new HashMap<>();
        copdParams.put("BronchitisSeverity", 0.5);
        copdParams.put("LeftLungEmphysemaSeverity", 0.6);
        copdParams.put("RightLungEmphysemaSeverity", 0.4);
        Action copdAction = new Action("COPD Exacerbation", copdParams);

        // Dyspnea
        Map<String, Double> dyspneaParams = new HashMap<>();
        dyspneaParams.put("RespirationRateSeverity", 0.7);
        Action dyspneaAction = new Action("Dyspnea", dyspneaParams);

        // Exercise
        Map<String, Double> exerciseParams = new HashMap<>();
        exerciseParams.put("Intensity", 0.8);
        Action exerciseAction = new Action("Exercise", exerciseParams);

        // Pericardial Effusion
        Map<String, Double> effusionParams = new HashMap<>();
        effusionParams.put("EffusionRate ml/s", 1.5);
        Action effusionAction = new Action("Pericardial Effusion", effusionParams);

        // Pneumonia Exacerbation
        Map<String, Double> pneumoniaParams = new HashMap<>();
        pneumoniaParams.put("LeftLungSeverity", 0.6);
        pneumoniaParams.put("RightLungSeverity", 0.7);
        Action pneumoniaAction = new Action("Pneumonia Exacerbation", pneumoniaParams);

        // Pulmonary Shunt Exacerbation
        Map<String, Double> shuntParams = new HashMap<>();
        shuntParams.put("Severity", 0.4);
        Action shuntAction = new Action("Pulmonary Shunt Exacerbation", shuntParams);

        // Respiratory Fatigue
        Map<String, Double> fatigueParams = new HashMap<>();
        fatigueParams.put("Severity", 0.5);
        Action fatigueAction = new Action("Respiratory Fatigue", fatigueParams);

        // Urinate
        Action urinateAction = new Action("Urinate", new HashMap<>());

        // Ventilator Leak
        Map<String, Double> leakParams = new HashMap<>();
        leakParams.put("Severity", 0.3);
        Action leakAction = new Action("Ventilator Leak", leakParams);
        
        leakAction.getName();
        leakAction.getParameters();
        leakAction.toString();
	}
	
	@Test
	public void testAllConditions() {
        // Chronic Anemia
        Map<String, Double> anemiaParams = new HashMap<>();
        anemiaParams.put("ReductionFactor", 0.5);
        Condition anemiaCondition = new Condition("Chronic Anemia", anemiaParams);

        // ARDS
        Map<String, Double> ardsParams = new HashMap<>();
        ardsParams.put("LeftLungSeverity", 0.7);
        ardsParams.put("RightLungSeverity", 0.8);
        Condition ardsCondition = new Condition("ARDS", ardsParams);

        // COPD
        Map<String, Double> copdParams = new HashMap<>();
        copdParams.put("BronchitisSeverity", 0.6);
        copdParams.put("LeftLungEmphysemaSeverity", 0.5);
        copdParams.put("RightLungEmphysemaSeverity", 0.4);
        Condition copdCondition = new Condition("COPD", copdParams);

        // Pericardial Effusion
        Map<String, Double> effusionParams = new HashMap<>();
        effusionParams.put("AccumulatedVolume", 200.0);
        Condition effusionCondition = new Condition("Pericardial Effusion", effusionParams);

        // Renal Stenosis
        Map<String, Double> stenosisParams = new HashMap<>();
        stenosisParams.put("LeftKidneySeverity", 0.6);
        stenosisParams.put("RightKidneySeverity", 0.7);
        Condition stenosisCondition = new Condition("Renal Stenosis", stenosisParams);

        // Chronic Ventricular Systolic Dysfunction
        Condition vsdCondition = new Condition("Chronic Ventricular Systolic Disfunction", new HashMap<>());

        // Pneumonia
        Map<String, Double> pneumoniaParams = new HashMap<>();
        pneumoniaParams.put("LeftLungSeverity", 0.4);
        pneumoniaParams.put("RightLungSeverity", 0.5);
        Condition pneumoniaCondition = new Condition("Pneumonia", pneumoniaParams);

        // Pulmonary Fibrosis
        Map<String, Double> fibrosisParams = new HashMap<>();
        fibrosisParams.put("Severity", 0.7);
        Condition fibrosisCondition = new Condition("Pulmonary Fibrosis", fibrosisParams);

        // Pulmonary Shunt
        Map<String, Double> shuntParams = new HashMap<>();
        shuntParams.put("Severity", 0.6);
        Condition shuntCondition = new Condition("Pulmonary Shunt", shuntParams);
        
        shuntCondition.getName();
        shuntCondition.getParameters();
        shuntCondition.toString();
	}
	
}
