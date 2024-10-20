package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import app.App;
import data.Action;
import files.DownloadLinksArea;
import utils.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScenarioPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private App app;

	private ComboBox<String> patientFileComboBox;
	private TextField scenarioNameField;

	private Grid<Pair<Action, Integer>> actionsGrid;
	private ListDataProvider<Pair<Action, Integer>> dataProvider;

	private ArrayList<Pair<Action, Integer>> actions = new ArrayList<>();

	public ScenarioPanel(App app) {
		this.app = app;
		setSpacing(false);
		setWidth("33vw");
		getStyle().set("margin", "0px");
		getStyle().set("padding", "0px");

		scenarioNameField = new TextField("Scenario Name");
		scenarioNameField.setWidth("70%");
		
		patientFileComboBox = new ComboBox<>("Patient");
		patientFileComboBox.setWidth("70%");
		String[] directories = { "../breathe.engine/states/exported/", "../breathe.engine/states/" };
		updatePatientFiles(directories);


	    Div fixedSizeDiv = new Div();
        fixedSizeDiv.getStyle().set("box-sizing", "border-box"); // Include padding e bordo nelle dimensioni

        // Scrollable container for all conditions
        VerticalLayout tableLayout = new VerticalLayout();
        tableLayout.setPadding(false);
        tableLayout.setSpacing(false);

		actionsGrid = new Grid<>();
		actionsGrid.addColumn(pair -> pair.getKey().toString().split("\n")[0]).setHeader("Action")
				.setTooltipGenerator(pair -> pair.getKey().toString());

		actionsGrid.addColumn(pair -> formatTime(pair.getValue())).setHeader("Time");

		actionsGrid.setSelectionMode(Grid.SelectionMode.MULTI);

		dataProvider = new ListDataProvider<>(actions);
		actionsGrid.setDataProvider(dataProvider);
        actionsGrid.setWidth("30vw");
        actionsGrid.setHeight("50vh");
		
		Div scrollableDiv = new Div();
        scrollableDiv.getStyle().set("overflow-y", "auto");  // Scorrimento verticale
        scrollableDiv.getStyle().set("scrollbar-width", "none");
        
        scrollableDiv.setHeight("50vh");  // Altezza fissa per il pannello scorrevole
        scrollableDiv.add(actionsGrid); 
        fixedSizeDiv.add(scrollableDiv);
        fixedSizeDiv.setHeight("50vh");

		Button createScenarioButton = new Button("Create Scenario", e -> createScenario());

		Button removeActionButton = new Button("Remove Selected Actions", e -> removeSelectedActions());
		removeActionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.add(removeActionButton, createScenarioButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonsLayout.setWidth("100%");

        VerticalLayout fieldLayout = new VerticalLayout();
        fieldLayout.setAlignItems(Alignment.CENTER);
        fieldLayout.setPadding(false);
        fieldLayout.setSpacing(false);
        fieldLayout.add(scenarioNameField, patientFileComboBox);
        add(fieldLayout);
        add(fixedSizeDiv);
		add(buttonsLayout);
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
		Notification.show("Scenario \"" + scenarioName + "\" created successfully.",3000,Position.BOTTOM_END);
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
