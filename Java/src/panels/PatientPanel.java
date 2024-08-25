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
    private JComboBox<String> weightUnitComboBox, heightUnitComboBox;
    
    
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
             
        
        addLabelAndField("Name:", nameField_Patient = new JTextField("Standard", 20), patientPanel, gbc);
        addLabelAndField("Sex:", sexComboBox_Patient, patientPanel, gbc); 
        
        addLabelFieldAndUnit("Age:", ageField_Patient = new JTextField("44"),new JLabel("yr"), patientPanel, gbc);

        // JComboBox per peso
        weightUnitComboBox = new JComboBox<>(new String[]{"lbs", "kg"});
        addLabelFieldAndUnit("Weight:", weightField_Patient = new JTextField("170"), weightUnitComboBox, patientPanel, gbc);

        // JComboBox per altezza
        heightUnitComboBox = new JComboBox<>(new String[]{"inches", "m", "cm", "ft"});
        addLabelFieldAndUnit("Height:", heightField_Patient = new JTextField("71"), heightUnitComboBox, patientPanel, gbc);

        // JLabel per body fat fraction
        addLabelFieldAndUnit("Body Fat Fraction:", bodyFatField_Patient = new JTextField("0.21"), new JLabel("%"), patientPanel, gbc);

        // JLabel per heart rate
        addLabelFieldAndUnit("Heart Rate Baseline:", heartRateField_Patient = new JTextField("72"), new JLabel("mmHg"), patientPanel, gbc);

        // JLabel per diastolic pressure
        addLabelFieldAndUnit("Diastolic Pressure:", diastolicField_Patient = new JTextField("72"), new JLabel("mmHg"), patientPanel, gbc);
        
        // JLabel per systolic pressure
        addLabelFieldAndUnit("Systolic Pressure:", systolicField_Patient = new JTextField("114"), new JLabel("mmHg"), patientPanel, gbc);

        // JLabel per respiration rate
        addLabelFieldAndUnit("Respiration Rate Baseline:", respirationRateField_Patient = new JTextField("16"), new JLabel("breaths/min"), patientPanel, gbc);

        // JLabel per basal metabolic rate
        addLabelFieldAndUnit("Basal Metabolic Rate:", basalMetabolicRateField_Patient = new JTextField("1600"), new JLabel("kcal/day"), patientPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;  
        gbc.weightx = 1.0;  
        gbc.fill = GridBagConstraints.HORIZONTAL; 

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
                    String weightUnit = rootNode.path("CurrentPatient").path("Weight").path("ScalarMass").path("Unit").asText();
                    int height = rootNode.path("CurrentPatient").path("Height").path("ScalarLength").path("Value").asInt();
                    String heightUnit = rootNode.path("CurrentPatient").path("Height").path("ScalarLength").path("Unit").asText();
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
                    weightUnitComboBox.setSelectedItem(convertWeightUnitToComboBoxValue(weightUnit));
                    heightField_Patient.setText(String.valueOf(height));
                    heightUnitComboBox.setSelectedItem(convertHeightUnitToComboBoxValue(heightUnit));
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
    

    private String convertWeightUnitToComboBoxValue(String unit) {
        switch (unit) {
            case "lb":
                return "lbs";
            case "kg":
                return "kg";
            default:
                return "lbs"; // Valore di default
        }
    }

    private String convertHeightUnitToComboBoxValue(String unit) {
        switch (unit) {
            case "in":
                return "inches";
            case "m":
                return "m";
            case "cm":
                return "cm";
            case "ft":
                return "ft";
            default:
                return "inches"; // Valore di default
        }
    }
    
    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0; // Il label non espande lo spazio
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER; // Il campo di testo si espande orizzontalmente
        gbc.weightx = 1.0; // Il campo di testo espande per occupare lo spazio disponibile
        panel.add(component, gbc);
        
        gbc.gridy++;
    }

    
    
    
    private void addLabelFieldAndUnit(String labelText, JComponent component, JComponent unitComponent, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridx = 2;
        panel.add(unitComponent, gbc);
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
    
    public JPanel getPatientPanel() {
    	return patientPanel;
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
    
    public String getWeightUnit_PATIENT() {
    	return (String) weightUnitComboBox.getSelectedItem();
    }
    
    public String getHeightUnit_PATIENT() {
    	return (String) heightUnitComboBox.getSelectedItem();
    }
}
