package panels;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import app.App;

import com.vaadin.flow.component.html.Div;

public class PatientPanel extends VerticalLayout {

    public PatientPanel(App app) {
        // Main panel setup
        setWidthFull();  // Imposta la larghezza al 100%
        setHeight("50vh");  // Imposta l'altezza al 100% della viewport
        getStyle().set("background-color", "white");  // Colore di sfondo

        // Create a fixed size Div for the panel
        Div fixedSizeDiv = new Div();
        fixedSizeDiv.setWidth("100%");  // Imposta la larghezza al 100%
        fixedSizeDiv.setHeight("100vh");  // Imposta l'altezza al 100% della viewport
        fixedSizeDiv.getStyle().set("box-sizing", "border-box"); // Include padding e bordo nelle dimensioni

        // Form layout for patient data
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));  // Layout reattivo
        formLayout.setWidthFull();  // Imposta la larghezza al 100%

        // Define text fields
        TextField nameField = new TextField("Name");
        TextField ageField = new TextField("Age");
        TextField weightField = new TextField("Weight");
        TextField heightField = new TextField("Height");
        TextField bodyFatField = new TextField("Body Fat Fraction");
        TextField heartRateField = new TextField("Heart Rate Baseline");
        TextField diastolicPressureField = new TextField("Diastolic Pressure");
        TextField systolicPressureField = new TextField("Systolic Pressure");
        TextField respirationRateField = new TextField("Respiration Rate Baseline");
        TextField basalMetabolicRateField = new TextField("Basal Metabolic Rate");

        // Define ComboBoxes for units
        ComboBox<String> sexComboBox = new ComboBox<>("Sex", "Male", "Female");
        ComboBox<String> weightUnitComboBox = new ComboBox<>("Weight Unit", "kg", "lb");
        ComboBox<String> heightUnitComboBox = new ComboBox<>("Height Unit", "cm", "m", "in", "ft");

        // Add tooltips
        ageField.setHelperText("Value must be between 18 and 65");
        heightField.setHelperText("Value must be between 163cm and 190cm for male patients and between 151cm and 175cm for female patients");
        bodyFatField.setHelperText("Value must be between 0.02% and 0.25% for male patients and 0.32% for female patients");
        heartRateField.setHelperText("Value must be between 50bpm and 110bpm");
        diastolicPressureField.setHelperText("Value must be between 60mmHg and 80mmHg");
        systolicPressureField.setHelperText("Value must be between 90mmHg and 120mmHg");
        respirationRateField.setHelperText("Value must be between 8bpm and 20bpm");

        // Add fields to form layout (in a similar manner to GridBagLayout in Swing)
        formLayout.add(nameField);
        formLayout.add(sexComboBox);
        formLayout.add(ageField, 1);
        formLayout.add(weightField);
        formLayout.add(weightUnitComboBox);
        formLayout.add(heightField);
        formLayout.add(heightUnitComboBox);
        formLayout.add(bodyFatField);
        formLayout.add(heartRateField);
        formLayout.add(diastolicPressureField);
        formLayout.add(systolicPressureField);
        formLayout.add(respirationRateField);
        formLayout.add(basalMetabolicRateField);

        // Create a scrollable panel for patient data
        Div scrollableDiv = new Div();
        scrollableDiv.setWidth("100%");  // Imposta la larghezza al 100%
        scrollableDiv.setHeight("50vh"); // Altezza calcolata per il contenuto, sottraendo il bordo
        scrollableDiv.getStyle().set("overflow-y", "auto");  // Scorrimento verticale
        scrollableDiv.add(formLayout);

        // Add the scrollable panel to the fixed size Div
        fixedSizeDiv.add(scrollableDiv);

        // Add the fixed size Div to the main layout
        add(fixedSizeDiv);
    }
}
