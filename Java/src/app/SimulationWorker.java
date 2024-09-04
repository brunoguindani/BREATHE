package app;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorPressureControlData;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorVolumeControlData;
import com.kitware.pulse.cdm.bind.Patient.PatientData.eSex;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.cdm.patient.actions.SEMechanicalVentilation;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.cdm.properties.CommonUnits.ElectricPotentialUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.LengthUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.MassUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PowerUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorContinuousPositiveAirwayPressure;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorPressureControl;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;
import com.kitware.pulse.cdm.properties.SEScalarTime;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.utilities.Log;

import zeroMQ.ZeroServer;

import com.kitware.pulse.utilities.JNIBridge;

public class SimulationWorker extends SwingWorker<Void, String> {

	/*
	 * Class with the pulseEngine that controls the simulation
	 */
	
    private final App app;
    public static PulseEngine pe;
    private String[] requestList;
    private SEDataRequestManager dataRequests;
    private static volatile boolean stopRequested = false;
    public static volatile boolean ventilationStartRequest = false;
    public static volatile boolean ventilationDisconnectRequest = false;
    public static volatile boolean started = false; 
    
    private static ZeroServer zmqServer;  
    private static boolean ext_running = false;
    private enum extMode { VOLUME, PRESSURE }
    private extMode currentEXTMode;

    public SimulationWorker(App appTest) {
        this.app = appTest;
    }
    
    public static void requestStop() {
        stopRequested = true;
        if(ext_running)
			try {
				zmqServer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    }

    @Override
    protected Void doInBackground() throws Exception {
        stopRequested = false;
        started = true;
        
        // Initialize JNIBridge and PulseEngine
        JNIBridge.initialize();
        pe = new PulseEngine();
        

        // Creation of Data Request
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);


        //Paztient data depending on PatientPanel config
		String patientFilePath = app.patient.getSelectedFilePath();
		 
		if (patientFilePath == null || patientFilePath.isEmpty()) {
			
			SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
			SEPatient patient = patient_configuration.getPatient();
			setPatientParameter(patient);

	        for(SECondition any : app.condition.getActiveConditions())
	        {
	          patient_configuration.getConditions().add(any);
	        }
			
			pe.initializeEngine(patient_configuration, dataRequests);
		}
		else {
			pe.serializeFromFile(patientFilePath, dataRequests);
			SEPatient initialPatient = new SEPatient();
			pe.getInitialPatient(initialPatient);
		}

        
        //Ventilators
        SEMechanicalVentilatorPressureControl pc = new SEMechanicalVentilatorPressureControl();
        SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap = new SEMechanicalVentilatorContinuousPositiveAirwayPressure();
        SEMechanicalVentilatorVolumeControl vc = new SEMechanicalVentilatorVolumeControl();
        SEMechanicalVentilation ext = new SEMechanicalVentilation();
    	
        //Start Simulation
        publish("Started\n");
        SEScalarTime time = new SEScalarTime(0, TimeUnit.s);
        while (!stopRequested) {
        	
            if (!pe.advanceTime(time)) {
                publish("Something bad happened\n");
                return null;
            }
            
            handilngVentilator(ext,pc,cpap,vc);

            //Log and data printing
            if(ext_running)
            	zmqServer.setSimulationData(dataPrint());
            else
            	dataPrint();

            time.setValue(0.02, TimeUnit.s);
            Log.info("Advancing "+time+"...");
        }
        
        // Final Cleaning
	    started = false;
        pe.clear();
        pe.cleanUp();
        publish("Simulation Complete\n");

        return null;
    }

    
    @Override
    protected void process(java.util.List<String> chunks) {
        for (String chunk : chunks) {
            app.log.getResultArea().append(chunk);
        }
    }
    

    @Override
    protected void done() {
        app.log.getResultArea().append("Simulazione fermata.\n");
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
    
    private void setPatientParameter(SEPatient patient) {
    	
    	//retrieved patient field and set them (not from file)
		patient.setName(app.patient.getName_PATIENT());
		patient.getAge().setValue(Double.parseDouble(app.patient.getAge_PATIENT()), TimeUnit.yr);
		patient.getBodyFatFraction().setValue(Double.parseDouble(app.patient.getBodyFatFraction_PATIENT()));
		patient.getHeartRateBaseline().setValue(Double.parseDouble(app.patient.getHeartRate_PATIENT()), FrequencyUnit.Per_min);
		patient.getDiastolicArterialPressureBaseline().setValue(Double.parseDouble(app.patient.getDiastolicPressure_PATIENT()), PressureUnit.mmHg);
		patient.getSystolicArterialPressureBaseline().setValue(Double.parseDouble(app.patient.getSystolicPressure_PATIENT()), PressureUnit.mmHg);
		patient.getRespirationRateBaseline().setValue(Double.parseDouble(app.patient.getRespirationRate_PATIENT()), FrequencyUnit.Per_min);
		patient.getBasalMetabolicRate().setValue(Double.parseDouble(app.patient.getBasalMetabolicRate_PATIENT()), PowerUnit.kcal_Per_day);
		if (app.patient.getSex_PATIENT().equals("Male")) {
		    patient.setSex(eSex.Male);
		} else {
		    patient.setSex(eSex.Female);
		}

		//Weight
		String weightUnit = app.patient.getWeightUnit_PATIENT();
    	if(weightUnit.equals("kg"))
    		patient.getWeight().setValue(Double.parseDouble(app.patient.getWeight_PATIENT()), MassUnit.kg);
    	else
    		patient.getWeight().setValue(Double.parseDouble(app.patient.getWeight_PATIENT()), MassUnit.lb);
    	
    	//Height
    	String heightUnit = app.patient.getHeightUnit_PATIENT();
    	if(heightUnit.equals("inches"))
    		patient.getHeight().setValue(Double.parseDouble(app.patient.getHeight_PATIENT()), LengthUnit.in);
    	else if(heightUnit.equals("m"))
    		patient.getHeight().setValue(Double.parseDouble(app.patient.getHeight_PATIENT()), LengthUnit.m);
    	else if(heightUnit.equals("cm"))
    		patient.getHeight().setValue(Double.parseDouble(app.patient.getHeight_PATIENT()), LengthUnit.cm);
    	else 
    		patient.getHeight().setValue(Double.parseDouble(app.patient.getHeight_PATIENT()), LengthUnit.ft);
		
    }
    
  //Handling Ventilators
    private void handilngVentilator(SEMechanicalVentilation ext, SEMechanicalVentilatorPressureControl pc, SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap, SEMechanicalVentilatorVolumeControl vc) {
    	if(ventilationDisconnectRequest) {
        	ventilationDisconnectRequest = false;
        	if(app.ventilator.isPCConnected()){ 
            	stop_pc(pc);
            }
            else if(app.ventilator.isCPAPConnected()){
    	        stop_cpap(cpap);
    	    }
            else if(app.ventilator.isVCConnected()){
    	        stop_vc(vc);
    	    }
            else if(app.ventilator.isEXTConnected()){
            	ext_running = false;
    	        stop_ext(ext);
    	    }
        }
        else if(ventilationStartRequest) {
        	ventilationStartRequest = false;
        	
            if(app.ventilator.isPCConnected()){ 
            	start_pc(pc);
            }
            else if(app.ventilator.isCPAPConnected()){
    	        start_cpap(cpap);
    	    }
            else if(app.ventilator.isVCConnected()){
    	        start_vc(vc);
    	    }
            else if(app.ventilator.isEXTConnected()){
            	try {
					start_ext();
	    	        manage_ext(ext);
	    	        ext_running = true;
	    	        return;
				} catch (Exception e) {
					e.printStackTrace();
				}
    	    }
        }
    	
    	if(ext_running)
			try {
				manage_ext(ext);
			} catch (Exception e) {
				e.printStackTrace();
			}
    }
    

	//Set and Starts of Ventilators
    
    private void start_pc(SEMechanicalVentilatorPressureControl pc) {
    	if (app.ventilator.getAssistedMode_PC().equals("AC")) {
    		pc.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
		} else {
			pc.setMode(MechanicalVentilatorPressureControlData.eMode.ContinuousMandatoryVentilation);
		}
        pc.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
        pc.getFractionInspiredOxygen().setValue(Double.parseDouble(app.ventilator.getFractionInspOxygenValue_PC()));
        pc.getInspiratoryPeriod().setValue(Double.parseDouble(app.ventilator.getInspiratoryPeriodValue_PC()),TimeUnit.s);
        pc.getInspiratoryPressure().setValue(Double.parseDouble(app.ventilator.getInspiratoryPressureValue_PC()), PressureUnit.cmH2O);
        pc.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.ventilator.getPositiveEndExpPresValue_PC()), PressureUnit.cmH2O);
        pc.getRespirationRate().setValue(Double.parseDouble(app.ventilator.getRespirationRateValue_PC()), FrequencyUnit.Per_min);
        pc.getSlope().setValue(Double.parseDouble(app.ventilator.getSlopeValue_PC()), TimeUnit.s);
        pc.setConnection(eSwitch.On);
        pe.processAction(pc);
    }
    
    private void stop_pc(SEMechanicalVentilatorPressureControl pc) {
        pc.setConnection(eSwitch.Off);
        pe.processAction(pc);
    }
    
    private void start_cpap(SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap) {
        cpap.getFractionInspiredOxygen().setValue(Double.parseDouble(app.ventilator.getFractionInspOxygenValue_CPAP()));
        cpap.getDeltaPressureSupport().setValue(Double.parseDouble(app.ventilator.getDeltaPressureSupValue_CPAP()), PressureUnit.cmH2O);
        cpap.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.ventilator.getPositiveEndExpPresValue_CPAP()), PressureUnit.cmH2O);
        cpap.getSlope().setValue(Double.parseDouble(app.ventilator.getSlopeValue_CPAP()), TimeUnit.s);
        cpap.setConnection(eSwitch.On);
        pe.processAction(cpap);
    }
    
    private void stop_cpap(SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap) {
        cpap.setConnection(eSwitch.Off);
        pe.processAction(cpap);
    }
    
    private void start_vc(SEMechanicalVentilatorVolumeControl vc) {
    	if (app.ventilator.getAssistedMode_PC().equals("AC")) {
    		vc.setMode(MechanicalVentilatorVolumeControlData.eMode.AssistedControl);
		} else {
			vc.setMode(MechanicalVentilatorVolumeControlData.eMode.ContinuousMandatoryVentilation);
		}
    	vc.setMode(MechanicalVentilatorVolumeControlData.eMode.AssistedControl);
        vc.getFlow().setValue(Double.parseDouble(app.ventilator.getFlow_VC()), VolumePerTimeUnit.L_Per_min);
        vc.getFractionInspiredOxygen().setValue(Double.parseDouble(app.ventilator.getFractionInspOxygenValue_VC()));
        vc.getInspiratoryPeriod().setValue(Double.parseDouble(app.ventilator.getInspiratoryPeriod_VC()), TimeUnit.s);
        vc.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.ventilator.getPositiveEndExpPres_VC()), PressureUnit.cmH2O);
        vc.getRespirationRate().setValue(Double.parseDouble(app.ventilator.getRespirationRate_VC()), FrequencyUnit.Per_min);
        vc.getTidalVolume().setValue(Double.parseDouble(app.ventilator.getTidalVol_VC()), VolumeUnit.mL);
        vc.setConnection(eSwitch.On);
        pe.processAction(vc);
    }
   
    private void stop_vc(SEMechanicalVentilatorVolumeControl vc) {
        vc.setConnection(eSwitch.Off);
        pe.processAction(vc);
    }
    

    // method to set the volume/pressure from external ventilator
    private void start_ext() throws Exception {
    	zmqServer = new ZeroServer();
    	zmqServer.connect();
		zmqServer.startReceiving();
    }
    
	private void manage_ext(SEMechanicalVentilation ext){
		if(zmqServer.isConnectionStable()) {
			setEXTMode(zmqServer.getSelectedMode());
			//here change to add ZeroMQ call	
			if (currentEXTMode == extMode.VOLUME) {
	            setVolume(ext);
	        } else if (currentEXTMode == extMode.PRESSURE) {
	            setPressure(ext);
	        }
	        ext.setState(eSwitch.On);
	        pe.processAction(ext);
		}
		else{
			disconnectEXTClient(ext);
		}
			
    }
	
	private void setVolume(SEMechanicalVentilation ext) {
		double volume = zmqServer.getVolume();
		ext.getFlow().setValue(volume, VolumePerTimeUnit.mL_Per_s);
    	app.ventilator.setVolumeLabel_EXT(volume);
	}
	
	private void setPressure(SEMechanicalVentilation ext) {
		double pressure = zmqServer.getPressure();
		ext.getPressure().setValue(pressure,PressureUnit.mmHg);
    	app.ventilator.setPressureLabel_EXT(pressure);
	}
	
    public void setEXTMode(String mode) {
        if (mode.equalsIgnoreCase("VOLUME")) {
        	currentEXTMode = extMode.VOLUME;
        } else {
        	currentEXTMode = extMode.PRESSURE;
        }
    }
    
    public void disconnectEXTClient(SEMechanicalVentilation ext) {
    	ext.setState(eSwitch.Off);
        pe.processAction(ext);
        app.ventilator.setPressureLabel_EXT(Double.NaN);
        app.ventilator.setVolumeLabel_EXT(Double.NaN);
        //NEW press button when client disconnects
        if(zmqServer.isDisconnecting()) {
        	zmqServer.setDisconnecting();
            app.ventilator.disconnectButton.doClick();
        }

    }
    
    private void stop_ext(SEMechanicalVentilation ext) {
    	ext.setState(eSwitch.Off);
    	pe.processAction(ext);
    	zmqServer.close();
    }
    
    //Print data in console and log Panel and Charts
    private ArrayList<String> dataPrint() {
    	ArrayList<String> data = new ArrayList<String>();
    	
    	//print conditions
        pe.getConditions(app.condition.getActiveConditions());
        for(SECondition any : app.condition.getActiveConditions())
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

        //send data to graphs to be printed
        double x = dataValues.get(0);
        double y = 0;
        for (int i = 1; i < (dataValues.size()); i++) {
            y = dataValues.get(i);
            app.charts.getChartsPanel()[i - 1].addPoint(x, y);
        }
        
        return data;
    }
    

}
