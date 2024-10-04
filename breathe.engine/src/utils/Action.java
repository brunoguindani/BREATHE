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
import javax.swing.SpinnerNumberModel;

import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.patient.actions.*;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.*;

import app.App;
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
    private App app;
    
    private JSpinner[] time = new JSpinner[3]; //0 hours, 1 minutes, 2 seconds
    
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
        
        JLabel timeLabel = new JLabel("Time (hh:mm:ss)");

        time[0] = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)); 
        time[1] = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1)); 
        time[2] = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1)); 

        for (JComponent spinner : time) {
            spinner.setPreferredSize(new Dimension(60, 30));
        }

        JButton plusButton = new JButton("+");
        plusButton.setPreferredSize(new Dimension(40, 30));
        plusButton.setFocusPainted(false);
        plusButton.setToolTipText("Add to Scenario");
        
        plusButton.addActionListener(e -> {
            int totalSeconds = getTotalTimeInSeconds();
            app.scenario.addAction(getAction(), totalSeconds);
            app.rightTabbedPane.setSelectedIndex(1);
        });

        JPanel timePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTime = new GridBagConstraints();
        gbcTime.insets = new Insets(0, 2, 0, 0);

        gbcTime.gridy = 0;
        gbcTime.anchor = GridBagConstraints.WEST;
        
        gbcTime.gridx = 0; 
        timePanel.add(timeLabel, gbcTime);
        
        gbcTime.gridx++; 
        timePanel.add(time[0], gbcTime);

        gbcTime.gridx++;
        timePanel.add(time[1], gbcTime);

        gbcTime.gridx++;
        timePanel.add(time[2], gbcTime);

        gbcTime.gridx++;
        timePanel.add(plusButton, gbcTime);
        gbcTime.gridx++;
        
        timePanel.setBackground(Color.LIGHT_GRAY);
        gbc.gridx--;
        fieldsPanel.add(timePanel, gbc);

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
            headerButton.setText(isVisible ? title + " (Close)" : title);
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
            	SEAction action = getAction();
            	SimulationWorker.pe.processAction(action);
            	MiniLogPanel.append("\nApplying\n" + action.toString()+"");
            }
        };
    }
    
    private SEAction getAction() {
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
                    return ards;
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
                    return stress;
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
                    return obstruction;
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
                    return ashtma;
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
                    return brainInjury;
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
                    return bronchoconstriction;
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
                    return copd;
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
                    return dyspnea;
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
                    return exercise;
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
                    return effusion;
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
                    return pneumonia;
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
                    return shunt;
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
                    return fatigue;
                } catch (NumberFormatException ex) {
                    Log.error("Invalid input for severity value");
                }
                break;
            case "Urinate":
                SEUrinate urinate = new SEUrinate();
                try {
                	return urinate;
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
                    return leak;
                } catch (NumberFormatException ex) {
                    Log.error("Invalid input for severity value");
                }
                break;
        }
    
        return null;
    }

    public void enableButtonState() {
        applySectionButton.setEnabled(true);
    }
    
    public void disableButtonState() {
        applySectionButton.setEnabled(false);
    }
    
    public int getTotalTimeInSeconds() {
        int hours = (Integer) time[0].getValue();
        int minutes = (Integer) time[1].getValue();
        int seconds = (Integer) time[2].getValue();
        
        // Convertion in seconds
        return hours * 3600 + minutes * 60 + seconds;
    }
    
    public void setApp(App app) {
    	this.app = app;
    }
}
