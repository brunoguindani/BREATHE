package toRemove;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.kitware.pulse.cdm.properties.CommonUnits.ElectricPotentialUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.Unit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;

import outputItems.InfoBox;
import outputItems.ItemDisplay;
import outputItems.LineChart;

import javax.swing.JToggleButton;

public class ChartsPanel {
	
	private HashMap<String, Unit> chartsMap;
	private HashMap<String, ItemDisplay> chartPanels;
    public JPanel chartsPanel = new JPanel();
    JScrollPane scrollChartPane;
    JPanel selectionPanel;
    JPanel infoBoxPanel;
    JScrollPane scrollInfoBoxPane;
    private JToggleButton[] chartToggleButtons;
    JPanel mainPanel = new JPanel();
    
    private boolean mainChange = false;
    
    public ChartsPanel() {
    	
    	chartsMap = new HashMap<>();
       
    	chartsMap.put("Total Lung Volume", VolumeUnit.mL);
        chartsMap.put("ECG", ElectricPotentialUnit.mV);
        chartsMap.put("CO2", PressureUnit.mmHg);
        chartsMap.put("Pleth", PressureUnit.mmHg);
        chartsMap.put("Heart Rate", FrequencyUnit.Per_min);
        chartsMap.put("Respiratory Rate", FrequencyUnit.Per_min);
        
        chartPanels = new HashMap<>();
        chartToggleButtons = new JToggleButton[chartsMap.size()];
        
        selectionPanel = new JPanel();
        selectionPanel.setBackground(Color.BLACK);
        
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.setBackground(Color.BLACK);

        infoBoxPanel = new JPanel();
        infoBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoBoxPanel.setBackground(Color.BLACK);

        scrollInfoBoxPane = new JScrollPane(infoBoxPanel);
        scrollInfoBoxPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollInfoBoxPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollInfoBoxPane.setBorder(null);
        scrollInfoBoxPane.setPreferredSize(new Dimension(150, 300));
        
        //ADDING LINE CHARTS
        String[] chartOrder = {
        	    "Total Lung Volume",
        	    "CO2",
        	    "Pleth",
        	    "ECG"
        	};
        
        int i = 0;
        for (String chartName : chartOrder) {
            LineChart chart = new LineChart(chartName, chartsMap.get(chartName));
            chartPanels.put(chartName, chart);
            
            chartToggleButtons[i] = new JToggleButton(chartName);
            
            if (i < 4) {
                chartToggleButtons[i].setSelected(true);
            } else {
                chartToggleButtons[i].setSelected(false);
            }
            
            chartToggleButtons[i].setBackground(Color.BLACK);
            chartToggleButtons[i].setForeground(Color.WHITE);
            chartToggleButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateItemDisplay();
                }
            });
            selectionPanel.add(chartToggleButtons[i]);
            
            if (chartToggleButtons[i].isSelected()) {
                chartsPanel.add(chartPanels.get(chartName));
            }
            i++;
        }
        
        //ADDING INFO BOXES
        String[] infoOrder = {
        	    "Heart Rate",
        	    "Respiratory Rate"
        	};
        
        for (String chartName : infoOrder) {
            InfoBox chart = new InfoBox(chartName, chartsMap.get(chartName));
            chart.setPreferredSize(new Dimension(150, 100));
            chartPanels.put(chartName, chart);
            
            chartToggleButtons[i] = new JToggleButton(chartName);
            chartToggleButtons[i].setSelected(true);
            
            chartToggleButtons[i].setBackground(Color.BLACK);
            chartToggleButtons[i].setForeground(Color.WHITE);
            chartToggleButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateItemDisplay();
                }
            });
            selectionPanel.add(chartToggleButtons[i]);
            
            infoBoxPanel.add(chartPanels.get(chartName));
            i++;
        }
        
        //SET UP MAIN PANEL
        scrollChartPane = new JScrollPane(chartsPanel);
        scrollChartPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChartPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollChartPane.setBorder(null);
        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(selectionPanel);
        mainPanel.add(scrollInfoBoxPane);
        mainPanel.add(scrollChartPane);
        mainPanel.setBackground(Color.BLACK);
    }

    public HashMap<String, ItemDisplay> getChartsPanel() {
        return chartPanels;
    }
    
    public JPanel getChartPanel() {
        return mainPanel;
    }
    
    //GRAPHIC UPDATE OF THE PANELS
    private void updateItemDisplay() {
        chartsPanel.removeAll();
        infoBoxPanel.removeAll();

        for (int i = 0; i < chartToggleButtons.length; i++) {
            JToggleButton toggleButton = chartToggleButtons[i];
            String chartName = toggleButton.getText();
            if (toggleButton.isSelected()) {
                if (chartPanels.get(chartName) instanceof LineChart) {
                    chartsPanel.add(chartPanels.get(chartName));
                } else if (chartPanels.get(chartName) instanceof InfoBox) {
                    infoBoxPanel.add(chartPanels.get(chartName));
                }
            }
        }

        if (infoBoxPanel.getComponentCount() == 0) {
            scrollInfoBoxPane.setPreferredSize(new Dimension(0, 0));
            
            if(!mainChange) {
            	mainChange = true;
                mainPanel.revalidate(); 
                mainPanel.repaint(); 
            }
        } else {
        	scrollInfoBoxPane.setPreferredSize(new Dimension(150, 300));  
            if(mainChange) {
            	mainChange = false;
                mainPanel.revalidate(); 
                mainPanel.repaint(); 
            }
        }

        scrollInfoBoxPane.revalidate();
        chartsPanel.revalidate();
        infoBoxPanel.revalidate();

        scrollInfoBoxPane.repaint();
        chartsPanel.repaint();
        infoBoxPanel.repaint();
    }

    //ADDING VALUES 
    public void addValueToItemDisplay(String chartName, double x, double y) {
        String mapChartName;

        switch (chartName) {
            case "HeartRate":
            	mapChartName = "Heart Rate";
                break;
            case "TotalLungVolume":
            	mapChartName = "Total Lung Volume";
                break;
            case "RespirationRate":
            	mapChartName = "Respiratory Rate";
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
}
