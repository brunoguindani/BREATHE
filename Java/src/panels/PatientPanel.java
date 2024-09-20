package panels;

import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import app.App;
import app.SimulationWorker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatientPanel {
	
	/*
	 * Panel to manage patient data
	 */
	
    private JTextField nameField_Patient, ageField_Patient, weightField_Patient, heightField_Patient, bodyFatField_Patient;
    private JTextField heartRateField_Patient, diastolicField_Patient, systolicField_Patient, respirationRateField_Patient, basalMetabolicRateField_Patient;
    JComboBox<String> sexComboBox_Patient = new JComboBox<>(new String[]{"Male", "Female"});
    private JComboBox<String> weightUnitComboBox, heightUnitComboBox;
      
    private JScrollPane  patientPanel = new JScrollPane ();
    private JPanel mainPanel = new JPanel();
    private String selectedFilePath;
    
    public PatientPanel(App app) {
    	
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	
        patientPanel = new JScrollPane();
        JPanel innerPanel = new JPanel(); // Crea un pannello interno per contenere i componenti
        innerPanel.setLayout(new GridBagLayout());
        innerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        innerPanel.setBackground(Color.LIGHT_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Aggiungi i selettori di dati al pannello interno
        addLabelAndField("Name:", nameField_Patient = new JTextField("Standard", 20), innerPanel, gbc);
        addLabelAndField("Sex:", sexComboBox_Patient, innerPanel, gbc);
        addLabelFieldAndUnit("Age:", ageField_Patient = new JTextField("44"), new JLabel("yr"), innerPanel, gbc);
        weightUnitComboBox = new JComboBox<>(new String[]{"lbs", "kg"});
        addLabelFieldAndUnit("Weight:", weightField_Patient = new JTextField("170"), weightUnitComboBox, innerPanel, gbc);
        heightUnitComboBox = new JComboBox<>(new String[]{"inches", "m", "cm", "ft"});
        addLabelFieldAndUnit("Height:", heightField_Patient = new JTextField("71"), heightUnitComboBox, innerPanel, gbc);
        addLabelFieldAndUnit("Body Fat Fraction:", bodyFatField_Patient = new JTextField("0.21"), new JLabel("%"), innerPanel, gbc);
        addLabelFieldAndUnit("Heart Rate Baseline:", heartRateField_Patient = new JTextField("72"), new JLabel("heartbeats/min"), innerPanel, gbc);
        addLabelFieldAndUnit("Diastolic Pressure:", diastolicField_Patient = new JTextField("72"), new JLabel("mmHg"), innerPanel, gbc);
        addLabelFieldAndUnit("Systolic Pressure:", systolicField_Patient = new JTextField("114"), new JLabel("mmHg"), innerPanel, gbc);
        addLabelFieldAndUnit("Respiration Rate Baseline:", respirationRateField_Patient = new JTextField("16"), new JLabel("breaths/min"), innerPanel, gbc);
        addLabelFieldAndUnit("Basal Metabolic Rate:", basalMetabolicRateField_Patient = new JTextField("1600"), new JLabel("kcal/day"), innerPanel, gbc);

        // Aggiungi il pannello interno al JScrollPane
        patientPanel.setViewportView(innerPanel);
        patientPanel.setPreferredSize(new Dimension(450, 450)); // Dimensione appropriata
        patientPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(patientPanel, BorderLayout.CENTER);
        //button to start simulation from a pre loaded file
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        JButton startFromFileButton = new JButton("Start From File");
        JButton startButton = new JButton("Start Simulation");
        JButton stopButton = new JButton("Stop Simulation");
       
        startFromFileButton.setBackground(new Color(0, 122, 255)); 
        startFromFileButton.setForeground(Color.WHITE);
        startFromFileButton.setFocusPainted(false);

        //button to start simulation from given data
        //this will take much longer than pre loaded file
        //because the engine must first stabilize the new patient
        startButton.setBackground(new Color(0, 122, 255)); 
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        gbc.gridy++;

        //stop simulation
        stopButton.setEnabled(false); 
        stopButton.setBackground(new Color(255, 59, 48));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        gbc.gridy++;

        // Configurazione dei pulsanti
        buttonPanel.add(startFromFileButton);
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        // Aggiungi il pannello dei pulsanti al pannello principale
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        /*
         * ACTIONS for buttons
         */
        
        startButton.addActionListener(e -> {
        	setFieldsEnabled(false); //disable changing of parameters 
            startButton.setEnabled(false); // disable starting buttons
            startFromFileButton.setEnabled(false); 
            stopButton.setEnabled(true); // enable stop button
            for (int i =0; i< app.chartPanels.length ;i++) {
            	app.chartPanels[i].clear(); //restart panels
            }
            app.ventilator.connectButton.setEnabled(true); //enable ventilators
            app.log.getResultArea().setText(""); //empty log
            app.action.enableButtonStates(); //enable actions
            app.condition.disableButtonStates(); //disable conditions changes
            new SimulationWorker(app).execute(); //start simulation
            });

        stopButton.addActionListener(e -> {
            SimulationWorker.requestStop(); //stop simulation
            startButton.setEnabled(true);
            startFromFileButton.setEnabled(true);
            selectedFilePath = null;
            stopButton.setEnabled(false);
            app.ventilator.connectButton.setEnabled(false);
            app.action.disableButtonStates();
            app.condition.enableButtonStates();
            setFieldsEnabled(true);
        });
        
        
        startFromFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./states/");
            int returnValue = fileChooser.showOpenDialog(null); //pick a file
            if (returnValue == JFileChooser.APPROVE_OPTION) {
            	selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            	
                try { 
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(new File(selectedFilePath));
                    
                    //Retrieve patient data from selected file
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
                    
                    //Set them to the proper field 
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

                    startButton.doClick();//start simulation
                    startFromFileButton.setEnabled(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
    }
    
    //selection weight unit
    private String convertWeightUnitToComboBoxValue(String unit) {
        switch (unit) {
            case "lb":
                return "lbs";
            case "kg":
                return "kg";
            default:
                return "lbs";
        }
    }

    //selection height unit
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
                return "inches";
        }
    }
    
    
    //method to add visual to panel
    private void addLabelAndField(String labelText, JComponent component, JPanel innerPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0; 
        innerPanel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER; 
        gbc.weightx = 1.0; 
        innerPanel.add(component, gbc);
        
        gbc.gridy++;
    }
  
    //method to add visual to panel
    private void addLabelFieldAndUnit(String labelText, JComponent component, JComponent unitComponent, JPanel innerPanel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        innerPanel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        innerPanel.add(component, gbc);
        gbc.gridx = 2;
        innerPanel.add(unitComponent, gbc);
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
    
    //method to return panel
    public JPanel getPatientPanel() {
    	return mainPanel;
    }
    
    //Get file 
    public String getSelectedFilePath() {
        return selectedFilePath;
    }
    
    //Get patient data
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
