package inputItems;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import app.App;
import data.Condition;

public class ConditionBox {
    
    private JPanel sectionPanel;
    private JButton applySectionButton;
    private JButton headerButton;
    
    private App app;
    
    private String title;
    private Map<String, JComponent> components;

    private boolean applied = false;
    
    public ConditionBox(App app, String title, Map<String, JComponent> components) {
    	
    	this.app = app;
        this.title = title;
        this.components = components;
        
        // Pannello principale che conterrà il tutto
        sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.LIGHT_GRAY);
        
        // Pannello che contiene il titolo e il pulsante di espansione
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        
        // Pulsante per espandere/contrarre i componenti
        headerButton = new JButton(title);
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
            
            Dimension minSize = new Dimension(100, 30); // 100px larghezza, 50px altezza
            entry.getValue().setMinimumSize(minSize);
            entry.getValue().setPreferredSize(minSize); 
            gbc.gridx = gridX++;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            fieldsPanel.add(entry.getValue(), gbc);
            
            gbc.gridy++;
            gridX = 0;
        }
        
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
        gbc.gridwidth = 2;
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
                if (!applied) {
                    // Azione per quando si applica la condizione
                    enableFields(false);  
                    applySectionButton.setText("Rimuovi");
                    headerButton.setBackground(new Color(100, 149, 237));
                    
                	Map<String,Double> parameters = new HashMap<>();
                	for (Map.Entry<String, JComponent> entry : components.entrySet()) {
            		   String chiave = entry.getKey();
            		   JComponent component = entry.getValue();
            		    
            		   Double valore = null;
            		   if (component instanceof JSpinner) {
            		       valore = ((JSpinner) component).getValue() instanceof Number ? 
            		                ((Number) ((JSpinner) component).getValue()).doubleValue() : null;
            		   } else {
            		       valore = Double.parseDouble(component.toString());
            		   }
            		    
            		   parameters.put(chiave, valore);
                	}
                    
                    app.applyCondition(new Condition(title,parameters));
                    applied = true;
                } else {
                    // Azione per quando si rimuove la condizione
                    enableFields(true);  
                    applySectionButton.setText("Applica");
                    headerButton.setBackground(Color.DARK_GRAY);
                    app.removeCondition(title);
                    applied = false;
                }
            }
        };
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
        return applied;
    }
    
    // Metodo per ottenere il titolo
    public String getTitle() {
        return title;
    }
    
    public void enableBox(boolean enable) {
    	enableFields(enable);
    	applySectionButton.setEnabled(enable);
    }
    
    public void reset() {
        enableFields(true);  
        applySectionButton.setText("Applica");
        headerButton.setBackground(Color.DARK_GRAY);
        for (Map.Entry<String, JComponent> entry : components.entrySet()) {
            if (entry.getValue() instanceof JSpinner) {
                JSpinner spinner = (JSpinner) entry.getValue();
                spinner.setValue(0);  
            }
        }
        applied = false;
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
