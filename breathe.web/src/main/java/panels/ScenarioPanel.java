package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import app.App;
import data.Action;
import files.DownloadLinksArea;
import utils.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScenarioPanel extends VerticalLayout {

    private App app;

    private ComboBox<String> patientFileComboBox;
    private TextField scenarioNameField;

    private Grid<Pair<Action, Integer>> actionsGrid;
    private ListDataProvider<Pair<Action, Integer>> dataProvider;

    private ArrayList<Pair<Action, Integer>> actions = new ArrayList<>();

    public ScenarioPanel(App app) {
        this.app = app;
		getStyle().set("border", "1px solid #ccc"); // Imposta il bordo
        
        // ComboBox for patient files
        patientFileComboBox = new ComboBox<>("Patient");
        String[] directories = {"../breathe.engine/states/exported/", "../breathe.engine/states/"};
        updatePatientFiles(directories);

        // TextField for scenario name
        scenarioNameField = new TextField("Scenario Name");

        // Grid for actions
        actionsGrid = new Grid<>();

     // Colonna per il nome dell'azione
	     actionsGrid.addColumn(pair -> pair.getKey().toString().split("\n")[0])
	         .setHeader("Action")
	         .setTooltipGenerator(pair -> pair.getKey().toString()); 
	
	     // Colonna per il tempo
	     actionsGrid.addColumn(pair -> formatTime(pair.getValue())).setHeader("Time");
	
	     // Imposta la modalità di selezione
	     actionsGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        dataProvider = new ListDataProvider<>(actions);
        actionsGrid.setDataProvider(dataProvider);

        // Buttons
        Button createScenarioButton = new Button("Create Scenario", e -> createScenario());

        Button removeActionButton = new Button("Remove Selected Actions", e -> removeSelectedActions());
        removeActionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        // Add components to layout
        add(patientFileComboBox, scenarioNameField, actionsGrid, removeActionButton, createScenarioButton);
        setPadding(true);
        setSpacing(true);
    }

    private void updatePatientFiles(String[] directories) {
        List<String> patientFiles = new ArrayList<>(); // Crea una lista per i file paziente

        for (String dirPath : directories) {
            File dir = new File(dirPath);
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            patientFiles.add(file.getName()); // Aggiungi il nome del file alla lista
                        }
                    }
                }
            }
        }

        patientFileComboBox.setItems(patientFiles); // Imposta gli elementi del ComboBox
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public void addAction(Action action, int seconds) {
        Pair<Action, Integer> newAction = new Pair<>(action, seconds);
        actions.add(newAction);
        actions.sort((pair1, pair2) -> pair1.getValue().compareTo(pair2.getValue()));
        dataProvider.refreshAll();
    }

    private void createScenario() {
        String scenarioName = scenarioNameField.getValue();

        if (scenarioName.isEmpty()) {
            Notification.show("Please enter a name for the scenario.", 3000, Notification.Position.MIDDLE);
            return;
        }

        String patientFile = patientFileComboBox.getValue();
        if (patientFile == null || patientFile.isEmpty()) {
            Notification.show("Please select a patient file.", 3000, Notification.Position.MIDDLE);
            return;
        }

        File patientTempFile = new File("../breathe.engine/states/" + patientFile);
        if (patientTempFile.exists()) {
            patientFile = "../breathe.engine/states/" + patientFile;
        } else {
            patientFile = "../breathe.engine/states/exported/" + patientFile;
        }

        app.createScenario(patientFile, scenarioName, actions);
        Notification.show("Scenario \"" + scenarioName + "\" created successfully.", 3000, Notification.Position.MIDDLE);
        Dialog dialog = new Dialog();
        
        File uploadFolder = app.getFolder("scenario");
        DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder);
        VerticalLayout dialogLayout = new VerticalLayout(linksArea);

        Button closeButton = new Button("Close", e -> {
            dialog.close();
        });
      
        dialog.setHeaderTitle("Select File Option");
        dialog.add(dialogLayout, closeButton);
        
        dialog.open();
    }

    private void removeSelectedActions() {
        List<Pair<Action, Integer>> selectedActions = new ArrayList<>(actionsGrid.getSelectedItems());

        if (selectedActions.isEmpty()) {
            Notification.show("Please select actions to remove.", 3000, Notification.Position.MIDDLE);
            return;
        }

        actions.removeAll(selectedActions);
        dataProvider.refreshAll();
        Notification.show("Selected actions removed.", 3000, Notification.Position.MIDDLE);
    }
}
