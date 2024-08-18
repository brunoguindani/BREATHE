package tests;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ChartsPanelTest {
	public LineChartPanelTest[] chartPanels = new LineChartPanelTest[3];
    public JPanel chartsPanel = new JPanel();
    JScrollPane scrollChartPane;
    
    public ChartsPanelTest() {
 // Crea un pannello per contenere i grafici
        
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.setBackground(Color.LIGHT_GRAY);

        // Aggiungi i grafici al pannello
        chartPanels[0] = new LineChartPanelTest("Heart Rate"); // Primo grafico
        chartPanels[1] = new LineChartPanelTest("Total Lung Volume"); // Secondo grafico
        chartPanels[2] = new LineChartPanelTest("Respiratory Rate"); // Secondo grafico
        for (int i =0; i< chartPanels.length ;i++) {
        	 chartsPanel.add(chartPanels[i]);
        }
        

        // Crea un JScrollPane per rendere il pannello scorrevole
        scrollChartPane = new JScrollPane(chartsPanel);
        scrollChartPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChartPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public JScrollPane getChartsScrollPane() {
    	return scrollChartPane;
    }
    
    public LineChartPanelTest[] getChartPanel() {
        return chartPanels;
    }
}
