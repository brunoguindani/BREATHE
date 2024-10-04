package panels;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.patient.conditions.*;

import utils.Condition;

public class ConditionPanel {
	
	/*
	 * Panel to activate a condition before simulation
	 */
	
    private JPanel sectionsPanel = new JPanel(); 
    private JButton resetConditions;
    private JScrollPane scrollConditionPane;
    private List<Condition> conditions = new ArrayList<>();
    private static List<SECondition> activeConditions = new ArrayList<>();

    public ConditionPanel() {
    	// base
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.LIGHT_GRAY); 
        
        resetConditions = new JButton("Reset conditions");
        resetConditions.setPreferredSize(new Dimension(150, 30));
        resetConditions.setBackground(new Color(0, 122, 255)); 
        resetConditions.setForeground(Color.WHITE);
        resetConditions.setFocusPainted(false);
        resetConditions.setMargin(new Insets(0, 0, 0, 0));  
        buttonPanel.add(resetConditions);

        /*
         * CONDITIONS
         */
        conditions = new ArrayList<>();
        
        conditions.add(new Condition(
                "Chronic Anemia",
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
                "Chronic Ventricular Systolic Disfunction"
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
        
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout(new BorderLayout()); 
        spacePanel.setPreferredSize(new Dimension(150, 230));
        spacePanel.setBackground(Color.LIGHT_GRAY);

        sectionsPanel.add(buttonPanel);
        sectionsPanel.add(spacePanel);

                          
        /*
         * END CONDITIONS
         */
       
        // Add scrollPane
        scrollConditionPane = new JScrollPane(sectionsPanel);
        scrollConditionPane.setPreferredSize(new Dimension(400, 600));
        scrollConditionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollConditionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //set all conditions to 0 and remove them
        resetConditions.addActionListener(e -> {
            for (Condition condition : conditions) {
            	if(condition.isActive()) {
            		if(condition.getActiveCondition() != null) {
                        removeCondition(condition.getActiveCondition());
                        MiniLogPanel.append(condition.getTitle()+" removed\n");            			
            		}
                    condition.switchActive();
                    condition.enable();
            	}
                condition.setUnloadedCondition();
            }
        });

    }
    
    //method to return the panel
    public JScrollPane getConditionScrollPane() {
        return scrollConditionPane;
    }
    
    public void enableButtonStates() {
        for (Condition condition : conditions) {
        	condition.enable();
        }
        resetConditions.setEnabled(true);
    }
    
    public void disableButtonStates() {
        for (Condition condition : conditions) {
        	condition.disable();
        }
        resetConditions.setEnabled(false);
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
    
    public JButton getRemoveAllConditionsButton() {
    	return resetConditions;
    }
    
    /*SETTING INITIAL CONDITIONS FROM FILE*/
    
    public void setInitialConditions(SECondition any) {
        String name = "";
        ArrayList<Double> values = new ArrayList<Double>();

        switch (any.getClass().getSimpleName()) {
            case "SEChronicAnemia":
                SEChronicAnemia ca = (SEChronicAnemia) any;
                name = "Chronic Anemia";
                values.add(ca.getReductionFactor().getValue());
                break;
            case "SEAcuteRespiratoryDistressSyndrome":
            	SEAcuteRespiratoryDistressSyndrome ards = (SEAcuteRespiratoryDistressSyndrome) any;
            	name = "ARDS";
            	values.add(ards.getSeverity(eLungCompartment.LeftLung).getValue());
            	values.add(ards.getSeverity(eLungCompartment.RightLung).getValue());
            	break;
            case "SEChronicObstructivePulmonaryDisease":
            	SEChronicObstructivePulmonaryDisease copd = (SEChronicObstructivePulmonaryDisease) any;
            	name = "COPD";
            	values.add(copd.getBronchitisSeverity().getValue());
            	values.add(copd.getEmphysemaSeverity(eLungCompartment.LeftLung).getValue());
            	values.add(copd.getEmphysemaSeverity(eLungCompartment.RightLung).getValue());
            	break;
            case "SEChronicPericardialEffusion":
            	SEChronicPericardialEffusion cpe = (SEChronicPericardialEffusion) any;
            	name = "Pericardial Effusion";
            	values.add(cpe.getAccumulatedVolume().getValue());
            	break;
            case "SEChronicRenalStenosis":
            	SEChronicRenalStenosis crs = (SEChronicRenalStenosis) any;
            	name = "Renal Stenosis";
            	values.add(crs.getLeftKidneySeverity().getValue());
            	values.add(crs.getRightKidneySeverity().getValue());
            	break;
            case "SEChronicVentricularSystolicDysfunction":
            	//SEChronicVentricularSystolicDysfunction cvsd = (SEChronicVentricularSystolicDysfunction) any;
            	name = "Chronic Ventricular Systolic Disfunction";
            	break;
            case "SEImpairedAlveolarExchange":
            	//SEImpairedAlveolarExchange iae = (SEImpairedAlveolarExchange) any;
            	name = "Impaired Alveolar Exchange";
            	break;
            case "SEPneumonia":
            	SEPneumonia p = (SEPneumonia) any;
            	name = "Pneumonia";
            	values.add(p.getSeverity(eLungCompartment.LeftLung).getValue());
            	values.add(p.getSeverity(eLungCompartment.RightLung).getValue());
            	break;
            case "SEPulmonaryFibrosis":
            	SEPulmonaryFibrosis pf = (SEPulmonaryFibrosis) any;
            	name = "Pulmonary Fibrosis";
            	values.add(pf.getSeverity().getValue());
            	break;
            case "SEPulmonaryShunt":
            	SEPulmonaryShunt ps = (SEPulmonaryShunt) any;
            	name = "Pulmonary Shunt";
            	values.add(ps.getSeverity().getValue());
            	break;
        }
        
        for (Condition condition : conditions) {
        	if(condition.getTitle().equals(name)) {
        		condition.setLoadedCondition(values);
        	}
        }
    }
    
    public void setInitialConditionsTo0() {
        for (Condition condition : conditions) {
        	condition.setUnloadedCondition();
        }
    }
}
