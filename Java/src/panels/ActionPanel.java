package panels;

import java.awt.*;
import javax.swing.*;

import utils.Action;

public class ActionPanel {
    private JPanel sectionsPanel = new JPanel();  
    private JScrollPane scrollActionPane;
    private JPanel actionPanel;  
    private Action[] actions;

    public ActionPanel() {
        // Configurazione del pannello delle sezioni
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        /*
         * AZIONI
         */
        actions = new Action[] {
                new Action(
                    "ARDS Exacerbation",
                    new JLabel("LLung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01)),
                    new JLabel("RLung Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
                ),
                new Action(
                    "Airway Obstruction",
                    new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
                ),
                new Action(
                    "Dyspnea",
                    new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
                )
            };

            for (Action action : actions) {
                sectionsPanel.add(action.sectionPanel);
            }
            
            sectionsPanel.add(Box.createVerticalStrut(Math.max(0, 560 - 10 * actions.length))); // da rimuovere piu avanti
            
        /*
         * FINE AZIONI
         */
        // Aggiungi lo scroll pane
        scrollActionPane = new JScrollPane(sectionsPanel);
        scrollActionPane.setPreferredSize(new Dimension(400, 600));
        scrollActionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
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
