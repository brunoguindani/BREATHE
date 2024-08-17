package tests;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorPressureControlData;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorVolumeControlData;
import com.kitware.pulse.cdm.bind.Patient.PatientData.eSex;
import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.cdm.patient.actions.SEAcuteRespiratoryDistressSyndromeExacerbation;
import com.kitware.pulse.cdm.patient.actions.SEDyspnea;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.LengthUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.MassUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PowerUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureTimePerVolumeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerPressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorContinuousPositiveAirwayPressure;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorPressureControl;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;
import com.kitware.pulse.cdm.properties.SEScalarTime;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.utilities.JNIBridge;

public class SimulationWorkerTest extends SwingWorker<Void, String> {

	private static volatile boolean stopRequested = false;
    private final AppTest app;
    public static PulseEngine pe;
    public static volatile boolean ventilationSwitchRequest = false;
    public static volatile boolean ventilationDisconnectRequest = false;

    public SimulationWorkerTest(AppTest appTest) {
        this.app = appTest;
    }
    
    public static void requestStop() {
        stopRequested = true;
    }

    @Override
    protected Void doInBackground() throws Exception {
        stopRequested = false;
        // Inizializzazione di JNIBridge e PulseEngine
        JNIBridge.initialize();
        pe = new PulseEngine();
        
        String[] requestList = {"SimTime","HeartRate","TotalLungVolume","RespirationRate","BloodVolume"};

        // Creazione e configurazione delle richieste di dati
        SEDataRequestManager dataRequests = new SEDataRequestManager();
        dataRequests.createPhysiologyDataRequest(requestList[1], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[2], VolumeUnit.mL);
        dataRequests.createPhysiologyDataRequest(requestList[3], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[4], VolumeUnit.mL);
        //dataRequests.setResultsFilename("./test_results/HowTo_EngineUse.java.csv");

        /*
        //Paziente
		SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
		SEPatient patient = patient_configuration.getPatient();
		
		// Supponendo che "app" sia un'istanza della classe App
		patient.setName(app.getName_PATIENT());
		patient.getAge().setValue(Double.parseDouble(app.getAge_PATIENT()), TimeUnit.yr);
		patient.getWeight().setValue(Double.parseDouble(app.getWeight_PATIENT()), MassUnit.lb);
		patient.getHeight().setValue(Double.parseDouble(app.getHeight_PATIENT()), LengthUnit.in);
		patient.getBodyFatFraction().setValue(Double.parseDouble(app.getBodyFatFraction_PATIENT()));
		patient.getHeartRateBaseline().setValue(Double.parseDouble(app.getHeartRate_PATIENT()), FrequencyUnit.Per_min);
		patient.getDiastolicArterialPressureBaseline().setValue(Double.parseDouble(app.getDiastolicPressure_PATIENT()), PressureUnit.mmHg);
		patient.getSystolicArterialPressureBaseline().setValue(Double.parseDouble(app.getSystolicPressure_PATIENT()), PressureUnit.mmHg);
		patient.getRespirationRateBaseline().setValue(Double.parseDouble(app.getRespirationRate_PATIENT()), FrequencyUnit.Per_min);
		patient.getBasalMetabolicRate().setValue(Double.parseDouble(app.getBasalMetabolicRate_PATIENT()), PowerUnit.kcal_Per_day);
		if (app.getSex_PATIENT().equals("Male")) {
		    patient.setSex(eSex.Male);
		} else {
		    patient.setSex(eSex.Female);
		}

        // Inizializzazione del motore Pulse con la configurazione del paziente e le richieste di dati
        pe.initializeEngine(patient_configuration, dataRequests);
       */
        
        //SOLO PER DEBUG
        pe.serializeFromFile("./states/StandardMale@0s.json", dataRequests);
        SEPatient initialPatient = new SEPatient();
        pe.getInitialPatient(initialPatient);

        SEAcuteRespiratoryDistressSyndromeExacerbation ards = new SEAcuteRespiratoryDistressSyndromeExacerbation();
        ards.getSeverity(eLungCompartment.LeftLung).setValue(0.5);
        ards.getSeverity(eLungCompartment.RightLung).setValue(0.5);
        pe.processAction(ards);
        
        SEDyspnea dyspnea = new SEDyspnea();
        dyspnea.getTidalVolumeSeverity().setValue(1.0);
        pe.processAction(dyspnea);   
        
        //Creo i ventilatori
        SEMechanicalVentilatorPressureControl pc_ac = new SEMechanicalVentilatorPressureControl();
        SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap = new SEMechanicalVentilatorContinuousPositiveAirwayPressure();
        SEMechanicalVentilatorVolumeControl vcac = new SEMechanicalVentilatorVolumeControl();
        
        publish("Started\n");
        // Avanzamento temporale e gestione degli errori
        SEScalarTime time = new SEScalarTime(0, TimeUnit.s);
        while (!stopRequested) {
        	
            if (!pe.advanceTime(time)) {
                publish("Something bad happened\n");
                return null;
            }
            
            if(ventilationDisconnectRequest) {
            	ventilationDisconnectRequest = false;
            	if(app.isPCACConnected()){ 
                	stop_pc_ac(pc_ac);
                }
                else if(app.isCPAPConnected()){
        	        stop_cpap(cpap);
        	    }
                else if(app.isVCACConnected()){
        	        stop_vcac(vcac);
        	    }
            	
            }
            else if(ventilationSwitchRequest) {
            	ventilationSwitchRequest = false;
            	
                if(app.isPCACConnected()){ 
                	start_pc_ac(pc_ac);
                }
                else if(app.isCPAPConnected()){
        	        start_cpap(cpap);
        	    }
                else if(app.isVCACConnected()){
        	        start_vcac(vcac);
        	    }
            }

    	        
            // Estrazione e scrittura dei dati
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

            // Aggiungi punto al grafico usando SimTime (dataValues.get(0)) e HeartRate (dataValues.get(1))
            int x = (int)(dataValues.get(0)*30+50);  // Scala il tempo per renderlo visibile
            int y = (int) (250 - dataValues.get(1)*2);
            app.getChartPanel()[0].addPoint(x, y);
            y = (int) (250 - dataValues.get(3)*2);
            app.getChartPanel()[1].addPoint(x, y);

            time.setValue(0.10, TimeUnit.s);
            Log.info("Advancing "+time+"...");
        }
	    
        // Pulizia finale e chiusura della simulazione
        pe.clear();
        pe.cleanUp();
        publish("Simulation Complete\n");
        
        return null;
    }

    @Override
    protected void process(java.util.List<String> chunks) {
        for (String chunk : chunks) {
            app.getResultArea().append(chunk);
        }
    }
    

    @Override
    protected void done() {
        app.getResultArea().append("Simulazione fermata.\n");
    }
    
    
    private void start_pc_ac(SEMechanicalVentilatorPressureControl pc_ac) {
        pc_ac.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
        pc_ac.getFractionInspiredOxygen().setValue(Double.parseDouble(app.getFractionInspOxygenValue_PCAC()));
        pc_ac.getInspiratoryPeriod().setValue(Double.parseDouble(app.getInspiratoryPeriodValue_PCAC()),TimeUnit.s);
        pc_ac.getInspiratoryPressure().setValue(Double.parseDouble(app.getInspiratoryPressureValue_PCAC()), PressureUnit.cmH2O);
        pc_ac.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.getPositiveEndExpPresValue_PCAC()), PressureUnit.cmH2O);
        pc_ac.getRespirationRate().setValue(Double.parseDouble(app.getRespirationRateValue_PCAC()), FrequencyUnit.Per_min);
        pc_ac.getSlope().setValue(Double.parseDouble(app.getSlopeValue_PCAC()), TimeUnit.s);
        pc_ac.setConnection(eSwitch.On);
        pe.processAction(pc_ac);
    }
    
    private void stop_pc_ac(SEMechanicalVentilatorPressureControl pcac) {
        pcac.setConnection(eSwitch.Off);
        pe.processAction(pcac);
    }
    
    private void start_cpap(SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap) {
        cpap.getFractionInspiredOxygen().setValue(Double.parseDouble(app.getFractionInspOxygenValue_CPAP()));
        cpap.getDeltaPressureSupport().setValue(Double.parseDouble(app.getDeltaPressureSupValue_CPAP()), PressureUnit.cmH2O);
        cpap.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.getPositiveEndExpPresValue_CPAP()), PressureUnit.cmH2O);
        cpap.getSlope().setValue(Double.parseDouble(app.getSlopeValue_CPAP()), TimeUnit.s);
        cpap.setConnection(eSwitch.On);
        pe.processAction(cpap);
    }
    
    private void stop_cpap(SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap) {
        cpap.setConnection(eSwitch.Off);
        pe.processAction(cpap);
    }
    
    private void start_vcac(SEMechanicalVentilatorVolumeControl vcac) {
    	vcac.setMode(MechanicalVentilatorVolumeControlData.eMode.AssistedControl);
        vcac.getFlow().setValue(Double.parseDouble(app.getFlow_VCAC()), VolumePerTimeUnit.L_Per_min);
        vcac.getFractionInspiredOxygen().setValue(Double.parseDouble(app.getFractionInspOxygenValue_VCAC()));
        vcac.getInspiratoryPeriod().setValue(Double.parseDouble(app.getInspiratoryPeriod_VCAC()), TimeUnit.s);
        vcac.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.getPositiveEndExpPres_VCAC()), PressureUnit.cmH2O);
        vcac.getRespirationRate().setValue(Double.parseDouble(app.getRespirationRate_VCAC()), FrequencyUnit.Per_min);
        vcac.getTidalVolume().setValue(Double.parseDouble(app.getTidalVol_VCAC()), VolumeUnit.mL);
        vcac.setConnection(eSwitch.On);
        pe.processAction(vcac);
    }
   
    private void stop_vcac(SEMechanicalVentilatorVolumeControl vcac) {
        vcac.setConnection(eSwitch.Off);
        pe.processAction(vcac);
    }
}
