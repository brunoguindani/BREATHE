package ventilators;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import app.App;

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

        getStyle().set("background-color", "white"); // Colore di sfondo
        getStyle().set("margin", "0px");
        getStyle().set("padding", "2px");
        getStyle().set("border-bottom", "2px solid #ccc"); // Imposta il bordo

        setSpacing(false);

        Div fixedSizeDiv = new Div();
        fixedSizeDiv.getStyle().set("box-sizing", "border-box"); // Include padding e bordo nelle dimensioni

        // Create form layout for PC ventilator settings
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setPadding(false);
        fieldLayout.setSpacing(false);

        fractionInspOxygen = new NumberField("Fraction Inspired Oxygen - FiO2");
        fractionInspOxygen.setValue(0.21);
        fractionInspOxygen.setMin(0);
        fractionInspOxygen.setMax(1);
        fractionInspOxygen.setStep(0.01);
        fractionInspOxygen.setWidth("23vw"); // Larghezza consistente
        fractionInspOxygen.setStepButtonsVisible(true);

        inspiratoryPeriod = new NumberField("Inspiratory Period - Ti");
        inspiratoryPeriod.setValue(1.0);
        inspiratoryPeriod.setMin(0);
        inspiratoryPeriod.setMax(10);
        inspiratoryPeriod.setStep(0.1);
        inspiratoryPeriod.setWidth("23vw");
        inspiratoryPeriod.setStepButtonsVisible(true);

        inspiratoryPressure = new NumberField("Inspiratory Pressure - Pinsp");
        inspiratoryPressure.setValue(19.0);
        inspiratoryPressure.setMin(0);
        inspiratoryPressure.setMax(100);
        inspiratoryPressure.setStep(1);
        inspiratoryPressure.setWidth("23vw");
        inspiratoryPressure.setStepButtonsVisible(true);

        positiveEndExpPres = new NumberField("Positive End Expiratory Pressure - PEEP");
        positiveEndExpPres.setValue(5.0);
        positiveEndExpPres.setMin(0);
        positiveEndExpPres.setMax(20);
        positiveEndExpPres.setStep(1);
        positiveEndExpPres.setWidth("23vw");
        positiveEndExpPres.setStepButtonsVisible(true);

        respirationRate = new NumberField("Respiration Rate - RR");
        respirationRate.setValue(12.0);
        respirationRate.setMin(0);
        respirationRate.setMax(60);
        respirationRate.setStep(1);
        respirationRate.setWidth("23vw");
        respirationRate.setStepButtonsVisible(true);

        slope = new NumberField("Slope");
        slope.setValue(0.2);
        slope.setMin(0);
        slope.setMax(2);
        slope.setStep(0.1);
        slope.setWidth("23vw");
        slope.setStepButtonsVisible(true);

        assistedMode = new ComboBox<>("Assisted Mode");
        assistedMode.setItems("AC", "CMV");
        assistedMode.setValue("AC");
        assistedMode.setWidth("23vw");

        // Add fields to the form layout
        fieldLayout.add(fractionInspOxygen, inspiratoryPeriod, inspiratoryPressure,
                positiveEndExpPres, respirationRate, slope, assistedMode);

        // Create a scrollable panel for fields
        Div scrollableDiv = new Div();
        scrollableDiv.getStyle().set("overflow-y", "auto"); // Scorrimento verticale
        scrollableDiv.getStyle().set("scrollbar-width", "none");
        scrollableDiv.setHeight("55vh"); // Altezza fissa per il pannello scorrevole
        scrollableDiv.add(fieldLayout);
        fixedSizeDiv.add(scrollableDiv);
        fixedSizeDiv.setHeight("55vh");

        // Apply button
        applyButton = new Button("Apply");
        applyButton.setEnabled(false);
        applyButton.addClickListener(e -> applySettings());
        HorizontalLayout buttonLayout = new HorizontalLayout(applyButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Centra orizzontalmente
        buttonLayout.setWidthFull();

        // Add the fixed size Div and button layout to the main layout
        add(fixedSizeDiv);
        add(buttonLayout);
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
