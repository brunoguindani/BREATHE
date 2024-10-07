package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import app.App_temp;
import data.Ventilator;
import utils.VentilationMode;
import ventilators.*;

public class VentilatorsPanel {

	//Panels
    private JPanel mainPanel = new JPanel();

    private pcVentilatorPanel pc = new pcVentilatorPanel();
    private cpapVentilatorPanel cpap = new cpapVentilatorPanel();
    private vcVentilatorPanel vc = new vcVentilatorPanel();
    private extVentilatorPanel ext = new extVentilatorPanel();
    
    //Button
    JToggleButton pcToggleButton,cpapToggleButton, vcToggleButton, extToggleButton;
    
    JButton connectButton, disconnectButton;

    public VentilatorsPanel(App_temp app) {
        mainPanel.setBackground(Color.LIGHT_GRAY);
        mainPanel.setPreferredSize(new Dimension(550, 650));

        CardLayout ventilatorCardLayout = new CardLayout();
        JPanel ventilatorsCardPanel = new JPanel(ventilatorCardLayout);

        // get all the ventilators panels
        JPanel pcPanel = pc.getMainPanel();
        JPanel cpapPanel = cpap.getMainPanel();
        JPanel vcPanel = vc.getMainPanel();
        JPanel extPanel = ext.getMainPanel();

        // add all the panels to the cardPanel
        ventilatorsCardPanel.add(pcPanel, "PC");
        ventilatorsCardPanel.add(cpapPanel, "CPAP");
        ventilatorsCardPanel.add(vcPanel, "VC");
        ventilatorsCardPanel.add(extPanel, "EXT");

        // TOGGLEBUTTON
        pcToggleButton = new JToggleButton("PC");
        cpapToggleButton = new JToggleButton("CPAP");
        vcToggleButton = new JToggleButton("VC");
        extToggleButton = new JToggleButton("EXT");
        
        pcToggleButton.setSelected(true);
        
        // Add toggle buttons to the ButtonGroup
        ButtonGroup ventilatorGroup = new ButtonGroup();
        ventilatorGroup.add(pcToggleButton);
        ventilatorGroup.add(cpapToggleButton);
        ventilatorGroup.add(vcToggleButton);
        ventilatorGroup.add(extToggleButton);
        
        // add all the toggle buttons to the radio panel
        JPanel ventilatorsRadioPanel = new JPanel(new GridLayout(1, 3));
        ventilatorsRadioPanel.setBackground(Color.LIGHT_GRAY);
        ventilatorsRadioPanel.add(pcToggleButton);
        ventilatorsRadioPanel.add(cpapToggleButton);
        ventilatorsRadioPanel.add(vcToggleButton);
        ventilatorsRadioPanel.add(extToggleButton);

        // add action to the toggle buttons
        pcToggleButton.addActionListener(e -> {
            ventilatorCardLayout.show(ventilatorsCardPanel, "PC");
        });
        cpapToggleButton.addActionListener(e -> {
            ventilatorCardLayout.show(ventilatorsCardPanel, "CPAP");
        });
        vcToggleButton.addActionListener(e -> {
            ventilatorCardLayout.show(ventilatorsCardPanel, "VC");
        });
        extToggleButton.addActionListener(e -> {
            ventilatorCardLayout.show(ventilatorsCardPanel, "EXT");
        });

        // buttons to manage connect/disconnect of the selected ventilator
        connectButton = new JButton("Connect");
        connectButton.setEnabled(false);
        connectButton.setForeground(Color.WHITE);
        connectButton.setBackground(new Color(0, 122, 255));
        connectButton.setFocusPainted(false);
        connectButton.addActionListener(e -> {
            app.connectVentilator();
            setEnableConnectButton(false);
            setEnableDisconnectButton(true);
        });

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        disconnectButton.setBackground(new Color(255, 59, 48));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setFocusPainted(false);
        disconnectButton.addActionListener(e -> {
            app.disconnectVentilator();
            setEnableConnectButton(true);
            setEnableDisconnectButton(false);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        // add all the elements to the main panel
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(ventilatorsRadioPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(ventilatorsCardPanel, BorderLayout.CENTER);
    }

    // method to return panel
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    public Ventilator getCurrentVentilator() {

        if (pcToggleButton.isSelected()) {
        	return new Ventilator(VentilationMode.PC,pc.getData());
        } else if (cpapToggleButton.isSelected()) {
        	return new Ventilator(VentilationMode.CPAP,cpap.getData());
        } else if (vcToggleButton.isSelected()) {
        	return new Ventilator(VentilationMode.VC,vc.getData());
        } else if (extToggleButton.isSelected()) {
        	return new Ventilator(VentilationMode.EXTERNAL,ext.getData());
        }
        
        return null;
    }
    
    public void setEnableConnectButton(boolean enable) {
    	connectButton.setEnabled(enable);
    }
    
    public void setEnableDisconnectButton(boolean enable) {
    	disconnectButton.setEnabled(enable);
    }

}
