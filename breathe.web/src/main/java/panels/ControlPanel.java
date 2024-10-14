package panels;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.notification.Notification;

import app.App;

public class ControlPanel extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	
	
	App app;

    public ControlPanel(App app) {
    	this.app = app;
        // Crea pulsanti
        Button startButton = new Button("Start", e -> {
            Notification.show("Start button clicked");
            startingFileSimulation();
        });
        
        Button stopButton = new Button("Stop", e -> {
            Notification.show("Stop button clicked");
            app.stopSimulation();
        });
        
        Button exportButton = new Button("Export", e -> {
            Notification.show("Export button clicked");
        });

        // Aggiungi i pulsanti al layout orizzontale
        add(startButton, stopButton, exportButton);

        // Imposta uno stile semplice ai pulsanti
        startButton.getStyle().set("margin-right", "10px");
        stopButton.getStyle().set("margin-right", "10px");
    }
    
    private void startingFileSimulation() {
    	//app.startFromFileSimulation("C:\\Documenti\\UniBG\\Tesi\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");
    	//app.startFromFileSimulation("D:\\Unibg\\Tesi\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");
    	app.startFromFileSimulation("C:\\Users\\doubl\\Desktop\\Breathe\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");

    	/*
        Upload upload = new Upload();
        upload.setAcceptedFileTypes(".json"); // Imposta i tipi di file accettati, se necessario
        upload.setMaxFileSize(52428800);
        
        upload.addSucceededListener(event -> {
            String patientFilePath = event.getFileName();
            Notification.show("File selezionato: " + patientFilePath);
            app.startFromFileSimulation(patientFilePath);
        });

        add(upload); // Aggiungi il componente upload al layout*/
    }
}