package utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.patient.conditions.*;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;

import app.SimulationWorker;
import panels.ConditionPanel;
import panels.MiniLogPanel;

public class Condition {
	/*
	 * Handling of different Conditions subPanel
	 */
    public JPanel sectionPanel;
    private String title;
    private ArrayList<JComponent> components = new ArrayList<JComponent>();
    private JButton applySectionButton;
    private boolean enabled = false;
    private SECondition condition;

    public Condition(String title, JComponent... components) {
    	//This method is only for GUI
        this.title = title;
        
        sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout()); 
        sectionPanel.setBackground(Color.LIGHT_GRAY);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        JButton headerButton = new JButton(title);
        headerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerButton.setBackground(Color.DARK_GRAY);
        headerButton.setForeground(Color.WHITE);
        headerButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        headerButton.setFocusPainted(false);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  
        fieldsPanel.setBackground(Color.LIGHT_GRAY);
        fieldsPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        int gridX = 0;
        for (JComponent component : components) {
            if (component instanceof JLabel) {
                gbc.gridx = gridX;
                gbc.gridwidth = 1;
                fieldsPanel.add(component, gbc);
                gridX++;
            } else {
                this.components.add(component);
                component.setPreferredSize(new Dimension(100, 40)); // Imposta dimensione preferita
                component.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Aggiungi padding interno
                gbc.gridx = gridX;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                fieldsPanel.add(component, gbc);
                gridX = 0; 
                gbc.gridy++;
            }
        }

        applySectionButton = new JButton("Apply");
        applySectionButton.setPreferredSize(new Dimension(120, 30));
        applySectionButton.setBackground(new Color(0, 122, 255)); 
        applySectionButton.setForeground(Color.WHITE);
        applySectionButton.setFocusPainted(false);
        applySectionButton.setMargin(new Insets(0, 0, 0, 0));  
        applySectionButton.setEnabled(true); // Initially enabled

        applySectionButton.addActionListener(buttonAction());

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy++;
        fieldsPanel.add(applySectionButton, gbc);

        headerButton.addActionListener(e -> {
            boolean isVisible = !fieldsPanel.isVisible();
            fieldsPanel.setVisible(isVisible);
            headerButton.setText(isVisible ? title + " (Chiudi)" : title);
            sectionPanel.revalidate();
            sectionPanel.repaint();
        });

        headerPanel.add(headerButton, BorderLayout.NORTH);
        headerPanel.add(fieldsPanel, BorderLayout.CENTER);
        sectionPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private ActionListener buttonAction() {
    	//Changes action completed at button pression depending on action name
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!SimulationWorker.started) {
                    JSpinner field;
                    double value;

                    switch (title) {
	                    case "Anemia":
	                        SEChronicAnemia anemia = new SEChronicAnemia();	                        
                            try {
                                field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                anemia.getReductionFactor().setValue(value);
                                sendAction(anemia,"Anemia");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;
	                    case "ARDS":
	                        SEAcuteRespiratoryDistressSyndrome ARDS = new SEAcuteRespiratoryDistressSyndrome();   
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                ARDS.getSeverity(eLungCompartment.LeftLung).setValue(value);
                            	field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                ARDS.getSeverity(eLungCompartment.RightLung).setValue(value);
                                sendAction(ARDS,"ARDS");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;
	                    case "COPD":
	                        SEChronicObstructivePulmonaryDisease COPD = new SEChronicObstructivePulmonaryDisease();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                COPD.getBronchitisSeverity().setValue(value);
                            	field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                COPD.getEmphysemaSeverity(eLungCompartment.LeftLung).setValue(value);
                            	field = (JSpinner) components.get(2);
                                value = (double) field.getValue();
                                COPD.getEmphysemaSeverity(eLungCompartment.RightLung).setValue(value);
                                sendAction(COPD,"COPD");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;   
	                    case "Pericardial Effusion":
	                        SEChronicPericardialEffusion CPE = new SEChronicPericardialEffusion();	                        
                            try {
                                field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                CPE.getAccumulatedVolume().setValue(value, VolumeUnit.mL);
                                sendAction(CPE, "Pericardial Effusion");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break; 
	                    case "Renal Stenosis":
	                        SEChronicRenalStenosis Stenosis = new SEChronicRenalStenosis();   
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                Stenosis.getLeftKidneySeverity().setValue(value);
                            	field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                Stenosis.getRightKidneySeverity().setValue(value);
                                sendAction(Stenosis,"Renal Stenosis");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;	  
	                    case "Ventricular Systolic Disfunction":
	                        SEChronicVentricularSystolicDysfunction VSD = new SEChronicVentricularSystolicDysfunction();   
                            try {
                                sendAction(VSD,"Ventricular Systolic Disfunction");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;	
	                    case "Impaired Alveolar Exchange":
	                        SEImpairedAlveolarExchange IAE = new SEImpairedAlveolarExchange();   
                            try {
                                sendAction(IAE,"Impaired Alveolar Exchange");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;		  
	                    case "Pneumonia":
	                        SEPneumonia Pneumonia = new SEPneumonia();   
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                Pneumonia.getSeverity(eLungCompartment.LeftLung).setValue(value);
                            	field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                Pneumonia.getSeverity(eLungCompartment.RightLung).setValue(value);
                                sendAction(Pneumonia,"Pneumonia");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;
	                    case "Pulmonary Fibrosis":
	                        SEPulmonaryFibrosis fibrosis = new SEPulmonaryFibrosis();	                        
                            try {
                                field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                fibrosis.getSeverity().setValue(value);
                                sendAction(fibrosis,"Pulmonary Fibrosis");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;
	                    case "Pulmonary Shunt":
	                    	SEPulmonaryShunt shunt = new SEPulmonaryShunt();	                        
                            try {
                                field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                shunt.getSeverity().setValue(value);
                                sendAction(shunt,"Pulmonary Shunt");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input");
                            }	                        
	                        break;	                        
                    }                 
                } 
            }
        };
    }
    
    //Add or remove conditions from activeCondition
    private boolean sendAction(SECondition e, String name) {
    	boolean success = false;
        if(!enabled) {
            success = ConditionPanel.addCondition(e);
            MiniLogPanel.append(name+" applied");
            enabled = !enabled;
            disableFields();
            applySectionButton.setText("Remove");
            condition = e;
        }else {
            success = ConditionPanel.removeCondition(condition);
            MiniLogPanel.append(name+" removed");
            enabled = !enabled;
            enableFields();
            applySectionButton.setText("Apply");
        }	
    	System.out.println(success);
        return success;
    }

    private void enableButtonState() {
        applySectionButton.setEnabled(true);
    }
    
    private void disableButtonState() {
        applySectionButton.setEnabled(false);
    }
    
    private void enableFields() {
        for (JComponent component : components) {
        	component.setEnabled(true);
        }
    }
    
    private void disableFields() {
        for (JComponent component : components) {
        	component.setEnabled(false);
        }
    }
    
    public void enable() {
    	enableFields();
    	enableButtonState();
    }
    
    public void disable() {
    	disableFields();
    	disableButtonState();
    }
    
    public String getTitle() {
    	return title;
    }
}
