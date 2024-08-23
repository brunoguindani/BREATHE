package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import app.App;
import app.SimulationWorker;
import utils.Action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientPanel {
    private JTextField nameField_Patient, ageField_Patient, weightField_Patient, heightField_Patient, bodyFatField_Patient;
    private JTextField heartRateField_Patient, diastolicField_Patient, systolicField_Patient, respirationRateField_Patient, basalMetabolicRateField_Patient;
    JComboBox<String> sexComboBox_Patient = new JComboBox<>(new String[]{"Male", "Female"});
    private JPanel patientPanel = new JPanel();
    private String selectedFilePath;
    
    public PatientPanel(App app) {
    	
        patientPanel.setLayout(new GridBagLayout());
        patientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        patientPanel.setPreferredSize(new Dimension(250, 0));
        patientPanel.setBackground(Color.LIGHT_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
             
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

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton startFromFileButton = new JButton("Start From File");
        startFromFileButton.setBackground(new Color(0, 122, 255)); 
        startFromFileButton.setForeground(Color.WHITE);
        startFromFileButton.setFocusPainted(false);
        patientPanel.add(startFromFileButton, gbc);
        
        JButton startButton = new JButton("Start Simulation");
        startButton.setBackground(new Color(0, 122, 255)); 
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        gbc.gridy++;
        patientPanel.add(startButton, gbc);


        JButton stopButton = new JButton("Stop Simulation");
        stopButton.setEnabled(false); 
        stopButton.setBackground(new Color(255, 59, 48));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        gbc.gridy++;
        patientPanel.add(stopButton, gbc);
        


        startButton.addActionListener(e -> {
        	setFieldsEnabled(false);
            startButton.setEnabled(false);
            startFromFileButton.setEnabled(false);
            stopButton.setEnabled(true);
            App.connectButton.setEnabled(true);
            for (int i =0; i< app.chartPanels.length ;i++) {
            	app.chartPanels[i].clear();
            }
            app.log.getResultArea().setText("");
            app.action.enableButtonStates();
            app.condition.disableButtonStates();
            new SimulationWorker(app).execute(); 
            });

        stopButton.addActionListener(e -> {
            SimulationWorker.requestStop(); 
            startButton.setEnabled(true);
            startFromFileButton.setEnabled(true);
            selectedFilePath = null;
            stopButton.setEnabled(false);
            App.connectButton.setEnabled(false);
            app.action.disableButtonStates();
            app.condition.enableButtonStates();
            setFieldsEnabled(true);
        });
        
        
        startFromFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./states/");
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
            	selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            	
                //Imposto i fields come presente nel file
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(new File(selectedFilePath));

                    String name = rootNode.path("CurrentPatient").path("Name").asText();
                    String sex = rootNode.path("CurrentPatient").path("Sex").asText(); 
                    if(sex.isBlank()) sex = "Male";
                    int age = rootNode.path("CurrentPatient").path("Age").path("ScalarTime").path("Value").asInt();
                    double weight = rootNode.path("CurrentPatient").path("Weight").path("ScalarMass").path("Value").asDouble();
                    int height = rootNode.path("CurrentPatient").path("Height").path("ScalarLength").path("Value").asInt();
                    double bodyFat = rootNode.path("CurrentPatient").path("BodyFatFraction").path("Scalar0To1").path("Value").asDouble();
                    double heartRate = rootNode.path("CurrentPatient").path("HeartRateBaseline").path("ScalarFrequency").path("Value").asDouble();
                    double diastolicPressure = rootNode.path("CurrentPatient").path("DiastolicArterialPressureBaseline").path("ScalarPressure").path("Value").asDouble();
                    double systolicPressure = rootNode.path("CurrentPatient").path("SystolicArterialPressureBaseline").path("ScalarPressure").path("Value").asDouble();
                    int respirationRate = rootNode.path("CurrentPatient").path("RespirationRateBaseline").path("ScalarFrequency").path("Value").asInt();
                    double basalMetabolicRate = rootNode.path("CurrentPatient").path("BasalMetabolicRate").path("ScalarPower").path("Value").asDouble();
                    
                    // Aggiorna i campi con i valori del file
                    nameField_Patient.setText(name);
                    sexComboBox_Patient.setSelectedItem(sex);
                    ageField_Patient.setText(String.valueOf(age));
                    weightField_Patient.setText(String.format("%.2f", weight));
                    heightField_Patient.setText(String.valueOf(height));
                    bodyFatField_Patient.setText(String.format("%.2f", bodyFat));
                    heartRateField_Patient.setText(String.format("%.2f", heartRate));
                    diastolicField_Patient.setText(String.format("%.2f", diastolicPressure));
                    systolicField_Patient.setText(String.format("%.2f", systolicPressure));
                    respirationRateField_Patient.setText(String.valueOf(respirationRate));
                    basalMetabolicRateField_Patient.setText(String.format("%.2f", basalMetabolicRate));
                    startButton.doClick();
                    startFromFileButton.setEnabled(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
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
    
    private void setFieldsEnabled(boolean enabled) {
        nameField_Patient.setEnabled(enabled);
        ageField_Patient.setEnabled(enabled);
        weightField_Patient.setEnabled(enabled);
        heightField_Patient.setEnabled(enabled);
        bodyFatField_Patient.setEnabled(enabled);
        heartRateField_Patient.setEnabled(enabled);
        diastolicField_Patient.setEnabled(enabled);
        systolicField_Patient.setEnabled(enabled);
        respirationRateField_Patient.setEnabled(enabled);
        basalMetabolicRateField_Patient.setEnabled(enabled);
        sexComboBox_Patient.setEnabled(enabled);
    }
    
    //Get del file (se selezionato)
    public String getSelectedFilePath() {
        return selectedFilePath;
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
