package panels;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;

import utils.Action;
import utils.Condition;

public class ConditionPanel {
    private JPanel sectionsPanel = new JPanel();  
    private JScrollPane scrollConditionPane;
    private JPanel conditionPanel;  
    private List<Condition> conditions = new ArrayList<>();

    public ConditionPanel() {
        // Configurazione del pannello delle sezioni
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        /*
         * CONDIZIONI
         */
        conditions = new ArrayList<>();
        
        conditions.add(new Condition(
                "Anemia",
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
                new JLabel("Accumulated Volume (mL)"), new JSpinner(new SpinnerNumberModel(0, 0, 100, 1))
            ));
        conditions.add(new Condition(
                "Renal Stenosis",
                new JLabel("Left Kidney Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Right Kidney Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        	));
        conditions.add(new Condition(
                "Ventricular Systolic Disfunction"
        	));
        conditions.add(new Condition( //Questo qui è un po' da sistemare
                "Impaired Alveolar Exchange (Not implemented yet)",
                new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Impaired Faction"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                new JLabel("Impaired Surface Area (m^2)"), new JSpinner(new SpinnerNumberModel(0, 0, 100, 1))
        	));  
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
            
       sectionsPanel.add(Box.createVerticalStrut(Math.max(0, 560 - 10 * conditions.size()))); // da rimuovere piu avanti
            
        /*
         * FINE CONDIZIONI
         */
       
        // Aggiungi lo scroll pane
        scrollConditionPane = new JScrollPane(sectionsPanel);
        scrollConditionPane.setPreferredSize(new Dimension(400, 600));
        scrollConditionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollConditionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Nuovo pannello che contiene lo scroll pane e il pannello del pulsante "Applica"
        conditionPanel = new JPanel();
        conditionPanel.setLayout(new BorderLayout());
        conditionPanel.add(scrollConditionPane, BorderLayout.CENTER);
    }

    public JPanel getConditionPanel() {
        return conditionPanel;
    }
    
    public JScrollPane getConditionScrollPane() {
        return scrollConditionPane;
    }
    
    public void enableButtonStates() {
        for (Condition condition : conditions) {
        	condition.enableButtonState();
        }
    }
    
    public void disableButtonStates() {
        for (Condition condition : conditions) {
        	condition.disableButtonState();
        }
    }
}
