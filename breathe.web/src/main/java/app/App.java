package app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

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

    private final PatientConditionPanel patientConditionPanel = new PatientConditionPanel(this);  
    private final ActionsPanel actionsPanel = new ActionsPanel(this, false); 
    private final VentilatorsPanel ventilatorsPanel = new VentilatorsPanel(this);
    private final OutputPanel outputPanel = new OutputPanel(this);
    private final ControlPanel controlPanel = new ControlPanel(this);
    
    private SimulationWorker sim;
    private ProgressBar loadingIndicator;
    
    public App() {
        Initializer.initilizeJNI();
        sim = new SimulationWorker(this);
        
        VerticalLayout mainLayout = getContent();
        mainLayout.setFlexGrow(1);
        
        HorizontalLayout topArea = new HorizontalLayout();
        topArea.getStyle().set("border-bottom", "1px solid #ccc");
        topArea.setWidth("98vw");
        topArea.setHeight("9vh");
        topArea.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        topArea.setAlignItems(FlexComponent.Alignment.CENTER);

        loadingIndicator = new ProgressBar();
        loadingIndicator.setIndeterminate(false);
        loadingIndicator.setVisible(true);
        loadingIndicator.setWidth("145px");

        H1 logo = new H1("Breathe");
        logo.getStyle().set("font-size", "40px");
        logo.getStyle().set("font-style", "italic");

        Div logoContainer = new Div(logo, loadingIndicator);
        logoContainer.getStyle().set("margin", "0").set("padding", "0");
        loadingIndicator.getStyle().set("padding", "0");
        loadingIndicator.getStyle().set("margin-top", "0");

        HorizontalLayout buttonContainer = new HorizontalLayout(controlPanel);
        buttonContainer.setWidth("100vw");
        buttonContainer.setAlignItems(FlexComponent.Alignment.CENTER);

        topArea.add(logoContainer, buttonContainer);

        mainLayout.add(topArea);
        
        VerticalLayout leftColumn = createColumn();
        leftColumn.getStyle().set("padding", "0px"); 
        //leftColumn.setWidth("25vw");

        Tabs leftTabs = createLeftTabs();
        VerticalLayout leftContentLayout = new VerticalLayout();
        leftContentLayout.getStyle().set("margin", "0").set("padding", "0");
        leftTabs.addSelectedChangeListener(event -> updateContent(event.getSelectedTab(), leftContentLayout));

        leftColumn.add(leftTabs);
        leftColumn.add(leftContentLayout);
        
        outputPanel.setWidth("75vw");

        HorizontalLayout mainRow = new HorizontalLayout(leftColumn, outputPanel);
	    mainRow.setWidthFull();
	    mainLayout.add(mainRow);

        updateContent(leftTabs.getSelectedTab(), leftContentLayout);        
    }

    private VerticalLayout createColumn() {
        VerticalLayout column = new VerticalLayout();
        column.setWidthFull();
        column.setHeightFull();
        column.setFlexGrow(1);
        return column;
    }

    private Tabs createLeftTabs() {
        Tabs tabs = new Tabs();
        tabs.setWidthFull();
        tabs.getStyle().set("margin", "0").set("padding", "0"); 
        tabs.add(new Tab("Patient"), new Tab("Actions"), new Tab("Ventilators"));
        tabs.addThemeVariants(TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        return tabs;
    }

    private void updateContent(Tab selectedTab, VerticalLayout contentLayout) {
        contentLayout.removeAll(); 
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
        loadingIndicator.setIndeterminate(true);
    }

    public void stopLoading() {
    	loadingIndicator.setIndeterminate(false); 
    }
    
	public void applyCondition(Condition condition) {
		patientConditionPanel.getConditionsPanel().addCondition(condition);
		Notification.show(condition.getTitle() + " added",3000,Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_PRIMARY);
	}
	
	public void removeCondition(String title) {
		patientConditionPanel.getConditionsPanel().removeCondition(title);
		Notification.show(title + " removed",3000,Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_PRIMARY);;
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
    		clearOutputDisplay();
    		sim = new SimulationWorker(this);
    		sim.simulationFromScenario(file);
    		return true;
    	}else 
    		return false;
	}
    
    public boolean startFromFileSimulation(String file) {
		startLoading();
    	if(file != null) {
    		clearOutputDisplay();
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
	
	public void applyAction(Action action) {
		sim.applyAction(action);
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
	public void minilogStringData(String data) {
		getUI().ifPresent(ui -> ui.access(() -> {
			Notification.show(data,3000,Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_PRIMARY);
		}));
	}

	@Override
	public void logItemDisplayData(String data, double x, double y) {
		getUI().ifPresent(ui -> ui.access(() -> {
			 outputPanel.addValueToItemDisplay(data, x, y);
         }));
	}
	
	@Override
	public void logStringData(String data) {
		//DATA FOR LOGPANEL
	}

	@Override
	public void setInitialCondition(List<Condition> list) {
		patientConditionPanel.getConditionsPanel().setInitialConditions(list);
	}	

	@Override
	public void logPressureExternalVentilatorData(double pressure) {

	}

	@Override
	public void logVolumeExternalVentilatorData(double volume) {

	}


}
