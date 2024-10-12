package app;

import data.*;
import data.Action;
import interfaces.GuiCallback;
import server.ZeroServer;
import utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.swing.*;

import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.properties.CommonUnits.*;
import com.kitware.pulse.engine.PulseEngine;
import com.vaadin.flow.component.UI;
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
    private UI ui;
    
    private boolean stopRequest = false;
    
    SEScalarTime stime = new SEScalarTime(0, TimeUnit.s);
    SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
    
    //Data for scenario
    String scenarioFilePath = null;
    String patientFilePath = null;
    
    //Data for external ventilator
    ZeroServer zmqServer;
    SEMechanicalVentilation ventilator_ext = new SEMechanicalVentilation();
    private boolean standardVent_running = false;
    private boolean extVent_running = false;
    private boolean firstEXTConnection = true;
    
    public SimulationWorker(GuiCallback guiCallback, UI ui) {
    	this.gui = guiCallback;
    	this.ui = ui;
    }
    
    public SimulationWorker(GuiCallback guiCallback) {
    	this.gui = guiCallback;
    	this.ui = null;
    }
    
	/*
	 * STARTING NORMAL SIMULATION
	 */
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
        gui.minilogStringData("Loading...");
    	this.execute();
    }
    
	/*
	 * STARTING FILE SIMULATION
	 */
    public void simulationFromFile(String file) {
    	initializeMode = "file";
    	pe = new PulseEngine();
    	
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
        
		gui.minilogStringData("Loading state file " + file);
		pe.serializeFromFile(file, dataRequests);

		//check that patient has loaded
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
		//get conditions
		List<SECondition> list = new ArrayList<>();
		List<Condition> temp_list = new ArrayList<>();
        pe.getConditions(list);
        for(SECondition c : list) {
        	Condition temp = new Condition(c);
        	temp_list.add(temp);
        }
        gui.setInitialCondition(temp_list);

    	this.execute();
    }
    
	/*
	 * STARTING SCENARIO
	 */
    public void simulationFromScenario(String scenarioFilePath) {
    	initializeMode = "scenario";
    	pe = new PulseEngine();
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
    	
        this.scenarioFilePath = scenarioFilePath;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode_scenario = null;
		try {
			rootNode_scenario = mapper.readTree(new File(scenarioFilePath));
		} catch (IOException e) {		
			e.printStackTrace();
		}
        patientFilePath = rootNode_scenario.path("EngineStateFile").asText();
        
        gui.minilogStringData("Loading Scenario " + scenarioFilePath);
		pe.serializeFromFile(patientFilePath, dataRequests);

		//check that patient has loaded
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
		gui.minilogStringData("Load state ("+patientFilePath+")");
		List<SECondition> list = new ArrayList<>();
		List<Condition> temp_list = new ArrayList<>();
        pe.getConditions(list);
        for(SECondition c : list) {
        	Condition temp = new Condition(c);
        	temp_list.add(temp);
        }
        gui.setInitialCondition(temp_list);

    	this.execute();
    }

    public void stopSimulation() {
    	stopRequest = true;
    }
    
	@Override
	protected Void doInBackground() throws Exception {
        
		if(initializeMode.equals("standard")) {
	        pe.initializeEngine(patient_configuration, dataRequests);    
	        exportInitialPatient(patient_configuration.getPatient());	
	        
	        //Advice for stabilization completed
			gui.stabilizationComplete(true);
			gui.minilogStringData("\nSimulation Started");
		}else if(initializeMode.equals("file")) {
			gui.stabilizationComplete(true);
			gui.minilogStringData("\nSimulation Started");
		}else if(initializeMode.equals("scenario")) {
			run_scenario();
		}

		
		while (!stopRequest) {
        	
            if (!pe.advanceTime(stime)) {
        		gui.minilogStringData("\nError!");
        		gui.minilogStringData("Simulation stopped!");
                return null;
            }
            
            //Send data (to gui and to ext ventilator)
            if(extVent_running) {
            	manage_ext();
            	zmqServer.setSimulationData(sendData());
            }
            else
            	sendData();

            stime.setValue(0.02, TimeUnit.s);
        }
		
		stopRequest = false;
        pe.clear();
        pe.cleanUp();
		gui.minilogStringData("\nSimulation has been stopped");
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
			e.printStackTrace();
		}
		
		if(sce.hasEngineState()) {
			
			if(!pe.serializeFromFile(patientFilePath, dataRequests));
		} else if(sce.hasPatientConfiguration()) {
			if(!pe.initializeEngine(sce.getPatientConfiguration(), dataRequests));
		}
		
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
		//Advice for stabilaztion completed
		gui.stabilizationComplete(true);
		gui.minilogStringData("\nSimulation Started");

		for (SEAction a : sce.getActions()) {
			if(stopRequest)
		    	return;
			
		    if (a instanceof SEAdvanceTime) {
		        
		        for(int i = 0; i<50; i++){		  
		            if (!pe.advanceTime(stime)) {
		        		gui.minilogStringData("\nError!");
		        		gui.minilogStringData("Simulation stopped!");
		                return;
		            }

		            //Send data (to gui and to ext ventilator)
		            if(extVent_running) {
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
        if( pe.serializeToFile(filePath) ) 
        	gui.minilogStringData("\nExported Patient File to " + filePath);
    }
    
    
    private ArrayList<String> sendData() {
    	ArrayList<String> data = new ArrayList<String>();
    	
    	//print conditions
        pe.getConditions(patient_configuration.getConditions());
        for(SECondition any : patient_configuration.getConditions())
        {
        	//publish(any.toString());
            gui.logStringData(any.toString()+ "\n");
            data.add(any.toString());
        }
        
        //print requested data
    	List<Double> dataValues = pe.pullData();
        dataRequests.writeData(dataValues);
        gui.logStringData("---------------------------\n");
        //publish("---------------------------\n");
        for(int i = 0; i < (dataValues.size()); i++ ) {
        	//publish(requestList[i] + ": " + dataValues.get(i) + "\n");
            gui.logStringData(requestList[i] + ": " + dataValues.get(i) + "\n");
            data.add(requestList[i] + ": " + dataValues.get(i));
        }
        
        //print actions
        List<SEAction> actions = new ArrayList<SEAction>();
        pe.getActiveActions(actions);
        for(SEAction any : actions)
        {
        	//publish(any.toString()+ "\n");
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
            if(pe.processAction(ventilator_PC)) {
	        	if(!standardVent_running) {
	        		gui.minilogStringData("\nPC Ventilator Connected ");
	        		standardVent_running = true;
	        	} else
	        		gui.minilogStringData("\nVentilator modify applied ");
            } else
            	gui.minilogStringData("\nPC Ventilator error!!! ");
            break;
            
        case CPAP:
            // Gestisci la connessione per un ventilatore CPAP
        	SEMechanicalVentilatorContinuousPositiveAirwayPressure ventilator_CPAP = (SEMechanicalVentilatorContinuousPositiveAirwayPressure) v.getVentilator();
        	ventilator_CPAP.setConnection(eSwitch.On);
        	if(pe.processAction(ventilator_CPAP)) {
	        	if(!standardVent_running) {
	        		gui.minilogStringData("\nCPAP Ventilator Connected ");
	        		standardVent_running = true;
	        	} else
	        		gui.minilogStringData("\nVentilator modify applied ");
            } else
            	gui.minilogStringData("\nCPAP Ventilator error!!! ");
            break;

        case VC:
        	SEMechanicalVentilatorVolumeControl ventilator_VC = (SEMechanicalVentilatorVolumeControl) v.getVentilator();
        	ventilator_VC.setConnection(eSwitch.On);
        	if(pe.processAction(ventilator_VC)) {
	        	if(!standardVent_running) {
	        		gui.minilogStringData("\nVC Ventilator Connected ");
	        		standardVent_running = true;
	        	} else
	        		gui.minilogStringData("\nVentilator modify applied ");
            } else
            	gui.minilogStringData("\nVC Ventilator error!!! ");
            break;

        case EXTERNAL:
        	ventilator_ext = (SEMechanicalVentilation) v.getVentilator_External();
        	zmqServer = new ZeroServer();
        	try {
				zmqServer.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}
        	//Server wait for data
        	gui.minilogStringData("\nSearching for EXTERNAL ventilators...");
    		zmqServer.startReceiving();
    		extVent_running = true;
            break;
        }
    }
    
    public void disconnectVentilator(Ventilator v) {
        switch (v.getMode()) {
        case PC:
        	SEMechanicalVentilatorPressureControl ventilator_PC = (SEMechanicalVentilatorPressureControl) v.getVentilator();
        	ventilator_PC.setConnection(eSwitch.Off);
            pe.processAction(ventilator_PC);
            gui.minilogStringData("\nPC Ventilator Disconnected");
            break;
            
        case CPAP:
        	SEMechanicalVentilatorContinuousPositiveAirwayPressure ventilator_CPAP = (SEMechanicalVentilatorContinuousPositiveAirwayPressure) v.getVentilator();
        	ventilator_CPAP.setConnection(eSwitch.Off);
            pe.processAction(ventilator_CPAP);
            gui.minilogStringData("\nCPAP Ventilator Disconnected");
            break;

        case VC:
        	SEMechanicalVentilatorVolumeControl ventilator_VC = (SEMechanicalVentilatorVolumeControl) v.getVentilator();
        	ventilator_VC.setConnection(eSwitch.Off);
            pe.processAction(ventilator_VC);
            gui.minilogStringData("\nVC Ventilator Disconnected");
            break;

        case EXTERNAL:	//CLOSED BY SERVER
	        ventilator_ext = (SEMechanicalVentilation) v.getVentilator_External();
			ventilator_ext.setState(eSwitch.Off);
		    pe.processAction(ventilator_ext);
	        zmqServer.close();
        	gui.minilogStringData("EXTERNAL Ventilator server closed");
        	extVent_running = false;
        	firstEXTConnection = true;
        	resetLogExtVentilator();
        	break;
        }
        standardVent_running = false;
    }
    
    //Methods for external ventilator
	private void manage_ext(){
		if(zmqServer.isConnectionStable() && zmqServer.getSelectedMode() != null) {
			
			if(firstEXTConnection) {
				ventilator_ext.setState(eSwitch.On);
				gui.minilogStringData("EXTERNAL Ventilator connected");
				firstEXTConnection = false;
			}
			
			if (zmqServer.getSelectedMode().equals("VOLUME")) {
	            setExtVolume();
	        } else {
	            setExtPressure();
	        }
	        if(!pe.processAction(ventilator_ext)) {
	        	gui.minilogStringData("\nEXTERNAL Ventilator error!!!");
	        }
		} else if(zmqServer.isDisconnecting()) {	//CLOSED BY CLIENT
			resetLogExtVentilator();
			ventilator_ext.setState(eSwitch.Off);
		    gui.minilogStringData("\nEXTERNAL Ventilator disconnected");
	        pe.processAction(ventilator_ext);
	        resetLogExtVentilator();
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
	
	private void resetLogExtVentilator() {
		gui.logPressureExternalVentilatorData(Double.NaN);
		gui.logVolumeExternalVentilatorData(Double.NaN);
	}
	
	public void applyAction(Action action) {
		gui.minilogStringData("\nApplying " +  action.getAction().toString());
		pe.processAction(action.getAction());
	}

	public void exportSimulation(String exportFilePath) {
		if ( pe.serializeToFile(exportFilePath) )  
			gui.minilogStringData("\nExported Patient File to " + exportFilePath);
	}

	
    public void createScenario(String patientFile,String scenarioName, ArrayList<Pair<Action, Integer>> actions) {
        SEScenario sce = new SEScenario();

        sce.setName(scenarioName);
        sce.setEngineState(patientFile);

        int seconds = 0;
        SEAdvanceTime adv = new SEAdvanceTime();
        adv.getTime().setValue(1, TimeUnit.s);
        if(actions != null) {
	        for (Pair<Action, Integer> action : actions) {
	            int target = action.getValue();
	
	            while (seconds < target) {
	                sce.getActions().add(adv);
	                seconds++;
	            }
	
	            sce.getActions().add(action.getKey().getAction());
	        }
        }

        sce.writeFile("./scenario/" + scenarioName + ".json");
        gui.minilogStringData("\nScenario exported to: " + "./scenario/" + scenarioName + ".json");
    }
    
    
    @Override
    protected void process(List<String> chunks) {
        // Usa ui.access() per modificare la UI
        ui.access(() -> {
            for (String message : chunks) {
                gui.logStringData(message); // chiama il metodo di log nella UI
            }
        });
    
    }
    
}
