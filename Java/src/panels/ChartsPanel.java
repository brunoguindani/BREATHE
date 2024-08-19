package panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

import utilities.LineChart;

public class ChartsPanel {
	public LineChart[] chartPanels;
    public JPanel chartsPanel = new JPanel();
    JScrollPane scrollChartPane;
    JPanel selectionPanel;
    private JCheckBox[] chartCheckboxes;
    
    public ChartsPanel() {
    	String[] chartNames = {"Heart Rate","Total Lung Volume","Respiratory Rate"};
    	chartPanels = new LineChart[chartNames.length];
    	chartCheckboxes = new JCheckBox[chartNames.length];
    	
    	selectionPanel = new JPanel();
    	selectionPanel.setBackground(Color.BLACK);
        
        chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
        chartsPanel.setBackground(Color.BLACK);

        for (int i =0; i< chartNames.length ;i++) {
        	chartPanels[i] = new LineChart(chartNames[i]); 
            chartCheckboxes[i] = new JCheckBox(chartNames[i]);
            chartCheckboxes[i].setSelected(true); 
            chartCheckboxes[i].setBackground(Color.BLACK);
            chartCheckboxes[i].setForeground(Color.WHITE);
            chartCheckboxes[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateChartsPanel();
                }
            });
            selectionPanel.add(chartCheckboxes[i]);
            chartsPanel.add(chartPanels[i]);
        }
        
        scrollChartPane = new JScrollPane(chartsPanel);
        scrollChartPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollChartPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollChartPane.setBorder(null);
    }
    
    public LineChart[] getChartsPanel() {
        return chartPanels;
    }
    
    public JPanel getChartPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(selectionPanel);
        mainPanel.add(scrollChartPane);
        return mainPanel;
    }
    
    private void updateChartsPanel() {
        chartsPanel.removeAll();
        for (int i = 0; i < chartCheckboxes.length; i++) {
            if (chartCheckboxes[i].isSelected()) {
                chartsPanel.add(chartPanels[i]);
            }
        }
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
}
