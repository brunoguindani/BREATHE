package panels;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

import app.App;

@Route("patient-condition-panel")
public class PatientConditionPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private final PatientPanel patientPanel; // Assuming you have a PatientPanel class
    private final ConditionsPanel conditionsPanel; // Assuming you have a ConditionsPanel class

    public PatientConditionPanel(App app) {
        this.patientPanel = new PatientPanel(app);
        this.conditionsPanel = new ConditionsPanel(app);
        setSpacing(false);
        getStyle().set("margin","0px" );
        getStyle().set("padding","0px" );

        // Crea i bottoni per la selezione
        Button patientButton = new Button("Patient");
        Button conditionsButton = new Button("Conditions");

        // Imposta il pulsante "Patient" come attivo di default
        patientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        // Div per contenere il pannello attuale
        Div contentLayout = new Div();
        contentLayout.getStyle().set("margin", "0").set("padding", "0"); // Imposta margine e padding a zero
        contentLayout.add(patientPanel); // Pannello di default

        // Listener per il pulsante "Patient"
        patientButton.addClickListener(event -> {
            contentLayout.removeAll();
            contentLayout.add(patientPanel);
            patientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            conditionsButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        // Listener per il pulsante "Conditions"
        conditionsButton.addClickListener(event -> {
            contentLayout.removeAll();
            contentLayout.add(conditionsPanel);
            conditionsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            patientButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

        // Aggiungi i pulsanti e il layout del contenuto al layout principale
        HorizontalLayout topArea = new HorizontalLayout();
        topArea.setWidthFull(); 
        topArea.setJustifyContentMode(JustifyContentMode.CENTER); 
        
        patientButton.setMaxWidth("30%");
        conditionsButton.setMaxWidth("30%");

        topArea.add(patientButton, conditionsButton);
        add(topArea, contentLayout);
        add(topArea, contentLayout);
    }
    
    public PatientPanel getPatientPanel() {
    	return patientPanel;
    }
    
    public ConditionsPanel getConditionsPanel() {
    	return conditionsPanel;
    }
}
