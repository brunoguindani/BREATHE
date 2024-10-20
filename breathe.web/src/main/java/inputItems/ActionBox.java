package inputItems;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.Map;
import java.util.HashMap;

import app.App;
import data.Action;

public class ActionBox extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private String title;
    private Map<String, Component> components;
    private Button applySectionButton, headerButton;

    // TextFields for time input
    private NumberField hoursField;
    private NumberField minutesField;
    private NumberField secondsField;

    private App app;

    public ActionBox(App app, String title, Map<String, Component> components2, boolean forScenario) {
        this.app = app;
        this.title = title;
        this.components = components2;
        
        setSpacing(false);

        this.getStyle().set("background-color", "white");
		getStyle().set("margin","0px" );
		getStyle().set("padding","0px" );
      
        // Header Button
        headerButton = new Button(title);
        headerButton.getStyle().set("text-align", "center");
        headerButton.setWidth("23.5vw");
        headerButton.addClickListener(e -> toggleFields());

        // Create fields layout
        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.getStyle().set("margin","0px" );
        fieldsLayout.getStyle().set("padding","0px" );
        fieldsLayout.setAlignItems(Alignment.CENTER);
        fieldsLayout.setVisible(false);
        
        // Add fields and spans
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            if (entry.getValue() instanceof NumberField) {
                NumberField numberField = (NumberField) entry.getValue(); 
                numberField.setMin(0.0);
                numberField.setStep(0.01);
                numberField.setStepButtonsVisible(true);
                numberField.setValue(0.0);
                if(!title.equals("Pericardial Effusion")) numberField.setMax(1.0);
            }
            fieldsLayout.add(entry.getValue()); 
        }

        // Time fields

        // "Apply" button
        applySectionButton = new Button("Apply", e -> applyAction());
        applySectionButton.setEnabled(false);
        
        if(forScenario) {
            createTimeFields(fieldsLayout); 	
        }else{
            fieldsLayout.add(applySectionButton);     	
        }

        // Add components to the layout
        add(headerButton, fieldsLayout);
    }

    private void createTimeFields(VerticalLayout fieldsLayout) {
        HorizontalLayout timeLayout = new HorizontalLayout();
        timeLayout.setWidth("23vw");
        
        // Imposta l'allineamento orizzontale e verticale
        timeLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Centra orizzontalmente
        timeLayout.setAlignItems(Alignment.CENTER); // Centra verticalmente

        hoursField = new NumberField("Hours");
        hoursField.setValue(0.0);
        hoursField.setStep(1); 
        hoursField.setMin(0);
        hoursField.setWidth("20%");

        minutesField = new NumberField("Minutes");
        minutesField.setValue(0.0);
        minutesField.setStep(1); 
        minutesField.setMin(0); 
        minutesField.setWidth("20%");

        secondsField = new NumberField("Seconds");
        secondsField.setValue(0.0);
        secondsField.setStep(1); 
        secondsField.setMin(0); 
        secondsField.setWidth("20%");

        // Set input restrictions
        hoursField.setPlaceholder("0");
        minutesField.setPlaceholder("0");
        secondsField.setPlaceholder("0");

        timeLayout.add(hoursField, minutesField, secondsField);

        Button plusButton = new Button(VaadinIcon.PLUS_CIRCLE.create(), e -> addToScenario());
        plusButton.setWidth("10%");
        timeLayout.add(plusButton);

        timeLayout.setAlignItems(Alignment.BASELINE);

        fieldsLayout.add(timeLayout);
    }

    private void toggleFields() {
        boolean isVisible = !getComponentAt(1).isVisible();
        getComponentAt(1).setVisible(isVisible);
        headerButton.setText(isVisible ? title + " (Close)" : title);
    }
    
    public void enableButton(boolean enable) {
    	applySectionButton.setEnabled(enable);
    	for (Map.Entry<String, Component> entry : components.entrySet()) {
    	    if (entry.getValue() instanceof HasEnabled) { 
    	        ((HasEnabled) entry.getValue()).setEnabled(enable); 
    	    }
    	}
    }

    private void addToScenario() {
        int totalSeconds = getTotalTimeInSeconds();
        Map<String, Double> parameters = new HashMap<>();

        for (Map.Entry<String, Component> entry : components.entrySet()) {
            if (entry.getValue() instanceof NumberField) {
            	NumberField numberField = (NumberField) entry.getValue();
            	Double value = numberField.getValue();
            	Double minValue = numberField.getMin();
            	Double maxValue = numberField.getMax();

            	// Controlla se il valore è fuori dall'intervallo
            	if (value < minValue || value > maxValue) {
            	    numberField.setInvalid(true);
            	    numberField.setErrorMessage("Value must be between " + minValue + " and " + maxValue);
            	    return; 
            	}

            	numberField.setInvalid(false);
            	parameters.put(entry.getKey(), value);
            }
        }

        app.addActiontoScenario(new Action(title, parameters), totalSeconds);
        Notification.show("Action added to scenario!",3000,Position.BOTTOM_END);
    }

    private void applyAction() {
        Map<String, Double> parameters = new HashMap<>();
        
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            if (entry.getValue() instanceof NumberField) {
                NumberField numberField = (NumberField) entry.getValue();
                Double value = numberField.getValue();
                if (value == null) value = 0.00;
            	Double minValue = numberField.getMin();
            	Double maxValue = numberField.getMax();

            	// Controlla se il valore è fuori dall'intervallo
            	if (value < minValue || value > maxValue) {
            	    numberField.setInvalid(true);
            	    numberField.setErrorMessage("Value must be between " + minValue + " and " + maxValue);
            	    return; 
            	}

            	numberField.setInvalid(false);
            	parameters.put(entry.getKey(), value);
            }
        }

        app.applyAction(new Action(title, parameters));
    }

    public int getTotalTimeInSeconds() {
        int hours = Math.max(0, hoursField.getValue().intValue());
        int minutes = Math.max(0, minutesField.getValue().intValue());
        int seconds = Math.max(0, secondsField.getValue().intValue());
        return hours * 3600 + minutes * 60 + seconds;
    }

}
