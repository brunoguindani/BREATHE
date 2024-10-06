package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import app.App_temp;
import data.*;

public class PatientPanel {
	

	/*
	 * Fields
	 */
	private Map<String, JTextField> fieldMap = new HashMap<>();
    private JComboBox<String> sexComboBox_Patient = new JComboBox<>(new String[]{"Male", "Female"});
    private JComboBox<String> weightUnitComboBox, heightUnitComboBox;
	
	/*
	 * Inner Panels
	 */
	private JScrollPane dataPanel;
    private JPanel mainPanel = new JPanel();
    
    public PatientPanel(App_temp app) {
    	
    	//set up main panel
    	mainPanel.setBackground(Color.LIGHT_GRAY);
    	mainPanel.setPreferredSize(new Dimension(550, 650));
    	
    	//set up dataPanel
    	dataPanel = new JScrollPane();
        JPanel innerPanel = new JPanel(); 
        innerPanel.setLayout(new GridBagLayout());
        innerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        innerPanel.setBackground(Color.LIGHT_GRAY);
        
        //Grid
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        //fill up map
        fieldMap.put("Name", new JTextField("Standard", 20));
        fieldMap.put("Age", new JTextField("44"));
        fieldMap.put("Weight", new JTextField("77"));
        fieldMap.put("Height", new JTextField("180"));
        fieldMap.put("BodyFatFraction", new JTextField("0.21"));
        fieldMap.put("HeartRateBaseline", new JTextField("72"));
        fieldMap.put("DiastolicArterialPressureBaseline", new JTextField("72"));
        fieldMap.put("SystolicArterialPressureBaseline", new JTextField("114"));
        fieldMap.put("RespirationRateBaseline", new JTextField("16"));
        fieldMap.put("BasalMetabolicRate", new JTextField("1600"));
        
        // Selectors for patient data
        addLabelAndField("Name:", fieldMap.get("Name"), innerPanel, gbc);
        addLabelAndField("Sex:", sexComboBox_Patient, innerPanel, gbc);
        addLabelFieldAndUnit("Age:", fieldMap.get("Age"), new JLabel("yr"), innerPanel, gbc);
        weightUnitComboBox = new JComboBox<>(new String[]{"kg", "lbs"});
        addLabelFieldAndUnit("Weight:", fieldMap.get("Weight"), weightUnitComboBox, innerPanel, gbc);
        heightUnitComboBox = new JComboBox<>(new String[]{"cm", "m", "inches", "ft"});
        addLabelFieldAndUnit("Height:", fieldMap.get("Height"), heightUnitComboBox, innerPanel, gbc);
        addLabelFieldAndUnit("Body Fat Fraction:", fieldMap.get("BodyFatFraction"), new JLabel("%"), innerPanel, gbc);
        addLabelFieldAndUnit("Heart Rate Baseline:", fieldMap.get("HeartRateBaseline"), new JLabel("heartbeats/min"), innerPanel, gbc);
        addLabelFieldAndUnit("Diastolic Pressure:", fieldMap.get("DiastolicArterialPressureBaseline"), new JLabel("mmHg"), innerPanel, gbc);
        addLabelFieldAndUnit("Systolic Pressure:", fieldMap.get("SystolicArterialPressureBaseline"), new JLabel("mmHg"), innerPanel, gbc);
        addLabelFieldAndUnit("Respiration Rate Baseline:", fieldMap.get("RespirationRateBaseline"), new JLabel("breaths/min"), innerPanel, gbc);
        addLabelFieldAndUnit("Basal Metabolic Rate:", fieldMap.get("BasalMetabolicRate"), new JLabel("kcal/day"), innerPanel, gbc);
        
        //Add labels
        fieldMap.get("Age").setToolTipText("Value must be between 18 and 65");
        fieldMap.get("Height").setToolTipText("Value must be between 163cm and 190cm for male patients and between 151cm and 175cm for female patients");
        fieldMap.get("BodyFatFraction").setToolTipText("Value must be between 0.02% and 0.25% for male patients and between 0.1% and 0.32% for female patients");
        fieldMap.get("HeartRateBaseline").setToolTipText("Value must be between 50bpm and 110bpm");
        fieldMap.get("DiastolicArterialPressureBaseline").setToolTipText("Value must be between 60mmHg and 80mmHg");
        fieldMap.get("SystolicArterialPressureBaseline").setToolTipText("Value must be between 90mmHg and 120mmHg");
        fieldMap.get("RespirationRateBaseline").setToolTipText("Value must be between 8bpm and 20bpm");
        
        //Add the new panel to the scrollable View
        dataPanel.setViewportView(innerPanel);
        dataPanel.setPreferredSize(new Dimension(500, 400)); 
        dataPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        //Include everything to the main Panel
        mainPanel.add(dataPanel, BorderLayout.CENTER);
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
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
    
    public Patient getInitialPatient() {
    	if(checkFieldsNumeric()){
        	String name = fieldMap.get("Name").getText();
        	Map<String,Double> parameters = new HashMap<>();
        	char sex = 'F';
        	for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
        	    String chiave = entry.getKey();
        	    
        	    if(!chiave.equals("Name")) {
            	    Double valore = Double.parseDouble( entry.getValue().getText());
        	    	parameters.put(chiave, valore);
        	    }
        	}
    		if (sexComboBox_Patient.getSelectedItem().equals("Male")) {
    		    sex = 'M';
    		} 
    		List<Condition> conditions = new ArrayList<>();
        	//app.getConditions(); to get conditions and pass them to contruction;
        	return new Patient(name,sex,parameters,conditions); 	
    	}else{
    		JOptionPane.showMessageDialog(null, 
    			    "One or more fields contain invalid characters.\nPlease ensure all numeric fields contain only valid numbers.", 
    			    "Invalid Input", 
    			    JOptionPane.WARNING_MESSAGE);
    		return null;
    	}
    }
    
    //COMMETNS
    private boolean checkFieldsNumeric() {
        for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
            String key = entry.getKey();
            JTextField field = entry.getValue();

            if (key.equals("Name")) {
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
   //COMMETNS
    public boolean isValidNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int commaCount = str.length() - str.replace(",", "").length();
        commaCount += str.length() - str.replace(".", "").length();
        
        if (commaCount > 1) {
            return false;
        }
        
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                return false;
            }
        }

        try {
            Double.parseDouble(str.replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
