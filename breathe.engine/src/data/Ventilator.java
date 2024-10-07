package data;

import java.util.Map;

import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorPressureControlData;
import com.kitware.pulse.cdm.bind.MechanicalVentilatorActions.MechanicalVentilatorVolumeControlData;
import com.kitware.pulse.cdm.patient.actions.SEMechanicalVentilation;
import com.kitware.pulse.cdm.properties.CommonUnits.FrequencyUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.PressureUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumePerTimeUnit;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;
import com.kitware.pulse.cdm.system.equipment.SEEquipmentAction;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorContinuousPositiveAirwayPressure;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorPressureControl;
import com.kitware.pulse.cdm.system.equipment.mechanical_ventilator.actions.SEMechanicalVentilatorVolumeControl;

import utils.Pair;
import utils.VentilationMode;

public class Ventilator {
	
	private VentilationMode mode; 
	private Map<String, Number> parameters; //Parameter name and Parameter value
	
	//Ventilators object depending on mode
	private SEEquipmentAction ventilator; 
	private SEMechanicalVentilation ventilator_EXTERNAL;

	/*
	 * Constructor for "standard" ventilators
	 */
	public Ventilator(VentilationMode mode, Map<String, Number> parameters) {
		
	    this.mode = mode;
	    //Receive a list of parameters as pairs String Double
	    //Doesn't check for duplicates cause it is completely handled client side
	    this.parameters = parameters;
	    
	    manageSEVentilator(); //generate and update the proper ventilator
	}
	
	/*
	 * Constructor for "external" ventilators
	 */
	public Ventilator(VentilationMode mode) {
		
	    this.mode = mode;
	    //Receive a list of parameters as pairs String Double
	    //Doesn't check for duplicates cause it is completely handled client side
	    
	    manageSEVentilator(); //generate and update the proper ventilator
	}
	
	
	//Set up parameters depending on mode
	private void manageSEVentilator() {
		if(mode == VentilationMode.VC) {
			
			SEMechanicalVentilatorVolumeControl ventilator_VC = new SEMechanicalVentilatorVolumeControl();
			
			//AC = 0, CMV = 1
			if((int)parameters.get("AssistedMode") == 0) ventilator_VC.setMode(MechanicalVentilatorVolumeControlData.eMode.AssistedControl);
			else ventilator_VC.setMode(MechanicalVentilatorVolumeControlData.eMode.ContinuousMandatoryVentilation);
			
			ventilator_VC.getFlow().setValue((int) parameters.get("Flow"), VolumePerTimeUnit.L_Per_min);
			ventilator_VC.getFractionInspiredOxygen().setValue((double) parameters.get("FractionInspiredOxygen"));
			ventilator_VC.getInspiratoryPeriod().setValue((double) parameters.get("InspiratoryPeriod"), TimeUnit.s);
			ventilator_VC.getPositiveEndExpiratoryPressure().setValue((int) parameters.get("PositiveEndExpiratoryPressure"), PressureUnit.cmH2O);
			ventilator_VC.getRespirationRate().setValue((int) parameters.get("RespirationRate"), FrequencyUnit.Per_min);
			ventilator_VC.getTidalVolume().setValue((int) parameters.get("TidalVolume"), VolumeUnit.mL);
			
			this.ventilator = ventilator_VC;
			
		}else if(mode == VentilationMode.PC){
			
			SEMechanicalVentilatorPressureControl ventilator_PC = new SEMechanicalVentilatorPressureControl();
			
			if((int)parameters.get("AssistedMode") == 0) ventilator_PC.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
			else ventilator_PC.setMode(MechanicalVentilatorPressureControlData.eMode.ContinuousMandatoryVentilation);
			
			ventilator_PC.getSlope().setValue((double) parameters.get("Slope"), TimeUnit.s);
			ventilator_PC.getFractionInspiredOxygen().setValue((double) parameters.get("FractionInspiredOxygen"));
			ventilator_PC.getInspiratoryPeriod().setValue((double) parameters.get("InspiratoryPeriod"), TimeUnit.s);
			ventilator_PC.getPositiveEndExpiratoryPressure().setValue((int) parameters.get("PositiveEndExpiratoryPressure"), PressureUnit.cmH2O);
			ventilator_PC.getRespirationRate().setValue((int) parameters.get("RespirationRate"), FrequencyUnit.Per_min);
			ventilator_PC.getInspiratoryPressure().setValue((int) parameters.get("InspiratoryPressure"), PressureUnit.cmH2O);	
			
			this.ventilator = ventilator_PC;
			
		}else if(mode == VentilationMode.CPAP){
			
			SEMechanicalVentilatorContinuousPositiveAirwayPressure ventilator_CPAP = new SEMechanicalVentilatorContinuousPositiveAirwayPressure();
			
			ventilator_CPAP.getFractionInspiredOxygen().setValue((double) parameters.get("FractionInspiredOxygen"));
			ventilator_CPAP.getDeltaPressureSupport().setValue((int) parameters.get("DeltaPressureSupport"), PressureUnit.cmH2O);
			ventilator_CPAP.getPositiveEndExpiratoryPressure().setValue((int) parameters.get("PositiveEndExpiratoryPressure"), PressureUnit.cmH2O);
			ventilator_CPAP.getSlope().setValue((double) parameters.get("Slope"), TimeUnit.s);	
			
			this.ventilator = ventilator_CPAP;
		}else {
			if(ventilator_EXTERNAL == null) 
				ventilator_EXTERNAL = new SEMechanicalVentilation();
		}
	}
	
	//Clear and update all parameters, then update ventilator
	@SuppressWarnings("unchecked")
	public void updateSettings(Pair<String, Double>... pairs) {
	    this.parameters.clear();
	    for (Pair<String, Double> pair : pairs) {
	        this.parameters.put(pair.getKey(), pair.getValue());
	    } 
	    
	    manageSEVentilator(); 
	}
	
	/*
	 * Get ventilator
	 */
	public SEEquipmentAction getVentilator() {
	    return ventilator;
	}
	
	public SEMechanicalVentilation getVentilator_External() {
	    return ventilator_EXTERNAL;
	}
	
	public VentilationMode getMode() {
		return mode;
	}
	
}
