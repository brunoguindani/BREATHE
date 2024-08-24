package panels;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import utils.Action;

public class ActionPanel {
    private JPanel sectionsPanel = new JPanel();  
    private JScrollPane scrollActionPane;
    private JPanel actionPanel;  
    private List<Action> actions = new ArrayList<>();

    public ActionPanel() {
        // Configurazione del pannello delle sezioni
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        /*
         * AZIONI
         */
        actions = new ArrayList<>();
        
        actions.add(new Action(
            "ARDS Exacerbation",
            new JLabel("Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Acute Stress",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));  
        actions.add(new Action(
            "Airway Obstruction",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));        
        actions.add(new Action(
            "Asthma Attack",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));   
        actions.add(new Action(
            "Brain Injury",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));  
        actions.add(new Action(
            "Bronchoconstriction",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        )); 
        actions.add(new Action(
            "COPD Exacerbation",
            new JLabel("Bronchitis Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Emphysema Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Emphysema Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Dyspnea",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Exercise",
            new JLabel("Intensity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Pericardial Effusion",
            new JLabel("Effusion Rate (mL/min)"), new JSpinner(new SpinnerNumberModel(0, 0, 1000, 0.01))
        ));
        actions.add(new Action(
            "Pneumonia Exacerbation",
            new JLabel("Left Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
            new JLabel("Right Lung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));  
        actions.add(new Action(
            "Pulmonary Shunt Exacerbation",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Respiratory Fatigue",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        actions.add(new Action(
            "Urinate"
        ));
        actions.add(new Action(
            "Ventilator Leak",
            new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        ));
        for (Action action : actions) {
            sectionsPanel.add(action.sectionPanel);
        }
            
       sectionsPanel.add(Box.createVerticalStrut(Math.max(0, 560 - 10 * actions.size()))); // da rimuovere piu avanti
            
        /*
         * FINE AZIONI
         */
        // Aggiungi lo scroll pane
        scrollActionPane = new JScrollPane(sectionsPanel);
        scrollActionPane.setPreferredSize(new Dimension(400, 600));
        scrollActionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollActionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Nuovo pannello che contiene lo scroll pane e il pannello del pulsante "Applica"
        actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.add(scrollActionPane, BorderLayout.CENTER);
    }

    public JPanel getActionPanel() {
        return actionPanel;
    }
    
    public JScrollPane getActionScrollPane() {
        return scrollActionPane;
    }
    
    public void enableButtonStates() {
        for (Action action : actions) {
            action.enableButtonState();
        }
    }
    
    public void disableButtonStates() {
        for (Action action : actions) {
            action.disableButtonState();
        }
    }
}
