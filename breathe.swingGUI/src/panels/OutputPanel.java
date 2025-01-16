package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import outputItems.*;
import app.App;

public class OutputPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	/*
	 * PANEL to Display Output Items
	 */
    
    private Map<String, String> chartsMap;
    
	private Map<String, ItemDisplay> chartPanels;
    public JPanel chartsPanel = new JPanel();
    JScrollPane scrollChartPane;
    JPanel infoBoxPanel;
    
    public OutputPanel(App app) {
    	this.setBackground(Color.LIGHT_GRAY);
    	this.setPreferredSize(new Dimension(1300, 700));
    	
    	chartsMap = new HashMap<>();
        
    	//display ALL vitals
    	chartsMap.put("Total Lung Volume", "mL");
        chartsMap.put("ECG", "mV");
        chartsMap.put("CO2", "mmHg");
        chartsMap.put("Pleth", "mmHg");
        chartsMap.put("Heart Rate", "1/min");
        chartsMap.put("Respiratory Rate", "1/min");
        chartsMap.put("Airway Pressure", "mmHg");
        chartsMap.put("Oxygen Saturation", "%");
        
        chartPanels = new HashMap<>();
             
        chartsPanel.setLayout(new GridLayout(0, 2, 10, 10));
        chartsPanel.setBackground(Color.BLACK);

        infoBoxPanel = new JPanel();
        infoBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoBoxPanel.setBackground(Color.BLACK);
        
        //ADDING LINE CHARTS
        String[] chartOrder = {
        	    "Heart Rate",
        	    "Respiratory Rate",
        	    "Airway Pressure",
        	    "Oxygen Saturation",
        	    "Total Lung Volume",
        	    "CO2",
        	    "Pleth",
        	    "ECG"
        	};
        
        for (String chartName : chartOrder) {
            LineChart chart = new LineChart(chartName, chartsMap.get(chartName));
            chartPanels.put(chartName, chart);
            
            app.addOutputButton(chartName);
            
            chartsPanel.add(chartPanels.get(chartName));
        }
        
        //ADDING INFO BOXES
        String[] infoOrder = {
        	};
        
        for (String chartName : infoOrder) {
            InfoBox chart = new InfoBox(chartName, chartsMap.get(chartName));
            chart.setPreferredSize(new Dimension(150, 100));
            chartPanels.put(chartName, chart);
            
            app.addOutputButton(chartName);
            
            infoBoxPanel.add(chartPanels.get(chartName));
        }
        
        //SET UP MAIN PANEL
        scrollChartPane = new JScrollPane(chartsPanel);
        scrollChartPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChartPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollChartPane.setBorder(null);
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(scrollChartPane);
        this.setBackground(Color.BLACK);
    }

    
    //GRAPHIC UPDATE OF THE PANELS
    public void updateItemDisplay(List<JToggleButton> buttons) {
        chartsPanel.removeAll();
        infoBoxPanel.removeAll();

        for (int i = 0; i < buttons.size(); i++) {
            JToggleButton toggleButton = buttons.get(i);
            String chartName = toggleButton.getText();
            if (toggleButton.isSelected()) {
                if (chartPanels.get(chartName) instanceof LineChart) {
                    chartsPanel.add(chartPanels.get(chartName));
                } else if (chartPanels.get(chartName) instanceof InfoBox) {
                    infoBoxPanel.add(chartPanels.get(chartName));
                }
            }
        }

        chartsPanel.revalidate();
        infoBoxPanel.revalidate();

        chartsPanel.repaint();
        infoBoxPanel.repaint();
    }
    
    //ADDING VALUES depending on name
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
	        case "OxygenSaturation":
	        	mapChartName = "Oxygen Saturation";
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
    
    
    public void clearOutputDisplay() {
    	for (Map.Entry<String, ItemDisplay> chart : chartPanels.entrySet()) {
    	    chart.getValue().clear();
    	}
	}
}
