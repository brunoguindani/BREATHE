package tests;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;

public class ActionPanelTest {
    private JPanel sectionsPanel = new JPanel();  // Pannello per le sezioni espandibili
    private JButton applyButton;
    private JScrollPane scrollActionPane;
    private JPanel actionPanel;  // Nuovo JPanel che contiene lo JScrollPane e il pulsante "Applica"

    public ActionPanelTest() {
        // Configurazione del pannello delle sezioni
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sectionsPanel.setBackground(Color.LIGHT_GRAY);

        // Aggiungi le sezioni espandibili
        for (int i = 1; i <= 300; i++) {  // Modificato a 3 sezioni per esempio
            sectionsPanel.add(createExpandableSection("Azione " + i, "Campo A" + i, "Campo B" + i, "Campo C" + i));
        }

        // Pannello per il pulsante "Applica"
        JPanel applyPanel = new JPanel();
        applyPanel.setLayout(new GridBagLayout());  // Usa GridBagLayout per una disposizione precisa
        applyPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));  // Margine superiore per il pulsante
        applyPanel.setBackground(Color.LIGHT_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);  // Nessun spazio tra i bottoni
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;

        // Pulsante "Applica"
        applyButton = new JButton("Applica");
        applyButton.setPreferredSize(new Dimension(120, 30));
        applyButton.setBackground(new Color(0, 122, 255));  // Blu
        applyButton.setForeground(Color.WHITE);
        applyButton.setFocusPainted(false);
        applyButton.setMargin(new Insets(0, 0, 0, 0));  // Rimuove i margini del pulsante
        applyButton.addActionListener(e -> {
            // Logica per il pulsante "Applica"
            JOptionPane.showMessageDialog(sectionsPanel, "Settings Applied");
        });

        // Aggiungi il pulsante "Applica" al pannello
        applyPanel.add(applyButton, gbc);

        // Aggiungi lo scroll pane
        scrollActionPane = new JScrollPane(sectionsPanel);
        scrollActionPane.setPreferredSize(new Dimension(400, 600));
        scrollActionPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollActionPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Nuovo pannello che contiene lo scroll pane e il pannello del pulsante "Applica"
        actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.add(scrollActionPane, BorderLayout.CENTER);
        actionPanel.add(applyPanel, BorderLayout.SOUTH);  // Posiziona il pannello del pulsante "Applica" in basso
    }

    private JPanel createExpandableSection(String title, String... fieldLabels) {
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
        gbc.gridx = 0;
        gbc.gridy = 0;

        for (String label : fieldLabels) {
            addLabelAndField(label, new JTextField(12), fieldsPanel, gbc);
        }

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
