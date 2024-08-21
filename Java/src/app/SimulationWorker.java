package app;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorPressureControlData;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorVolumeControlData;
import com.kitware.pulse.cdm.bind.Patient.PatientData.eSex;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.patient.SEPatient;
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
import com.kitware.pulse.utilities.JNIBridge;

public class SimulationWorker extends SwingWorker<Void, String> {

	
    private final App app;
    public static PulseEngine pe;
    private String[] requestList;
    private SEDataRequestManager dataRequests;
    private static volatile boolean stopRequested = false;
    public static volatile boolean ventilationSwitchRequest = false;
    public static volatile boolean ventilationDisconnectRequest = false;
    public static volatile boolean started = false; 
    

    public SimulationWorker(App appTest) {
        this.app = appTest;
    }
    
    public static void requestStop() {
        stopRequested = true;
    }

    @Override
    protected Void doInBackground() throws Exception {
        stopRequested = false;
        started = true;
        
        // Inizializzazione di JNIBridge e PulseEngine
        JNIBridge.initialize();
        pe = new PulseEngine();
        

        // Creazione e configurazione delle richieste di dati
        dataRequests = new SEDataRequestManager();
        setDataRequests(dataRequests);

        
        //Paziente
		String patientFilePath = app.patient.getSelectedFilePath();
		 
		if (patientFilePath == null || patientFilePath.isEmpty()) {
			
			SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
			SEPatient patient = patient_configuration.getPatient();
			setPatientParameter(patient);
			pe.initializeEngine(patient_configuration, dataRequests);
			
			/*
			//PER DEBUG
			pe.serializeFromFile("./states/StandardMale@0s.json", dataRequests);
			//Controllo se è riuscito a caricare il file
			SEPatient initialPatient = new SEPatient();
			pe.getInitialPatient(initialPatient);
			*/
		}
		else {
			pe.serializeFromFile(patientFilePath, dataRequests);
			//Controllo se è riuscito a caricare il file
			SEPatient initialPatient = new SEPatient();
			pe.getInitialPatient(initialPatient);
		}

        
        //Creazione ventilatori
        SEMechanicalVentilatorPressureControl pc = new SEMechanicalVentilatorPressureControl();
        SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap = new SEMechanicalVentilatorContinuousPositiveAirwayPressure();
        SEMechanicalVentilatorVolumeControl vc = new SEMechanicalVentilatorVolumeControl();
        
        
        //Partenza simulazione
        publish("Started\n");
        SEScalarTime time = new SEScalarTime(0, TimeUnit.s);
        while (!stopRequested) {
        	
            if (!pe.advanceTime(time)) {
                publish("Something bad happened\n");
                return null;
            }
            
            //Gestione ventilatori
            if(ventilationDisconnectRequest) {
            	ventilationDisconnectRequest = false;
            	if(app.ventilator.isPCACConnected()){ 
                	stop_pc(pc);
                }
                else if(app.ventilator.isCPAPConnected()){
        	        stop_cpap(cpap);
        	    }
                else if(app.ventilator.isVCACConnected()){
        	        stop_vc(vc);
        	    }
            	
            }
            else if(ventilationSwitchRequest) {
            	ventilationSwitchRequest = false;
            	
                if(app.ventilator.isPCACConnected()){ 
                	start_pc(pc);
                }
                else if(app.ventilator.isCPAPConnected()){
        	        start_cpap(cpap);
        	    }
                else if(app.ventilator.isVCACConnected()){
        	        start_vc(vc);
        	    }
            }

            //Scrittura dei dati a schermo (log e grafici)
            dataPrint();

            time.setValue(0.02, TimeUnit.s);
            Log.info("Advancing "+time+"...");
        }
        
        // Pulizia finale e chiusura della simulazione
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
    	String[] requestList = {"SimTime",
				"HeartRate",
				"TotalLungVolume",
				"RespirationRate",
				"Lead3ElectricPotential"
				};
    	
    	this.requestList = requestList;
    	dataRequests.createPhysiologyDataRequest(requestList[1], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[2], VolumeUnit.mL);
        dataRequests.createPhysiologyDataRequest(requestList[3], FrequencyUnit.Per_min);
        dataRequests.createECGDataRequest(requestList[4], ElectricPotentialUnit.mV);
        
      //dataRequests.setResultsFilename("./test_results/HowTo_EngineUse.java.csv");
    }
    
    private void setPatientParameter(SEPatient patient) {
		patient.setName(app.patient.getName_PATIENT());
		patient.getAge().setValue(Double.parseDouble(app.patient.getAge_PATIENT()), TimeUnit.yr);
		patient.getWeight().setValue(Double.parseDouble(app.patient.getWeight_PATIENT()), MassUnit.lb);
		patient.getHeight().setValue(Double.parseDouble(app.patient.getHeight_PATIENT()), LengthUnit.in);
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

    }
    
    private void start_pc(SEMechanicalVentilatorPressureControl pc) {
    	if (app.ventilator.getAssistedMode_PC().equals("AC")) {
    		pc.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
		} else {
			pc.setMode(MechanicalVentilatorPressureControlData.eMode.ContinuousMandatoryVentilation);
		}
        pc.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
        pc.getFractionInspiredOxygen().setValue(Double.parseDouble(app.ventilator.getFractionInspOxygenValue_PCAC()));
        pc.getInspiratoryPeriod().setValue(Double.parseDouble(app.ventilator.getInspiratoryPeriodValue_PCAC()),TimeUnit.s);
        pc.getInspiratoryPressure().setValue(Double.parseDouble(app.ventilator.getInspiratoryPressureValue_PCAC()), PressureUnit.cmH2O);
        pc.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.ventilator.getPositiveEndExpPresValue_PCAC()), PressureUnit.cmH2O);
        pc.getRespirationRate().setValue(Double.parseDouble(app.ventilator.getRespirationRateValue_PCAC()), FrequencyUnit.Per_min);
        pc.getSlope().setValue(Double.parseDouble(app.ventilator.getSlopeValue_PCAC()), TimeUnit.s);
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
        vc.getFlow().setValue(Double.parseDouble(app.ventilator.getFlow_VCAC()), VolumePerTimeUnit.L_Per_min);
        vc.getFractionInspiredOxygen().setValue(Double.parseDouble(app.ventilator.getFractionInspOxygenValue_VCAC()));
        vc.getInspiratoryPeriod().setValue(Double.parseDouble(app.ventilator.getInspiratoryPeriod_VCAC()), TimeUnit.s);
        vc.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.ventilator.getPositiveEndExpPres_VCAC()), PressureUnit.cmH2O);
        vc.getRespirationRate().setValue(Double.parseDouble(app.ventilator.getRespirationRate_VCAC()), FrequencyUnit.Per_min);
        vc.getTidalVolume().setValue(Double.parseDouble(app.ventilator.getTidalVol_VCAC()), VolumeUnit.mL);
        vc.setConnection(eSwitch.On);
        pe.processAction(vc);
    }
   
    private void stop_vc(SEMechanicalVentilatorVolumeControl vc) {
        vc.setConnection(eSwitch.Off);
        pe.processAction(vc);
    }
    

    private void dataPrint() {
    	//Estrazione e scrittura dei dati
    	List<Double> dataValues = pe.pullData();
        dataRequests.writeData(dataValues);
        publish("---------------------------\n");
        for(int i = 0; i < (dataValues.size()); i++ ) {
            publish(requestList[i] + ": " + dataValues.get(i) + "\n");
        }
        
        List<SEAction> actions = new ArrayList<SEAction>();
        pe.getActiveActions(actions);
        for(SEAction any : actions)
        {
          Log.info(any.toString());
          publish(any.toString()+ "\n");
        }

        //Stampe dei dati nei grafici
        double x = dataValues.get(0);
        double y = 0;
        for (int i = 1; i <= 4; i++) {
            y = dataValues.get(i);
            app.charts.getChartsPanel()[i - 1].addPoint(x, y);
        }

    }
}
