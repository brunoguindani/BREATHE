package ventilators;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import app.App;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.HashMap;
import java.util.Map;

public class pcVentilatorPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;

    private NumberField fractionInspOxygen;
    private NumberField inspiratoryPeriod;
    private NumberField inspiratoryPressure;
    private NumberField positiveEndExpPres;
    private NumberField respirationRate;
    private NumberField slope;
    private ComboBox<String> assistedMode;

    private Button applyButton;

    private App app;
    
    
    // Constructor
    public pcVentilatorPanel(App app) {
    	this.app = app;
    	
        setSpacing(true);
        setPadding(true);
        setSizeFull();

        // Form layout for PC ventilator settings
        FormLayout formLayout = new FormLayout();

        fractionInspOxygen = new NumberField("Fraction Inspired Oxygen - FiO2");
        fractionInspOxygen.setValue(0.21);
        fractionInspOxygen.setMin(0);
        fractionInspOxygen.setMax(1);
        fractionInspOxygen.setStep(0.01);

        inspiratoryPeriod = new NumberField("Inspiratory Period - Ti");
        inspiratoryPeriod.setValue(1.0);
        inspiratoryPeriod.setMin(0);
        inspiratoryPeriod.setMax(10);
        inspiratoryPeriod.setStep(0.1);

        inspiratoryPressure = new NumberField("Inspiratory Pressure - Pinsp");
        inspiratoryPressure.setValue(19.0);
        inspiratoryPressure.setMin(0);
        inspiratoryPressure.setMax(100);
        inspiratoryPressure.setStep(1);

        positiveEndExpPres = new NumberField("Positive End Expiratory Pressure - PEEP");
        positiveEndExpPres.setValue(5.0);
        positiveEndExpPres.setMin(0);
        positiveEndExpPres.setMax(20);
        positiveEndExpPres.setStep(1);

        respirationRate = new NumberField("Respiration Rate - RR");
        respirationRate.setValue(12.0);
        respirationRate.setMin(0);
        respirationRate.setMax(60);
        respirationRate.setStep(1);

        slope = new NumberField("Slope");
        slope.setValue(0.2);
        slope.setMin(0);
        slope.setMax(2);
        slope.setStep(0.1);

        assistedMode = new ComboBox<>("Assisted Mode");
        assistedMode.setItems("AC", "CMV");
        assistedMode.setValue("AC");

        // Add fields to the form layout
        formLayout.add(fractionInspOxygen, inspiratoryPeriod, inspiratoryPressure,
                positiveEndExpPres, respirationRate, slope, assistedMode);

        // Apply button
        applyButton = new Button("Apply");
        applyButton.setEnabled(false);
        applyButton.addClickListener(e -> applySettings());

        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(applyButton);
        buttonLayout.setSpacing(true);

        // Add form and button to the main layout
        add(formLayout, buttonLayout);
    }

    private void applySettings() {
    	app.connectVentilator();
        Notification.show("Settings updated");
    }

    // Get ventilator data as a map
    public Map<String, Number> getData() {
        Map<String, Number> dataMap = new HashMap<>();
        dataMap.put("FractionInspiredOxygen", fractionInspOxygen.getValue());
        dataMap.put("InspiratoryPeriod", inspiratoryPeriod.getValue());
        dataMap.put("InspiratoryPressure", inspiratoryPressure.getValue().intValue());
        dataMap.put("PositiveEndExpiratoryPressure", positiveEndExpPres.getValue().intValue());
        dataMap.put("RespirationRate", respirationRate.getValue().intValue());
        dataMap.put("Slope", slope.getValue());
        dataMap.put("AssistedMode", assistedMode.getValue().equals("AC") ? 0 : 1);  // Convert assisted mode to 0 or 1
        return dataMap;
    }
    

    // Enable or disable the apply button
    public void setEnableApplyButton(boolean enable) {
        applyButton.setEnabled(enable);
    }
}
