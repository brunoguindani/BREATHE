package app;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.progressbar.ProgressBar;


@PageTitle("Breathe")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0)
@Route("")
public class App extends Composite<VerticalLayout>{
	private static final long serialVersionUID = 1L;

    // Contenuti per il primo gruppo di tabs
    private final VerticalLayout patientConditionPanel = new VerticalLayout();  
    private final VerticalLayout actionsPanel = new VerticalLayout(); 
    private final VerticalLayout ventilatorsPanel = new VerticalLayout();

    // Contenuti per il secondo gruppo di tabs
    private final VerticalLayout outputPanel = new VerticalLayout();
    private final VerticalLayout scenarioPanel = new VerticalLayout();
    private final VerticalLayout logPanel = new VerticalLayout();
    private final VerticalLayout controlPanel = new VerticalLayout();
    
    private SimulationWorker sim;
    private ProgressBar loadingIndicator;
    
    public App() {
        // Inizializzazione del JNI e del worker della simulazione
        Initializer.initilizeJNI();
        //sim = new SimulationWorker(this);
        
        VerticalLayout mainLayout = getContent();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.setFlexGrow(1);
        
        HorizontalLayout topArea = new HorizontalLayout();
        topArea.add(controlPanel);
        loadingIndicator = new ProgressBar();
        loadingIndicator.setIndeterminate(true); // Imposta come indeterminato per indicare il caricamento
        loadingIndicator.setVisible(false); // Inizialmente nascosto
        loadingIndicator.setWidth("100px");

        mainLayout.add(loadingIndicator); 
        // Aggiungere controlPanel e loadingIndicator al layout
        topArea.add(loadingIndicator);
        
        // Control Panel
        mainLayout.add(topArea);
        
        // Prima colonna
        VerticalLayout leftColumn = createColumn();
        leftColumn.getStyle().set("border", "1px solid #ccc"); // Imposta il bordo
        leftColumn.getStyle().set("border-radius", "8px"); // Angoli arrotondati (opzionale)
        leftColumn.getStyle().set("padding", "10px"); // Padding per aggiungere spazio interno (opzionale)

        Tabs leftTabs = createLeftTabs();
        VerticalLayout leftContentLayout = new VerticalLayout();
        leftTabs.addSelectedChangeListener(event -> updateContent(event.getSelectedTab(), leftContentLayout));

        // Aggiungi componenti alla colonna sinistra
        leftColumn.add(leftTabs);
        leftColumn.add(leftContentLayout);

        // Seconda colonna
        VerticalLayout rightColumn = createColumn();
        rightColumn.getStyle().set("border", "1px solid #ccc"); // Imposta il bordo
        rightColumn.getStyle().set("border-radius", "8px"); // Angoli arrotondati (opzionale)
        rightColumn.getStyle().set("padding", "10px"); // Padding per aggiungere spazio interno (opzionale)
        
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
        
        // Assicurati che entrambe le colonne abbiano la stessa dimensione
        mainRow.setFlexGrow(1, leftColumn);
        mainRow.setFlexGrow(1, rightColumn);

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
                contentLayout.add(patientConditionPanel);  // Aggiungi il pannello del paziente
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

}
