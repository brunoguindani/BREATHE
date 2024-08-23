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
                new JLabel("Red. Factor"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
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
