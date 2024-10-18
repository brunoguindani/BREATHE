package ventilators;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import app.App;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.HashMap;
import java.util.Map;

public class cpapVentilatorPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private NumberField fractionInspOxygen;
    private NumberField deltaPressureSup;
    private NumberField positiveEndExpPres;
    private NumberField slope;

    private Button applyButton;

    private App app;
    
    
    // Constructor
    public cpapVentilatorPanel(App app) {
    	this.app = app;
    	
        setSpacing(true);
        setPadding(true);
        
        // Form for CPAP ventilator settings
        FormLayout formLayout = new FormLayout();
        
        fractionInspOxygen = new NumberField("Fraction Inspired Oxygen - FiO2");
        fractionInspOxygen.setValue(0.21);
        fractionInspOxygen.setMin(0);
        fractionInspOxygen.setMax(1);
        fractionInspOxygen.setStep(0.01);

        deltaPressureSup = new NumberField("Delta Pressure Support - deltaPsupp");
        deltaPressureSup.setValue(10.0);
        deltaPressureSup.setMin(0);
        deltaPressureSup.setMax(50);
        deltaPressureSup.setStep(1);

        positiveEndExpPres = new NumberField("Positive End Expiratory Pressure - PEEP");
        positiveEndExpPres.setValue(5.0);
        positiveEndExpPres.setMin(0);
        positiveEndExpPres.setMax(20);
        positiveEndExpPres.setStep(1);

        slope = new NumberField("Slope");
        slope.setValue(0.2);
        slope.setMin(0);
        slope.setMax(2);
        slope.setStep(0.1);

        // Add fields to the form layout
        formLayout.add(fractionInspOxygen, deltaPressureSup, positiveEndExpPres, slope);

        // Apply button
        applyButton = new Button("Apply");
        applyButton.setEnabled(false);
        applyButton.addClickListener(e -> applySettings());

        // Button layout
        HorizontalLayout buttonLayout = new HorizontalLayout(applyButton);
        buttonLayout.setSpacing(true);

        // Add the form and button to the main layout
        add(formLayout, buttonLayout);
    }

    // Apply ventilator settings
    private void applySettings() {
    	app.connectVentilator();
    }

    // Get ventilator data as a map
    public Map<String, Number> getData() {
        Map<String, Number> dataMap = new HashMap<>();
        dataMap.put("FractionInspiredOxygen", fractionInspOxygen.getValue());
        dataMap.put("DeltaPressureSupport", deltaPressureSup.getValue().intValue()); 
        dataMap.put("PositiveEndExpiratoryPressure", positiveEndExpPres.getValue().intValue());
        dataMap.put("Slope", slope.getValue()); 
        return dataMap;
    }
    

    // Enable or disable the apply button
    public void setEnableApplyButton(boolean enable) {
        applyButton.setEnabled(enable);
    }
}
