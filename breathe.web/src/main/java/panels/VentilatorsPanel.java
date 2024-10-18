package panels;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;

import app.App;
import data.Ventilator;
import utils.VentilationMode;
import ventilators.cpapVentilatorPanel;
import ventilators.pcVentilatorPanel;
import ventilators.vcVentilatorPanel;

@Route("ventilators")
public class VentilatorsPanel extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private pcVentilatorPanel pcPanel;
    private cpapVentilatorPanel cpapPanel;
    private vcVentilatorPanel vcPanel;

    private VentilationMode selectedMode = VentilationMode.PC; // Default mode is PC
    private VentilationMode activeMode = VentilationMode.PC; // Default mode is PC
    
    RadioButtonGroup<VentilationMode> modeGroup;

    private Button connectButton, disconnectButton;
    private FlexLayout ventilatorLayout;
    
    App app;

    public VentilatorsPanel(App app) {
    	this.app = app;
        setSpacing(true);
		this.setWidthFull();
		this.setMaxHeight("70%");  
		getStyle().set("border", "1px solid #ccc"); // Imposta il bordo

        // Create the panels for each ventilation mode
        pcPanel = new pcVentilatorPanel(app);
        cpapPanel = new cpapVentilatorPanel(app);
        vcPanel = new vcVentilatorPanel(app);

        ventilatorLayout = new FlexLayout();
        ventilatorLayout.setSizeFull();

        // Initially show only the PC panel
        ventilatorLayout.add(pcPanel);

        // Radio buttons for selecting ventilation mode
        modeGroup = new RadioButtonGroup<>();
        modeGroup.setItems(VentilationMode.PC, VentilationMode.CPAP, VentilationMode.VC);
        modeGroup.setLabel("Select Ventilation Mode");
        modeGroup.setValue(VentilationMode.PC); // Default selected value

        modeGroup.addValueChangeListener(event -> updateVentilatorView(event.getValue()));

        // Connect and disconnect buttons
        connectButton = new Button("Connect", e -> connectVentilator());
        disconnectButton = new Button("Disconnect", e -> disconnectVentilator());
        
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(false);

        disconnectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        
        // Buttons layout
        HorizontalLayout buttonLayout = new HorizontalLayout(connectButton, disconnectButton);
        buttonLayout.setPadding(true);

        add(modeGroup, ventilatorLayout, buttonLayout);
    }

    private void updateVentilatorView(VentilationMode mode) {
        selectedMode = mode;
        ventilatorLayout.removeAll(); // Remove all panels
        switch (mode) {
            case PC:
                ventilatorLayout.add(pcPanel); // Show PC panel
                break;
            case CPAP:
                ventilatorLayout.add(cpapPanel); // Show CPAP panel
                break;
            case VC:
                ventilatorLayout.add(vcPanel); // Show VC panel
                break;
			case EXT:
				break;
			default:
				break;
	    }
    }

    private void connectVentilator() {
        if (selectedMode != null) {
        	app.connectVentilator();
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            activeMode = selectedMode;
            setEnableApplyButton(modeGroup.getValue(), true);
            disconnectButton.setText("Disconnect "+ modeGroup.getValue());
        }
    }

	private void disconnectVentilator() {
		app.disconnectVentilator();
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        setEnableApplyButton(modeGroup.getValue(), false);
        disconnectButton.setText("Disconnect");
    }
    

    public Ventilator getCurrentVentilator() {
    	System.out.println(activeMode);
        switch (activeMode) {
            case PC:
                return new Ventilator(VentilationMode.PC, pcPanel.getData());
            case CPAP:
                return new Ventilator(VentilationMode.CPAP, cpapPanel.getData());
            case VC:
                return new Ventilator(VentilationMode.VC, vcPanel.getData());
            default:
                return null;
        }
    }
    
    private void setEnableApplyButton(VentilationMode mode, boolean enable) {
        switch (mode) {
            case PC:
                pcPanel.setEnableApplyButton(enable);
                break;
            case CPAP:
                cpapPanel.setEnableApplyButton(enable);
                break;
            case VC:
                vcPanel.setEnableApplyButton(enable);
                break;
            default:
                break;
        }
    }
    
    public void resetButton() {
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        setEnableApplyButton(activeMode, false);
        disconnectButton.setText("Disconnect");
    }

	public void setEnableConnectButton(boolean b) {
		connectButton.setEnabled(b);
	}
}
