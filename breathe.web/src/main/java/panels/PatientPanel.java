package panels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
	private ComboBox<String> sexComboBox, weightUnitComboBox, heightUnitComboBox;
	private Map<String, NumberField> fieldMap = new HashMap<>();

	public PatientPanel(App app) {
		getStyle().set("margin", "0px");
		getStyle().set("padding", "2px");
		getStyle().set("border-bottom", "2px solid #ccc"); 

		setSpacing(false);

		Div fixedSizeDiv = new Div();
		fixedSizeDiv.getStyle().set("box-sizing", "border-box"); 

		FormLayout formLayout = new FormLayout();
		formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1)); 

		nameField = new TextField("Name");
		nameField.setValue("Standard");

		NumberField ageField = new NumberField("Age");
		ageField.setValue(44.0);
		ageField.setStep(1);
		ageField.setMin(18);
		ageField.setMax(65);
		ageField.setStepButtonsVisible(true);
		ageField.setWidth("10vw");
		fieldMap.put("Age", ageField);

		NumberField weightField = new NumberField("Weight");
		weightField.setValue(77.0);
		weightField.setStep(1);
		weightField.setStepButtonsVisible(true);
		weightField.setWidth("10vw");
		fieldMap.put("Weight", weightField);

		NumberField heightField = new NumberField("Height");
		heightField.setValue(180.0);
		heightField.setStep(1);
		heightField.setStepButtonsVisible(true);
		heightField.setWidth("10vw");
		fieldMap.put("Height", heightField);
		heightField.setTooltipText(
				"Value must be between 163cm and 190cm for male patients and between 151cm and 175cm for female patients");

		NumberField bodyFatField = new NumberField("Body Fat Fraction");
		bodyFatField.setValue(0.21);
		bodyFatField.setStep(0.01);
		bodyFatField.setStepButtonsVisible(true);
		bodyFatField.setWidth("10vw");
		bodyFatField.setTooltipText(
				"Value must be between 0.02% and 0.25% for male patients and 0.32% for female patients");
		fieldMap.put("BodyFatFraction", bodyFatField);

		NumberField heartRateField = new NumberField("Heart Rate Baseline");
		heartRateField.setValue(72.0);
		heartRateField.setStep(1);
		heartRateField.setMin(50);
		heartRateField.setMax(110);
		heartRateField.setWidth("10vw");
		heartRateField.setStepButtonsVisible(true);
		fieldMap.put("HeartRateBaseline", heartRateField);

		NumberField diastolicPressureField = new NumberField("Diastolic Pressure");
		diastolicPressureField.setValue(72.0);
		diastolicPressureField.setStep(1);
		diastolicPressureField.setMin(60);
		diastolicPressureField.setMax(80);
		diastolicPressureField.setWidth("10vw");
		diastolicPressureField.setStepButtonsVisible(true);
		fieldMap.put("DiastolicArterialPressureBaseline", diastolicPressureField);

		NumberField systolicPressureField = new NumberField("Systolic Pressure");
		systolicPressureField.setValue(114.0);
		systolicPressureField.setStep(1);
		systolicPressureField.setMin(90);
		systolicPressureField.setMax(120);
		systolicPressureField.setWidth("10vw");
		systolicPressureField.setStepButtonsVisible(true);
		fieldMap.put("SystolicArterialPressureBaseline", systolicPressureField);

		NumberField respirationRateField = new NumberField("Respiration Rate Baseline");
		respirationRateField.setValue(16.0);
		respirationRateField.setStep(1);
		respirationRateField.setMin(8);
		respirationRateField.setMax(20);
		respirationRateField.setWidth("10vw");
		respirationRateField.setStepButtonsVisible(true);
		fieldMap.put("RespirationRateBaseline", respirationRateField);

		NumberField basalMetabolicRateField = new NumberField("Basal Metabolic Rate");
		basalMetabolicRateField.setValue(1600.0);
		basalMetabolicRateField.setStep(10);
		basalMetabolicRateField.setStepButtonsVisible(true);
		basalMetabolicRateField.setWidth("10vw");
		fieldMap.put("BasalMetabolicRate", basalMetabolicRateField);

		sexComboBox = new ComboBox<>("Sex");
		sexComboBox.setItems("Male", "Female");
		sexComboBox.setValue("Male");
		sexComboBox.setWidth("10vw");

		weightUnitComboBox = new ComboBox<>("   ");
		weightUnitComboBox.setItems("kg", "lb");
		weightUnitComboBox.setValue("kg");
		weightUnitComboBox.setWidth("5vw");

		heightUnitComboBox = new ComboBox<>("   ");
		heightUnitComboBox.setItems("cm", "m", "in", "ft");
		heightUnitComboBox.setValue("cm");
		heightUnitComboBox.setWidth("5vw");

		formLayout.add(nameField);
		HorizontalLayout user = new HorizontalLayout(ageField, sexComboBox);
		formLayout.add(user);
		HorizontalLayout weight = new HorizontalLayout(weightField, weightUnitComboBox);
		weight.setAlignItems(Alignment.BASELINE);
		formLayout.add(weight);
		HorizontalLayout height = new HorizontalLayout(heightField, heightUnitComboBox);
		height.setAlignItems(Alignment.BASELINE);
		formLayout.add(height);
		formLayout.add(bodyFatField);
		formLayout.add(heartRateField);
		formLayout.add(diastolicPressureField);
		formLayout.add(systolicPressureField);
		formLayout.add(respirationRateField);
		formLayout.add(basalMetabolicRateField);

		Div scrollableDiv = new Div();
		scrollableDiv.getStyle().set("overflow-y", "auto");
		scrollableDiv.getStyle().set("scrollbar-width", "none");

		scrollableDiv.setHeight("70vh"); 
		scrollableDiv.add(formLayout);

		fixedSizeDiv.add(scrollableDiv);
		fixedSizeDiv.setHeight("70vh");

		add(fixedSizeDiv);

	}

	public Patient generateInitialPatient(List<Condition> conditions) {
		String name = nameField.getValue();
		Map<String, Double> parameters = new HashMap<>();

		char sex = 'F';
		for (Map.Entry<String, NumberField> entry : fieldMap.entrySet()) {

			if (!entry.getKey().equals("Name")) {
				NumberField numberField = (NumberField) entry.getValue();
				Double value = numberField.getValue();
				Double minValue = numberField.getMin();
				Double maxValue = numberField.getMax();

				// Controlla se il valore è fuori dall'intervallo
				if (value < minValue || value > maxValue) {
					numberField.setInvalid(true);
					numberField.setErrorMessage("Value must be between " + minValue + " and " + maxValue);
					return null;
				}

				numberField.setInvalid(false);
				parameters.put(entry.getKey(), value);
			}
		}

		if (sexComboBox.getValue().equals("Male"))
			sex = 'M';

		checkUnits(parameters);

		return new Patient(name, sex, parameters, conditions);
	}

	private void checkUnits(Map<String, Double> parameters) {

		if (weightUnitComboBox.getValue().equals("lb")) {
			parameters.put("Weight", fieldMap.get("Weight").getValue() * 0.453592);
		}

		switch ((String) heightUnitComboBox.getValue()) {
		case "m":
			parameters.put("Height", fieldMap.get("Height").getValue() * 100); 
			break;
		case "in":
			parameters.put("Height", fieldMap.get("Height").getValue() * 2.54); 
			break;
		case "ft":
			parameters.put("Height", fieldMap.get("Height").getValue() * 30.48);
			break;
		}
	}

	public void enableComponents(boolean enabled) {
		nameField.setEnabled(enabled);

		sexComboBox.setEnabled(enabled);
		weightUnitComboBox.setEnabled(enabled);
		heightUnitComboBox.setEnabled(enabled);

		for (NumberField field : fieldMap.values()) {
			field.setEnabled(enabled);
		}
	}

	public String getPatientName() {
		return nameField.getValue();
	}

}
