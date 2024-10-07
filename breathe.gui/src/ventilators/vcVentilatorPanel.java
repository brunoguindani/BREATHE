package ventilators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class vcVentilatorPanel{
	private JPanel mainPanel = new JPanel(new GridBagLayout());
	
	private JSpinner flow, fractionInspOxygen, inspiratoryPeriod, positiveEndExpPres, respirationRate, tidalVol;
    private JComboBox<String> AM = new JComboBox<>(new String[]{"AC", "CMV"});
    
    // SEMechanicalVentilatorVolumeControl (VC)
	public vcVentilatorPanel() {
        mainPanel.setBackground(Color.LIGHT_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        addLabelAndField("Flow", flow = new JSpinner(new SpinnerNumberModel(60, 0, 120, 1)), mainPanel, gbc);
        addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygen = new JSpinner(new SpinnerNumberModel(0.21, 0, 1, 0.01)), mainPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPres = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), mainPanel, gbc);
        addLabelAndField("Inspiratory Period", inspiratoryPeriod = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1)), mainPanel, gbc);
        addLabelAndField("Respiration Rate - RR", respirationRate = new JSpinner(new SpinnerNumberModel(12, 0, 60, 1)), mainPanel, gbc);
        addLabelAndField("Tidal Volume - VT", tidalVol = new JSpinner(new SpinnerNumberModel(900, 0, 2000, 10)), mainPanel, gbc);
        addLabelAndField("Assisted Mode", AM, mainPanel, gbc);
	}
	
    //method to add visual to panel
    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
    	component.setPreferredSize(new Dimension(65, 25));
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridy++;
    }
    
    //method to return panel
    public JPanel getMainPanel() {
    	return mainPanel;
    }
    
    //Get ventilator data 
    public Map<String, Number> getData() {
        Map<String, Number> dataMap = new HashMap<>();
        dataMap.put("Flow", (int) flow.getValue()); 
        dataMap.put("FractionInspiredOxygen", (double) fractionInspOxygen.getValue());
        dataMap.put("InspiratoryPeriod", (double) inspiratoryPeriod.getValue());
        dataMap.put("PositiveEndExpiratoryPressure", (int) positiveEndExpPres.getValue());
        dataMap.put("RespirationRate", (int) respirationRate.getValue());
        dataMap.put("TidalVolume", (int) tidalVol.getValue()); 
        if(AM.getSelectedItem().toString().equals("AC"))
        	dataMap.put("AssistedMode", 0); 
        else
        	dataMap.put("AssistedMode", 1); 
        return dataMap;
    }
}
