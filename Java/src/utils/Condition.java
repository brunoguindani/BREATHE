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
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.patient.conditions.SEChronicAnemia;

import app.SimulationWorker;

public class Condition {
    public JPanel sectionPanel;
    private String title;
    private ArrayList<JComponent> components = new ArrayList<JComponent>();
    private JButton applySectionButton;
    private boolean enabled = false;
    private SECondition condition;

    public Condition(String title, JComponent... components) {
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
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton sourceButton = (JButton) e.getSource();

                if (!SimulationWorker.started) {
                    boolean success = false;

                    switch (title) {
	                    case "Anemia":
	                        SEChronicAnemia anemia = new SEChronicAnemia();
	                        anemia.getReductionFactor().setValue(0.3);
	                        
	                        if (components.get(0) instanceof JSpinner) {
	                            JSpinner redFactorField = (JSpinner) components.get(0);
	                            try {
	                                double redFactorValue = (double) redFactorField.getValue();
	                                anemia.getReductionFactor().setValue(redFactorValue);
	                                success = sendAction(anemia);
	                            } catch (NumberFormatException ex) {
	                                Log.error("Invalid input for severity value");
	                            }
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
    
    private boolean sendAction(SECondition e) {
    	boolean success = false;
        if(!enabled) {
            success = SimulationWorker.addCondition(e);
            enabled = !enabled;
            disableFields();
            applySectionButton.setText("Remove");
            condition = e;
        }else {
            success = SimulationWorker.removeCondition(condition);
            enabled = !enabled;
            enableFields();
            applySectionButton.setText("Apply");
        }	
        return success;
    }

    // Metodo per aggiornare lo stato del bottone
    public void enableButtonState() {
        applySectionButton.setEnabled(true);
    }
    
    public void disableButtonState() {
        applySectionButton.setEnabled(false);
    }
    
    public void enableFields() {
        for (JComponent component : components) {
        	component.setEnabled(true);
        }
    }
    
    public void disableFields() {
        for (JComponent component : components) {
        	component.setEnabled(false);
        }
    }
}
