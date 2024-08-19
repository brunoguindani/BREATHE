package panels;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import utilities.LineChart;

public class ChartsPanel {
	public LineChart[] chartPanels = new LineChart[3];
    public JPanel chartsPanel = new JPanel();
    JScrollPane scrollChartPane;
    
    public ChartsPanel() {
        
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.setBackground(Color.LIGHT_GRAY);

        chartPanels[0] = new LineChart("Heart Rate"); 
        chartPanels[1] = new LineChart("Total Lung Volume"); 
        chartPanels[2] = new LineChart("Respiratory Rate"); 
        for (int i =0; i< chartPanels.length ;i++) {
        	 chartsPanel.add(chartPanels[i]);
        }
        
        scrollChartPane = new JScrollPane(chartsPanel);
        scrollChartPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChartPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public JScrollPane getChartsScrollPane() {
    	return scrollChartPane;
    }
    
    public LineChart[] getChartPanel() {
        return chartPanels;
    }
}
