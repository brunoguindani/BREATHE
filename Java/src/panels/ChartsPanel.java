package panels;

import java.awt.Color;
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

import utils.LineChart;

import javax.swing.JToggleButton;

public class ChartsPanel {
	
	private HashMap<String, Unit> chartsMap;
	private HashMap<String, LineChart> chartPanels;
    public JPanel chartsPanel = new JPanel();
    JScrollPane scrollChartPane;
    JPanel selectionPanel;
    private JToggleButton[] chartToggleButtons;
    
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

        String[] chartOrder = {
        	    "Total Lung Volume",
        	    "CO2",
        	    "Pleth",
        	    "ECG",
        	    "Heart Rate",
        	    "Respiratory Rate"
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
                    updateChartsPanel();
                }
            });
            selectionPanel.add(chartToggleButtons[i]);
            
            if (chartToggleButtons[i].isSelected()) {
                chartsPanel.add(chartPanels.get(chartName));
            }
            i++;
        }
        
        scrollChartPane = new JScrollPane(chartsPanel);
        scrollChartPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChartPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollChartPane.setBorder(null);
    }

    public HashMap<String, LineChart> getChartsPanel() {
        return chartPanels;
    }
    
    public JPanel getChartPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(selectionPanel);
        mainPanel.add(scrollChartPane);
        mainPanel.setBackground(Color.BLACK);
        return mainPanel;
    }
    
    private void updateChartsPanel() {
        chartsPanel.removeAll();
        for (JToggleButton toggleButton : chartToggleButtons) {
            if (toggleButton.isSelected()) {
                chartsPanel.add(chartPanels.get(toggleButton.getText()));
            }
        }
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
    
    
    public void addPointToChartsPanel(String chartName, double x, double y) {
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
