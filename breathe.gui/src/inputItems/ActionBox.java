package inputItems;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;

import app.App_temp;
import data.Action;

public class ActionBox {
    
    private JPanel sectionPanel;
    private String title;
    private Map<String, JComponent> components;
    private JButton applySectionButton;
    private boolean enabled = false;
    
    private JSpinner[] time = new JSpinner[3]; //0 hours, 1 minutes, 2 seconds
    
    public ActionBox(App_temp app, String title, Map<String, JComponent> components) {
        this.title = title;
        this.components = components;
        
        // Pannello principale che conterrà il tutto
        sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.LIGHT_GRAY);
        
        // Pannello che contiene il titolo e il pulsante di espansione
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        
        // Pulsante per espandere/contrarre i componenti
        JButton headerButton = new JButton(title);
        headerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerButton.setBackground(Color.DARK_GRAY);
        headerButton.setForeground(Color.WHITE);
        headerButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        headerButton.setFocusPainted(false);
        
        // Pannello che contiene i campi e il pulsante "Applica"
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        fieldsPanel.setBackground(Color.LIGHT_GRAY);
        fieldsPanel.setVisible(false);  // Inizialmente nascosto
        
        // Layout dei componenti nel pannello
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        int gridX = 0;
        
        // Aggiunge le coppie Label-Componenti al pannello dei campi
        for (Map.Entry<String, JComponent> entry : components.entrySet()) {
            JLabel label = new JLabel(addSpaceBeforeUpperCase(entry.getKey()) + ":");
            gbc.gridx = gridX++;
            fieldsPanel.add(label, gbc);
            
            gbc.gridx = gridX++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            fieldsPanel.add(entry.getValue(), gbc);
            
            gbc.gridy++;
            gridX = 0;
        }
        
        //Time
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
            
            Map<String, Double> parameters = new HashMap<>();

            for (Map.Entry<String, JComponent> entry : components.entrySet()) {
                String key = entry.getKey();
                JComponent component = entry.getValue();

                if (component instanceof JSpinner) {
                    JSpinner spinner = (JSpinner) component;
                    Double value = ((Number) spinner.getValue()).doubleValue(); 
                    parameters.put(key, value);
                }

            }
            
            app.addActiontoScenario(new Action(title, parameters), totalSeconds);
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
        
     // Pulsante "Applica"
        applySectionButton = new JButton("Applica");
        applySectionButton.setPreferredSize(new Dimension(120, 30));
        applySectionButton.setBackground(new Color(0, 122, 255));
        applySectionButton.setForeground(Color.WHITE);
        applySectionButton.setFocusPainted(false);
        applySectionButton.setMargin(new Insets(0, 0, 0, 0));
        applySectionButton.setEnabled(true);  // Abilitato inizialmente
        
        applySectionButton.addActionListener(buttonAction());

        // Aggiunge il pulsante "Applica" alla fine del pannello dei campi
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Assicurati che il pulsante occupi più spazio orizzontale
        gbc.gridy++;
        fieldsPanel.add(applySectionButton, gbc);
        
        // Aggiunge un listener al pulsante header per mostrare/nascondere il pannello dei campi
        headerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isVisible = !fieldsPanel.isVisible();
                fieldsPanel.setVisible(isVisible);
                headerButton.setText(isVisible ? title + " (Close)" : title);
                sectionPanel.revalidate();
                sectionPanel.repaint();
            }
        });
        
        // Aggiunge il pulsante header e i campi al pannello principale
        headerPanel.add(headerButton, BorderLayout.NORTH);
        headerPanel.add(fieldsPanel, BorderLayout.CENTER);
        sectionPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    // Metodo per l'azione del pulsante "Applica"
    private ActionListener buttonAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!enabled) {
                    // Azione per quando si applica la condizione
                    enableFields(false);  // Disabilita i campi
                    applySectionButton.setText("Rimuovi");
                    enabled = true;
                } else {
                    // Azione per quando si rimuove la condizione
                    enableFields(true);  // Riabilita i campi
                    applySectionButton.setText("Applica");
                    enabled = false;
                }
            }
        };
    }
    
    //get total time setted as seconds
    public int getTotalTimeInSeconds() {
        int hours = (Integer) time[0].getValue();
        int minutes = (Integer) time[1].getValue();
        int seconds = (Integer) time[2].getValue();
        
        // Convertion in seconds
        return hours * 3600 + minutes * 60 + seconds;
    }
    
    // Abilita o disabilita i campi
    private void enableFields(boolean enable) {
        for (Map.Entry<String, JComponent> entry : components.entrySet()) {
        	entry.getValue().setEnabled(enable);
        }
    }
    
    // Metodo per ottenere il pannello
    public JPanel getSectionPanel() {
        return sectionPanel;
    }
    
    // Metodo per verificare se è attiva
    public boolean isActive() {
        return enabled;
    }
    
    // Metodo per ottenere il titolo
    public String getTitle() {
        return title;
    }
    
    //add space to title
    private String addSpaceBeforeUpperCase(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Restituisce null o stringa vuota se l'input è nullo o vuoto
        }
        // Utilizza una regex per inserire uno spazio prima di ogni lettera maiuscola
        return input.replaceAll("(?<!^)([A-Z])", " $1").trim();
    }
}