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

public class vcVentilatorPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;

    private NumberField flow;
    private NumberField fractionInspOxygen;
    private NumberField positiveEndExpPres;
    private NumberField inspiratoryPeriod;
    private NumberField respirationRate;
    private NumberField tidalVol;
    private ComboBox<String> assistedMode;

    private Button applyButton;
    
    private App app;

    // Constructor for VC ventilator panel
    public vcVentilatorPanel(App app) {
    	this.app = app; 
    	
        setSpacing(true);
        setPadding(true);

        // Create form layout for ventilator settings
        FormLayout formLayout = new FormLayout();

        flow = new NumberField("Flow");
        flow.setValue(60.0);
        flow.setMin(0);
        flow.setMax(120);
        flow.setStep(1);

        fractionInspOxygen = new NumberField("Fraction Inspired Oxygen - FiO2");
        fractionInspOxygen.setValue(0.21);
        fractionInspOxygen.setMin(0);
        fractionInspOxygen.setMax(1);
        fractionInspOxygen.setStep(0.01);

        positiveEndExpPres = new NumberField("Positive End Expiratory Pressure - PEEP");
        positiveEndExpPres.setValue(5.0);
        positiveEndExpPres.setMin(0);
        positiveEndExpPres.setMax(20);
        positiveEndExpPres.setStep(1);

        inspiratoryPeriod = new NumberField("Inspiratory Period");
        inspiratoryPeriod.setValue(1.0);
        inspiratoryPeriod.setMin(0);
        inspiratoryPeriod.setMax(10);
        inspiratoryPeriod.setStep(0.1);

        respirationRate = new NumberField("Respiration Rate - RR");
        respirationRate.setValue(12.0);
        respirationRate.setMin(0);
        respirationRate.setMax(60);
        respirationRate.setStep(1);

        tidalVol = new NumberField("Tidal Volume - VT");
        tidalVol.setValue(900.0);
        tidalVol.setMin(0);
        tidalVol.setMax(2000);
        tidalVol.setStep(10);

        assistedMode = new ComboBox<>("Assisted Mode");
        assistedMode.setItems("AC", "CMV");
        assistedMode.setValue("AC");

        // Add fields to the form layout
        formLayout.add(flow, fractionInspOxygen, positiveEndExpPres, inspiratoryPeriod, respirationRate, tidalVol, assistedMode);

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
        dataMap.put("Flow", flow.getValue().intValue());
        dataMap.put("FractionInspiredOxygen", fractionInspOxygen.getValue());
        dataMap.put("PositiveEndExpiratoryPressure", positiveEndExpPres.getValue().intValue());
        dataMap.put("InspiratoryPeriod", inspiratoryPeriod.getValue());
        dataMap.put("RespirationRate", respirationRate.getValue().intValue());
        dataMap.put("TidalVolume", tidalVol.getValue().intValue());
        dataMap.put("AssistedMode", assistedMode.getValue().equals("AC") ? 0 : 1);  // Convert assisted mode to 0 or 1
        return dataMap;
    }
   
    // Enable or disable the apply button
    public void setEnableApplyButton(boolean enable) {
        applyButton.setEnabled(enable);
    }
}
