package panels;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.kitware.pulse.cdm.actions.SEAdvanceTime;
import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.patient.actions.SEAcuteRespiratoryDistressSyndromeExacerbation;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.scenario.SEScenario;
import com.kitware.pulse.engine.PulseScenarioExec;
import com.kitware.pulse.utilities.JNIBridge;

import app.SimulationWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScenarioPanel {

    private JPanel scenarioPanel;
    private JButton createScenarioButton;
    static PulseScenarioExec execOpts;

    public ScenarioPanel() {
    	scenarioPanel = new JPanel();
        createScenarioButton = new JButton("Create Scenario");

        createScenarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JNIBridge.initialize();
                example();
            }
        });

        scenarioPanel.add(createScenarioButton);
    }
    
    public static void example()
    {
      execOpts = new PulseScenarioExec();
      
      // Create and run a scenario
      execOpts.clear();
      SEScenario sce = new SEScenario();
      sce.setName("HowTo_StaticEngine");
      sce.setDescription("Simple Scenario to demonstraight building a scenario by the CDM API");
      sce.setEngineState("./states/StandardMale@0s.json");
      // When filling out a data request, units are optional
      // The units will be set to whatever units the engine uses.
      SEDataRequestManager dataRequests = sce.getDataRequestManager();
      dataRequests.createPhysiologyDataRequest("HeartRate", FrequencyUnit.Per_min);
      dataRequests.createPhysiologyDataRequest("TotalLungVolume", VolumeUnit.mL);
      dataRequests.createPhysiologyDataRequest("RespirationRate", FrequencyUnit.Per_min);
      dataRequests.createPhysiologyDataRequest("BloodVolume", VolumeUnit.mL);
      // Let's just run for 1 minutes
      SEAdvanceTime adv = new SEAdvanceTime();
      adv.getTime().setValue(1,TimeUnit.min);
      sce.getActions().add(adv);
      
      //Test azione
      SEAcuteRespiratoryDistressSyndromeExacerbation ards = new SEAcuteRespiratoryDistressSyndromeExacerbation();
      ards.getSeverity(eLungCompartment.RightLung).setValue(0.5);
      sce.getActions().add(ards);
      
      sce.writeFile("./scenario/test.json"); //Aggiunto da me, non penso sia il modo di esportare
      //execOpts.setScenarioFilename("./scenario/test.json");
      //execOpts.setScenarioContent(sce.toJSON()); ->Questo in teoria fa la stessa cosa del comando qua sopra
      //execOpts.execute(); ->Esegue lo scenario
    }

    public JPanel getScenarioPanel() {
        return scenarioPanel;
    }
    
}