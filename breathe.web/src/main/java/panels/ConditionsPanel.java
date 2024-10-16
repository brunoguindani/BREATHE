package panels;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

import app.App;
import data.Condition;
import inputItems.ConditionBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("conditions-panel")
public class ConditionsPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private List<ConditionBox> boxes = new ArrayList<>();
    private List<Condition> activeConditions = new ArrayList<>();
    private Button resetButton;

    public ConditionsPanel(App app) {
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "white");
        setWidth("90%");

        FlexLayout conditionsContainer = new FlexLayout();
        conditionsContainer.setFlexDirection(FlexDirection.COLUMN);
        conditionsContainer.setWidth("90%");
        conditionsContainer.getStyle().set("overflow", "auto");

        // Scrollable container for all conditions
        VerticalLayout scrollableContent = new VerticalLayout();
        scrollableContent.setPadding(false);
        scrollableContent.setWidth("90%");
        scrollableContent.setSpacing(false);
        
        conditionsContainer.add(scrollableContent);

        // Add ConditionBoxes
        addConditionBox(app, "Chronic Anemia", new String[]{"ReductionFactor"}, scrollableContent);
        addConditionBox(app, "ARDS", new String[]{"LeftLungSeverity", "RightLungSeverity"}, scrollableContent);
        addConditionBox(app, "COPD", new String[]{"BronchitisSeverity", "LeftLungEmphysemaSeverity", "RightLungEmphysemaSeverity"}, scrollableContent);
        addConditionBox(app, "Pericardial Effusion", new String[]{"AccumulatedVolume"}, scrollableContent, 100);
        addConditionBox(app, "Renal Stenosis", new String[]{"LeftKidneySeverity", "RightKidneySeverity"}, scrollableContent);
        addConditionBox(app, "Pneumonia", new String[]{"LeftLungSeverity", "RightLungSeverity"}, scrollableContent);
        addConditionBox(app, "Pulmonary Fibrosis", new String[]{"Severity"}, scrollableContent);
        addConditionBox(app, "Pulmonary Shunt", new String[]{"Severity"}, scrollableContent);

        // Create a scrollable panel for conditions data
        Div scrollableDiv = new Div();
        scrollableDiv.setWidth("100%");
        scrollableDiv.setHeight("50vh");
        scrollableDiv.getStyle().set("overflow-y", "auto");
        scrollableDiv.add(conditionsContainer);

        add(scrollableDiv);

        // Reset Button
        resetButton = new Button("Reset Conditions", e -> resetConditions());
        add(resetButton);
    }

    private void addConditionBox(App app, String title, String[] fields, VerticalLayout container) {
        addConditionBox(app, title, fields, container, 1.0);  // Default max value 1.0
    }

    private void addConditionBox(App app, String title, String[] fields, VerticalLayout container, double max) {
        Map<String, Component> components = new HashMap<>();
        for (String field : fields) {
            NumberField spinner = new NumberField(field);
            spinner.setMin(0);
            spinner.setMax(max);
            spinner.setStep(0.01);
            components.put(field, spinner);
        }
        ConditionBox conditionBox = new ConditionBox(app, title, components);
        boxes.add(conditionBox);
        container.add(conditionBox);
    }

    private void resetConditions() {
        for (ConditionBox box : boxes) {
            box.reset(); 
        }
        activeConditions.clear(); 
    }

    public void addCondition(Condition c) {
        activeConditions.add(c);
    }

    public void removeCondition(String title) {
        activeConditions.removeIf(c -> c.getTitle().equals(title));
    }

    public List<Condition> getActiveConditions() {
        return activeConditions;
    }

    public void enableButtons(boolean enable) {
        resetButton.setEnabled(enable);
        for (ConditionBox box : boxes) {
            box.enableBox(enable); // Assuming ConditionBox has a method to enable/disable
        }
    }

    public void setInitialConditions(List<Condition> list) {
        activeConditions.clear();
        for (ConditionBox box : boxes) {
            boolean found = false;
            for (Condition condition : list) {
                if (box.getTitle().equals(condition.getTitle())) {
                    found = true;
                    box.setComponents(condition.getParameters()); // Assuming ConditionBox has this method
                    break;
                }
            }
            if (!found) {
                box.reset(); 
            }
        }
    }
}
