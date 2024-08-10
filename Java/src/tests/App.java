package tests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.kitware.pulse.cdm.patient.actions.SEAirwayObstruction;
import com.kitware.pulse.utilities.Log;

public class App extends JFrame {

    private JTextField nameField, ageField, weightField, heightField, bodyFatField;
    private JTextField heartRateField, diastolicField, systolicField, respirationRateField, basalMetabolicRateField;
    private JTextArea resultArea;
    private LineChartPanel chartPanelTop, chartPanelBot;

    public App() {
        setTitle("Pulse Simulation");
        setSize(1400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Imposta il colore di sfondo della finestra principale
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Pannello per gli input (centro)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setPreferredSize(new Dimension(250, 0));
        inputPanel.setBackground(Color.LIGHT_GRAY);

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
        startButton.setBackground(new Color(0, 122, 255)); // Blu
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        inputPanel.add(startButton, gbc);

        // Bottone per fermare la simulazione
        JButton stopButton = new JButton("Stop Simulation");
        stopButton.setEnabled(false);  // Disabilitato finché la simulazione non parte
        stopButton.setBackground(new Color(255, 59, 48)); // Rosso
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        gbc.gridy++;
        inputPanel.add(stopButton, gbc);

        // Bottone per iniziare un'azione
        JButton actionButton = new JButton("Action");
        actionButton.setEnabled(false);  // Disabilitato finché la simulazione non parte
        actionButton.setBackground(new Color(52, 199, 89)); // Verde
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        gbc.gridy++;
        inputPanel.add(actionButton, gbc);

        chartPanelTop = new LineChartPanel(); // Primo grafico
        chartPanelBot = new LineChartPanel(); // Secondo grafico

        // Pannello per il grafico (destra)
        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.add(chartPanelTop);
        chartsPanel.add(chartPanelBot);
        chartsPanel.setBackground(Color.LIGHT_GRAY);

        // Area di testo per visualizzare i risultati (sinistra)
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        resultArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(350, 0));
        scrollPane.setBackground(Color.LIGHT_GRAY);

        // Aggiungi i pannelli al layout principale
        add(scrollPane, BorderLayout.WEST);    // Area di testo con scroll a sinistra
        add(inputPanel, BorderLayout.CENTER);  // Pannello input al centro
        add(chartsPanel, BorderLayout.EAST);   // Pannello del grafico a destra

        // Azione per avviare la simulazione
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                actionButton.setEnabled(true);
                chartPanelTop.clear(); // Pulizia del grafico
                chartPanelBot.clear(); // Pulizia del grafico
                resultArea.setText(""); // Pulizia dell'area risultati
                new SimulationWorker(App.this).execute(); // Usa un SwingWorker per eseguire la simulazione
            }
        });

        // Azione per fermare la simulazione
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimulationWorker.requestStop(); // Richiedi l'arresto
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                actionButton.setEnabled(false);
            }
        });

        // Azione per iniziare un'azione
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SEAirwayObstruction h = new SEAirwayObstruction();
                h.getSeverity().setValue(0.8);

                if (!SimulationWorker.pe.processAction(h)) {
                    Log.error("Engine was unable to process requested actions");
                    return;
                }
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

    public LineChartPanel[] getChartPanel() {
        return new LineChartPanel[]{chartPanelTop, chartPanelBot};
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