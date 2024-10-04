package toRemove;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	Map<String, JTextField> fieldMap = new HashMap<>();
	 
    JComboBox<String> sexComboBox_Patient = new JComboBox<>(new String[]{"Male", "Female"});
    private JComboBox<String> weightUnitComboBox, heightUnitComboBox;
      
    private JScrollPane  patientPanel = new JScrollPane ();
    private JPanel mainPanel = new JPanel();
    JComboBox<String> fileComboBox = new JComboBox<>();
    
    
    private String selectedPatientFilePath;
    private String selectedScenarioFilePath;
    
    JButton exportButton, stopButton;
    
    
    public PatientPanel(App app) {
    	
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	
        patientPanel = new JScrollPane();
        JPanel innerPanel = new JPanel(); 
        innerPanel.setLayout(new GridBagLayout());
        innerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        innerPanel.setBackground(Color.LIGHT_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        //fill up map
        fieldMap.put("name", new JTextField("Standard", 20));
        fieldMap.put("age", new JTextField("44"));
        fieldMap.put("weight", new JTextField("170"));
        fieldMap.put("height", new JTextField("71"));
        fieldMap.put("bodyFat", new JTextField("0.21"));
        fieldMap.put("heartRate", new JTextField("72"));
        fieldMap.put("diastolic", new JTextField("72"));
        fieldMap.put("systolic", new JTextField("114"));
        fieldMap.put("respirationRate", new JTextField("16"));
        fieldMap.put("basalMetabolicRate", new JTextField("1600"));
        
        // Selectors for patient data
        addLabelAndField("Name:", fieldMap.get("name"), innerPanel, gbc);
        addLabelAndField("Sex:", sexComboBox_Patient, innerPanel, gbc);
        addLabelFieldAndUnit("Age:", fieldMap.get("age"), new JLabel("yr"), innerPanel, gbc);
        weightUnitComboBox = new JComboBox<>(new String[]{"lbs", "kg"});
        addLabelFieldAndUnit("Weight:", fieldMap.get("weight"), weightUnitComboBox, innerPanel, gbc);
        heightUnitComboBox = new JComboBox<>(new String[]{"inches", "m", "cm", "ft"});
        addLabelFieldAndUnit("Height:", fieldMap.get("height"), heightUnitComboBox, innerPanel, gbc);
        addLabelFieldAndUnit("Body Fat Fraction:", fieldMap.get("bodyFat"), new JLabel("%"), innerPanel, gbc);
        addLabelFieldAndUnit("Heart Rate Baseline:", fieldMap.get("heartRate"), new JLabel("heartbeats/min"), innerPanel, gbc);
        addLabelFieldAndUnit("Diastolic Pressure:", fieldMap.get("diastolic"), new JLabel("mmHg"), innerPanel, gbc);
        addLabelFieldAndUnit("Systolic Pressure:", fieldMap.get("systolic"), new JLabel("mmHg"), innerPanel, gbc);
        addLabelFieldAndUnit("Respiration Rate Baseline:", fieldMap.get("respirationRate"), new JLabel("breaths/min"), innerPanel, gbc);
        addLabelFieldAndUnit("Basal Metabolic Rate:", fieldMap.get("basalMetabolicRate"), new JLabel("kcal/day"), innerPanel, gbc);
        
        //Add labels
        fieldMap.get("age").setToolTipText("Value must be between 18 and 65");
        fieldMap.get("height").setToolTipText("Value must be between 163cm and 190cm for male patients and between 151cm and 175cm for female patients");
        fieldMap.get("bodyFat").setToolTipText("Value must be between 0.02% and 0.25% for male patients and between 0.1% and 0.32% for female patients");
        fieldMap.get("heartRate").setToolTipText("Value must be between 50bpm and 110bpm");
        fieldMap.get("diastolic").setToolTipText("Value must be between 60mmHg and 80mmHg");
        fieldMap.get("systolic").setToolTipText("Value must be between 90mmHg and 120mmHg");
        fieldMap.get("respirationRate").setToolTipText("Value must be between 8bpm and 20bpm");
        
        //Add to scrollable panel
        patientPanel.setViewportView(innerPanel);
        patientPanel.setPreferredSize(new Dimension(450, 400)); 
        patientPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(patientPanel, BorderLayout.CENTER);
        
        //button to start simulation from a pre loaded file
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        
        JButton startFromFileButton = new JButton("Start From File");
        startFromFileButton.setToolTipText("Start Simulation from Patient File");
               
        JButton startFromScenarioButton = new JButton("Start From Scenario");
        startFromScenarioButton.setToolTipText("Start a Scenario");
        
        JButton startButton = new JButton("Start Simulation");
        startButton.setToolTipText("Start new Simulation");
        
        stopButton = new JButton("Stop Simulation");
        stopButton.setToolTipText("Stop Simulation");
        
        exportButton = new JButton("Export Simulation");
        exportButton.setToolTipText("Export current patient state");
        
        startFromScenarioButton.setBackground(new Color(0, 122, 255)); 
        startFromScenarioButton.setForeground(Color.WHITE);
        startFromScenarioButton.setFocusPainted(false);
        
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

        exportButton.setEnabled(false); 
        exportButton.setBackground(new Color(0, 128, 0));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        gbc.gridy++;
        
        buttonPanel.setLayout(new GridLayout(2, 3, 10, 10));
        
        buttonPanel.add(startFromScenarioButton);
        buttonPanel.add(startFromFileButton);
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(exportButton);


        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        /*
         * ACTIONS for buttons
         */
        
        startButton.addActionListener(e -> {
        	if(!checkFieldsNumeric()) {
        		JOptionPane.showMessageDialog(null, 
        			    "One or more fields contain invalid characters.\nPlease ensure all numeric fields contain only valid numbers.", 
        			    "Invalid Input", 
        			    JOptionPane.WARNING_MESSAGE);
        		return;
        	}else{
            	setFieldsEnabled(false); //disable changing of parameters 
                startButton.setEnabled(false); // disable starting buttons
                startFromFileButton.setEnabled(false); 
                startFromScenarioButton.setEnabled(false);
                for (String chartName : app.chartPanels.keySet()) {
                    app.chartPanels.get(chartName).clear(); // Restart panels
                }
                app.ventilator.connectButton.setEnabled(true); //enable ventilators
                app.log.getResultArea().setText(""); //empty log
                app.action.enableButtonStates(); //enable actions
                app.condition.disableButtonStates(); //disable conditions changes
                new SimulationWorker(app).execute(); //start simulation	
        	}
        });

        stopButton.addActionListener(e -> {
        	SimulationWorker.requestStop(); //stop simulation
            startButton.setEnabled(true);
            startFromScenarioButton.setEnabled(true);
            exportButton.setEnabled(false);
            startFromFileButton.setEnabled(true);
            selectedPatientFilePath = null;
            selectedScenarioFilePath = null;
            stopButton.setEnabled(false);
            app.ventilator.disableButton();
            app.action.disableButtonStates();
            app.condition.enableButtonStates();
            setFieldsEnabled(true);     
        });
        
        
        startFromFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./states/");
            int returnValue = fileChooser.showOpenDialog(null); // pick a file
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedPatientFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                
                if(loadPatientData(selectedPatientFilePath)) {
                    app.condition.getRemoveAllConditionsButton().doClick();
                    startButton.doClick();               	
                }
            }
        });
        
        exportButton.addActionListener(e -> {
            String defaultFileName = "./states/exported/" + fieldMap.get("name").getText() + ".json";
            JFileChooser fileChooser = new JFileChooser("./states/exported/");
            fileChooser.setDialogTitle("Export simulation");
            fileChooser.setSelectedFile(new File(defaultFileName)); // Pre-set default filename
            fileChooser.setApproveButtonText("Export");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            boolean validFileName = false; // Flag to track valid filename

            while (!validFileName) {
                int userSelection = fileChooser.showSaveDialog(null);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String fileName = fileToSave.getAbsolutePath();
                    
                    // Ensure the file ends with .json
                    if (!fileName.endsWith(".json")) {
                        fileName += ".json";
                    }
                    
                    File file = new File(fileName);
                    
                    // Check if file exists and ask for overwrite confirmation
                    if (file.exists()) {
                        int response = JOptionPane.showConfirmDialog(null, 
                            "File already exists. Do you want to overwrite it?", 
                            "Overwrite Confirmation", 
                            JOptionPane.YES_NO_OPTION, 
                            JOptionPane.WARNING_MESSAGE);
                        
                        if (response == JOptionPane.YES_OPTION) {
                            SimulationWorker.pe.serializeToFile(fileName); 
                            validFileName = true; // Exit loop
                        }
                        // If the user selects NO, the loop continues
                    } else {
                        SimulationWorker.pe.serializeToFile(fileName); 
                        validFileName = true; 
                    }
                    
                    app.scenario.updatePatientFiles();
                } else {
                    // User cancelled the operation
                    validFileName = true; 
                }
            }
        });

        startFromScenarioButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("./scenario/");
            int returnValue = fileChooser.showOpenDialog(null); // pick a file
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedScenarioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode_scenario = mapper.readTree(new File(selectedScenarioFilePath));
                    selectedPatientFilePath = rootNode_scenario.path("EngineStateFile").asText();

                    if(loadPatientData(selectedPatientFilePath)) {
                        app.condition.getRemoveAllConditionsButton().doClick();
                        startButton.doClick();               	
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading scenario JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
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
    
    //load Patient Data from File
    private boolean loadPatientData(String patientFilePath) {
        try { 
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(patientFilePath));

            // Retrieve patient data from the selected file
            String name = rootNode.path("InitialPatient").path("Name").asText();
            String sex = rootNode.path("InitialPatient").path("Sex").asText();
            if (sex.isBlank()) sex = "Male";
            int age = rootNode.path("InitialPatient").path("Age").path("ScalarTime").path("Value").asInt();
            double weight = rootNode.path("InitialPatient").path("Weight").path("ScalarMass").path("Value").asDouble();
            String weightUnit = rootNode.path("InitialPatient").path("Weight").path("ScalarMass").path("Unit").asText();
            int height = rootNode.path("InitialPatient").path("Height").path("ScalarLength").path("Value").asInt();
            String heightUnit = rootNode.path("InitialPatient").path("Height").path("ScalarLength").path("Unit").asText();
            double bodyFat = rootNode.path("InitialPatient").path("BodyFatFraction").path("Scalar0To1").path("Value").asDouble();
            double heartRate = rootNode.path("InitialPatient").path("HeartRateBaseline").path("ScalarFrequency").path("Value").asDouble();
            double diastolicPressure = rootNode.path("InitialPatient").path("DiastolicArterialPressureBaseline").path("ScalarPressure").path("Value").asDouble();
            double systolicPressure = rootNode.path("InitialPatient").path("SystolicArterialPressureBaseline").path("ScalarPressure").path("Value").asDouble();
            int respirationRate = rootNode.path("InitialPatient").path("RespirationRateBaseline").path("ScalarFrequency").path("Value").asInt();
            double basalMetabolicRate = rootNode.path("InitialPatient").path("BasalMetabolicRate").path("ScalarPower").path("Value").asDouble();

            // Set the values to the appropriate fields
            fieldMap.get("name").setText(name);
            sexComboBox_Patient.setSelectedItem(sex);
            fieldMap.get("age").setText(String.valueOf(age));
            fieldMap.get("weight").setText(String.format("%.2f", weight));
            weightUnitComboBox.setSelectedItem(convertWeightUnitToComboBoxValue(weightUnit));
            fieldMap.get("height").setText(String.valueOf(height));
            heightUnitComboBox.setSelectedItem(convertHeightUnitToComboBoxValue(heightUnit));
            fieldMap.get("bodyFat").setText(String.format("%.2f", bodyFat));
            fieldMap.get("heartRate").setText(String.format("%.2f", heartRate));
            fieldMap.get("diastolic").setText(String.format("%.2f", diastolicPressure));
            fieldMap.get("systolic").setText(String.format("%.2f", systolicPressure));
            fieldMap.get("respirationRate").setText(String.valueOf(respirationRate));
            fieldMap.get("basalMetabolicRate").setText(String.format("%.2f", basalMetabolicRate));
            
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading JSON file.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
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
    
    private boolean checkFieldsNumeric() {
        for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            JTextField field = entry.getValue();

            if (key.equals("name")) {
                continue;
            }

            String fieldValue = field.getText().replace(",", ".");
            field.setText(fieldValue);

            if (!isValidNumber(fieldValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int commaCount = str.length() - str.replace(",", "").length();
        commaCount += str.length() - str.replace(".", "").length();
        
        if (commaCount > 1) {
            return false;
        }

        try {
            Double.parseDouble(str.replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setFieldsEnabled(boolean enabled) { 	
    	for (JTextField field : fieldMap.values()) {
    	    field.setEnabled(enabled);
    	}
        sexComboBox_Patient.setEnabled(enabled);
    }
    
    //method to return panel
    public JPanel getPatientPanel() {
    	return mainPanel;
    }
    
    //Get files
    public String getSelectedPatientFilePath() {
        return selectedPatientFilePath;
    }
    

	public String getSelectedScenarioFilePath() {
		return selectedScenarioFilePath;
	}
	
    /*
     * GET PATIENT DATA
     */
    public String getName_PATIENT() {
        return fieldMap.get("name").getText();
    }
    
    public String getSex_PATIENT() {
        return (String) sexComboBox_Patient.getSelectedItem();
    }
    
    public String getAge_PATIENT() {
        return fieldMap.get("age").getText();
    }

    public String getWeight_PATIENT() {
        return fieldMap.get("weight").getText();
    }

    public String getHeight_PATIENT() {
        return fieldMap.get("height").getText();
    }

    public String getBodyFatFraction_PATIENT() {
        return fieldMap.get("bodyFat").getText();
    }

    public String getHeartRate_PATIENT() {
        return fieldMap.get("heartRate").getText();
    }

    public String getDiastolicPressure_PATIENT() {
        return fieldMap.get("diastolic").getText();
    }

    public String getSystolicPressure_PATIENT() {
        return fieldMap.get("systolic").getText();
    }

    public String getRespirationRate_PATIENT() {
        return fieldMap.get("respirationRate").getText();
    }

    public String getBasalMetabolicRate_PATIENT() {
        return fieldMap.get("basalMetabolicRate").getText();
    }

    public String getWeightUnit_PATIENT() {
    	return (String) weightUnitComboBox.getSelectedItem();
    }
    
    public String getHeightUnit_PATIENT() {
    	return (String) heightUnitComboBox.getSelectedItem();
    }
    
    public void enableExportButton() {
    	exportButton.setEnabled(true);
    }

    public void enableStopButton() {
    	stopButton.setEnabled(true);
    }
}
