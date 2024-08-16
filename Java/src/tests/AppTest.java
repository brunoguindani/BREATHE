package tests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.kitware.pulse.cdm.patient.actions.SEAirwayObstruction;
import com.kitware.pulse.utilities.Log;

public class AppTest extends JFrame {

    private JTextField nameField, ageField, weightField, heightField, bodyFatField;
    private JTextField heartRateField, diastolicField, systolicField, respirationRateField, basalMetabolicRateField;
    private JTextArea resultArea;
    private LineChartPanelTest chartPanelTop, chartPanelBot;
    private JButton switchButton;
    private JPanel cardPanel, ventilatorCardPanel;
    private CardLayout cardLayout, ventilatorCardLayout;

    //Ventilatori
    private JRadioButton pcac;
    public JTextField fractionInspOxygenPCACField, inspiratoryPeriodPCACField, inspiratoryPressurePCACField, positiveEndExpPresPCACField, respirationRatePCACField, slopePCACField;
    private JCheckBox pcacConnection;
    
    private JRadioButton cpap;
    public JTextField fractionInspOxygenCPAPField, deltaPressureSupCPAPField, positiveEndExpPresCPAPField, slopeCPAPField;
    private JCheckBox cpapConnection;
    
    ButtonGroup ventilatori = new ButtonGroup();
   

    public AppTest() {
        setTitle("Pulse Simulation");
        setSize(1400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Imposta il colore di sfondo della finestra principale
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Pannello per gli input (centro)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel patientPanel = new JPanel();
        patientPanel.setLayout(new GridBagLayout());
        patientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        patientPanel.setPreferredSize(new Dimension(250, 0));
        patientPanel.setBackground(Color.LIGHT_GRAY);

        JPanel ventilatorPanel = new JPanel(new BorderLayout());
        ventilatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ventilatorPanel.setPreferredSize(new Dimension(250, 0));
        ventilatorPanel.setBackground(Color.LIGHT_GRAY);

        cardPanel.add(patientPanel, "Paziente");
        cardPanel.add(ventilatorPanel, "Ventilatore");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Aggiungi campi di input con GridBagLayout
        addLabelAndField("Name:", nameField = new JTextField("Standard"), patientPanel, gbc);
        addLabelAndField("Age (years):", ageField = new JTextField("44"), patientPanel, gbc);
        addLabelAndField("Weight (lbs):", weightField = new JTextField("170"), patientPanel, gbc);
        addLabelAndField("Height (inches):", heightField = new JTextField("71"), patientPanel, gbc);
        addLabelAndField("Body Fat Fraction:", bodyFatField = new JTextField("0.21"), patientPanel, gbc);
        addLabelAndField("Heart Rate Baseline:", heartRateField = new JTextField("72"), patientPanel, gbc);
        addLabelAndField("Diastolic Pressure (mmHg):", diastolicField = new JTextField("72"), patientPanel, gbc);
        addLabelAndField("Systolic Pressure (mmHg):", systolicField = new JTextField("114"), patientPanel, gbc);
        addLabelAndField("Respiration Rate Baseline:", respirationRateField = new JTextField("16"), patientPanel, gbc);
        addLabelAndField("Basal Metabolic Rate (kcal/day):", basalMetabolicRateField = new JTextField("1600"), patientPanel, gbc);

        // Bottone per avviare la simulazione
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;

        JButton startButton = new JButton("Start Simulation");
        startButton.setBackground(new Color(0, 122, 255)); // Blu
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        patientPanel.add(startButton, gbc);

        // Bottone per fermare la simulazione
        JButton stopButton = new JButton("Stop Simulation");
        stopButton.setEnabled(false);  // Disabilitato finché la simulazione non parte
        stopButton.setBackground(new Color(255, 59, 48)); // Rosso
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        gbc.gridy++;
        patientPanel.add(stopButton, gbc);

        // Bottone per iniziare un'azione
        JButton actionButton = new JButton("Action");
        actionButton.setEnabled(false);  // Disabilitato finché la simulazione non parte
        actionButton.setBackground(new Color(52, 199, 89)); // Verde
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        gbc.gridy++;
        patientPanel.add(actionButton, gbc);

        // Pannello ventilatore con CardLayout per diverse opzioni di ventilazione
        ventilatorCardLayout = new CardLayout();
        ventilatorCardPanel = new JPanel(ventilatorCardLayout);

        JPanel pcapPanel = new JPanel(new GridBagLayout());
        JPanel cpapPanel = new JPanel(new GridBagLayout());

        // Campi per ventilatore MechanicalVentilatorContinuousPositiveAirwayPressure
        addLabelAndField("Fraction Inspired Oxygen:", fractionInspOxygenPCACField = new JTextField("0.21"), pcapPanel, gbc);
        addLabelAndField("Inspiratory Period:", inspiratoryPeriodPCACField = new JTextField("1"), pcapPanel, gbc);
        addLabelAndField("Inspiratory Pressure:", inspiratoryPressurePCACField = new JTextField("19"), pcapPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure:", positiveEndExpPresPCACField = new JTextField("5"), pcapPanel, gbc);
        addLabelAndField("Respiration Rate:", respirationRatePCACField = new JTextField("12"), pcapPanel, gbc);
        addLabelAndField("Slope:", slopePCACField = new JTextField("0.2"), pcapPanel, gbc);
        pcacConnection = new JCheckBox("Connessione", false);
        pcapPanel.add(pcacConnection, gbc);

        
        // Campi per ventilatore MechanicalVentilatorContinuousPositiveAirwayPressure (CPAP)
        addLabelAndField("Fraction Inspired Oxygen:", fractionInspOxygenCPAPField = new JTextField("0.21"), cpapPanel, gbc);
        addLabelAndField("Delta Pressure Support:", deltaPressureSupCPAPField = new JTextField("10"), cpapPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure:", positiveEndExpPresCPAPField = new JTextField("5"), cpapPanel, gbc);
        addLabelAndField("Slope:", slopeCPAPField = new JTextField("0.2"), cpapPanel, gbc);
        cpapConnection = new JCheckBox("Connessione", false);
        cpapPanel.add(cpapConnection, gbc);
        
        ventilatorCardPanel.add(pcapPanel, "PCAC");
        ventilatorCardPanel.add(cpapPanel, "CPAP");

        pcac = new JRadioButton("Ventilazione pcac");
        pcac.setSelected(true);
        cpap = new JRadioButton("Ventilazione cpap");

        ventilatori.add(pcac);
        ventilatori.add(cpap);

        JPanel radioPanel = new JPanel(new GridLayout(1, 2));
        radioPanel.add(pcac);
        radioPanel.add(cpap);

        ventilatorPanel.add(radioPanel, BorderLayout.NORTH);
        ventilatorPanel.add(ventilatorCardPanel, BorderLayout.CENTER);

        // Imposta i listener per i JRadioButton
        pcac.addActionListener(e -> ventilatorCardLayout.show(ventilatorCardPanel, "PCAC"));
        cpap.addActionListener(e -> ventilatorCardLayout.show(ventilatorCardPanel, "CPAP"));

        // Pannello per il grafico (destra)
        JPanel chartsPanel = new JPanel();
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartPanelTop = new LineChartPanelTest("Heart Rate"); // Primo grafico
        chartPanelBot = new LineChartPanelTest("Respiratory Rate"); // Secondo grafico
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
        scrollPane.setPreferredSize(new Dimension(450, 0));
        scrollPane.setBackground(Color.LIGHT_GRAY);

        // Pulsante per cambiare pannello
        switchButton = new JButton("Ventilatore");
        switchButton.setPreferredSize(new Dimension(120, 30)); // Imposta una dimensione fissa per il pulsante
        gbc.gridy++;
        add(switchButton, BorderLayout.SOUTH);

        // Aggiungi i pannelli al layout principale
        add(scrollPane, BorderLayout.WEST);    // Area di testo con scroll a sinistra
        add(cardPanel, BorderLayout.CENTER);  // Pannello input al centro
        add(chartsPanel, BorderLayout.EAST);   // Pannello del grafico a destra

        // Azione per avviare la simulazione
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            actionButton.setEnabled(true);
            chartPanelTop.clear(); // Pulizia del grafico
            chartPanelBot.clear(); // Pulizia del grafico
            resultArea.setText(""); // Pulizia dell'area risultati

            //TEST
            new SimulationWorkerTest(AppTest.this).execute(); //new SimulationWorker(App.this).execute(); // Usa un SwingWorker per eseguire la simulazione
        });

        // Azione per fermare la simulazione
        stopButton.addActionListener(e -> {
            //TEST
            SimulationWorkerTest.requestStop(); 
            // SimulationWorker.requestStop(); // Richiedi l'arresto
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            actionButton.setEnabled(false);
        });

        // Azione per iniziare un'azione
        actionButton.addActionListener(e -> {
            SEAirwayObstruction h = new SEAirwayObstruction();
            h.getSeverity().setValue(0.8);

            //TEST
            if (!SimulationWorkerTest.pe.processAction(h)) {
                Log.error("Engine was unable to process requested actions");
                return;
            }
        });

        // Azione per cambiare pannello
        switchButton.addActionListener(new ActionListener() {
            private boolean showingPatientPanel = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (showingPatientPanel) {
                    cardLayout.show(cardPanel, "Ventilatore");
                    switchButton.setText("Paziente");
                } else {
                    cardLayout.show(cardPanel, "Paziente");
                    switchButton.setText("Ventilatore");
                }
                showingPatientPanel = !showingPatientPanel;
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

    public LineChartPanelTest[] getChartPanel() {
        return new LineChartPanelTest[]{chartPanelTop, chartPanelBot};
    }

    public String getTextFieldValue(JTextField textField) {
        return textField.getText();
    }

    
    //Get ventilatore PCAC
    public boolean isPCACConnected() {
        return pcacConnection.isSelected();
    }
    
    public String getInspiratoryPeriodValue_PCAC() {
        return inspiratoryPeriodPCACField.getText();
    }

    public String getInspiratoryPressureValue_PCAC() {
        return inspiratoryPressurePCACField.getText();
    }

    public String getRespirationRateValue_PCAC() {
        return respirationRatePCACField.getText();
    }

    public String getFractionInspOxygenValue_PCAC() {
        return fractionInspOxygenPCACField.getText();
    }

    public String getPositiveEndExpPresValue_PCAC() {
        return positiveEndExpPresPCACField.getText();
    }

    public String getSlopeValue_PCAC() {
        return slopePCACField.getText();
    }

  //Get ventilatore CPAP
    public boolean isCPAPConnected() {
        return cpapConnection.isSelected();
    }
    
    public String getFractionInspOxygenValue_CPAP() {
        return fractionInspOxygenCPAPField.getText();
    }
    
    public String getDeltaPressureSupValue_CPAP() {
        return deltaPressureSupCPAPField.getText();
    }

    public String getPositiveEndExpPresValue_CPAP() {
        return positiveEndExpPresCPAPField.getText();
    }

    public String getSlopeValue_CPAP() {
        return slopeCPAPField.getText();
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTest app = new AppTest();
            app.setVisible(true);
        });
    }
}
