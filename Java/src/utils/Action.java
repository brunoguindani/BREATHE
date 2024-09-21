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
import com.kitware.pulse.cdm.patient.actions.*;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.*;
import app.SimulationWorker;
import panels.MiniLogPanel;

public class Action {
	/*
	 * Handling of different Actions subPanel
	 */
    public JPanel sectionPanel;
    private String title;
    private ArrayList<JComponent> components = new ArrayList<JComponent>();
    private JButton applySectionButton;

    public Action(String title, JComponent... components) {
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
                component.setPreferredSize(new Dimension(100, 40)); 
                component.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
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
        applySectionButton.setEnabled(false); // Initially disabled

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

                if (SimulationWorker.started) {
                    JSpinner field;
                    double value;

                    switch (title) {
                    	case "ARDS Exacerbation":
	                        SEAcuteRespiratoryDistressSyndromeExacerbation ards = new SEAcuteRespiratoryDistressSyndromeExacerbation();	                        
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                ards.getSeverity(eLungCompartment.LeftLung).setValue(value);
                                field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                ards.getSeverity(eLungCompartment.RightLung).setValue(value);
                                SimulationWorker.pe.processAction(ards);
                                MiniLogPanel.append("ARDS Exacerbation applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Acute Stress":
                            SEAcuteStress stress = new SEAcuteStress();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                stress.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(stress);
                                MiniLogPanel.append("Acute Stress applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Airway Obstruction":
                            SEAirwayObstruction obstruction = new SEAirwayObstruction();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                obstruction.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(obstruction);
                                MiniLogPanel.append("Airway Obstruction applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Asthma Attack":
                            SEAsthmaAttack ashtma = new SEAsthmaAttack();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                ashtma.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(ashtma);
                                MiniLogPanel.append("Asthma Attack applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Brain Injury":
                            SEAcuteStress brainInjury = new SEAcuteStress();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                brainInjury.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(brainInjury);
                                MiniLogPanel.append("Brain Injury applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Bronchoconstriction":
                            SEBronchoconstriction bronchoconstriction = new SEBronchoconstriction();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                bronchoconstriction.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(bronchoconstriction);
                                MiniLogPanel.append("Bronchoconstriction applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                    	case "COPD Exacerbation":
	                        SEChronicObstructivePulmonaryDiseaseExacerbation copd = new SEChronicObstructivePulmonaryDiseaseExacerbation();	                        
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                copd.getBronchitisSeverity().setValue(value);
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                copd.getEmphysemaSeverity(eLungCompartment.LeftLung).setValue(value);
                                field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                copd.getEmphysemaSeverity(eLungCompartment.RightLung).setValue(value);
                                SimulationWorker.pe.processAction(copd);
                                MiniLogPanel.append("COPD Exacerbation applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Dyspnea":
                            SEDyspnea dyspnea = new SEDyspnea();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                dyspnea.getRespirationRateSeverity().setValue(value);
                                SimulationWorker.pe.processAction(dyspnea);
                                MiniLogPanel.append("Dyspnea applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Exercise":
                            SEExercise exercise = new SEExercise();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                exercise.getIntensity().setValue(value);
                                SimulationWorker.pe.processAction(exercise);
                                MiniLogPanel.append("Exercise applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Pericardial Effusion":
                            SEPericardialEffusion effusion = new SEPericardialEffusion();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                effusion.getEffusionRate().setValue(value, VolumePerTimeUnit.mL_Per_min);
                                SimulationWorker.pe.processAction(effusion);
                                MiniLogPanel.append("Pericardial Effusion applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                    	case "Pneumonia Exacerbation":
	                        SEPneumoniaExacerbation pneumonia = new SEPneumoniaExacerbation();	                        
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                pneumonia.getSeverity(eLungCompartment.LeftLung).setValue(value);
                                field = (JSpinner) components.get(1);
                                value = (double) field.getValue();
                                pneumonia.getSeverity(eLungCompartment.RightLung).setValue(value);
                                SimulationWorker.pe.processAction(pneumonia);
                                MiniLogPanel.append("Pneumonia Exacerbation applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Pulmonary Shunt Exacerbation":
                            SEPulmonaryShuntExacerbation shunt = new SEPulmonaryShuntExacerbation();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                shunt.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(shunt);
                                MiniLogPanel.append("Pulmonary Shunt Exacerbation applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Respiratory Fatigue":
                            SERespiratoryFatigue fatigue = new SERespiratoryFatigue();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                fatigue.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(fatigue);
                                MiniLogPanel.append("Respiratory Fatigue applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Urinate":
                            SEUrinate urinate = new SEUrinate();
                            try {
                                SimulationWorker.pe.processAction(urinate);
                                MiniLogPanel.append("Urinate applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Ventilator Leak":
                            SEMechanicalVentilatorLeak leak = new SEMechanicalVentilatorLeak();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                leak.getSeverity().setValue(value);
                                SimulationWorker.pe.processAction(leak);
                                MiniLogPanel.append("Ventilator Leak applied");
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                    }
                } 
            }
        };
    }

    // Metodo per aggiornare lo stato del bottone
    public void enableButtonState() {
        applySectionButton.setEnabled(true);
    }
    
    public void disableButtonState() {
        applySectionButton.setEnabled(false);
    }
}
