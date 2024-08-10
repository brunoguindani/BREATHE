package tests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {

    private JTextField nameField, ageField, weightField, heightField, bodyFatField;
    private JTextField heartRateField, diastolicField, systolicField, respirationRateField, basalMetabolicRateField;
    private JTextArea resultArea;
    private LineChartPanel chartPanel; // Aggiungi il pannello per il grafico

    public App() {
        setTitle("Pulse Simulation");
        setSize(800, 700);  // Aumenta la dimensione per fare spazio al grafico
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Pannello per gli input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Aggiungi campi di input con GridBagLayout
        addLabelAndField("Name:", nameField = new JTextField("Standard"), inputPanel, gbc);
        addLabelAndField("Age (years):", ageField = new JTextField("44"), inputPanel, gbc);
        addLabelAndField("Weight (lbs):", weightField = new JTextField("170"), inputPanel, gbc);
        addLabelAndField("Height (inches):", heightField = new JTextField("71"), inputPanel, gbc);
        addLabelAndField("Body Fat Fraction:", bodyFatField = new JTextField("0.21"), inputPanel, gbc);
        addLabelAndField("Heart Rate Baseline:", heartRateField = new JTextField("72"), inputPanel, gbc);
        addLabelAndField("Diastolic Pressure (mmHg):", diastolicField = new JTextField("72"), inputPanel, gbc);
        addLabelAndField("Systolic Pressure (mmHg):", systolicField = new JTextField("114"), inputPanel, gbc);
        addLabelAndField("Respiration Rate Baseline:", respirationRateField = new JTextField("16"), inputPanel, gbc);
        addLabelAndField("Basal Metabolic Rate (kcal/day):", basalMetabolicRateField = new JTextField("1600"), inputPanel, gbc);

        // Bottone per avviare la simulazione
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton startButton = new JButton("Start Simulation");
        inputPanel.add(startButton, gbc);

        // Pannello per il grafico
        chartPanel = new LineChartPanel();

        // Area di testo per visualizzare i risultati
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Pannello per la parte inferiore
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(chartPanel, BorderLayout.WEST);  // Pannello del grafico
        bottomPanel.add(scrollPane, BorderLayout.CENTER); // Area di testo con scroll

        add(inputPanel, BorderLayout.NORTH);   // Parte superiore
        add(bottomPanel, BorderLayout.CENTER); // Parte inferiore con grafico e area di testo
   
        // Azione per avviare la simulazione
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SimulationWorker(App.this).execute(); // Usa un SwingWorker per eseguire la simulazione
            }
        });
    }
    
    private void addLabelAndField(String labelText, JTextField textField, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(textField, gbc);
        gbc.gridy++;
    }

    public JTextArea getResultArea() {
        return resultArea;
    }

    public LineChartPanel getChartPanel() {
        return chartPanel;
    }

    public String getTextFieldValue(JTextField textField) {
        return textField.getText();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}
