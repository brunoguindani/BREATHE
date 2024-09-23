package panels;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.google.protobuf.InvalidProtocolBufferException;
import com.kitware.pulse.cdm.actions.SEAdvanceTime;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.patient.actions.SEAirwayObstruction;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.scenario.SEScenario;
import com.kitware.pulse.engine.PulseScenarioExec;
import com.kitware.pulse.utilities.JNIBridge;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ScenarioPanel {

    private JPanel scenarioPanel;
    private JButton createScenarioButton;
    private JTextField scenarioNameField; 
    private JTextField scenarioDescriptionField; 
    static PulseScenarioExec execOpts;

    public ScenarioPanel() {
        scenarioPanel = new JPanel();
        scenarioPanel.setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        
        scenarioNameField = new JTextField(25); 
        scenarioDescriptionField = new JTextField(25); 
        createScenarioButton = new JButton("Create Scenario");

        // Aggiungi i campi di testo con le etichette
        addLabelAndField("Name:", scenarioNameField, scenarioPanel, gbc, 0);
        addLabelAndField("Description:", scenarioDescriptionField, scenarioPanel, gbc, 1);

        
        gbc.gridx = 0; 
        gbc.gridy = 2; 
        gbc.gridwidth = 2; 
        scenarioPanel.add(createScenarioButton, gbc);
        
        createScenarioButton.addActionListener(e -> {
            JNIBridge.initialize();
            createScenario();
        });
    }
    
    private void addLabelAndField(String labelText, JTextField textField, JPanel panel, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row; 
        gbc.insets = new Insets(5, 5, 5, 5); 
        panel.add(new JLabel(labelText), gbc); 
        
        gbc.gridx = 1; 
        panel.add(textField, gbc); 
    }

    
    public void createScenario() {
        SEScenario sce = new SEScenario();
        String scenarioName = scenarioNameField.getText(); 
        String scenarioDescription = scenarioDescriptionField.getText();
        sce.setName(scenarioName);
        sce.setDescription(scenarioDescription);
        JFileChooser fileChooser = new JFileChooser("./states/");
        int returnValue = fileChooser.showOpenDialog(null); //pick a file
        if (returnValue == JFileChooser.APPROVE_OPTION) {
        	String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
        	sce.setEngineState(selectedFilePath);

	        SEAdvanceTime adv = new SEAdvanceTime();
	        adv.getTime().setValue(0.02, TimeUnit.s);
	        for (int i = 0; i < 100; i++) {
	            sce.getActions().add(adv);
	        }
	
	        SEAirwayObstruction obstruction = new SEAirwayObstruction();
	        obstruction.getSeverity().setValue(1);
	        sce.getActions().add(obstruction);
	
	        sce.writeFile("./scenario/"+scenarioName+".json");
	        MiniLogPanel.append("Scenario exported");
        }
    }

    
    public JPanel getScenarioPanel() {
        return scenarioPanel;
    }
}
