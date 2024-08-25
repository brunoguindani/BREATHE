package panels;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import utils.Action;

public class ActionPanel {
	
	/*
	 * Panel to activate an action during simulation
	 */
	
    private JPanel sectionsPanel = new JPanel();  
    private JScrollPane scrollActionPane;  
    private List<Action> actions = new ArrayList<>();

    public ActionPanel() {
    	//base
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        /*
         * ACTIONS
         */
        actions = new ArrayList<>();
        
        actions.add(new Action(
            "ARDS Exacerbation",
            new JLabel("Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Acute Stress",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));  
        actions.add(new Action(
            "Airway Obstruction",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));        
        actions.add(new Action(
            "Asthma Attack",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));   
        actions.add(new Action(
            "Brain Injury",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));  
        actions.add(new Action(
            "Bronchoconstriction",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        )); 
        actions.add(new Action(
            "COPD Exacerbation",
            new JLabel("Bronchitis Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Emphysema Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Emphysema Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Dyspnea",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Exercise",
            new JLabel("Intensity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Pericardial Effusion",
            new JLabel("Effusion Rate (mL/min)"), new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.01))
        ));
        actions.add(new Action(
            "Pneumonia Exacerbation",
            new JLabel("Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));  
        actions.add(new Action(
            "Pulmonary Shunt Exacerbation",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Respiratory Fatigue",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Urinate"
        ));
        actions.add(new Action(
            "Ventilator Leak",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        for (Action action : actions) {
            sectionsPanel.add(action.sectionPanel);
        }
            
        /*
         * END ACTIONS
         */
        
        // Add scrollPane
        scrollActionPane = new JScrollPane(sectionsPanel);
        scrollActionPane.setPreferredSize(new Dimension(400, 600));
        scrollActionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollActionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
    //method to return the panel
    public JScrollPane getActionScrollPane() {
        return scrollActionPane;
    }
    
    public void enableButtonStates() {
        for (Action action : actions) {
            action.enableButtonState();
        }
    }
    
    public void disableButtonStates() {
        for (Action action : actions) {
            action.disableButtonState();
        }
    }
}
