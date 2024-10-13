package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import app.App;
import data.Condition;
import inputItems.ConditionBox;

public class ConditionsPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/*
	 * PANEL WITH ALL CONDITION BOXES
	 */

    private List<ConditionBox> boxes = new ArrayList<>();
    private List<Condition> activeConditions = new ArrayList<>();
    private JButton reset;
    private App app;

    public ConditionsPanel(App app) {
        this.app = app;
    	 
        this.setBackground(Color.LIGHT_GRAY);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 
        
        
        // Panels containing all conditions
        JPanel conditionsContainer = new JPanel();
        conditionsContainer.setBackground(Color.LIGHT_GRAY);
        conditionsContainer.setPreferredSize(new Dimension(500, 50));
        conditionsContainer.setLayout(new BoxLayout(conditionsContainer, BoxLayout.Y_AXIS)); 
        conditionsContainer.setBorder(null);
        
        /*
         * ADD CONDITIONS
         */
        Map<String, JComponent> anemiaComponents = new HashMap<>();
        anemiaComponents.put("ReductionFactor", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox anemiaBox = new ConditionBox(app, "Chronic Anemia", anemiaComponents);
        boxes.add(anemiaBox);

        Map<String, JComponent> ardsComponents = new HashMap<>();
        ardsComponents.put("LeftLungSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ardsComponents.put("RightLungSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox ardsBox = new ConditionBox(app, "ARDS", ardsComponents);
        boxes.add(ardsBox);

        Map<String, JComponent> copdComponents = new HashMap<>();
        copdComponents.put("BronchitisSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        copdComponents.put("LeftLungEmphysemaSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        copdComponents.put("RightLungEmphysemaSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox copdBox = new ConditionBox(app, "COPD", copdComponents);
        boxes.add(copdBox);

        Map<String, JComponent> pericardialEffusionComponents = new HashMap<>();
        pericardialEffusionComponents.put("AccumulatedVolume", new JSpinner(new SpinnerNumberModel(0, 0, 100, 0.01)));
        ConditionBox pericardialEffusionBox = new ConditionBox(app, "Pericardial Effusion", pericardialEffusionComponents);
        boxes.add(pericardialEffusionBox);

        Map<String, JComponent> renalStenosisComponents = new HashMap<>();
        renalStenosisComponents.put("LeftKidneySeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        renalStenosisComponents.put("RightKidneySeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox renalStenosisBox = new ConditionBox(app, "Renal Stenosis", renalStenosisComponents);
        boxes.add(renalStenosisBox);

        Map<String, JComponent> pneumoniaComponents = new HashMap<>();
        pneumoniaComponents.put("LeftLungSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        pneumoniaComponents.put("RightLungSeverity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox pneumoniaBox = new ConditionBox(app, "Pneumonia", pneumoniaComponents);
        boxes.add(pneumoniaBox);

        Map<String, JComponent> fibrosisComponents = new HashMap<>();
        fibrosisComponents.put("Severity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox fibrosisBox = new ConditionBox(app, "Pulmonary Fibrosis", fibrosisComponents);
        boxes.add(fibrosisBox);

        Map<String, JComponent> shuntComponents = new HashMap<>();
        shuntComponents.put("Severity", new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)));
        ConditionBox shuntBox = new ConditionBox(app, "Pulmonary Shunt", shuntComponents);
        boxes.add(shuntBox);

        for(ConditionBox box : boxes) {
        	conditionsContainer.add(box.getSectionPanel());
        }
        
        //BUTTON TO RESET CONDITIONS
        JPanel rigidAreaPanel = new JPanel();
        rigidAreaPanel.setPreferredSize(new Dimension(10, 140));
        rigidAreaPanel.setBackground(Color.LIGHT_GRAY); 
        conditionsContainer.add(rigidAreaPanel);
           	
    	Dimension buttonSize = new Dimension(150, 30);
    	
		reset = new JButton("Reset Conditions");
		reset.setToolTipText("remove all conditions and set values to 0");
		reset.setPreferredSize(buttonSize);
		reset.setAlignmentX(JButton.CENTER_ALIGNMENT);
		reset.setFocusPainted(false);
		reset.addActionListener(e -> {
			resetConditions();
		});
        
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        buttonPanel.setBackground(Color.LIGHT_GRAY); 
        buttonPanel.setPreferredSize(new Dimension(550, 60));
        buttonPanel.add(reset);
        
        JPanel borderPanel = new JPanel();
        borderPanel.setLayout(new BorderLayout());
        borderPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        borderPanel.add(conditionsContainer, BorderLayout.CENTER);
        borderPanel.add(buttonPanel, BorderLayout.SOUTH);
        
		this.add(borderPanel);
    }
    
    public void addCondition(Condition c) {
    	activeConditions.add(c);
    }
    
    public void removeCondition(String title) {
    	for(int i=0; i< activeConditions.size() ;i++) {
    		if(activeConditions.get(i).getTitle().equals(title)) {
            	activeConditions.remove(i);
    			break;
    		}
    	}
    }
    
    public List<Condition> getActiveConditions() {
    	return activeConditions;
    }
    
    private void resetConditions() {
        List<Condition> toRemove = new ArrayList<>();  
        
        for (Condition c : activeConditions) {
            for (int i = 0; i < boxes.size(); i++) {
                if (boxes.get(i).getTitle().equals(c.getTitle())) {
                    boxes.get(i).reset();
                    break;
                }
            }
            toRemove.add(c); 
        }
        
        activeConditions.removeAll(toRemove);
        app.minilogStringData("\nConditions resetted");
    }

    public void enableButtons(boolean enable) {
    	reset.setEnabled(enable);
        for(ConditionBox box : boxes) {
        	box.enableBox(enable);
        }    	
    }

    public void setInitialCondition(List<Condition> list) {
    	activeConditions.clear();
        boolean found = false;
        int i = 0;
        for (ConditionBox box : boxes) {
            found = false;
            i = 0;

            for (Condition condition : list) {
                if (box.getTitle().equals(condition.getTitle())) {
                    found = true;
                    break;
                }
                i++;
            }

            if (found) {
            	box.reset();
            	box.setComponents(list.get(i).getParameters());
            } else {
            	box.reset();
            }
        }
    }
    
}

