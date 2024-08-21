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
        sectionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));  
        sectionPanel.setBackground(Color.WHITE);

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
        fieldsPanel.setBackground(Color.WHITE);
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

                    switch (title) {
                        case "Airway Obstruction":
                            SEAirwayObstruction h1 = new SEAirwayObstruction();
                            if (components.get(0) instanceof JSpinner) {
                                JSpinner severityField = (JSpinner) components.get(0);
                                try {
                                    double severityValue = (double) severityField.getValue();
                                    h1.getSeverity().setValue(severityValue);
                                    success = SimulationWorker.pe.processAction(h1);
                                } catch (NumberFormatException ex) {
                                    Log.error("Invalid input for severity value");
                                }
                            }
                            break;
                        case "Dyspnea":
                            SEDyspnea h2 = new SEDyspnea();
                            if (components.get(0) instanceof JSpinner) {
                                JSpinner severityField = (JSpinner) components.get(0);
                                try {
                                    double severityValue = (double) severityField.getValue();
                                    h2.getTidalVolumeSeverity().setValue(severityValue);
                                    success = SimulationWorker.pe.processAction(h2);
                                } catch (NumberFormatException ex) {
                                    Log.error("Invalid input for severity value");
                                }
                            }
                            break;
                        case "ARDS Exacerbation":
                            SEAcuteRespiratoryDistressSyndromeExacerbation h3 = new SEAcuteRespiratoryDistressSyndromeExacerbation();
                            if (components.get(0) instanceof JSpinner && components.get(1) instanceof JSpinner) {
                                JSpinner leftLungField = (JSpinner) components.get(0);
                                JSpinner rightLungField = (JSpinner) components.get(1);
                                try {
                                    double leftLungValue = (double) leftLungField.getValue();
                                    double rightLungValue = (double) rightLungField.getValue();
                                    h3.getSeverity(eLungCompartment.LeftLung).setValue(leftLungValue);
                                    h3.getSeverity(eLungCompartment.RightLung).setValue(rightLungValue);
                                    success = SimulationWorker.pe.processAction(h3);
                                } catch (NumberFormatException ex) {
                                    Log.error("Invalid input for severity value");
                                }
                            }
                            break;
                    }

                    if (success) {
                        // Cambia colore a verde e inizia il fading
                        setButtonColor(sourceButton, Color.GREEN);
                        Timer timer = new Timer(100, new ActionListener() {
                            int step = 0;
                            Color startColor = Color.GREEN;
                            Color endColor = new Color(0, 122, 255); // Colore originale
                            int duration = 20; // Numero di passi per il fading

                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                step++;
                                float ratio = (float) step / duration;
                                int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
                                int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
                                int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);
                                setButtonColor(sourceButton, new Color(red, green, blue));
                                if (step >= duration) {
                                    ((Timer) evt.getSource()).stop();
                                }
                            }
                        });
                        timer.setInitialDelay(1000); // Tempo di attesa in verde prima di iniziare il fading
                        timer.start();
                    } else {
                        Log.error("Engine was unable to process requested actions");
                    }
                } else {
                    // Se il simulatore non è partito
                    Log.error("Simulation not started");
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
