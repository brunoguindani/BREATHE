package tests;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.kitware.pulse.cdm.actions.SEAction;
import com.kitware.pulse.cdm.bind.Events.eEvent;
import com.kitware.pulse.cdm.bind.Patient.PatientData.eSex;
import com.kitware.pulse.cdm.bind.PatientActions.HemorrhageData.eCompartment;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.engine.SEDataRequestManager;
import com.kitware.pulse.cdm.engine.SEActiveEvent;
import com.kitware.pulse.cdm.engine.SEEventHandler;
import com.kitware.pulse.cdm.engine.SEPatientConfiguration;
import com.kitware.pulse.cdm.patient.SEPatient;
import com.kitware.pulse.cdm.patient.actions.SEHemorrhage;
import com.kitware.pulse.cdm.patient.actions.SESubstanceCompoundInfusion;
import com.kitware.pulse.cdm.patient.assessments.SECompleteBloodCount;
import com.kitware.pulse.cdm.patient.conditions.SEChronicAnemia;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.LengthUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.MassUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PowerUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.engine.PulseEngine;
import com.kitware.pulse.cdm.properties.SEScalarTime;
import com.kitware.pulse.utilities.Log;
import com.kitware.pulse.utilities.LogListener;
import com.kitware.pulse.utilities.JNIBridge;

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
                startSimulation();
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

    private void startSimulation() {
        // Inizializzazione di JNIBridge e PulseEngine
        JNIBridge.initialize();
        PulseEngine pe = new PulseEngine();
        
        String[] requestList = {"SimTime","HeartRate","TotalLungVolume","RespirationRate","BloodVolume"};

        // Creazione e configurazione delle richieste di dati
        SEDataRequestManager dataRequests = new SEDataRequestManager();
        dataRequests.createPhysiologyDataRequest(requestList[1], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[2], VolumeUnit.mL);
        dataRequests.createPhysiologyDataRequest(requestList[3], FrequencyUnit.Per_min);
        dataRequests.createPhysiologyDataRequest(requestList[4], VolumeUnit.mL);
        dataRequests.setResultsFilename("./test_results/HowTo_EngineUse.java.csv");

        // Configurazione del paziente
        /*
        SEPatientConfiguration patient_configuration = new SEPatientConfiguration();
        SEPatient patient = patient_configuration.getPatient();
        patient.setName(nameField.getText());
        patient.setSex(eSex.Male); 
        patient.getAge().setValue(Double.parseDouble(ageField.getText()), TimeUnit.yr);
        patient.getWeight().setValue(Double.parseDouble(weightField.getText()), MassUnit.lb);
        patient.getHeight().setValue(Double.parseDouble(heightField.getText()), LengthUnit.in);
        patient.getBodyFatFraction().setValue(Double.parseDouble(bodyFatField.getText()));
        patient.getHeartRateBaseline().setValue(Double.parseDouble(heartRateField.getText()), FrequencyUnit.Per_min);
        patient.getDiastolicArterialPressureBaseline().setValue(Double.parseDouble(diastolicField.getText()), PressureUnit.mmHg);
        patient.getSystolicArterialPressureBaseline().setValue(Double.parseDouble(systolicField.getText()), PressureUnit.mmHg);
        patient.getRespirationRateBaseline().setValue(Double.parseDouble(respirationRateField.getText()), FrequencyUnit.Per_min);
        patient.getBasalMetabolicRate().setValue(Double.parseDouble(basalMetabolicRateField.getText()), PowerUnit.kcal_Per_day);
        
        // Inizializzazione del motore Pulse con la configurazione del paziente e le richieste di dati
        pe.initializeEngine(patient_configuration, dataRequests);
        */
        
        //SOLO PER DEBUG
        pe.serializeFromFile("./states/StandardMale@0s.json", dataRequests);
        SEPatient initialPatient = new SEPatient();
        pe.getInitialPatient(initialPatient);

        resultArea.append("Started\n");
        // Avanzamento temporale e gestione degli errori
        SEScalarTime time = new SEScalarTime(0, TimeUnit.s);
        for(int tempo = 0; tempo < 10; tempo++) {
            if (!pe.advanceTime(time)) {
                resultArea.append("Something bad happened\n");
                return;
            }

            // Estrazione e scrittura dei dati
            List<Double> dataValues = pe.pullData();
            dataRequests.writeData(dataValues);
            resultArea.append("---------------------------\n");
            for(int i = 0; i < (dataValues.size()); i++ ) {
                resultArea.append(requestList[i] + ": " + dataValues.get(i) + "\n");
            }

            // Aggiungi punto al grafico usando SimTime (dataValues.get(0)) e HeartRate (dataValues.get(1))
            int x = (int)(dataValues.get(0)*30+50);  // Scala il tempo per renderlo visibile
            int y = (int) (250 - dataValues.get(1)*2);
            chartPanel.addPoint(x, y);

            time.setValue(1, TimeUnit.s);
            Log.info("Advancing "+time+"...");
        }

        // Pulizia finale e chiusura della simulazione
        pe.clear();
        pe.cleanUp();
        resultArea.append("Simulation Complete\n");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}

