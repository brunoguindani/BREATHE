package inputItems;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.HashMap;
import java.util.Map;

import app.App;
import data.Condition;

public class ConditionBox extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private Button applySectionButton;
    private Button headerButton;

    private App app;

    private String title;
    private Map<String, Component> components;

    private boolean applied = false;

    public ConditionBox(App app, String title, Map<String, Component> components) {
        this.app = app;
        this.title = title;
        this.components = components;
        
        setSpacing(false);

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

        // "Apply" button
        applySectionButton = new Button("Apply", e -> applyCondition());
        applySectionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY); 
        applySectionButton.setEnabled(true);
        fieldsLayout.add(applySectionButton);

        // Add components to the layout
        add(headerButton, fieldsLayout);
    }

    private void toggleFields() {
        VerticalLayout fieldsLayout = (VerticalLayout) getComponentAt(1);
        boolean isVisible = !fieldsLayout.isVisible();
        fieldsLayout.setVisible(isVisible);
        headerButton.setText(isVisible ? title + " (Close)" : title);
    }

    private void applyCondition() {
        if (!applied) {

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
            
            enableFields(false);
            applySectionButton.setText("Remove");
            headerButton.getStyle().set("background-color", "lightblue");
            app.applyCondition(new Condition(title, parameters));
            applied = true;
        } else {
            // Removing Condition
            enableFields(true);
            applySectionButton.setText("Apply");
            headerButton.getStyle().set("background-color", "");
            app.removeCondition(title);
            applied = false;
        }
    }

    private void enableFields(boolean enable) {
        for (Map.Entry<String, Component> entry : components.entrySet()) {
        	if (entry.getValue() instanceof HasEnabled) { 
    	        ((HasEnabled) entry.getValue()).setEnabled(enable); 
    	    }
        }
    }

    public boolean isActive() {
        return applied;
    }

    public String getTitle() {
        return title;
    }

    public void reset() {
        enableFields(true);
        applySectionButton.setText("Apply");
        headerButton.getStyle().set("background-color", "");
        for (Map.Entry<String, Component> entry : components.entrySet()) {
        	if (entry.getValue() instanceof NumberField) {
                NumberField numberField = (NumberField) entry.getValue();
                numberField.setValue(0.00);
        	}
        }
        applied = false;
    }


    public void setComponents(Map<String, Double> parameters) {
        for (Map.Entry<String, Double> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            if (components.containsKey(key)) {
            	 if (components.get(key) instanceof NumberField) {
	            	NumberField textField = (NumberField) components.get(key);
	                textField.setValue(value);
	                applySectionButton.setText("Remove");
	                headerButton.getStyle().set("background-color", "lightblue");
	                app.applyCondition(new Condition(title, parameters));
	                applied = true;
            	 }
            }
        }
    }
    
    public void enableBox(boolean enable) {
    	applySectionButton.setEnabled(enable);
    	enableFields(enable);
    }
}
