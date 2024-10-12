package inputItems;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;

import java.util.HashMap;
import java.util.Map;

import app.App;
import data.Condition;

public class ConditionBox extends VerticalLayout {

    private Button applySectionButton;
    private Button headerButton;

    private App app;

    private String title;
    private Map<String, TextField> components;

    private boolean applied = false;

    public ConditionBox(App app, String title, Map<String, TextField> components) {
        this.app = app;
        this.title = title;
        this.components = components;

        setBackgroundColor();

        // Header Button
        headerButton = new Button(title);
        headerButton.addClickListener(e -> toggleFields());

        // Create fields layout
        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.setVisible(false);

        // Add fields and spans
        for (Map.Entry<String, TextField> entry : components.entrySet()) {
            Span label = new Span(addSpaceBeforeUpperCase(entry.getKey()) + ":");
            fieldsLayout.add(label);
            fieldsLayout.add(entry.getValue());
        }

        // "Apply" button
        applySectionButton = new Button("Apply", e -> applyCondition());
        applySectionButton.setEnabled(true);
        fieldsLayout.add(applySectionButton);

        // Add components to the layout
        add(headerButton, fieldsLayout);
    }

    private void setBackgroundColor() {
        this.getStyle().set("background-color", "lightgray");
    }

    private void toggleFields() {
        VerticalLayout fieldsLayout = (VerticalLayout) getComponentAt(1);
        boolean isVisible = !fieldsLayout.isVisible();
        fieldsLayout.setVisible(isVisible);
        headerButton.setText(isVisible ? title + " (Close)" : title);
    }

    private void applyCondition() {
        if (!applied) {
            enableFields(false);
            applySectionButton.setText("Remove");
            headerButton.getStyle().set("background-color", "lightblue");

            Map<String, Double> parameters = new HashMap<>();
            for (Map.Entry<String, TextField> entry : components.entrySet()) {
                String key = entry.getKey();
                TextField textField = entry.getValue();
                Double value = Double.parseDouble(textField.getValue());
                parameters.put(key, value);
            }

            //app.applyCondition(new Condition(title, parameters));
            app.minilogStringData(title + " applied");
            applied = true;
        } else {
            // Removing Condition
            enableFields(true);
            applySectionButton.setText("Apply");
            headerButton.getStyle().set("background-color", "darkgray");
            //app.removeCondition(title);
            app.minilogStringData(title + " removed");
            applied = false;
        }
    }

    private void enableFields(boolean enable) {
        for (Map.Entry<String, TextField> entry : components.entrySet()) {
            entry.getValue().setEnabled(enable);
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
        headerButton.getStyle().set("background-color", "darkgray");
        for (Map.Entry<String, TextField> entry : components.entrySet()) {
            entry.getValue().setValue("0");  // Resetting values
        }
        applied = false;
    }

    private String addSpaceBeforeUpperCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("(?<!^)([A-Z])", " $1").trim();
    }

    public void setComponents(Map<String, Double> parameters) {
        for (Map.Entry<String, Double> entry : parameters.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            if (components.containsKey(key)) {
                TextField textField = components.get(key);
                textField.setValue(value.toString());
                applySectionButton.setText("Remove");
                headerButton.getStyle().set("background-color", "lightblue");
                //app.applyCondition(new Condition(title, parameters));
                applied = true;
            }
        }
    }
}
