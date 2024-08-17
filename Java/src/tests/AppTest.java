package tests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.kitware.pulse.cdm.patient.actions.SEAirwayObstruction;
import com.kitware.pulse.utilities.Log;

public class AppTest extends JFrame {

    private JTextArea resultArea;
    private LineChartPanelTest chartPanelTop, chartPanelBot;
    private JTabbedPane switchTabbedPane;
    private JPanel cardPanel, ventilatorCardPanel;
    private CardLayout cardLayout, ventilatorCardLayout;


    //Dati del paziente
    private JTextField nameField_Patient, ageField_Patient, weightField_Patient, heightField_Patient, bodyFatField_Patient;
    private JTextField heartRateField_Patient, diastolicField_Patient, systolicField_Patient, respirationRateField_Patient, basalMetabolicRateField_Patient;
    JComboBox<String> sexComboBox_Patient = new JComboBox<>(new String[]{"Male", "Female"});
    
    //Ventilatori
    private JRadioButton pcac;
    public JTextField fractionInspOxygenPCACField, inspiratoryPeriodPCACField, inspiratoryPressurePCACField, positiveEndExpPresPCACField, respirationRatePCACField, slopePCACField;
    
    private JRadioButton cpap;
    public JTextField fractionInspOxygenCPAPField, deltaPressureSupCPAPField, positiveEndExpPresCPAPField, slopeCPAPField;
    
    private JRadioButton vcac;
    public JTextField flowVCACField, fractionInspOxygenVCACField, inspiratoryPeriodVCACField, positiveEndExpPresVCACField, respirationRateVCACField, tidalVolVCACField;
   
    ButtonGroup ventilatori = new ButtonGroup();
    private VentilationModeTest selectedVentilationMode = VentilationModeTest.PCAC;

    
    
    public AppTest() {
        setTitle("Pulse Simulation");
        setSize(1400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Imposta il colore di sfondo della finestra principale
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Pannello per gli input (centro)
        switchTabbedPane = new JTabbedPane();
        switchTabbedPane.setBackground(Color.LIGHT_GRAY);
        
        JPanel patientPanel = new JPanel();
        patientPanel.setLayout(new GridBagLayout());
        patientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        patientPanel.setPreferredSize(new Dimension(250, 0));
        patientPanel.setBackground(Color.LIGHT_GRAY);

        JPanel ventilatorPanel = new JPanel(new BorderLayout());
        ventilatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ventilatorPanel.setPreferredSize(new Dimension(250, 0));
        ventilatorPanel.setBackground(Color.LIGHT_GRAY);

        switchTabbedPane.addTab("Paziente", patientPanel);
        switchTabbedPane.addTab("Ventilatore", ventilatorPanel);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Aggiungi campi di input con GridBagLayout
        addLabelAndField("Name:", nameField_Patient = new JTextField("Standard"), patientPanel, gbc);
        addLabelAndField("Sex:", sexComboBox_Patient, patientPanel, gbc); 
        addLabelAndField("Age (years):", ageField_Patient = new JTextField("44"), patientPanel, gbc);
        addLabelAndField("Weight (lbs):", weightField_Patient = new JTextField("170"), patientPanel, gbc);
        addLabelAndField("Height (inches):", heightField_Patient = new JTextField("71"), patientPanel, gbc);
        addLabelAndField("Body Fat Fraction:", bodyFatField_Patient = new JTextField("0.21"), patientPanel, gbc);
        addLabelAndField("Heart Rate Baseline:", heartRateField_Patient = new JTextField("72"), patientPanel, gbc);
        addLabelAndField("Diastolic Pressure (mmHg):", diastolicField_Patient = new JTextField("72"), patientPanel, gbc);
        addLabelAndField("Systolic Pressure (mmHg):", systolicField_Patient = new JTextField("114"), patientPanel, gbc);
        addLabelAndField("Respiration Rate Baseline:", respirationRateField_Patient = new JTextField("16"), patientPanel, gbc);
        addLabelAndField("Basal Metabolic Rate (kcal/day):", basalMetabolicRateField_Patient = new JTextField("1600"), patientPanel, gbc);

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
        JPanel vcacPanel = new JPanel(new GridBagLayout());

        // Campi per ventilatore MechanicalVentilatorContinuousPositiveAirwayPressure (PCAC)
        addLabelAndField("Fraction Inspired Oxygen:", fractionInspOxygenPCACField = new JTextField("0.21"), pcapPanel, gbc);
        addLabelAndField("Inspiratory Period:", inspiratoryPeriodPCACField = new JTextField("1"), pcapPanel, gbc);
        addLabelAndField("Inspiratory Pressure:", inspiratoryPressurePCACField = new JTextField("19"), pcapPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure:", positiveEndExpPresPCACField = new JTextField("5"), pcapPanel, gbc);
        addLabelAndField("Respiration Rate:", respirationRatePCACField = new JTextField("12"), pcapPanel, gbc);
        addLabelAndField("Slope:", slopePCACField = new JTextField("0.2"), pcapPanel, gbc);
        
        // Campi per ventilatore MechanicalVentilatorContinuousPositiveAirwayPressure (CPAP)
        addLabelAndField("Fraction Inspired Oxygen:", fractionInspOxygenCPAPField = new JTextField("0.21"), cpapPanel, gbc);
        addLabelAndField("Delta Pressure Support:", deltaPressureSupCPAPField = new JTextField("10"), cpapPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure:", positiveEndExpPresCPAPField = new JTextField("5"), cpapPanel, gbc);
        addLabelAndField("Slope:", slopeCPAPField = new JTextField("0.2"), cpapPanel, gbc);
        
        // Campi per ventilatore SEMechanicalVentilatorVolumeControl (VCAC)
        addLabelAndField("Flow:", flowVCACField = new JTextField("60"), vcacPanel, gbc);
        addLabelAndField("Fraction Inspired Oxygen:", fractionInspOxygenVCACField = new JTextField("0.21"), vcacPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure:", positiveEndExpPresVCACField = new JTextField("5"), vcacPanel, gbc);
        addLabelAndField("Inspiratory Period:", inspiratoryPeriodVCACField = new JTextField("1"), vcacPanel, gbc);
        addLabelAndField("Respiration Rate:", respirationRateVCACField = new JTextField("12"), vcacPanel, gbc);
        addLabelAndField("Tidal Volume:", tidalVolVCACField = new JTextField("900"), vcacPanel, gbc);
        
        
        ventilatorCardPanel.add(pcapPanel, "PCAC");
        ventilatorCardPanel.add(cpapPanel, "CPAP");
        ventilatorCardPanel.add(vcacPanel, "VCAC");

        pcac = new JRadioButton("PC_AC");
        pcac.setSelected(true);
        cpap = new JRadioButton("CPAP");
        vcac = new JRadioButton("VC_AC");

        ventilatori.add(pcac);
        ventilatori.add(cpap);
        ventilatori.add(vcac);

        JPanel radioPanel = new JPanel(new GridLayout(1, 3));
        radioPanel.add(pcac);
        radioPanel.add(cpap);
        radioPanel.add(vcac);
        
        JButton connectButton = new JButton("Connect");
        connectButton.setEnabled(false);  // Disabilitato finché la simulazione non parte
        connectButton.setForeground(Color.BLACK);
        connectButton.setFocusPainted(false);
        
        JButton disconnectButton = new JButton("Disconnect all");
        disconnectButton.setEnabled(false);  // Disabilitato finché la simulazione non parte
        disconnectButton.setForeground(Color.RED);
        disconnectButton.setFocusPainted(false);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1)); // 2 righe, 1 colonna
        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        
        
        ventilatorPanel.add(radioPanel, BorderLayout.NORTH);
        ventilatorPanel.add(buttonPanel, BorderLayout.SOUTH);
        ventilatorPanel.add(ventilatorCardPanel, BorderLayout.CENTER);
        
        
        // Imposta i listener per i JRadioButton
        pcac.addActionListener(e -> {
        	ventilatorCardLayout.show(ventilatorCardPanel, "PCAC");
        	selectedVentilationMode = VentilationModeTest.PCAC;
        	});
        cpap.addActionListener(e -> {
        	ventilatorCardLayout.show(ventilatorCardPanel, "CPAP");
        	selectedVentilationMode = VentilationModeTest.CPAP;
        });
        vcac.addActionListener(e -> {
        	ventilatorCardLayout.show(ventilatorCardPanel, "VCAC");
        	selectedVentilationMode = VentilationModeTest.VCAC;
        });

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


        // Aggiungi i pannelli al layout principale
        add(scrollPane, BorderLayout.WEST);    // Area di testo con scroll a sinistra
        add(switchTabbedPane, BorderLayout.CENTER);  // Pannello input al centro
        add(chartsPanel, BorderLayout.EAST);   // Pannello del grafico a destra

        // Azione per avviare la simulazione
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            connectButton.setEnabled(true);
            actionButton.setEnabled(true);
            chartPanelTop.clear(); // Pulizia del grafico
            chartPanelBot.clear(); // Pulizia del grafico
            resultArea.setText(""); // Pulizia dell'area risultati

            //TEST
            new SimulationWorkerTest(AppTest.this).execute(); // Usa un SwingWorker per eseguire la simulazione
        });

        // Azione per fermare la simulazione
        stopButton.addActionListener(e -> {
            //TEST
            SimulationWorkerTest.requestStop(); 
            // SimulationWorker.requestStop(); // Richiedi l'arresto
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            connectButton.setEnabled(false);
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
        
        // Azione per connettere i ventilatori
        connectButton.addActionListener(e -> {
        	if(!SimulationWorkerTest.ventilationSwitchRequest)
        		SimulationWorkerTest.ventilationSwitchRequest = true;
        	else
        		SimulationWorkerTest.ventilationSwitchRequest = false;
        	disconnectButton.setEnabled(true);
        });

     // Azione per disconnettere i ventilatori
        disconnectButton.addActionListener(e -> {
        	if(!SimulationWorkerTest.ventilationDisconnectRequest)
        		SimulationWorkerTest.ventilationDisconnectRequest = true;
        	else
        		SimulationWorkerTest.ventilationDisconnectRequest = false;
        	disconnectButton.setEnabled(false);
        });
        
    }

    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
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
    
    
    //Get dati paziente
    public String getName_PATIENT() {
        return nameField_Patient.getText();
    }
    
    public String getSex_PATIENT() {
        return (String) sexComboBox_Patient.getSelectedItem();
    }
    
    public String getAge_PATIENT() {
        return ageField_Patient.getText();
    }

    public String getWeight_PATIENT() {
        return weightField_Patient.getText();
    }

    public String getHeight_PATIENT() {
        return heightField_Patient.getText();
    }

    public String getBodyFatFraction_PATIENT() {
        return bodyFatField_Patient.getText();
    }

    public String getHeartRate_PATIENT() {
        return heartRateField_Patient.getText();
    }

    public String getDiastolicPressure_PATIENT() {
        return diastolicField_Patient.getText();
    }

    public String getSystolicPressure_PATIENT() {
        return systolicField_Patient.getText();
    }

    public String getRespirationRate_PATIENT() {
        return respirationRateField_Patient.getText();
    }

    public String getBasalMetabolicRate_PATIENT() {
        return basalMetabolicRateField_Patient.getText();
    }

    
    //Get ventilatore PCAC
    public boolean isPCACConnected() {
    	return selectedVentilationMode == VentilationModeTest.PCAC;
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
        return selectedVentilationMode == VentilationModeTest.CPAP;
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
    
  //Get ventilatore VCAC
    public boolean isVCACConnected() {
        return selectedVentilationMode == VentilationModeTest.VCAC;
    }
    
    public String getFractionInspOxygenValue_VCAC() {
        return fractionInspOxygenVCACField.getText();
    }
    
    public String getFlow_VCAC() {
        return flowVCACField.getText();
    }

    public String getInspiratoryPeriod_VCAC() {
        return inspiratoryPeriodVCACField.getText();
    }

    public String getTidalVol_VCAC() {
        return tidalVolVCACField.getText();
    }
    
    public String getRespirationRate_VCAC() {
        return respirationRateVCACField.getText();
    }
    
    public String getPositiveEndExpPres_VCAC() {
        return positiveEndExpPresVCACField.getText();
    }
    
}
