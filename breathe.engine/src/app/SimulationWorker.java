package app;

import data.*;

import java.io.File;

import javax.swing.*;

import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.properties.CommonUnits.*;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.cdm.properties.SEScalarTime;

public class SimulationWorker extends SwingWorker<Void, String>{
	
    public PulseEngine pe;
    private SEDataRequestManager dataRequests;
    private String[] requestList;
    
    SEScalarTime stime = new SEScalarTime(0, TimeUnit.s);;
    
    public SimulationWorker() { }
    
    public void simulation(Patient patient) {
		
        pe = new PulseEngine("../breathe.engine/");
		
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);
        
        SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
        SEPatient pp = patient_configuration.getPatient();
        patient.getPatient2(pp);
        
        pe.initializeEngine(patient.getPatientConfiguration(), dataRequests);
        
        exportInitialPatient(patient);
        
    	this.execute();
    
    }
    
    public void simulationfromFile() {
    	
    }
    
    public void simulationfromScenario() {
    	
    }

	@Override
	protected Void doInBackground() throws Exception {
		
		while (true) {
        	
            if (!pe.advanceTime(stime)) {
                publish("\nSomething bad happened");
                return null;
            }
            /*
            handlingVentilator(ext,pc,cpap,vc);

            //Log and data printing
            if(ext_running)
            	zmqServer.setSimulationData(dataPrint());
            else
            	dataPrint();*/

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
    
    private void exportInitialPatient(Patient patient) {
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
    
    

}
