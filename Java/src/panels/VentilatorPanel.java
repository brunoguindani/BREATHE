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

import app.App;
import app.SimulationWorker;
import utilities.VentilationMode;

public class VentilatorPanel {
	JPanel ventilatorPanel = new JPanel(new BorderLayout());
	
    private CardLayout ventilatorCardLayout;
    private JPanel ventilatorCardPanel;

    private JRadioButton pc;
    public JTextField fractionInspOxygenPCField, inspiratoryPeriodPCField, inspiratoryPressurePCField, positiveEndExpPresPCField, respirationRatePCField, slopePCField;
    JComboBox<String> AMComboBox_PC = new JComboBox<>(new String[]{"AC", "CMV"});
    
    private JRadioButton cpap;
    public JTextField fractionInspOxygenCPAPField, deltaPressureSupCPAPField, positiveEndExpPresCPAPField, slopeCPAPField;
    
    private JRadioButton vc;
    public JTextField flowVCField, fractionInspOxygenVCField, inspiratoryPeriodVCField, positiveEndExpPresVCField, respirationRateVCField, tidalVolVCField;
    JComboBox<String> AMComboBox_VC = new JComboBox<>(new String[]{"AC", "CMV"});
    
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

         JPanel pcPanel = new JPanel(new GridBagLayout());
         JPanel cpapPanel = new JPanel(new GridBagLayout());
         JPanel vcPanel = new JPanel(new GridBagLayout());
     
         // Campi per ventilatore MechanicalVentilatorContinuousPositiveAirwayPressure (PCAC)
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenPCField = new JTextField("0.21"), pcPanel, gbc);
         addLabelAndField("Inspiratory Period - Ti", inspiratoryPeriodPCField = new JTextField("1"), pcPanel, gbc);
         addLabelAndField("Inspiratory Pressure - Pinsp", inspiratoryPressurePCField = new JTextField("19"), pcPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresPCField = new JTextField("5"), pcPanel, gbc);
         addLabelAndField("Respiration Rate - RR", respirationRatePCField = new JTextField("12"), pcPanel, gbc);
         addLabelAndField("Slope", slopePCField = new JTextField("0.2"), pcPanel, gbc);
         addLabelAndField("Assisted Mode", AMComboBox_PC, pcPanel, gbc);
         
         // Campi per ventilatore MechanicalVentilatorContinuousPositiveAirwayPressure (CPAP)
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenCPAPField = new JTextField("0.21"), cpapPanel, gbc);
         addLabelAndField("Delta Pressure Support - deltaPsupp", deltaPressureSupCPAPField = new JTextField("10"), cpapPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresCPAPField = new JTextField("5"), cpapPanel, gbc);
         addLabelAndField("Slope", slopeCPAPField = new JTextField("0.2"), cpapPanel, gbc);
         
         // Campi per ventilatore SEMechanicalVentilatorVolumeControl (VCAC)
         addLabelAndField("Flow", flowVCField = new JTextField("60"), vcPanel, gbc);
         addLabelAndField("Fraction Inspired Oxygen - FiO2", fractionInspOxygenVCField = new JTextField("0.21"), vcPanel, gbc);
         addLabelAndField("Positive End Expiratory Pressure - PEEP", positiveEndExpPresVCField = new JTextField("5"), vcPanel, gbc);
         addLabelAndField("Inspiratory Period", inspiratoryPeriodVCField = new JTextField("1"), vcPanel, gbc);
         addLabelAndField("Respiration Rate - RR", respirationRateVCField = new JTextField("12"), vcPanel, gbc);
         addLabelAndField("Tidal Volume - VT", tidalVolVCField = new JTextField("900"), vcPanel, gbc);
         addLabelAndField("Assisted Mode", AMComboBox_VC, vcPanel, gbc);
         
         ventilatorCardPanel.add(pcPanel, "PC");
         ventilatorCardPanel.add(cpapPanel, "CPAP");
         ventilatorCardPanel.add(vcPanel, "VC");

         pc = new JRadioButton("PC");
         pc.setSelected(true);
         cpap = new JRadioButton("CPAP");
         vc = new JRadioButton("VC");

         ventilatori.add(pc);
         ventilatori.add(cpap);
         ventilatori.add(vc);

         JPanel radioPanel = new JPanel(new GridLayout(1, 3));
         radioPanel.add(pc);
         radioPanel.add(cpap);
         radioPanel.add(vc);
         
         
         
         App.connectButton.setEnabled(false);  
         App.connectButton.setForeground(Color.BLACK);
         App.connectButton.setFocusPainted(false);
         
         JButton disconnectButton = new JButton("Disconnect all");
         disconnectButton.setEnabled(false); 
         disconnectButton.setForeground(Color.RED);
         disconnectButton.setFocusPainted(false);
         
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new GridLayout(2, 1)); 
         buttonPanel.add(App.connectButton);
         buttonPanel.add(disconnectButton);
         
         
         ventilatorPanel.add(radioPanel, BorderLayout.NORTH);
         ventilatorPanel.add(buttonPanel, BorderLayout.SOUTH);
         ventilatorPanel.add(ventilatorCardPanel, BorderLayout.CENTER);
         
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

         App.connectButton.addActionListener(e -> {
         	if(!SimulationWorker.ventilationSwitchRequest)
         		SimulationWorker.ventilationSwitchRequest = true;
         	else
         		SimulationWorker.ventilationSwitchRequest = false;
         	disconnectButton.setEnabled(true);
         });

         disconnectButton.addActionListener(e -> {
         	if(!SimulationWorker.ventilationDisconnectRequest)
         		SimulationWorker.ventilationDisconnectRequest = true;
         	else
         		SimulationWorker.ventilationDisconnectRequest = false;
         	disconnectButton.setEnabled(false);
         });
    }
    
    public JPanel getVentilatorPanel() {
    	return ventilatorPanel;
    }
    
    private void addLabelAndField(String labelText, JComponent component, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(component, gbc);
        gbc.gridy++;
    }
    
    //Get ventilatore PCAC
    public boolean isPCACConnected() {
    	return selectedVentilationMode == VentilationMode.PC;
    }
    
    public String getInspiratoryPeriodValue_PCAC() {
        return inspiratoryPeriodPCField.getText();
    }

    public String getInspiratoryPressureValue_PCAC() {
        return inspiratoryPressurePCField.getText();
    }

    public String getRespirationRateValue_PCAC() {
        return respirationRatePCField.getText();
    }

    public String getFractionInspOxygenValue_PCAC() {
        return fractionInspOxygenPCField.getText();
    }

    public String getPositiveEndExpPresValue_PCAC() {
        return positiveEndExpPresPCField.getText();
    }

    public String getSlopeValue_PCAC() {
        return slopePCField.getText();
    }

    public String getAssistedMode_PC() {
        return (String) AMComboBox_PC.getSelectedItem();
    }
    
  //Get ventilatore CPAP
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
    
  //Get ventilatore VCAC
    public boolean isVCACConnected() {
        return selectedVentilationMode == VentilationMode.VC;
    }
    
    public String getFractionInspOxygenValue_VCAC() {
        return fractionInspOxygenVCField.getText();
    }
    
    public String getFlow_VCAC() {
        return flowVCField.getText();
    }

    public String getInspiratoryPeriod_VCAC() {
        return inspiratoryPeriodVCField.getText();
    }

    public String getTidalVol_VCAC() {
        return tidalVolVCField.getText();
    }
    
    public String getRespirationRate_VCAC() {
        return respirationRateVCField.getText();
    }
    
    public String getPositiveEndExpPres_VCAC() {
        return positiveEndExpPresVCField.getText();
    }
    
    public String getAssistedMode_VC() {
        return (String) AMComboBox_VC.getSelectedItem();
    }
}
