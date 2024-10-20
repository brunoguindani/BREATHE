package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;

import data.Action;
import data.Condition;
import data.Ventilator;
import data.Patient;
import interfaces.GuiCallback;
import panels.*;
import utils.Pair;

@PageTitle("Breathe")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0)
@Route("")
public class App extends Composite<VerticalLayout> implements GuiCallback {
	private static final long serialVersionUID = 1L;

    // Contenuti per il primo gruppo di tabs
    private final PatientConditionPanel patientConditionPanel = new PatientConditionPanel(this);  
    private final ActionsPanel actionsPanel = new ActionsPanel(this, false); 
    private final VentilatorsPanel ventilatorsPanel = new VentilatorsPanel(this);

    // Contenuti per il secondo gruppo di tabs
    private final OutputPanel outputPanel = new OutputPanel(this);
    private final LogPanel logPanel = new LogPanel(this);
    private final ControlPanel controlPanel = new ControlPanel(this);
    
    private SimulationWorker sim;
    private ProgressBar loadingIndicator;
    
    public App() {
        // Inizializzazione del JNI e del worker della simulazione
        Initializer.initilizeJNI();
        sim = new SimulationWorker(this);
        
        VerticalLayout mainLayout = getContent();
        mainLayout.setFlexGrow(1);
        
        HorizontalLayout topArea = new HorizontalLayout();
        topArea.add(controlPanel);
        topArea.getStyle().set("border-bottom", "1px solid #ccc");
        topArea.setWidth("95vw");
        topArea.setHeight("7vh");

        // Barra di caricamento
        loadingIndicator = new ProgressBar();
        loadingIndicator.setIndeterminate(true);
        loadingIndicator.setVisible(false);
        loadingIndicator.setWidth("100px");

        // Logo
        H1 logo = new H1("Breathe");
        logo.getStyle().set("font-size", "32px");
        logo.setWidth("auto");
        logo.setHeight("auto");

        // Contenitore per il logo e la barra di caricamento
        Div logoContainer = new Div(logo, loadingIndicator);
        logoContainer.getStyle().set("text-align", "right"); // Allinea a destra il contenuto
        logoContainer.getStyle().set("width", "100%"); // Occupa tutta la larghezza disponibile
        logoContainer.getStyle().set("margin", "0").set("padding", "0");
        loadingIndicator.getStyle().set("padding", "0");
        loadingIndicator.getStyle().set("margin-top", "0");
        loadingIndicator.getStyle().set("margin-left", "auto"); // Spinge la barra verso destra

        // Aggiungi il contenitore all'area superiore
        topArea.add(logoContainer);
        topArea.setAlignItems(Alignment.START); // Mantieni il logo in alto

        // Control Panel
        mainLayout.add(topArea);
        
        // Prima colonna
        VerticalLayout leftColumn = createColumn();
        //leftColumn.getStyle().set("border", "1px solid #ccc"); // Imposta il bordo
        leftColumn.getStyle().set("padding", "0px"); // Padding per aggiungere spazio interno (opzionale)
        leftColumn.setWidth("25vw");

        Tabs leftTabs = createLeftTabs();
        VerticalLayout leftContentLayout = new VerticalLayout();
        leftContentLayout.getStyle().set("margin", "0").set("padding", "0"); // Imposta margine e padding a zero
        leftTabs.addSelectedChangeListener(event -> updateContent(event.getSelectedTab(), leftContentLayout));

        // Aggiungi componenti alla colonna sinistra
        leftColumn.add(leftTabs);
        leftColumn.add(leftContentLayout);
        
        outputPanel.setWidth("75vw");

        // Layout principale con due colonne
        HorizontalLayout mainRow = new HorizontalLayout(leftColumn, outputPanel);
	    
	    mainRow.setWidthFull();
	
	    mainLayout.add(mainRow);

        updateContent(leftTabs.getSelectedTab(), leftContentLayout);        
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
        tabs.getStyle().set("margin", "0").set("padding", "0"); // Imposta margine e padding a zero
        tabs.add(new Tab("Patient"), new Tab("Actions"), new Tab("Ventilators"));
        return tabs;
    }

    // Metodo per aggiornare il contenuto del layout in base al tab selezionato
    private void updateContent(Tab selectedTab, VerticalLayout contentLayout) {
        contentLayout.removeAll();  // Rimuovi tutto il contenuto esistente
        String tabLabel = selectedTab.getLabel();
        switch (tabLabel) {
            case "Patient":
                contentLayout.add(patientConditionPanel); 
                break;
            case "Actions":
                contentLayout.add(actionsPanel);
                break;
            case "Ventilators":
                contentLayout.add(ventilatorsPanel);
                break;
        }
    }
    
    
    /*
     * GUI TO GUI
     */
    public File getFolder(String f) {
        File folder = new File("../breathe.engine/" + f);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
    
    public void startLoading() {
        loadingIndicator.setVisible(true); // Mostra l'indicatore di caricamento
    }

    public void stopLoading() {
        loadingIndicator.setVisible(false); // Nascondi l'indicatore di caricamento
    }
    
	public void applyCondition(Condition condition) {
		patientConditionPanel.getConditionsPanel().addCondition(condition);
		Notification.show("Added");
	}
	
	public void removeCondition(String title) {
		patientConditionPanel.getConditionsPanel().removeCondition(title);
		Notification.show("Removed");
	}
	
	public List<Condition> getActiveConditions() {
		return patientConditionPanel.getConditionsPanel().getActiveConditions();
	}
	
	public void addActiontoScenario(Action action, int totalSeconds) {
		controlPanel.getScenarioPanel().addAction(action, totalSeconds);	
	}
    
	public String getPatientName() {
		return patientConditionPanel.getPatientPanel().getPatientName();
	}
	
	public void clearOutputDisplay() {
		outputPanel.clearOutputDisplay();
	}
	
    /*
     * GUI TO SIMULATION WORKER
     */
	
	public void createScenario(String patientFile, String scenarioName, ArrayList<Pair<Action, Integer>> actions) {
		sim.createScenario(patientFile, scenarioName, actions);
	}
	
	public boolean startSimulation() {
		startLoading();
    	Patient new_patient = patientConditionPanel.getPatientPanel().generateInitialPatient(getActiveConditions());
    	if(new_patient != null) {
    		sim = new SimulationWorker(this);
    		sim.simulation(new_patient);	
        	patientConditionPanel.getConditionsPanel().enableButtons(false);
        	patientConditionPanel.getPatientPanel().enableComponents(false);
    		return true;
    	}
    	return false;
	}
	
	public boolean startScenario(String file) {
		startLoading();
    	if(file != null) {
    		sim = new SimulationWorker(this);
    		sim.simulationFromScenario(file);
    		return true;
    	}else 
    		return false;
	}
    
    public boolean startFromFileSimulation(String file) {
		startLoading();
    	if(file != null) {
    		sim = new SimulationWorker(this);
    		sim.simulationFromFile(file);
    		return true;
    	}else 
    		return false;
    }
    
    public void stopSimulation() {
    	sim.stopSimulation();	
		actionsPanel.enableButtons(false);
	  	patientConditionPanel.getConditionsPanel().enableButtons(true);
		patientConditionPanel.getPatientPanel().enableComponents(true);
		ventilatorsPanel.resetButton();
		stopLoading();
	}
    
    public void exportSimulation(String exportFilePath) {
		sim.exportSimulation(exportFilePath);
	}
    
	public void connectVentilator() {
		Ventilator v = ventilatorsPanel.getCurrentVentilator();
    	if(v != null) {
    		sim.connectVentilator(v);	
    	}
	}
	
	public void disconnectVentilator() {
    	Ventilator v = ventilatorsPanel.getCurrentVentilator();
    	if(v != null)
    		sim.disconnectVentilator(v);
    }
		
    /*
     * SIMULATION WORKER TO GUI
     */
    
	@Override
	public void stabilizationComplete(boolean enable) {
		 getUI().ifPresent(ui -> ui.access(() -> {
			controlPanel.enableControlStartButton(!enable);
			actionsPanel.enableButtons(enable);
			ventilatorsPanel.setEnableConnectButton(true);
			stopLoading();
         }));
	}

	@Override
	public void logStringData(String data) {
		getUI().ifPresent(ui -> ui.access(() -> {
			logPanel.append(data);
         }));
		
	}

	@Override
	public void minilogStringData(String data) {
		getUI().ifPresent(ui -> ui.access(() -> {
			Notification.show(data);
		}));
	}

	@Override
	public void logItemDisplayData(String data, double x, double y) {
		getUI().ifPresent(ui -> ui.access(() -> {
			 outputPanel.addValueToItemDisplay(data, x, y);
         }));
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

	public void applyAction(Action action) {
		sim.applyAction(action);
	}

	

}
