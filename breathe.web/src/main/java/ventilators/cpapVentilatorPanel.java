package ventilators;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

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
		getStyle().set("background-color", "white"); // Colore di sfondo
		getStyle().set("margin", "0px");
		getStyle().set("padding", "2px");
		getStyle().set("border-bottom", "2px solid #ccc"); // Imposta il bordo

		setSpacing(false);
		
		Div fixedSizeDiv = new Div();
		fixedSizeDiv.getStyle().set("box-sizing", "border-box"); // Include padding e bordo nelle dimensioni

		// Form for CPAP ventilator settings
        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setPadding(false);
        fieldLayout.setSpacing(false);

		fractionInspOxygen = new NumberField("Fraction Inspired Oxygen - FiO2");
		fractionInspOxygen.setValue(0.21);
		fractionInspOxygen.setMin(0);
		fractionInspOxygen.setMax(1);
		fractionInspOxygen.setStep(0.01);
		fractionInspOxygen.setWidth("23vw");
		fractionInspOxygen.setStepButtonsVisible(true);

		deltaPressureSup = new NumberField("Delta Pressure Support - deltaPsupp");
		deltaPressureSup.setValue(10.0);
		deltaPressureSup.setMin(0);
		deltaPressureSup.setMax(50);
		deltaPressureSup.setStep(1);
		deltaPressureSup.setWidth("23vw");
		deltaPressureSup.setStepButtonsVisible(true);

		positiveEndExpPres = new NumberField("Positive End Expiratory Pressure - PEEP");
		positiveEndExpPres.setValue(5.0);
		positiveEndExpPres.setMin(0);
		positiveEndExpPres.setMax(20);
		positiveEndExpPres.setStep(1);
		positiveEndExpPres.setWidth("23vw");
		positiveEndExpPres.setStepButtonsVisible(true);

		slope = new NumberField("Slope");
		slope.setValue(0.2);
		slope.setMin(0);
		slope.setMax(2);
		slope.setWidth("23vw");
		slope.setStep(0.1);
		slope.setStepButtonsVisible(true);

		// Add fields to the form layout
		fieldLayout.add(fractionInspOxygen, deltaPressureSup, positiveEndExpPres, slope);

		// Apply button
		applyButton = new Button("Apply");
		applyButton.setEnabled(false);
		applyButton.addClickListener(e -> applySettings());
		HorizontalLayout buttonLayout = new HorizontalLayout(applyButton);
		buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Centra orizzontalmente
		buttonLayout.setWidthFull(); 

		Div scrollableDiv = new Div();
		scrollableDiv.getStyle().set("overflow-y", "auto");  // Scorrimento verticale
		scrollableDiv.getStyle().set("scrollbar-width", "none");

	    scrollableDiv.setHeight("55vh");  // Altezza fissa per il pannello scorrevole
        scrollableDiv.add(fieldLayout);
        fixedSizeDiv.add(scrollableDiv);
        fixedSizeDiv.setHeight("55vh");

        add(fixedSizeDiv);
        add(buttonLayout);
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
