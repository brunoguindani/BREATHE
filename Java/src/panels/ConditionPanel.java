package panels;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import com.kitware.pulse.cdm.conditions.SECondition;

import utils.Condition;

public class ConditionPanel {
	
	/*
	 * Panel to activate a condition before simulation
	 */
	
    private JPanel sectionsPanel = new JPanel();  
    private JScrollPane scrollConditionPane;
    private List<Condition> conditions = new ArrayList<>();
    private static List<SECondition> activeConditions = new ArrayList<>();

    public ConditionPanel() {
    	// base
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        /*
         * CONDITIONS
         */
        conditions = new ArrayList<>();
        
        conditions.add(new Condition(
                "Anemia",
                new JLabel("Reduction Factor"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
            ));
        conditions.add(new Condition(
                "ARDS",
                new JLabel("Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
            ));
        conditions.add(new Condition(
                "COPD",
                new JLabel("Bronchitis Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Emphysema Left Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Emphysema Right Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
            ));
        conditions.add(new Condition(
                "Pericardial Effusion",
                new JLabel("Accumulated Volume (mL)"), new JSpinner(new SpinnerNumberModel(0, 0, 100, 0.01))
            ));
        conditions.add(new Condition(
                "Renal Stenosis",
                new JLabel("Left Kidney Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Right Kidney Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        	));
        conditions.add(new Condition(
                "Ventricular Systolic Disfunction"
        	));/*
        conditions.add(new Condition( //To fix
                "Impaired Alveolar Exchange (Not implemented yet)",
                new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Impaired Faction"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Impaired Surface Area (m^2)"), new JSpinner(new SpinnerNumberModel(0, 0, 100, 1))
        	));  */
        conditions.add(new Condition(
                "Pneumonia",
                new JLabel("Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
            )); 
        conditions.add(new Condition(
                "Pulmonary Fibrosis",
                new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
            )); 
        conditions.add(new Condition(
                "Pulmonary Shunt",
                new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
            )); 
        for (Condition condition : conditions) {
            sectionsPanel.add(condition.sectionPanel);
        }
                        
        /*
         * END CONDITIONS
         */
       
        // Add scrollPane
        scrollConditionPane = new JScrollPane(sectionsPanel);
        scrollConditionPane.setPreferredSize(new Dimension(400, 600));
        scrollConditionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollConditionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    }
    
    //method to return the panel
    public JScrollPane getConditionScrollPane() {
        return scrollConditionPane;
    }
    
    public void enableButtonStates() {
        for (Condition condition : conditions) {
        	condition.enable();
        }
    }
    
    public void disableButtonStates() {
        for (Condition condition : conditions) {
        	condition.disable();
        }
    }
    
    public static boolean addCondition(SECondition e) {
    	return activeConditions.add(e);
    }
    
    public static boolean removeCondition(SECondition e) {
    	return activeConditions.remove(e);
    }
    
    public List<SECondition> getActiveConditions() {
    	return activeConditions;
    }
    
    public int getNumActiveCondition() {
    	return activeConditions.size();
    }
    
    /*SETTING INITIAL CONDITIONS FROM FILE*/
    
    public void setInitialConditions() {
        for (Condition condition : conditions) {
        	
        }
    }
    
}
