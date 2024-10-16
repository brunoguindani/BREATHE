package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;

import utils.VentilationMode;
import ventilators.cpapVentilatorPanel;
import ventilators.pcVentilatorPanel;
import ventilators.vcVentilatorPanel;

@Route("ventilators")
public class VentilatorsPanel extends VerticalLayout {

    private pcVentilatorPanel pcPanel;
    private cpapVentilatorPanel cpapPanel;
    private vcVentilatorPanel vcPanel;
    
    private VentilationMode activeMode = null;

    private Button connectButton, disconnectButton;
    private FlexLayout ventilatorLayout;

    public VentilatorsPanel() {
        setSpacing(true);
        setSizeFull();

        // Create the panels for each ventilation mode
        pcPanel = new pcVentilatorPanel();
        cpapPanel = new cpapVentilatorPanel();
        vcPanel = new vcVentilatorPanel();

        ventilatorLayout = new FlexLayout(pcPanel, cpapPanel, vcPanel);
        ventilatorLayout.setSizeFull();

        // Radio buttons for selecting ventilation mode
        RadioButtonGroup<VentilationMode> modeGroup = new RadioButtonGroup<>();
        modeGroup.setItems(VentilationMode.PC, VentilationMode.CPAP, VentilationMode.VC, VentilationMode.EXT);
        modeGroup.setLabel("Select Ventilation Mode");
        modeGroup.setValue(VentilationMode.PC);

        modeGroup.addValueChangeListener(event -> updateVentilatorView(event.getValue()));

        // Connect and disconnect buttons
        connectButton = new Button("Connect", e -> connectVentilator());
        disconnectButton = new Button("Disconnect", e -> disconnectVentilator());
        disconnectButton.setEnabled(false);

        // Buttons layout
        HorizontalLayout buttonLayout = new HorizontalLayout(connectButton, disconnectButton);
        buttonLayout.setPadding(true);

        add(modeGroup, ventilatorLayout, buttonLayout);
    }

    private void updateVentilatorView(VentilationMode mode) {
        activeMode = mode;
        ventilatorLayout.removeAll();
        switch (mode) {
            case PC:
                ventilatorLayout.add(pcPanel);
                break;
            case CPAP:
                ventilatorLayout.add(cpapPanel);
                break;
            case VC:
                ventilatorLayout.add(vcPanel);
                break;
        }
    }

    private void connectVentilator() {
        if (activeMode != null) {
            Notification.show("Connected to " + activeMode);
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
        }
    }

    private void disconnectVentilator() {
        Notification.show("Disconnected from " + activeMode);
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
    }
}
