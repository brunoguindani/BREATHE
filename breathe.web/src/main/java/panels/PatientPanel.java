package panels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.NumberField;

import app.App;
import data.Condition;
import data.Patient;

import com.vaadin.flow.component.html.Div;

public class PatientPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	
	private TextField nameField;
	private ComboBox<String> sexComboBox,weightUnitComboBox,heightUnitComboBox;
	private Map<String, NumberField> fieldMap = new HashMap<>();

    public PatientPanel(App app) {
        // Main panel setup
        setWidthFull();  // Imposta la larghezza al 100%
        setHeight("50vh");  // Imposta l'altezza al 100% della viewport
        getStyle().set("background-color", "white");  // Colore di sfondo

        // Create a fixed size Div for the panel
        Div fixedSizeDiv = new Div();
        fixedSizeDiv.setWidth("100%");  // Imposta la larghezza al 100%
        fixedSizeDiv.setHeight("100vh");  // Imposta l'altezza al 100% della viewport
        fixedSizeDiv.getStyle().set("box-sizing", "border-box"); // Include padding e bordo nelle dimensioni

        // Form layout for patient data
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));  // Layout reattivo
        formLayout.setWidthFull();  // Imposta la larghezza al 100%

        nameField = new TextField("Name");
        nameField.setValue("Standard");

        NumberField ageField = new NumberField("Age");
        ageField.setValue(44.0);
        ageField.setStep(1); 
        ageField.setMin(0); 
        fieldMap.put("Age", ageField);

        NumberField weightField = new NumberField("Weight");
        weightField.setValue(77.0);
        weightField.setStep(1);
        fieldMap.put("Weight", weightField);

        NumberField heightField = new NumberField("Height");
        heightField.setValue(180.0);
        heightField.setStep(1);
        fieldMap.put("Height", heightField);

        NumberField bodyFatField = new NumberField("Body Fat Fraction");
        bodyFatField.setValue(0.21); 
        fieldMap.put("BodyFatFraction", bodyFatField);

        NumberField heartRateField = new NumberField("Heart Rate Baseline");
        heartRateField.setValue(72.0);
        heartRateField.setStep(1);
        fieldMap.put("HeartRateBaseline", heartRateField);

        NumberField diastolicPressureField = new NumberField("Diastolic Pressure");
        diastolicPressureField.setValue(72.0);
        diastolicPressureField.setStep(1);
        fieldMap.put("DiastolicArterialPressureBaseline", diastolicPressureField);

        NumberField systolicPressureField = new NumberField("Systolic Pressure");
        systolicPressureField.setValue(114.0);
        systolicPressureField.setStep(1);
        fieldMap.put("SystolicArterialPressureBaseline", systolicPressureField);

        NumberField respirationRateField = new NumberField("Respiration Rate Baseline");
        respirationRateField.setValue(16.0);
        respirationRateField.setStep(1);
        fieldMap.put("RespirationRateBaseline", respirationRateField);

        NumberField basalMetabolicRateField = new NumberField("Basal Metabolic Rate");
        basalMetabolicRateField.setValue(1600.0);
        basalMetabolicRateField.setStep(1);
        fieldMap.put("BasalMetabolicRate", basalMetabolicRateField);

        // Define ComboBoxes for units
        sexComboBox = new ComboBox<>("Sex");
        sexComboBox.setItems("Male", "Female"); 
        sexComboBox.setValue("Male"); 

        weightUnitComboBox = new ComboBox<>("Weight Unit");
        weightUnitComboBox.setItems("kg", "lb"); 
        weightUnitComboBox.setValue("kg");

        heightUnitComboBox = new ComboBox<>("Height Unit");
        heightUnitComboBox.setItems("cm", "m", "in", "ft"); 
        heightUnitComboBox.setValue("cm");

        // Add tooltips
        ageField.setHelperText("Value must be between 18 and 65");
        heightField.setHelperText("Value must be between 163cm and 190cm for male patients and between 151cm and 175cm for female patients");
        bodyFatField.setHelperText("Value must be between 0.02% and 0.25% for male patients and 0.32% for female patients");
        heartRateField.setHelperText("Value must be between 50bpm and 110bpm");
        diastolicPressureField.setHelperText("Value must be between 60mmHg and 80mmHg");
        systolicPressureField.setHelperText("Value must be between 90mmHg and 120mmHg");
        respirationRateField.setHelperText("Value must be between 8bpm and 20bpm");

        // Add fields to form layout (in a similar manner to GridBagLayout in Swing)
        formLayout.add(nameField);
        formLayout.add(sexComboBox);
        formLayout.add(ageField, 1);
        formLayout.add(weightField);
        formLayout.add(weightUnitComboBox);
        formLayout.add(heightField);
        formLayout.add(heightUnitComboBox);
        formLayout.add(bodyFatField);
        formLayout.add(heartRateField);
        formLayout.add(diastolicPressureField);
        formLayout.add(systolicPressureField);
        formLayout.add(respirationRateField);
        formLayout.add(basalMetabolicRateField);

        // Create a scrollable panel for patient data
        Div scrollableDiv = new Div();
        scrollableDiv.setWidth("100%");  // Imposta la larghezza al 100%
        scrollableDiv.setHeight("50vh"); // Altezza calcolata per il contenuto, sottraendo il bordo
        scrollableDiv.getStyle().set("overflow-y", "auto");  // Scorrimento verticale
        scrollableDiv.add(formLayout);

        // Add the scrollable panel to the fixed size Div
        fixedSizeDiv.add(scrollableDiv);

        // Add the fixed size Div to the main layout
        add(fixedSizeDiv);
    }
    
    public Patient generateInitialPatient(List<Condition> conditions) {
    	String name = nameField.getValue();
    	Map<String,Double> parameters = new HashMap<>();
    	
    	char sex = 'F';
    	for (Map.Entry<String, NumberField> entry : fieldMap.entrySet()) {
    	    String chiave = entry.getKey();
    	    
    	    if(!chiave.equals("Name")) {
        	    Double valore = entry.getValue().getValue();
    	    	parameters.put(chiave, valore);
    	    }
    	}
    	
		if (sexComboBox.getValue().equals("Male")) 
		    sex = 'M';
		
		//methods to convert weight and height (kg and cm)
		checkUnits(parameters);

    	return new Patient(name,sex,parameters,conditions); 	
    }
    
	private void checkUnits(Map<String, Double> parameters) {
		
	    // weight conversion (if necessary)
	    if(weightUnitComboBox.getValue().equals("lb")) {
	    	parameters.put("Weight", fieldMap.get("Weight").getValue() * 0.453592); 
	    }

	    // height conversion (if necessary)
	    switch ((String) heightUnitComboBox.getValue()) {
	        case "m":
	        	parameters.put("Height", fieldMap.get("Height").getValue() * 100); // m to cm
	            break;
	        case "in":
	        	parameters.put("Height", fieldMap.get("Height").getValue() * 2.54); // in to cm
	            break;
	        case "ft":
	        	parameters.put("Height", fieldMap.get("Height").getValue() * 30.48); // feat to cm
	            break;
	    }
	}

	public void enableComponents(boolean enabled) {
	    // Abilita o disabilita i TextField
	    nameField.setEnabled(enabled);

	    // Abilita o disabilita i ComboBox
	    sexComboBox.setEnabled(enabled);
	    weightUnitComboBox.setEnabled(enabled);
	    heightUnitComboBox.setEnabled(enabled);

	    // Abilita o disabilita tutti i NumberField nella mappa
	    for (NumberField field : fieldMap.values()) {
	        field.setEnabled(enabled);
	    }
	}

	public String getPatientName() {
		return fieldMap.get("Name").getValue().toString();
	}
	
	
}
