package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import app.App;

public class ControlPanel extends HorizontalLayout {
    private static final long serialVersionUID = 1L;

    App app;

    public ControlPanel(App app) {
        this.app = app;
        // Crea pulsanti
        Button startButton = new Button("Start", e -> showStartOptions());
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


    private void showStartOptions() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Select Start Option");

        // Crea i pulsanti per le opzioni
        Button startSimulationButton = new Button("Start Simulation", e -> {
            Notification.show("Start Simulation selected");
            dialog.close();
            startingSimulation(); // Puoi cambiare il metodo a seconda della logica
        });

        Button startFromFileButton = new Button("Start from File", e -> {
            Notification.show("Start from File selected");
            dialog.close();
            startingFileSimulation(); 
        });

        Button startScenarioButton = new Button("Start Scenario", e -> {
            Notification.show("Start Scenario selected");
            dialog.close();
            startingScenario();
        });

        // Aggiungi i pulsanti al layout del dialogo
        VerticalLayout dialogLayout = new VerticalLayout(startSimulationButton, startFromFileButton, startScenarioButton);
        dialog.add(dialogLayout);

        // Mostra il dialogo
        dialog.open();
    }
    
    private void startingSimulation() {
    	app.startSimulation();
    }

    private void startingFileSimulation() {
        //app.startFromFileSimulation("C:\\Documenti\\UniBG\\Tesi\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");
    	app.startFromFileSimulation("D:\\Unibg\\Tesi\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");
    	//app.startFromFileSimulation("C:\\Users\\doubl\\Desktop\\Breathe\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");
    }
    
    private void startingScenario() {
    	
    }
}
