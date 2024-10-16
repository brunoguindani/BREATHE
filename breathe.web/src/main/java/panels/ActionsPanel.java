package panels;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import app.App;
import inputItems.ActionBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("actions-panel")
public class ActionsPanel extends VerticalLayout {
	
    private List<ActionBox> boxes = new ArrayList<>();

    public ActionsPanel(App app) {
        //setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "white"); // Light gray
        setMaxWidth("90%"); 

        FlexLayout actionsContainer = new FlexLayout();
        actionsContainer.setFlexDirection(FlexDirection.COLUMN);
        //actionsContainer.setSizeFull();
        actionsContainer.setMaxWidth("90%"); 
        
        // Scrollable container for all actions
        VerticalLayout scrollableContent = new VerticalLayout();
        scrollableContent.setPadding(false);
        scrollableContent.setMaxWidth("90%"); 
        scrollableContent.setSpacing(false);

        actionsContainer.add(scrollableContent);
        actionsContainer.getStyle().set("overflow", "auto");  // Enable scrolling

        // Add ActionBoxes
        addActionBox(app, "ARDS Exacerbation", new String[]{"LeftLungSeverity", "RightLungSeverity"}, scrollableContent);
        addActionBox(app, "Acute Stress", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "Airway Obstruction", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "Asthma Attack", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "Brain Injury", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "Bronchoconstriction", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "COPD Exacerbation", new String[]{
                "BronchitisSeverity", "LeftLungEmphysemaSeverity", "RightLungEmphysemaSeverity"}, scrollableContent);
        addActionBox(app, "Dyspnea", new String[]{"RespirationRateSeverity"}, scrollableContent);
        addActionBox(app, "Exercise", new String[]{"Intensity"}, scrollableContent);
        addActionBox(app, "Pericardial Effusion", new String[]{"EffusionRate ml/s"}, scrollableContent, 1000);
        addActionBox(app, "Pneumonia Exacerbation", new String[]{"LeftLungSeverity", "RightLungSeverity"}, scrollableContent);
        addActionBox(app, "Pulmonary Shunt Exacerbation", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "Respiratory Fatigue", new String[]{"Severity"}, scrollableContent);
        addActionBox(app, "Urinate", new String[]{}, scrollableContent);
        addActionBox(app, "Ventilator Leak", new String[]{"Severity"}, scrollableContent);

        // Create a scrollable panel for patient data
        Div scrollableDiv = new Div();
        scrollableDiv.setWidth("100%");  // Imposta la larghezza al 100%
        scrollableDiv.setHeight("50vh"); // Altezza calcolata per il contenuto, sottraendo il bordo
        scrollableDiv.getStyle().set("overflow-y", "auto");  // Scorrimento verticale
        scrollableDiv.add(actionsContainer);
        
        add(scrollableDiv);
    }

    private void addActionBox(App app, String title, String[] fields, VerticalLayout container) {
        addActionBox(app, title, fields, container, 1.0);  // Default max value 1.0
    }

    private void addActionBox(App app, String title, String[] fields, VerticalLayout container, double max) {
        Map<String, Component> components = new HashMap<>();
        for (String field : fields) {
            NumberField spinner = new NumberField(field);
            spinner.setMin(0);
            spinner.setMax(max);
            spinner.setStep(0.01);
            components.put(field, spinner);
        }
        ActionBox actionBox = new ActionBox(app, title, components);
        boxes.add(actionBox);
        container.add(actionBox);
    }

    public void enableButtons(boolean enable) {
        for (ActionBox box : boxes) {
            box.enableButton(enable);
        }
    }
}
