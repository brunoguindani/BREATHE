package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import app.App;
import data.Condition;
import inputItems.ConditionBox;

public class ConditionsPanel {

    private JPanel mainPanel = new JPanel();
    private List<ConditionBox> boxes = new ArrayList<>();
    private List<Condition> activeConditions = new ArrayList<>();
    private JButton reset;

    public ConditionsPanel(App app) {
    	 
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setPreferredSize(new Dimension(550, 650));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); 
        
        // Crea un pannello per le condition box e lo metti dentro lo scroll pane
        JPanel conditionsContainer = new JPanel();
        conditionsContainer.setLayout(new BoxLayout(conditionsContainer, BoxLayout.Y_AXIS)); 
        conditionsContainer.setBorder(null);
        
        JScrollPane scrollablePanel = new JScrollPane(conditionsContainer);  // Avvolgi il pannello
        scrollablePanel.setPreferredSize(new Dimension(550, 650)); 
        scrollablePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollablePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollablePanel.setBorder(null);

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
        ConditionBox pericardialEffusionBox = new ConditionBox(app, "PericardialEffusion", pericardialEffusionComponents);
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


        // Aggiungi i pannelli delle ConditionBox al mainPanel
        for(ConditionBox box : boxes) {
        	conditionsContainer.add(box.getSectionPanel());
        }
        JPanel rigidAreaPanel = new JPanel();
        rigidAreaPanel.setPreferredSize(new Dimension(500, 500));
        rigidAreaPanel.setBackground(Color.LIGHT_GRAY); 
        conditionsContainer.add(rigidAreaPanel);
           	
    	Dimension buttonSize = new Dimension(150, 50);
    	
		reset = new JButton("Reset Conditions");
		reset.setToolTipText("remove all conditions and set values to 0");
		reset.setPreferredSize(buttonSize);
		reset.setMaximumSize(buttonSize);
		reset.setAlignmentX(JButton.CENTER_ALIGNMENT);
		reset.setBackground(new Color(0, 122, 255));
		reset.setForeground(Color.WHITE);
		reset.setFocusPainted(false);
		 
		reset.addActionListener(e -> {
			resetConditions();
		});
        
        mainPanel.add(scrollablePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(500, 10)));
        mainPanel.add(reset);
        
    }

    // Metodo per restituire il mainPanel
    public JPanel getMainPanel() {
        return mainPanel;
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
    }

    public void enableButtons(boolean enable) {
    	reset.setEnabled(enable);
        for(ConditionBox box : boxes) {
        	box.enableBox(enable);
        }    	
    }
    
}

