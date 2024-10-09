package app;

import data.*;
import interfaces.GuiCallback;
import server.ZeroServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.properties.CommonUnits.*;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.cdm.patient.actions.SEMechanicalVentilation;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.actions.SEAdvanceTime;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;
import com.kitware.pulse.cdm.properties.SEScalarTime;
import com.kitware.pulse.cdm.scenario.SEScenario;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorContinuousPositiveAirwayPressure;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorPressureControl;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;

public class SimulationWorker extends SwingWorker<Void, String>{
	
    public PulseEngine pe;
    private String initializeMode;
    
    private SEDataRequestManager dataRequests;
    private String[] requestList;
    
    private GuiCallback gui;
    
    private boolean stopRequest = false;
    
    SEScalarTime stime = new SEScalarTime(0, TimeUnit.s);

    SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
    
    //Data for scenario
    String scenarioFilePath = null;
    String patientFilePath = null;
    
    //Data for external ventilator
    ZeroServer zmqServer;
    SEMechanicalVentilation ventilator_ext = new SEMechanicalVentilation();
    private boolean ext_running = false;
    
    
    
    /*
     * Costruttore
     */
    public SimulationWorker(GuiCallback guiCallback) { 
    	this.gui = guiCallback;
    }
    
    //Public method (for inputs)
    public void simulation(Patient patient) {
    	initializeMode = "standard";
        pe = new PulseEngine();
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
        patient_configuration = patient.getPatientConfiguration();
        
        for(Condition any : patient.getConditions())
        {
          patient_configuration.getConditions().add(any.getCondition());
        }
        
    	this.execute();
    }
    
    public void simulationFromFile(String file) {
    	initializeMode = "file";
    	pe = new PulseEngine();
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
    	
		pe.serializeFromFile(file, dataRequests);

		//check that patient has loaded
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
		List<SECondition> list = new ArrayList<>();
        pe.getConditions(list);
        //gui.setInitialConditions(list);
		
    	this.execute();
    }
    
    public void simulationFromScenario(String scenarioFilePath) {
    	this.scenarioFilePath = scenarioFilePath;
    	
    	initializeMode = "scenario";
    	pe = new PulseEngine();
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
    	
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode_scenario = null;
		try {
			rootNode_scenario = mapper.readTree(new File(scenarioFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        patientFilePath = rootNode_scenario.path("EngineStateFile").asText();
        
		pe.serializeFromFile(patientFilePath, dataRequests);

		//check that patient has loaded
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
    	this.execute();
    }
    
    
    public void simulationfromScenario() {
    	initializeMode = "scenario";
    }

    public void stopSimulation() {
    	stopRequest = true;
    }
  
	
	@Override
	protected Void doInBackground() throws Exception {
        
		if(initializeMode.equals("standard")) {
	        pe.initializeEngine(patient_configuration, dataRequests);    
	        exportInitialPatient(patient_configuration.getPatient());	
	        
	        //Advice for stabilaztion completed
			gui.stabilizationComplete(false);
		}else if(initializeMode.equals("file")) {
			//boh se si vuole spostare qui
		}else if(initializeMode.equals("scenario")) {
			run_scenario();
		}

		
		
		while (!stopRequest) {
        	
            if (!pe.advanceTime(stime)) {
                publish("\nSomething bad happened");
                return null;
            }
            

            //Send data (to gui and to ext ventilator)
            if(ext_running) {
            	manage_ext();
            	zmqServer.setSimulationData(sendData());
            }
            else
            	sendData();

            stime.setValue(0.02, TimeUnit.s);
        }
		
        pe.clear();
        pe.cleanUp();
        publish("Simulation Complete\n");
        return null;
	}
	
    private void setDataRequests(SEDataRequestManager dataRequests) {
    	//list of data requests.
    	//SimTime is mandatory, since it is always retrieved
    	//order is important
    	String[] requestList = {"SimTime",
				"HeartRate",
				"TotalLungVolume",
				"RespirationRate",
				"Lead3ElectricPotential",
				"CarbonDioxide",
				"ArterialPressure",
				"AirwayPressure"
				};
    	
    	this.requestList = requestList;
    	
    	//create the requests
    	dataRequests.createPhysiologyDataRequest(requestList[1], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[2], VolumeUnit.mL);
        dataRequests.createPhysiologyDataRequest(requestList[3], FrequencyUnit.Per_min);
        dataRequests.createECGDataRequest(requestList[4], ElectricPotentialUnit.mV);
        dataRequests.createGasCompartmentDataRequest("Carina", "CarbonDioxide", "PartialPressure", PressureUnit.mmHg);
        dataRequests.createPhysiologyDataRequest(requestList[6], PressureUnit.mmHg);
        dataRequests.createPhysiologyDataRequest(requestList[7], PressureUnit.mmHg);
    }
    
    
    private void run_scenario() {
    	
    	//Load scenario
    	SEScenario sce = new SEScenario();
		try {
			sce.readFile(scenarioFilePath);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(sce.hasEngineState()) {
			
			if(!pe.serializeFromFile(patientFilePath, dataRequests));
		} else if(sce.hasPatientConfiguration()) {
			if(!pe.initializeEngine(sce.getPatientConfiguration(), dataRequests));
		}
		
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
		//TAKE CONDITION
		/*
		pe.getConditions(app.condition.getActiveConditions());
		for(SECondition any : app.condition.getActiveConditions())
        {
        	app.condition.setInitialConditions(any);
        }*/
		
		//Advice for stabilaztion completed
		gui.stabilizationComplete(false);
		
		for (SEAction a : sce.getActions()) {
			
		    if (a instanceof SEAdvanceTime) {
		        
		        for(int i = 0; i<50; i++){		  
		            if (!pe.advanceTime(stime)) {
		            	//ERRORR!
		                return;
		            }

		            //Send data (to gui and to ext ventilator)
		            if(ext_running) {
		            	manage_ext();
		            	zmqServer.setSimulationData(sendData());
		            }
		            else
		            	sendData();

		            stime.setValue(0.02, TimeUnit.s);	
		        }

		    } else {
		        pe.processAction(a);
		    }
		    if(stopRequest)
		    	return;
		}
	}
    
    private void exportInitialPatient(SEPatient patient) {
        String basePath = "./states/";
        String baseFileName = patient.getName() + "@0s.json";
        String filePath = basePath + baseFileName;

        int counter = 1;
        while (new File(filePath).exists()) {
            filePath = basePath + patient.getName() + counter + "@0s.json";
            counter++;
        }
        pe.serializeToFile(filePath);
    }
    
    
    private ArrayList<String> sendData() {
    	ArrayList<String> data = new ArrayList<String>();
    	
    	//print conditions
        pe.getConditions(patient_configuration.getConditions());
        for(SECondition any : patient_configuration.getConditions())
        {
            gui.logStringData(any.toString()+ "\n");
            data.add(any.toString());
        }
        
        //print requested data
    	List<Double> dataValues = pe.pullData();
        dataRequests.writeData(dataValues);
        gui.logStringData("---------------------------\n");
        for(int i = 0; i < (dataValues.size()); i++ ) {
            gui.logStringData(requestList[i] + ": " + dataValues.get(i) + "\n");
            data.add(requestList[i] + ": " + dataValues.get(i));
        }
        
        //print actions
        List<SEAction> actions = new ArrayList<SEAction>();
        pe.getActiveActions(actions);
        for(SEAction any : actions)
        {
        	gui.logStringData(any.toString()+ "\n");
          //Ext ventilator doesn't need the data actions
          //data.add(any.toString());
        }
        //send data to graphs to be printed
        double x = dataValues.get(0);
        double y = 0;
        for (int i = 1; i < (dataValues.size()); i++) {
        	y = dataValues.get(i);
            gui.logItemDisplayData(requestList[i],x, y);
        }
        
        return data;
    }
    
    
    public void connectVentilator(Ventilator v) {
        switch (v.getMode()) {
        case PC:
        	SEMechanicalVentilatorPressureControl ventilator_PC = (SEMechanicalVentilatorPressureControl) v.getVentilator();
        	ventilator_PC.setConnection(eSwitch.On);
            pe.processAction(ventilator_PC);
            break;
            
        case CPAP:
            // Gestisci la connessione per un ventilatore CPAP
        	SEMechanicalVentilatorContinuousPositiveAirwayPressure ventilator_CPAP = (SEMechanicalVentilatorContinuousPositiveAirwayPressure) v.getVentilator();
        	ventilator_CPAP.setConnection(eSwitch.On);
            pe.processAction(ventilator_CPAP);
            break;

        case VC:
        	SEMechanicalVentilatorVolumeControl ventilator_VC = (SEMechanicalVentilatorVolumeControl) v.getVentilator();
        	ventilator_VC.setConnection(eSwitch.On);
            pe.processAction(ventilator_VC);
            break;

        case EXTERNAL:
        	SEMechanicalVentilation ventilator_ext = (SEMechanicalVentilation) v.getVentilator_External();
        	zmqServer = new ZeroServer();
        	try {
				zmqServer.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
        	//Server wait for data
    		zmqServer.startReceiving();
    		ext_running = true;
            break;
        }
    }
    
    public void disconnectVentilator(Ventilator v) {
        switch (v.getMode()) {
        case PC:
        	SEMechanicalVentilatorPressureControl ventilator_PC = (SEMechanicalVentilatorPressureControl) v.getVentilator();
        	ventilator_PC.setConnection(eSwitch.Off);
            pe.processAction(ventilator_PC);
            break;
            
        case CPAP:
        	SEMechanicalVentilatorContinuousPositiveAirwayPressure ventilator_CPAP = (SEMechanicalVentilatorContinuousPositiveAirwayPressure) v.getVentilator();
        	ventilator_CPAP.setConnection(eSwitch.Off);
            pe.processAction(ventilator_CPAP);
            break;

        case VC:
        	SEMechanicalVentilatorVolumeControl ventilator_VC = (SEMechanicalVentilatorVolumeControl) v.getVentilator();
        	ventilator_VC.setConnection(eSwitch.Off);
            pe.processAction(ventilator_VC);
            break;

        case EXTERNAL:
        	ventilator_ext = (SEMechanicalVentilation) v.getVentilator_External();
        	ventilator_ext.setState(eSwitch.Off);
        	pe.processAction(ventilator_ext);
        	zmqServer.close();
        	ext_running = false;
        	break;
        }
    }
    
    //Methods for external ventilator
	private void manage_ext(){
		if(zmqServer.isConnectionStable() && zmqServer.getSelectedMode() != null) {
			//here change to add ZeroMQ call	
			if (zmqServer.getSelectedMode().equals("VOLUME")) {
	            setExtVolume();
	        } else {
	            setExtPressure();
	        }
			ventilator_ext.setState(eSwitch.On);
	        if(!pe.processAction(ventilator_ext)) {
	        	//ERROR!
	        }
		}
    }
    
	private void setExtVolume() {
		double volume = zmqServer.getVolume();
		ventilator_ext.getFlow().setValue(volume, VolumePerTimeUnit.mL_Per_s);
    	gui.logVolumeExternalVentilatorData(volume);
	}
	
	private void setExtPressure() {
		double pressure = zmqServer.getPressure();
		ventilator_ext.getPressure().setValue(pressure,PressureUnit.mmHg);
		gui.logPressureExternalVentilatorData(pressure);
	}

}
