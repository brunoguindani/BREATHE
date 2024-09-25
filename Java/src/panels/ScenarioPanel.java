package panels;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.kitware.pulse.cdm.actions.SEAdvanceTime;
import com.kitware.pulse.cdm.patient.actions.SEAirwayObstruction;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.scenario.SEScenario;
import com.kitware.pulse.engine.PulseScenarioExec;
import com.kitware.pulse.utilities.JNIBridge;
import com.kitware.pulse.cdm.actions.SEAction;
import java.util.ArrayList;
import utils.Pair;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ScenarioPanel {

    private JPanel scenarioPanel;
    private JButton createScenarioButton;
    private JTextField scenarioNameField; 
    private JTextField scenarioDescriptionField; 
    static PulseScenarioExec execOpts;
    private JTextArea actionsDisplay; // Aggiungi questa riga
    private ArrayList<Pair<SEAction, Integer>> actions = new ArrayList<>();

    public ScenarioPanel() {
        scenarioPanel = new JPanel();
        scenarioPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        scenarioNameField = new JTextField(25);
        scenarioDescriptionField = new JTextField(25);
        createScenarioButton = new JButton("Create Scenario");

        addLabelAndField("Name:", scenarioNameField, scenarioPanel, gbc, 0);
        addLabelAndField("Description:", scenarioDescriptionField, scenarioPanel, gbc, 1);

        actionsDisplay = new JTextArea(5, 25);
        actionsDisplay.setEditable(false); 
        updateActionsDisplay(); 
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        scenarioPanel.add(new JScrollPane(actionsDisplay), gbc); 

        gbc.gridy = 3;
        scenarioPanel.add(createScenarioButton, gbc);

        createScenarioButton.addActionListener(e -> {
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
        	int seconds = 0;
	        SEAdvanceTime adv = new SEAdvanceTime();
   	        adv.getTime().setValue(1, TimeUnit.s);
            for (Pair<SEAction, Integer> action : actions) {
            	int target = action.getValue();
            	
    	        while (seconds < target) {
    	            sce.getActions().add(adv);
    	            seconds++;
    	        }
    	        
    	        sce.getActions().add(action.getKey()); 	        	
            }       	
	
	        sce.writeFile("./scenario/"+scenarioName+".json");
	        MiniLogPanel.append("Scenario exported");
        }
    }
    
    public void addAction(SEAction action, int seconds) {
        Pair<SEAction,Integer> newAction = new Pair<>(action,seconds);
        actions.add(newAction);
        actions.sort((pair1, pair2) -> pair1.getValue().compareTo(pair2.getValue()));
        updateActionsDisplay();
    }
    
    private void updateActionsDisplay() {
        StringBuilder displayText = new StringBuilder();
        for (Pair<SEAction, Integer> action : actions) {
            displayText.append(action.getKey().toString()).append("\n Time:").append(formatTime(action.getValue())).append("\n");
        }
        actionsDisplay.setText(displayText.toString());
    }
    
    private String formatTime(int seconds) {
        int hours = seconds / 3600; 
        int minutes = (seconds % 3600) / 60; 
        int remainingSeconds = seconds % 60; 

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }
    
    public JPanel getScenarioPanel() {
        return scenarioPanel;
    }
}
