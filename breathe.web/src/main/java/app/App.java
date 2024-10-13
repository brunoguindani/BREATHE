package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import data.Condition;
import interfaces.GuiCallback;
import panels.ControlPanel;
import panels.LogPanel;
import panels.PatientPanel;
import panels.OutputPanel;

@PageTitle("Breathe")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0)
@Route("")
public class App extends Composite<VerticalLayout> implements GuiCallback {

    // Contenuti per il primo gruppo di tabs
    private final PatientPanel patientPanel = new PatientPanel();  // Usa la classe PatientPanel
    private final VerticalLayout actionsPanel = new VerticalLayout(); 
    private final VerticalLayout ventilatorsPanel = new VerticalLayout();

    // Contenuti per il secondo gruppo di tabs
    private final OutputPanel outputPanel = new OutputPanel(this);
    private final VerticalLayout scenarioPanel = new VerticalLayout();
    private final LogPanel logPanel = new LogPanel(this);
    
    // Altre tabs
    private final HorizontalLayout controlPanel = new ControlPanel(this);
    
    private SimulationWorker s;
    
    private boolean stopRequest;
    private boolean takeData;
    

    public App() {
    	
    	Initializer.initilizeJNI();
		s = new SimulationWorker(this);
		
        VerticalLayout mainLayout = getContent();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.setFlexGrow(1);
        
        // Control Panel
        mainLayout.add(controlPanel);

        // Prima colonna
        VerticalLayout leftColumn = createColumn();
        Tabs leftTabs = createLeftTabs();
        VerticalLayout leftContentLayout = new VerticalLayout();
        leftTabs.addSelectedChangeListener(event -> updateContent(event.getSelectedTab(), leftContentLayout));

        // Aggiungi componenti alla colonna sinistra
        leftColumn.add(leftTabs);
        leftColumn.add(leftContentLayout);

        // Seconda colonna
        VerticalLayout rightColumn = createColumn();
        Tabs rightTabs = createRightTabs();
        VerticalLayout rightContentLayout = new VerticalLayout();
        rightTabs.addSelectedChangeListener(event -> updateContent(event.getSelectedTab(), rightContentLayout));

        // Aggiungi componenti alla colonna destra
        rightColumn.add(rightTabs);
        rightColumn.add(rightContentLayout);

        // Layout principale con due colonne
        HorizontalLayout mainRow = new HorizontalLayout(leftColumn, rightColumn);
        mainRow.setWidthFull();
        mainRow.setHeightFull();
        mainRow.setFlexGrow(1, leftColumn, rightColumn);

        mainLayout.add(mainRow);

        updateContent(leftTabs.getSelectedTab(), leftContentLayout);
        updateContent(rightTabs.getSelectedTab(), rightContentLayout);
    }

    // Metodo per creare un layout colonna
    private VerticalLayout createColumn() {
        VerticalLayout column = new VerticalLayout();
        column.setWidthFull();
        column.setHeightFull();
        column.setFlexGrow(1);
        return column;
    }

    // Metodo per creare un insieme di tabs con dati di esempio
    private Tabs createLeftTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.add(new Tab("Patient"), new Tab("Actions"), new Tab("Ventilators"));
        return tabs;
    }

    // Metodo per creare un insieme di tabs con dati di esempio
    private Tabs createRightTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.add(new Tab("Output"), new Tab("Scenario"), new Tab("Log"));
        return tabs;
    }

    // Metodo per aggiornare il contenuto del layout in base al tab selezionato
    private void updateContent(Tab selectedTab, VerticalLayout contentLayout) {
        contentLayout.removeAll();  // Rimuovi tutto il contenuto esistente
        String tabLabel = selectedTab.getLabel();
        switch (tabLabel) {
            case "Patient":
                contentLayout.add(patientPanel);  // Aggiungi il pannello del paziente
                break;
            case "Actions":
                //actionsPanel.setText("This is Actions content");
                contentLayout.add(actionsPanel);
                break;
            case "Ventilators":
                //ventilatorsPanel.setText("This is Ventilators content");
                contentLayout.add(ventilatorsPanel);
                break;
            case "Output":
            	//outputPanel.setText("This is Output content");
                contentLayout.add(outputPanel);
                break;
            case "Scenario":
            	//scenarioPanel.setText("This is Scenario content");
                contentLayout.add(scenarioPanel);
                break;
            case "Log":
            	//logPanel.setText("This is Log content");
                contentLayout.add(logPanel);
                break;
        }
    }
    
    private void startDataUpdateThread() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                while (!stopRequest) {
                	
                	if(takeData) {
	                    ArrayList<String> data = s.getData();  
	                    
	                    if (data != null) {
	                        getUI().ifPresent(ui -> ui.access(() -> {
	                        	 for (String item : data) {
	                                 logPanel.append(item);
	                             }
	                        }));
	                    }
	                    takeData=false;
                	}
                    Thread.sleep(10);  
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                e.printStackTrace();  
            } catch (Exception e) {
                e.printStackTrace();  
            }
        });
    }

    
    /*
     * GUI TO GUI
     */
    
    /*
     * GUI TO SIMULATION WORKER
     */
    
    public boolean startFromFileSimulation(String file) {
    	if(file != null) {
    		s = new SimulationWorker(this);
            Notification.show(file);
    		s.simulationFromFile(file);	
    		stopRequest = false;
    		startDataUpdateThread();
    		return true;
    	}else {
    		return false;
    	}
    }
    
    public void stopSimulation() {
    	s.stopSimulation();	
    	stopRequest = true;
    }
    
    /*
     * SIMULATION WORKER TO GUI
     */
    
	@Override
	public void stabilizationComplete(boolean enable) {

	}

	@Override
	public void logStringData(String data) {
		takeData = true;
	}

	@Override
	public void minilogStringData(String data) {

	}

	@Override
	public void logItemDisplayData(String data, double x, double y) {
		//outputPanel.addValueToItemDisplay(data, x, y);
	}

	@Override
	public void logPressureExternalVentilatorData(double pressure) {

	}

	@Override
	public void logVolumeExternalVentilatorData(double volume) {

	}

	@Override
	public void setInitialCondition(List<Condition> list) {

	}
	
}
