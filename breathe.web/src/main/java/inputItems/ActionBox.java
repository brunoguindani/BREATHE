package inputItems;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Map;
import java.util.HashMap;

import app.App;
import data.Action;

public class ActionBox extends VerticalLayout {

    private String title;
    private Map<String, TextField> components;
    private Button applySectionButton;

    // TextFields for time input
    private TextField hoursField;
    private TextField minutesField;
    private TextField secondsField;

    private App app;

    public ActionBox(App app, String title, Map<String, TextField> components) {
        this.app = app;
        this.title = title;
        this.components = components;

        // Header Button
        Button headerButton = new Button(title);
        headerButton.addClickListener(e -> toggleFields());

        // Create fields layout
        VerticalLayout fieldsLayout = new VerticalLayout();
        fieldsLayout.setVisible(false);

        // Add fields and spans
        for (Map.Entry<String, TextField> entry : components.entrySet()) {
            fieldsLayout.add(new Span(addSpaceBeforeUpperCase(entry.getKey()) + ":"));
            fieldsLayout.add(entry.getValue());
        }

        // Time fields
        createTimeFields(fieldsLayout);

        // Apply Button
        applySectionButton = new Button("Apply", e -> applyAction());
        applySectionButton.setEnabled(false);
        fieldsLayout.add(applySectionButton);

        // Add components to the layout
        add(headerButton, fieldsLayout);
    }

    private void createTimeFields(VerticalLayout fieldsLayout) {
        HorizontalLayout timeLayout = new HorizontalLayout();
        timeLayout.add(new Span("Time (hh:mm:ss)"));

        hoursField = new TextField("Hours");
        minutesField = new TextField("Minutes");
        secondsField = new TextField("Seconds");

        // Set input restrictions
        hoursField.setPlaceholder("0");
        minutesField.setPlaceholder("0");
        secondsField.setPlaceholder("0");

        timeLayout.add(hoursField, minutesField, secondsField);

        Button plusButton = new Button("+", e -> addToScenario());
        timeLayout.add(plusButton);
        fieldsLayout.add(timeLayout);
    }

    private void toggleFields() {
        // Toggle visibility of the fields
        boolean isVisible = !getComponentAt(1).isVisible();
        getComponentAt(1).setVisible(isVisible);
    }

    private void addToScenario() {
        int totalSeconds = getTotalTimeInSeconds();
        Map<String, Double> parameters = new HashMap<>();

        for (Map.Entry<String, TextField> entry : components.entrySet()) {
            Double value = Double.parseDouble(entry.getValue().getValue());
            parameters.put(entry.getKey(), value);
        }

        //app.addActiontoScenario(new Action(title, parameters), totalSeconds);
        Notification.show("Action added to scenario!");
    }

    private void applyAction() {
        Map<String, Double> parameters = new HashMap<>();
        for (Map.Entry<String, TextField> entry : components.entrySet()) {
            Double value = Double.parseDouble(entry.getValue().getValue());
            parameters.put(entry.getKey(), value);
        }

        //app.applyAction(new Action(title, parameters));
        Notification.show("Action applied!");
    }

    public int getTotalTimeInSeconds() {
        int hours = Integer.parseInt(hoursField.getValue());
        int minutes = Integer.parseInt(minutesField.getValue());
        int seconds = Integer.parseInt(secondsField.getValue());
        return hours * 3600 + minutes * 60 + seconds;
    }

    private String addSpaceBeforeUpperCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("(?<!^)([A-Z])", " $1").trim();
    }
}
