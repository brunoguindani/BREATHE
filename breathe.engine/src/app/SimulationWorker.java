package app;

import data.*;
import interfaces.GuiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.properties.CommonUnits.*;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.properties.SEScalarTime;

public class SimulationWorker extends SwingWorker<Void, String>{
	
    public PulseEngine pe;
    private String initializeMode;
    
    private SEDataRequestManager dataRequests;
    private String[] requestList;
    
    SEScalarTime stime = new SEScalarTime(0, TimeUnit.s);

    SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
    
    private GuiCallback gui;
    
    /*
     * Costruttore
     */
    public SimulationWorker(GuiCallback guiCallback) { 
    	this.gui = guiCallback;
    }
    
    public void simulation(Patient patient) {
    	
    	initializeMode = "standard";
		
        pe = new PulseEngine("../breathe.engine/");
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
        
        patient_configuration = patient.getPatientConfiguration();
        
    	this.execute();
    
    }
    
    public void simulationfromFile(String file) {
    	
    	initializeMode = "file";
        
    	pe = new PulseEngine("../breathe.engine/");
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
    	
		pe.serializeFromFile(file, dataRequests);

		//questo non capisco cosa fa
		SEPatient initialPatient = new SEPatient();
		pe.getInitialPatient(initialPatient);
		
		/*
		GET CONDITIONS FROM CONDITION PANEL 
		
        pe.getConditions(app.condition.getActiveConditions());
        app.condition.setInitialConditionsTo0();
        for(SECondition any : app.condition.getActiveConditions())
        {
        	app.condition.setInitialConditions(any);
        }
        
        */
		
    	this.execute();
    	
    }
    
    public void simulationfromScenario() {
    	
    	initializeMode = "scenario";
    
    }

	@Override
	protected Void doInBackground() throws Exception {
        
		if(initializeMode.equals("standard")) {
	        pe.initializeEngine(patient_configuration, dataRequests);    
	        exportInitialPatient(patient_configuration.getPatient());		
		}else if(initializeMode.equals("file")) {
			//boh se si vuole spostare qui
		}else if(initializeMode.equals("scenario")) {
			//non so se serve
		}

		//Hide starting buttons and show others
		gui.showStartingButton(false);
		
		while (true) {
        	
            if (!pe.advanceTime(stime)) {
                publish("\nSomething bad happened");
                return null;
            }
            
            /*handlingVentilator(ext,pc,cpap,vc);

            //Log and data printing
            if(ext_running)
            	zmqServer.setSimulationData(dataPrint());
            else
            	*/
            	dataPrint();

            stime.setValue(0.02, TimeUnit.s);
            Log.info("Advancing "+stime+"...");
        }
        /*
        pe.clear();
        pe.cleanUp();
        publish("Simulation Complete\n");
        return null;*/
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
				"ArterialPressure"
				};
    	
    	this.requestList = requestList;
    	
    	//create the requests
    	dataRequests.createPhysiologyDataRequest(requestList[1], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[2], VolumeUnit.mL);
        dataRequests.createPhysiologyDataRequest(requestList[3], FrequencyUnit.Per_min);
        dataRequests.createECGDataRequest(requestList[4], ElectricPotentialUnit.mV);
        dataRequests.createGasCompartmentDataRequest("Carina", "CarbonDioxide", "PartialPressure", PressureUnit.mmHg);
        dataRequests.createPhysiologyDataRequest(requestList[6], PressureUnit.mmHg);
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
    
    private ArrayList<String> dataPrint() {
    	ArrayList<String> data = new ArrayList<String>();
    	
    	//print conditions
        pe.getConditions(patient_configuration.getConditions());
        for(SECondition any : patient_configuration.getConditions())
        {
            Log.info(any.toString());
            publish(any.toString()+ "\n");
            data.add(any.toString());
        }
        
        //print requested data
    	List<Double> dataValues = pe.pullData();
        dataRequests.writeData(dataValues);
        publish("---------------------------\n");
        for(int i = 0; i < (dataValues.size()); i++ ) {
            publish(requestList[i] + ": " + dataValues.get(i) + "\n");
            data.add(requestList[i] + ": " + dataValues.get(i));
        }
        
        //print actions
        List<SEAction> actions = new ArrayList<SEAction>();
        pe.getActiveActions(actions);
        for(SEAction any : actions)
        {
        	Log.info(any.toString());
        	publish(any.toString()+ "\n");
          
          //Ext ventilator doesn't need the data actions
          //data.add(any.toString());
        }
        
/*
        //send data to graphs to be printed
        double x = dataValues.get(0);
        double y = 0;
        for (int i = 1; i < (dataValues.size()); i++) {
        	y = dataValues.get(i);
            app.charts.addValueToItemDisplay(requestList[i],x, y);
        }*/
        
        return data;
    }
    

}
