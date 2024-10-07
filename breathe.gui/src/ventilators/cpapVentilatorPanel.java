package ventilators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class cpapVentilatorPanel{
	private JPanel mainPanel = new JPanel(new GridBagLayout());
	
	private JSpinner fractionInspOxygen, deltaPressureSup, positiveEndExpPres, slope;
    
    
	// MechanicalVentilatorContinuousPositiveAirwayPressure (CPAP)
	public cpapVentilatorPanel() {
        mainPanel.setBackground(Color.LIGHT_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        
    
        addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygen = new JSpinner(new SpinnerNumberModel(0.21, 0, 1, 0.01)), mainPanel, gbc);
        addLabelAndField("Delta Pressure Support - deltaPsupp", deltaPressureSup = new JSpinner(new SpinnerNumberModel(10, 0, 50, 1)), mainPanel, gbc);
        addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPres = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), mainPanel, gbc);
        addLabelAndField("Slope", slope = new JSpinner(new SpinnerNumberModel(0.2, 0, 2, 0.1)), mainPanel, gbc);
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
        dataMap.put("FractionInspiredOxygen", (double) fractionInspOxygen.getValue());
        dataMap.put("DeltaPressureSupport", (int) deltaPressureSup.getValue()); 
        dataMap.put("PositiveEndExpiratoryPressure", (int) positiveEndExpPres.getValue());
        dataMap.put("Slope", (double) slope.getValue()); 
        return dataMap;
    }
}
