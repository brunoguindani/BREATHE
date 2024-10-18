package panels;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import app.App;

@Route("patient-condition-panel")
public class PatientConditionPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private final PatientPanel patientPanel; // Assuming you have a PatientPanel class
    private final ConditionsPanel conditionsPanel; // Assuming you have a ConditionsPanel class

    public PatientConditionPanel(App app) {
        this.patientPanel = new PatientPanel(app);
        this.conditionsPanel = new ConditionsPanel(app);
		this.setWidthFull();
		this.setMaxHeight("70%");  
		getStyle().set("border", "1px solid #ccc"); // Imposta il bordo

        
        // Create RadioButtonGroup for switching
        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setLabel("Select Panel");
        radioButtonGroup.setItems("Patient", "Conditions");
        radioButtonGroup.setValue("Patient"); // Set default value

        // Div to hold the current visible panel
        Div contentLayout = new Div();
        contentLayout.setWidth("100%"); // Imposta la larghezza al 100%
        contentLayout.setHeight("80vh"); // Imposta l'altezza desiderata

        contentLayout.add(patientPanel); // Add the default patient panel

        // Add listener to change displayed panel
        radioButtonGroup.addValueChangeListener(event -> {
            String selected = event.getValue();
            contentLayout.removeAll(); // Clear the current panel
            if ("Patient".equals(selected)) {
                contentLayout.add(patientPanel); // Add patient panel
            } else if ("Conditions".equals(selected)) {
                contentLayout.add(conditionsPanel); // Add conditions panel
            }
        });

        // Add components to the main layout
        add(radioButtonGroup, contentLayout);
    }
    
    public PatientPanel getPatientPanel() {
    	return patientPanel;
    }
    
    public ConditionsPanel getConditionsPanel() {
    	return conditionsPanel;
    }
}
