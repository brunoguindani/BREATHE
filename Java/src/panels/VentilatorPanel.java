package panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import app.SimulationWorker;
import utils.VentilationMode;

public class VentilatorPanel {
	
	/*
	 * Panel to manage ventilators data and connection
	 * we have 3 different ventilators: PC, VC and CPAP.
	 * PC and VC can be either AC or CMV. 
	 */
	
	JPanel ventilatorPanel = new JPanel(new BorderLayout());
	
    private CardLayout ventilatorCardLayout;
    private JPanel ventilatorCardPanel;
    public JButton connectButton = new JButton("Connect");
    public JButton disconnectButton;

    //data for PC ventilator
    private JRadioButton pc;

	public JSpinner fractionInspOxygenPCField, inspiratoryPeriodPCField, inspiratoryPressurePCField, positiveEndExpPresPCField, respirationRatePCField, slopePCField;
    JComboBox<String> AMComboBox_PC = new JComboBox<>(new String[]{"AC", "CMV"});
    
    //data for CPAP ventilator
    private JRadioButton cpap;
    public JSpinner fractionInspOxygenCPAPField, deltaPressureSupCPAPField, positiveEndExpPresCPAPField, slopeCPAPField;
    
    //data for VC ventilator
    private JRadioButton vc;
    public JSpinner flowVCField, fractionInspOxygenVCField, inspiratoryPeriodVCField, positiveEndExpPresVCField, respirationRateVCField, tidalVolVCField;
    JComboBox<String> AMComboBox_VC = new JComboBox<>(new String[]{"AC", "CMV"});
    
    //data for External ventilator
    private JRadioButton ext;
    public JLabel pressureEXTLabel, volumeEXTLabel;
    
    
    ButtonGroup ventilatori = new ButtonGroup();
    private VentilationMode selectedVentilationMode = VentilationMode.PC;
    private VentilationMode runningVentilationMode;
    
    public VentilatorPanel() {
         ventilatorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
         ventilatorPanel.setPreferredSize(new Dimension(250, 0));
         ventilatorPanel.setBackground(Color.LIGHT_GRAY);
         
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.insets = new Insets(5, 5, 5, 5);
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.gridx = 0;
         gbc.gridy = 0;
         
         ventilatorCardLayout = new CardLayout();
         ventilatorCardPanel = new JPanel(ventilatorCardLayout);

         // create different types of Ventilators
         JPanel pcPanel = new JPanel(new GridBagLayout());
         pcPanel.setBackground(Color.LIGHT_GRAY);
         JPanel cpapPanel = new JPanel(new GridBagLayout());
         cpapPanel.setBackground(Color.LIGHT_GRAY);
         JPanel vcPanel = new JPanel(new GridBagLayout());
         vcPanel.setBackground(Color.LIGHT_GRAY);
         JPanel extPanel = new JPanel(new GridBagLayout());
         extPanel.setBackground(Color.LIGHT_GRAY);
     
         // MechanicalVentilatorContinuousPositiveAirwayPressure (PC)
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenPCField = new JSpinner(new SpinnerNumberModel(0.21, 0, 1, 0.01)), pcPanel, gbc);
         addLabelAndField("Inspiratory Period - Ti", inspiratoryPeriodPCField = new JSpinner(new SpinnerNumberModel(1, 0, 10, 0.1)), pcPanel, gbc);
         addLabelAndField("Inspiratory Pressure - Pinsp", inspiratoryPressurePCField = new JSpinner(new SpinnerNumberModel(19, 0, 100, 1)), pcPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresPCField = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), pcPanel, gbc);
         addLabelAndField("Respiration Rate - RR", respirationRatePCField = new JSpinner(new SpinnerNumberModel(12, 0, 60, 1)), pcPanel, gbc);
         addLabelAndField("Slope", slopePCField = new JSpinner(new SpinnerNumberModel(0.2, 0, 2, 0.1)), pcPanel, gbc);
         addLabelAndField("Assisted Mode", AMComboBox_PC, pcPanel, gbc);
         
         // MechanicalVentilatorContinuousPositiveAirwayPressure (CPAP)
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenCPAPField = new JSpinner(new SpinnerNumberModel(0.21, 0, 1, 0.01)), cpapPanel, gbc);
         addLabelAndField("Delta Pressure Support - deltaPsupp", deltaPressureSupCPAPField = new JSpinner(new SpinnerNumberModel(10, 0, 50, 1)), cpapPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresCPAPField = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), cpapPanel, gbc);
         addLabelAndField("Slope", slopeCPAPField = new JSpinner(new SpinnerNumberModel(0.2, 0, 2, 0.1)), cpapPanel, gbc);
         
         // SEMechanicalVentilatorVolumeControl (VC)
         addLabelAndField("Flow", flowVCField = new JSpinner(new SpinnerNumberModel(60, 0, 120, 1)), vcPanel, gbc);
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenVCField = new JSpinner(new SpinnerNumberModel(0.21, 0, 1, 0.01)), vcPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresVCField = new JSpinner(new SpinnerNumberModel(5, 0, 20, 1)), vcPanel, gbc);
         addLabelAndField("Inspiratory Period", inspiratoryPeriodVCField = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1)), vcPanel, gbc);
         addLabelAndField("Respiration Rate - RR", respirationRateVCField = new JSpinner(new SpinnerNumberModel(12, 0, 60, 1)), vcPanel, gbc);
         addLabelAndField("Tidal Volume - VT", tidalVolVCField = new JSpinner(new SpinnerNumberModel(900, 0, 2000, 10)), vcPanel, gbc);
         addLabelAndField("Assisted Mode", AMComboBox_VC, vcPanel, gbc);
         
         // SEMechanicalVentilation (for external ventilators)
         addLabelAndField("Pressure", pressureEXTLabel = new JLabel(""), extPanel, gbc);
         addLabelAndField("Volume", volumeEXTLabel = new JLabel(""), extPanel, gbc);
         
         ventilatorCardPanel.add(pcPanel, "PC");
         ventilatorCardPanel.add(cpapPanel, "CPAP");
         ventilatorCardPanel.add(vcPanel, "VC");
         ventilatorCardPanel.add(extPanel, "EXT");

         pc = new JRadioButton("PC");
         pc.setSelected(true);
         cpap = new JRadioButton("CPAP");
         vc = new JRadioButton("VC");
         ext = new JRadioButton("EXT");

         ventilatori.add(pc);
         ventilatori.add(cpap);
         ventilatori.add(vc);
         ventilatori.add(ext);

         JPanel radioPanel = new JPanel(new GridLayout(1, 3));
         radioPanel.setBackground(Color.LIGHT_GRAY);
         radioPanel.add(pc);
         radioPanel.add(cpap);
         radioPanel.add(vc);
         radioPanel.add(ext);
         
         //buttons to manage the selected ventilator
         connectButton.setEnabled(false);  
         connectButton.setForeground(Color.BLACK);
         connectButton.setFocusPainted(false);
         
         disconnectButton = new JButton("Disconnect");
         disconnectButton.setEnabled(false); 
         disconnectButton.setForeground(Color.RED);
         disconnectButton.setFocusPainted(false);
         
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new GridLayout(2, 1)); 
         buttonPanel.add(connectButton);
         buttonPanel.add(disconnectButton);
         buttonPanel.setBackground(Color.LIGHT_GRAY);
                 
         ventilatorPanel.add(radioPanel, BorderLayout.NORTH);
         ventilatorPanel.add(buttonPanel, BorderLayout.SOUTH);
         ventilatorPanel.add(ventilatorCardPanel, BorderLayout.CENTER);
         
         //switch between ventilators        
         pc.addActionListener(e -> {
         	ventilatorCardLayout.show(ventilatorCardPanel, "PC");
         	selectedVentilationMode = VentilationMode.PC;
         	});
         cpap.addActionListener(e -> {
         	ventilatorCardLayout.show(ventilatorCardPanel, "CPAP");
         	selectedVentilationMode = VentilationMode.CPAP;
         });
         vc.addActionListener(e -> {
         	ventilatorCardLayout.show(ventilatorCardPanel, "VC");
         	selectedVentilationMode = VentilationMode.VC;
         });
         ext.addActionListener(e -> {
          	ventilatorCardLayout.show(ventilatorCardPanel, "EXT");
          	selectedVentilationMode = VentilationMode.EXTERNAL;
          });

         //actions for buttons
         connectButton.addActionListener(e -> {
         	if(!SimulationWorker.ventilationStartRequest)
         		SimulationWorker.ventilationStartRequest = true;
         	else
         		SimulationWorker.ventilationStartRequest = false;
         	disconnectButton.setEnabled(true);
         	disconnectButton.setText("Disconnect " + selectedVentilationMode);
         	runningVentilationMode = selectedVentilationMode;
         	connectButton.setEnabled(false);
         });

         disconnectButton.addActionListener(e -> {
         	if(!SimulationWorker.ventilationDisconnectRequest) {
         		SimulationWorker.ventilationDisconnectRequest = true;
         		if(!(runningVentilationMode == VentilationMode.EXTERNAL))
         			MiniLogPanel.append(runningVentilationMode + " ventilator disconnected\n");
         	}
         	else
         		SimulationWorker.ventilationDisconnectRequest = false;
         	disconnectButton.setEnabled(false);
         	disconnectButton.setText("Disconnect");
         	connectButton.setEnabled(true);
         	pressureEXTLabel.setText(Double.NaN+"");
         	volumeEXTLabel.setText(Double.NaN+"");
         	
         });
    }
    
    //method to return the panel
    public JPanel getVentilatorPanel() {
    	return ventilatorPanel;
    }
    
    //adding visual to panel
    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
    	component.setPreferredSize(new Dimension(65, 25));
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridy++;
    }
    
    //Get ventilator data (PC)
    public boolean isPCConnected() {
    	return runningVentilationMode == VentilationMode.PC;
    }
    
    public Double getInspiratoryPeriodValue_PC() {
        return (Double) inspiratoryPeriodPCField.getValue();
    }

    public int getInspiratoryPressureValue_PC() {
        return (Integer) inspiratoryPressurePCField.getValue();
    }

    public int getRespirationRateValue_PC() {
        return (Integer) respirationRatePCField.getValue();
    }

    public Double getFractionInspOxygenValue_PC() {
        return (Double) fractionInspOxygenPCField.getValue();
    }

    public int getPositiveEndExpPresValue_PC() {
        return (Integer) positiveEndExpPresPCField.getValue();
    }

    public Double getSlopeValue_PC() {
        return (Double) slopePCField.getValue();
    }

    public String getAssistedMode_PC() {
        return (String) AMComboBox_PC.getSelectedItem();
    }
    
    //Get ventilator data (CPAP)
    public boolean isCPAPConnected() {
        return runningVentilationMode == VentilationMode.CPAP;
    }
    
 // CPAP methods
    public double getFractionInspOxygenValue_CPAP() {
        return (double) fractionInspOxygenCPAPField.getValue();
    }

    public int getDeltaPressureSupValue_CPAP() {
        return (int) deltaPressureSupCPAPField.getValue();
    }

    public int getPositiveEndExpPresValue_CPAP() {
        return (int) positiveEndExpPresCPAPField.getValue();
    }

    public double getSlopeValue_CPAP() {
        return (double) slopeCPAPField.getValue();
    }

    // VC methods
    public boolean isVCConnected() {
        return runningVentilationMode == VentilationMode.VC;
    }
    
    public double getFractionInspOxygenValue_VC() {
        return (double) fractionInspOxygenVCField.getValue();
    }

    public int getFlow_VC() {
        return (int) flowVCField.getValue();
    }

    public double getInspiratoryPeriod_VC() {
        return (double) inspiratoryPeriodVCField.getValue();
    }

    public int getTidalVol_VC() {
        return (int) tidalVolVCField.getValue();
    }

    public int getRespirationRate_VC() {
        return (int) respirationRateVCField.getValue();
    }

    public int getPositiveEndExpPres_VC() {
        return (int) positiveEndExpPresVCField.getValue();
    }
    
    public String getAssistedMode_VC() {
        return (String) AMComboBox_VC.getSelectedItem();
    }
    
    // set ext ventilator data
    public boolean isEXTConnected() {
        return runningVentilationMode == VentilationMode.EXTERNAL;
    }
    
    public void setPressureLabel_EXT(double pressure) {
    	pressureEXTLabel.setText(""+pressure);
    }
    
    public void setVolumeLabel_EXT(double volume) {
    	volumeEXTLabel.setText(""+volume);
    }
    
    public void setNullRunningVentilationMode() {
    	runningVentilationMode = null;
    }
}
