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
import javax.swing.JTextField;

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

    //data for PC ventilator
    private JRadioButton pc;
    public JTextField fractionInspOxygenPCField, inspiratoryPeriodPCField, inspiratoryPressurePCField, positiveEndExpPresPCField, respirationRatePCField, slopePCField;
    JComboBox<String> AMComboBox_PC = new JComboBox<>(new String[]{"AC", "CMV"});
    
    //data for CPAP ventilator
    private JRadioButton cpap;
    public JTextField fractionInspOxygenCPAPField, deltaPressureSupCPAPField, positiveEndExpPresCPAPField, slopeCPAPField;
    
    //data for VC ventilator
    private JRadioButton vc;
    public JTextField flowVCField, fractionInspOxygenVCField, inspiratoryPeriodVCField, positiveEndExpPresVCField, respirationRateVCField, tidalVolVCField;
    JComboBox<String> AMComboBox_VC = new JComboBox<>(new String[]{"AC", "CMV"});
    
    //data for External ventilator
    private JRadioButton ext;
    public JLabel pressureEXTLabel, volumeEXTLabel;
    
    
    ButtonGroup ventilatori = new ButtonGroup();
    private VentilationMode selectedVentilationMode = VentilationMode.PC;
    
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
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenPCField = new JTextField("0.21"), pcPanel, gbc);
         addLabelAndField("Inspiratory Period - Ti", inspiratoryPeriodPCField = new JTextField("1"), pcPanel, gbc);
         addLabelAndField("Inspiratory Pressure - Pinsp", inspiratoryPressurePCField = new JTextField("19"), pcPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresPCField = new JTextField("5"), pcPanel, gbc);
         addLabelAndField("Respiration Rate - RR", respirationRatePCField = new JTextField("12"), pcPanel, gbc);
         addLabelAndField("Slope", slopePCField = new JTextField("0.2"), pcPanel, gbc);
         addLabelAndField("Assisted Mode", AMComboBox_PC, pcPanel, gbc);
         
         // MechanicalVentilatorContinuousPositiveAirwayPressure (CPAP)
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenCPAPField = new JTextField("0.21"), cpapPanel, gbc);
         addLabelAndField("Delta Pressure Support - deltaPsupp", deltaPressureSupCPAPField = new JTextField("10"), cpapPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresCPAPField = new JTextField("5"), cpapPanel, gbc);
         addLabelAndField("Slope", slopeCPAPField = new JTextField("0.2"), cpapPanel, gbc);
         
         // SEMechanicalVentilatorVolumeControl (VC)
         addLabelAndField("Flow", flowVCField = new JTextField("60"), vcPanel, gbc);
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenVCField = new JTextField("0.21"), vcPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresVCField = new JTextField("5"), vcPanel, gbc);
         addLabelAndField("Inspiratory Period", inspiratoryPeriodVCField = new JTextField("1"), vcPanel, gbc);
         addLabelAndField("Respiration Rate - RR", respirationRateVCField = new JTextField("12"), vcPanel, gbc);
         addLabelAndField("Tidal Volume - VT", tidalVolVCField = new JTextField("900"), vcPanel, gbc);
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
         
         JButton disconnectButton = new JButton("Disconnect");
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
          	selectedVentilationMode = VentilationMode.EXT;
          });

         //actions for buttons
         connectButton.addActionListener(e -> {
         	if(!SimulationWorker.ventilationStartRequest)
         		SimulationWorker.ventilationStartRequest = true;
         	else
         		SimulationWorker.ventilationStartRequest = false;
         	disconnectButton.setEnabled(true);
         	disconnectButton.setText("Disconnect " + selectedVentilationMode);
         	connectButton.setEnabled(false);
         });

         disconnectButton.addActionListener(e -> {
         	if(!SimulationWorker.ventilationDisconnectRequest)
         		SimulationWorker.ventilationDisconnectRequest = true;
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
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridy++;
    }
    
    //Get ventilator data (PC)
    public boolean isPCConnected() {
    	return selectedVentilationMode == VentilationMode.PC;
    }
    
    public String getInspiratoryPeriodValue_PC() {
        return inspiratoryPeriodPCField.getText();
    }

    public String getInspiratoryPressureValue_PC() {
        return inspiratoryPressurePCField.getText();
    }

    public String getRespirationRateValue_PC() {
        return respirationRatePCField.getText();
    }

    public String getFractionInspOxygenValue_PC() {
        return fractionInspOxygenPCField.getText();
    }

    public String getPositiveEndExpPresValue_PC() {
        return positiveEndExpPresPCField.getText();
    }

    public String getSlopeValue_PC() {
        return slopePCField.getText();
    }

    public String getAssistedMode_PC() {
        return (String) AMComboBox_PC.getSelectedItem();
    }
    
    //Get ventilator data (CPAP)
    public boolean isCPAPConnected() {
        return selectedVentilationMode == VentilationMode.CPAP;
    }
    
    public String getFractionInspOxygenValue_CPAP() {
        return fractionInspOxygenCPAPField.getText();
    }
    
    public String getDeltaPressureSupValue_CPAP() {
        return deltaPressureSupCPAPField.getText();
    }

    public String getPositiveEndExpPresValue_CPAP() {
        return positiveEndExpPresCPAPField.getText();
    }

    public String getSlopeValue_CPAP() {
        return slopeCPAPField.getText();
    }
    
    //Get ventilator data (VC)
    public boolean isVCConnected() {
        return selectedVentilationMode == VentilationMode.VC;
    }
    
    public String getFractionInspOxygenValue_VC() {
        return fractionInspOxygenVCField.getText();
    }
    
    public String getFlow_VC() {
        return flowVCField.getText();
    }

    public String getInspiratoryPeriod_VC() {
        return inspiratoryPeriodVCField.getText();
    }

    public String getTidalVol_VC() {
        return tidalVolVCField.getText();
    }
    
    public String getRespirationRate_VC() {
        return respirationRateVCField.getText();
    }
    
    public String getPositiveEndExpPres_VC() {
        return positiveEndExpPresVCField.getText();
    }
    
    public String getAssistedMode_VC() {
        return (String) AMComboBox_VC.getSelectedItem();
    }
    
    // set ext ventilator data
    public boolean isEXTConnected() {
        return selectedVentilationMode == VentilationMode.EXT;
    }
    
    public void setPressureLabel_EXT(double pressure) {
    	pressureEXTLabel.setText(""+pressure);
    }
    
    public void setVolumeLabel_EXT(double volume) {
    	volumeEXTLabel.setText(""+volume);
    }
    

    
}
