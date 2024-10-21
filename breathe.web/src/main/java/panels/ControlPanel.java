package panels;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div;

import java.io.File;

import files.DownloadLinksArea;
import files.UploadArea;
import app.App;

public class ControlPanel extends HorizontalLayout {
    private static final long serialVersionUID = 1L;

    private Button startButton, stopButton, exportButton, scenarioButton;
    private String uploadedFileName;
    
    private final ActionsPanel actionsPanel; 
    private final ScenarioPanel scenarioPanel;
    
    
    App app;

    public ControlPanel(App app) {
        this.app = app;      
        
        actionsPanel = new ActionsPanel(app, true); 
        scenarioPanel = new ScenarioPanel(app);
        
        startButton = new Button(VaadinIcon.PLAY.create(), e -> showStartOptions());
        startButton.setTooltipText("Start simulation");
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); 
        
        stopButton = new Button(VaadinIcon.STOP.create());
        stopButton.setTooltipText("Stop simulation");
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        stopButton.setEnabled(false);
        stopButton.addClickListener(e -> {
            app.stopSimulation();
            enableControlStartButton(true);
        });
        
        exportButton = new Button(LumoIcon.DOWNLOAD.create());
        exportButton.setTooltipText("Export simulation");
        exportButton.setEnabled(false);
        exportButton.addClickListener(e -> {
        	exportSimulation();
        });
        
        scenarioButton = new Button(VaadinIcon.CLIPBOARD_TEXT.create());
        scenarioButton.setTooltipText("Create Scenario");
        scenarioButton.addClickListener(e -> {
        	openScenarioDialog();
        });
        
        Button themeButton = new Button(VaadinIcon.MOON.create());
        themeButton.getStyle().set("margin-right", "0px");
        themeButton.addClickListener(e -> {
            if (UI.getCurrent().getElement().getThemeList().contains(Lumo.DARK)) {
                UI.getCurrent().getElement().getThemeList().remove(Lumo.DARK);
                UI.getCurrent().getElement().getThemeList().add(Lumo.LIGHT);
            } else {
                UI.getCurrent().getElement().getThemeList().remove(Lumo.LIGHT);
                UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);
            }
        });
        
        setWidth("100%");
        Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");

        add(startButton, stopButton, exportButton, scenarioButton, spacer, themeButton);
    }

    
    private void openScenarioDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("New Scenario");

        HorizontalLayout hl = new HorizontalLayout();
        hl.add(actionsPanel, scenarioPanel);
        dialog.add(hl);

        dialog.open();
    }

    private void showStartOptions() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select Start Option");
        
        Button startSimulationButton = new Button("Start Simulation", e -> {
            dialog.close();
            startingSimulation();
        });

        Button startFromFileButton = new Button("Start from File", e -> {
            dialog.close();
            startingFileSimulation(); 
        });

        Button startScenarioButton = new Button("Start Scenario", e -> {
            dialog.close();
            startingScenario();
        });

        startSimulationButton.setWidth("100%");
        startFromFileButton.setWidth("100%");
        startScenarioButton.setWidth("100%");
        
        VerticalLayout dialogLayout = new VerticalLayout(startSimulationButton, startFromFileButton, startScenarioButton);
        dialog.add(dialogLayout);

        dialog.open();
    }
    
    private void startingSimulation() {
    	app.clearOutputDisplay();
    	app.startSimulation();
    }

    private void startingFileSimulation() {
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select File Option");
        
        File uploadFolder = app.getFolder("states/uploaded");
        
        UploadArea upload = new UploadArea(uploadFolder);
        upload.getUploadField().addSucceededListener(e -> {
            upload.hideErrorField();
            uploadedFileName = e.getFileName();
        });
        
        VerticalLayout dialogLayout = new VerticalLayout(upload);
        
        Button startSimulationButton = new Button("Start Simulation", e -> {
            if (uploadedFileName != null) {
                String filePath = "../breathe.engine/states/uploaded/" + uploadedFileName;
                dialog.close();
                app.startFromFileSimulation(filePath);
            } else {
                Notification.show("Please upload a file before starting the simulation.",3000,Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_PRIMARY);;
            	String filePath = "../breathe.engine/states/exported/Standard.json";
                dialog.close();
                app.startFromFileSimulation(filePath);
            }
        });

        dialog.add(dialogLayout, startSimulationButton);
        dialog.open();
    }
    
    private void startingScenario() {
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select File Option");
        
        File uploadFolder = app.getFolder("scenario");
        
        UploadArea upload = new UploadArea(uploadFolder);
        upload.getUploadField().addSucceededListener(e -> {
            upload.hideErrorField();
            uploadedFileName = e.getFileName();
        });
        
        VerticalLayout dialogLayout = new VerticalLayout(upload);
        
        Button startScenarioButton = new Button("Start Scenario", e -> {
            if (uploadedFileName != null) {
                String filePath = "../breathe.engine/scenario/" + uploadedFileName;
                dialog.close();
                app.startScenario(filePath);
            } else {
                Notification.show("Please upload a file before starting the simulation.",3000,Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_PRIMARY);;
            }
        });

        dialog.add(dialogLayout, startScenarioButton);
        dialog.open();
    	
    }
    
    private void exportSimulation() {
    	 String defaultFileName = "../breathe.engine/states/exported/"+ app.getPatientName()+ ".json";
         app.exportSimulation(defaultFileName);
         
         Dialog dialog = new Dialog();
         
         File uploadFolder = app.getFolder("states/exported");
         DownloadLinksArea linksArea = new DownloadLinksArea(uploadFolder);
         VerticalLayout dialogLayout = new VerticalLayout(linksArea);

         Button closeButton = new Button("Close", e -> {
             dialog.close();
         });
       
         dialog.setHeaderTitle("Select File Option");
         dialog.add(dialogLayout, closeButton);
         
         dialog.open();
    }
    
    public void enableControlStartButton(boolean enable) {
        startButton.setEnabled(enable); 
        stopButton.setEnabled(!enable);
        exportButton.setEnabled(!enable);
    }
    
    public ScenarioPanel getScenarioPanel() {
    	return scenarioPanel;
    }
    
}
