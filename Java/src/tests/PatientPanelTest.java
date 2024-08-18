package tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PatientPanelTest {
	 //Dati del paziente
    private JTextField nameField_Patient, ageField_Patient, weightField_Patient, heightField_Patient, bodyFatField_Patient;
    private JTextField heartRateField_Patient, diastolicField_Patient, systolicField_Patient, respirationRateField_Patient, basalMetabolicRateField_Patient;
    JComboBox<String> sexComboBox_Patient = new JComboBox<>(new String[]{"Male", "Female"});
    private JPanel patientPanel = new JPanel();
    
    public PatientPanelTest(AppTest app) {
    	
        patientPanel.setLayout(new GridBagLayout());
        patientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        patientPanel.setPreferredSize(new Dimension(250, 0));
        patientPanel.setBackground(Color.LIGHT_GRAY);
        
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
        
        // Azione per avviare la simulazione
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            app.connectButton.setEnabled(true);
            actionButton.setEnabled(true);
            for (int i =0; i< app.chartPanels.length ;i++) {
            	app.chartPanels[i].clear();
            }
            app.log.getResultArea().setText(""); // Pulizia dell'area risultati

            //TEST
            new SimulationWorkerTest(app).execute(); // Usa un SwingWorker per eseguire la simulazione

            });

        // Azione per fermare la simulazione
        stopButton.addActionListener(e -> {
            //TEST
            SimulationWorkerTest.requestStop(); 
            // SimulationWorker.requestStop(); // Richiedi l'arresto
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            app.connectButton.setEnabled(false);
            actionButton.setEnabled(false);
        });
        
    }
    
    public JPanel getPatientPanel() {
    	return patientPanel;
    }
    
    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridy++;
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
}
