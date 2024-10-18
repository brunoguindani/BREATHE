package panels;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.KeyDownEvent;
import outputItems.InfoBox;
import outputItems.LineChart;
import outputItems.ItemDisplay;
import app.App;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OutputPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private VerticalLayout mainPanel;
    private Map<String, String> chartsMap;
    private Map<String, ItemDisplay> chartPanels;
    private VerticalLayout chartsPanel;
    private FlexLayout infoBoxPanel;
    private Div scrollChartPane;

    public OutputPanel(App app) {
		this.setWidthFull();
		getStyle().set("border", "1px solid #ccc"); // Imposta il bordo
        mainPanel = new VerticalLayout();
        mainPanel.getStyle().set("background-color", "black");
        mainPanel.getStyle().set("border", "2px solid #fff");  // Bordo bianco
        mainPanel.getStyle().set("border-radius", "10px");
        mainPanel.getStyle().set("box-shadow", "inset 0 0 10px rgba(255, 255, 255, 0.5)");  // Ombra interna

        chartsMap = new HashMap<>();
        
        // Mappatura delle unità di misura
        chartsMap.put("Total Lung Volume", "mL");
        chartsMap.put("ECG", "mV");
        chartsMap.put("CO2", "mmHg");
        chartsMap.put("Pleth", "mmHg");
        chartsMap.put("Heart Rate", "1/min");
        chartsMap.put("Respiratory Rate", "1/min");
        chartsMap.put("Airway Pressure", "mmHg");
        
        MultiSelectComboBox<String> comboBox = new MultiSelectComboBox<>();
        comboBox.setLabel("Seleziona le opzioni");
        comboBox.setItems(chartsMap.keySet());
        comboBox.setItemLabelGenerator(item -> item); 
        comboBox.addValueChangeListener(event -> updateItemDisplay(comboBox.getValue()));
        
        add(comboBox);
        
        chartPanels = new HashMap<>();
             
        chartsPanel = new VerticalLayout();
        chartsPanel.getStyle().set("padding", "10px");  // Padding per distanziare gli oggetti

        infoBoxPanel = new FlexLayout();
        
        // Aggiunta dei grafici a linee
        String[] chartOrder = {
            "Total Lung Volume",
            "CO2",
            "Pleth",
            "ECG"
        };
        
        for (String chartName : chartOrder) {
            LineChart chart = new LineChart(chartName, chartsMap.get(chartName));
            chartPanels.put(chartName, chart);
            chartsPanel.add(chart);
        }
        
        // Aggiunta delle info box
        String[] infoOrder = {
            "Heart Rate",
            "Respiratory Rate",
            "Airway Pressure"
        };
        
        for (String chartName : infoOrder) {
            InfoBox infoBox = new InfoBox(chartName, chartsMap.get(chartName));
            infoBox.getStyle().set("margin", "5px");  // Margine per distanziare le info box
            chartPanels.put(chartName, infoBox);
            infoBoxPanel.add(infoBox);
        }
        
        // Imposta il pannello principale
        scrollChartPane = new Div(chartsPanel);
        scrollChartPane.getStyle().set("overflow-y", "auto").set("overflow-x", "hidden");
        scrollChartPane.setWidthFull();
        
        mainPanel.add(infoBoxPanel, scrollChartPane);
        add(mainPanel);
    }

    // Metodo per aggiornare i display degli oggetti
    public void updateItemDisplay(Set<String> selectedCharts) { // Modifica il tipo di parametro
        chartsPanel.removeAll();
        infoBoxPanel.removeAll();

        for (String chartName : selectedCharts) {
            if (chartPanels.containsKey(chartName)) {
                if (chartPanels.get(chartName) instanceof LineChart) {
                    chartsPanel.add(chartPanels.get(chartName));
                } else if (chartPanels.get(chartName) instanceof InfoBox) {
                    infoBoxPanel.add(chartPanels.get(chartName));
                }
            }
        }

        if (infoBoxPanel.getComponentCount() == 0) {
            infoBoxPanel.setWidth("0px");
        } else {
            infoBoxPanel.setWidth("160px");
        }

        scrollChartPane.getElement().executeJs("this.scrollTop = 0");
    }

    // Aggiunta dei valori ai display
    public void addValueToItemDisplay(String chartName, double x, double y) {
        String mapChartName;

        switch (chartName) {
            case "HeartRate":
                mapChartName = "Heart Rate";
                break;
            case "AirwayPressure":
                mapChartName = "Airway Pressure";
                break;
            case "RespirationRate":
                mapChartName = "Respiratory Rate";
                break;
            case "TotalLungVolume":
                mapChartName = "Total Lung Volume";
                break;
            case "Lead3ElectricPotential":
                mapChartName = "ECG";
                break;
            case "CarbonDioxide":
                mapChartName = "CO2";
                break;
            case "ArterialPressure":
                mapChartName = "Pleth";
                break;
            default:
                mapChartName = null; 
                break;
        }
        
        if (mapChartName != null) {
            chartPanels.get(mapChartName).addPoint(x, y);
        }
    }
    
    // Pulisci tutti i display
    public void clearOutputDisplay() {
        for (Map.Entry<String, ItemDisplay> entry : chartPanels.entrySet()) {
            entry.getValue().clear();
        }
    }
}
