package tests;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Enums.eSwitch;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorVolumeControlData;
import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.cdm.patient.actions.SEAcuteRespiratoryDistressSyndromeExacerbation;
import com.kitware.pulse.cdm.patient.actions.SEDyspnea;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureTimePerVolumeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerPressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorContinuousPositiveAirwayPressure;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;
import com.kitware.pulse.cdm.properties.SEScalarTime;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.utilities.JNIBridge;

public class SimulationWorkerTest extends SwingWorker<Void, String> {

	private static volatile boolean stopRequested = false;
    private final AppTest app;
    public static PulseEngine pe;

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

        //Ventilatore
       
        
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
        
        
        //Qua imposto il ventilatore
        SEMechanicalVentilatorContinuousPositiveAirwayPressure cpap = new SEMechanicalVentilatorContinuousPositiveAirwayPressure();
        
        if(app.isCPAPConnected())
        	cpap.setConnection(eSwitch.On);
        cpap.getFractionInspiredOxygen().setValue(Double.parseDouble(app.getFractionInspOxygenValue()));
        cpap.getDeltaPressureSupport().setValue(Double.parseDouble(app.getDeltaPressureSupValue()), PressureUnit.cmH2O);
        cpap.getPositiveEndExpiratoryPressure().setValue(Double.parseDouble(app.getPositiveEndExpPresValue()), PressureUnit.cmH2O);
        cpap.getSlope().setValue(Double.parseDouble(app.getSlopeValue()), TimeUnit.s);
        pe.processAction(cpap);
        
        publish("Started\n");
        // Avanzamento temporale e gestione degli errori
        SEScalarTime time = new SEScalarTime(0, TimeUnit.s);
        while (!stopRequested) {
        	
            if (!pe.advanceTime(time)) {
                publish("Something bad happened\n");
                return null;
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
}
