package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import app.App;

public class ControlPanel extends HorizontalLayout {
    private static final long serialVersionUID = 1L;

    private Button startButton, stopButton, exportButton;
    
    
    App app;

    public ControlPanel(App app) {
        this.app = app;
        
        
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
        app.startFromFileSimulation("C:\\Documenti\\UniBG\\Tesi\\BREATHE\\breathe.engine\\states\\StandardMale@0s.json");
    	//app.startFromFileSimulation("D:\\Unibg\\Tesi\\BREATHE\\breathe.gui\\states\\StandardMale@0s.json");
    	//app.startFromFileSimulation("C:\\Users\\doubl\\Desktop\\Breathe\\BREATHE\\breathe.engine\\states\\StandardMale@0s.json");
    }
    
    private void startingScenario() {
    	app.clearOutputDisplay();
    	
    }
    
    //TODO -> AGGIUNGERE CONTROLLO ESISTE GIA IL NOME DEL FILE IN MODO DA NON SOVRASCRIVERLO
    private void exportSimulation() {
    	 String defaultFileName = "../breathe.engine/states/exported/Test.json";
         app.exportSimulation(defaultFileName);
    }
    
    
    //From Start to Stop
    public void enableControlStartButton(boolean enable) {
        startButton.setEnabled(enable); 
        stopButton.setEnabled(!enable);
        exportButton.setEnabled(!enable);
    }
    
}
