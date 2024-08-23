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
import javax.swing.Timer;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.patient.actions.*;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.*;
import app.SimulationWorker;

public class Action {
    public JPanel sectionPanel;
    private String title;
    private ArrayList<JComponent> components = new ArrayList<JComponent>();
    private JButton applySectionButton;

    public Action(String title, JComponent... components) {
        this.title = title;
        
        sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout());
        //sectionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));  
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
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton sourceButton = (JButton) e.getSource();

                if (SimulationWorker.started) {
                    boolean success = false;
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
                                success = SimulationWorker.pe.processAction(ards);
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
                                success = SimulationWorker.pe.processAction(stress);
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
                                success = SimulationWorker.pe.processAction(obstruction);
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Ashtma Attack":
                            SEAcuteStress ashtma = new SEAcuteStress();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                ashtma.getSeverity().setValue(value);
                                success = SimulationWorker.pe.processAction(ashtma);
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
                                success = SimulationWorker.pe.processAction(brainInjury);
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
                                success = SimulationWorker.pe.processAction(bronchoconstriction);
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
                                success = SimulationWorker.pe.processAction(copd);
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
                                success = SimulationWorker.pe.processAction(dyspnea);
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
                                success = SimulationWorker.pe.processAction(exercise);
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Pericardial Effusion":
                            SEPericardialEffusion effusion = new SEPericardialEffusion();
                            try {
                            	field = (JSpinner) components.get(0);
                                value = (double) field.getValue();
                                effusion.getEffusionRate().setValue(value);
                                success = SimulationWorker.pe.processAction(effusion);
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
                                success = SimulationWorker.pe.processAction(pneumonia);
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
                                success = SimulationWorker.pe.processAction(shunt);
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
                                success = SimulationWorker.pe.processAction(fatigue);
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                        case "Urinate":
                            SEUrinate urinate = new SEUrinate();
                            try {
                                success = SimulationWorker.pe.processAction(urinate);
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
                                success = SimulationWorker.pe.processAction(leak);
                            } catch (NumberFormatException ex) {
                                Log.error("Invalid input for severity value");
                            }
                            break;
                    }
                } 
            }
        };
    }

    private void setButtonColor(JButton button, Color color) {
        button.setBackground(color);
        button.setOpaque(true);
        button.repaint();
    }

    // Metodo per aggiornare lo stato del bottone
    public void enableButtonState() {
        applySectionButton.setEnabled(true);
    }
    
    public void disableButtonState() {
        applySectionButton.setEnabled(false);
    }
}
