package panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.cdm.patient.actions.*;

import app.SimulationWorker;

public class ActionPanel {
    private JPanel sectionsPanel = new JPanel();  // Pannello per le sezioni espandibili
    private JButton applyButton;
    private JScrollPane scrollActionPane;
    private JPanel actionPanel;  // Nuovo JPanel che contiene lo JScrollPane e il pulsante "Applica"

    public ActionPanel() {
        // Configurazione del pannello delle sezioni
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        /*
         * AZIONI
         */
        sectionsPanel.add(createExpandableSection(
        	    "Airway Obstruction",
        	    new JLabel("Severity"), new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01))
        	)
        );
        // Aggiungi le sezioni espandibili
        /*
        for (int i = 1; i <= 30; i++) {  // Modificato a 3 sezioni per esempio
            sectionsPanel.add(createExpandableSection(
            	    "Sezione 1",
            	    new JLabel("Campo Testo 1"), new JTextField(12),
            	    new JLabel("Campo Numerico"), new JSpinner(new SpinnerNumberModel(0, 0, 100, 1))
            	)
            );
        }*/

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

    private JPanel createExpandableSection(String title, JComponent... components) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BorderLayout());
        sectionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));  // Bordo grigio per le sezioni
        sectionPanel.setBackground(Color.WHITE);

        // Crea il pannello del titolo
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.LIGHT_GRAY);
        JButton headerButton = new JButton(title);
        headerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerButton.setBackground(Color.DARK_GRAY);
        headerButton.setForeground(Color.WHITE);
        headerButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        headerButton.setFocusPainted(false);

        // Crea il pannello dei campi
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Margini dei campi
        fieldsPanel.setBackground(Color.WHITE);
        fieldsPanel.setVisible(false);  // I campi sono inizialmente nascosti

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Spazi tra i campi
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        // Aggiungi i componenti passati come argomenti
        int gridX = 0;
        for (JComponent component : components) {
            if (component instanceof JLabel) {
                gbc.gridx = gridX;
                gbc.gridwidth = 1;
                fieldsPanel.add(component, gbc);
                gridX++;
            } else {
                gbc.gridx = gridX;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                fieldsPanel.add(component, gbc);
                gridX = 0; // Reset per la prossima riga
                gbc.gridy++;
            }
        }

        // Aggiungi un pulsante "Applica" alla fine dei campi
        JButton applySectionButton = new JButton("Apply");
        applySectionButton.setPreferredSize(new Dimension(120, 30));
        applySectionButton.setBackground(new Color(0, 122, 255)); 
        applySectionButton.setForeground(Color.WHITE);
        applySectionButton.setFocusPainted(false);
        applySectionButton.setMargin(new Insets(0, 0, 0, 0));  
        /*applySectionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(sectionPanel, "Settings Applied for " + title);
        });*/
        applySectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cambia il colore del pulsante in verde se la simulazione è iniziata
                if (SimulationWorker.started) {
                    headerButton.setBackground(Color.GREEN);
                    
                    SEAirwayObstruction h = new SEAirwayObstruction();
                    h.getSeverity().setValue(0.8);

                    if (!SimulationWorker.pe.processAction(h)) {
                        Log.error("Engine was unable to process requested actions");
                        return;
                    }

                    // Crea un timer per ripristinare il colore dopo 3 secondi
                    Timer timer = new Timer(1500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            headerButton.setBackground(Color.DARK_GRAY); // Colore originale del pulsante
                        }
                    });
                    timer.setRepeats(false); // Non ripetere il timer
                    timer.start();

                } else {
                    // Cambia il colore del pulsante in rosso se la simulazione non è iniziata
                    headerButton.setBackground(Color.RED);

                    // Crea un timer per ripristinare il colore dopo 3 secondi
                    Timer timer = new Timer(1500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            headerButton.setBackground(Color.DARK_GRAY); // Colore originale del pulsante
                        }
                    });
                    timer.setRepeats(false); // Non ripetere il timer
                    timer.start();
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy++;
        fieldsPanel.add(applySectionButton, gbc);

        // Aggiungi un listener per il pulsante di intestazione
        headerButton.addActionListener(e -> {
            // Mostra o nascondi i campi
            boolean isVisible = !fieldsPanel.isVisible();
            fieldsPanel.setVisible(isVisible);
            headerButton.setText(isVisible ? title + " (Chiudi)" : title);
            // Ridimensiona il pannello della sezione per adattarsi al contenuto
            sectionPanel.revalidate();
            sectionPanel.repaint();
        });

        // Aggiungi il pulsante di intestazione e il pannello dei campi al pannello della sezione
        headerPanel.add(headerButton, BorderLayout.NORTH);
        headerPanel.add(fieldsPanel, BorderLayout.CENTER);
        sectionPanel.add(headerPanel, BorderLayout.NORTH);

        return sectionPanel;
    }

    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridy++;
    }

    public JPanel getActionPanel() {
        return actionPanel;
    }
    
    public JScrollPane getActionScrollPane() {
        return scrollActionPane;
    }
}
