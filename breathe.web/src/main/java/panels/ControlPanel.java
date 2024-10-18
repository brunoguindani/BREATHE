package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.io.File;

import files.DownloadLinksArea;
import files.UploadArea;
import app.App;

public class ControlPanel extends HorizontalLayout {
    private static final long serialVersionUID = 1L;

    private Button startButton, stopButton, exportButton;
    private String uploadedFileName;
    
    App app;

    public ControlPanel(App app) {
        this.app = app;
		this.setWidth("100%");
		this.setHeight("10%");        
        
        startButton = new Button("Start", e -> showStartOptions());
        startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY); 
        
        stopButton = new Button("Stop");
        stopButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        stopButton.setEnabled(false);
        stopButton.addClickListener(e -> {
            app.stopSimulation();
            enableControlStartButton(true);
        });
        
        exportButton = new Button("Export");
        exportButton.setEnabled(false);
        exportButton.addClickListener(e -> {
        	exportSimulation();
        });

        
        add(startButton, stopButton, exportButton);

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

        VerticalLayout dialogLayout = new VerticalLayout(startSimulationButton, startFromFileButton, startScenarioButton);
        dialog.add(dialogLayout);

        dialog.open();
    }
    
    private void startingSimulation() {
    	app.clearOutputDisplay();
    	app.startSimulation();
    }

    private void startingFileSimulation() {
        app.clearOutputDisplay();
        
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
                Notification.show("Please upload a file before starting the simulation.");
            	String filePath = "../breathe.engine/states/uploaded/Standard.json";
                dialog.close();
                app.startFromFileSimulation(filePath);
            }
        });

        dialog.add(dialogLayout, startSimulationButton);
        dialog.open();
    }
    
    private void startingScenario() {
        app.clearOutputDisplay();
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select File Option");
        
        File uploadFolder = app.getFolder("scenario");
        
        UploadArea upload = new UploadArea(uploadFolder);
        upload.getUploadField().addSucceededListener(e -> {
            upload.hideErrorField();
            uploadedFileName = e.getFileName();
        });
        
        VerticalLayout dialogLayout = new VerticalLayout(upload);
        
        Button startSimulationButton = new Button("Start Scenario", e -> {
            if (uploadedFileName != null) {
                String filePath = "../breathe.engine/scenario/" + uploadedFileName;
                dialog.close();
                app.startScenario(filePath);
            } else {
                Notification.show("Please upload a file before starting the simulation.");
            }
        });

        dialog.add(dialogLayout, startSimulationButton);
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
    
    
    //From Start to Stop
    public void enableControlStartButton(boolean enable) {
        startButton.setEnabled(enable); 
        stopButton.setEnabled(!enable);
        exportButton.setEnabled(!enable);
    }
    
}
