package data;

import java.util.HashMap;
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
	private boolean assisted;
	private Map<String, Double> parameters; //Parameter name and Parameter value
	
	//Ventilators object depending on mode
	private SEEquipmentAction ventilator; 
	private SEMechanicalVentilation ventilator_EXTERNAL;
	
	/*
	 * Constructor from Parameters with assisted option
	 */
	@SafeVarargs
	public Ventilator(VentilationMode mode, boolean assisted, Pair<String, Double>... pairs) {
		
	    this.mode = mode;
	    this.assisted = assisted;
	    //Receive a list of parameters as pairs String Double
	    //Doesn't check for duplicates cause it is completely handled client side
	    this.parameters = new HashMap<>(); 
	    for (Pair<String, Double> pair : pairs) {
	        this.parameters.put(pair.getKey(), pair.getValue());
	    } 
	    
	    manageSEVentilator(); //generate and update the proper ventilator
	}
	
	/*
	 * Constructor from Parameters without assisted option
	 */
	@SafeVarargs
	public Ventilator(VentilationMode mode, Pair<String, Double>... pairs) {
		
	    this.mode = mode;
	    this.assisted = false;
	    //Receive a list of parameters as pairs String Double
	    //Doesn't check for duplicates cause it is completely handled client side
	    this.parameters = new HashMap<>(); 
	    for (Pair<String, Double> pair : pairs) {
	        this.parameters.put(pair.getKey(), pair.getValue());
	    } 
	    
	    manageSEVentilator(); //generate and update the proper ventilator
	}
	
	//Set up parameters depending on mode
	private void manageSEVentilator() {
		if(mode == VentilationMode.VC) {
			
			SEMechanicalVentilatorVolumeControl ventilator_VC = new SEMechanicalVentilatorVolumeControl();
			
			if(assisted) ventilator_VC.setMode(MechanicalVentilatorVolumeControlData.eMode.AssistedControl);
			else ventilator_VC.setMode(MechanicalVentilatorVolumeControlData.eMode.ContinuousMandatoryVentilation);
			
			ventilator_VC.getFlow().setValue(parameters.get("Flow"), VolumePerTimeUnit.L_Per_min);
			ventilator_VC.getFractionInspiredOxygen().setValue(parameters.get("FractionInspiredOxygen"));
			ventilator_VC.getInspiratoryPeriod().setValue(parameters.get("InspiratoryPeriod"), TimeUnit.s);
			ventilator_VC.getPositiveEndExpiratoryPressure().setValue(parameters.get("PositiveEndExpiratoryPressure"), PressureUnit.cmH2O);
			ventilator_VC.getRespirationRate().setValue(parameters.get("RespirationRate"), FrequencyUnit.Per_min);
			ventilator_VC.getTidalVolume().setValue(parameters.get("TidalVolume"), VolumeUnit.mL);
			
			this.ventilator = ventilator_VC;
			
		}else if(mode == VentilationMode.PC){
			
			SEMechanicalVentilatorPressureControl ventilator_PC = new SEMechanicalVentilatorPressureControl();
			
			if(assisted) ventilator_PC.setMode(MechanicalVentilatorPressureControlData.eMode.AssistedControl);
			else ventilator_PC.setMode(MechanicalVentilatorPressureControlData.eMode.ContinuousMandatoryVentilation);
			
			ventilator_PC.getSlope().setValue(parameters.get("Slope"), TimeUnit.s);
			ventilator_PC.getFractionInspiredOxygen().setValue(parameters.get("FractionInspiredOxygen"));
			ventilator_PC.getInspiratoryPeriod().setValue(parameters.get("InspiratoryPeriod"), TimeUnit.s);
			ventilator_PC.getPositiveEndExpiratoryPressure().setValue(parameters.get("PositiveEndExpiratoryPressure"), PressureUnit.cmH2O);
			ventilator_PC.getRespirationRate().setValue(parameters.get("RespirationRate"), FrequencyUnit.Per_min);
			ventilator_PC.getInspiratoryPressure().setValue(parameters.get("InspiratoryPressure"), PressureUnit.cmH2O);	
			
			this.ventilator = ventilator_PC;
			
		}else if(mode == VentilationMode.CPAP){
			
			SEMechanicalVentilatorContinuousPositiveAirwayPressure ventilator_CPAP = new SEMechanicalVentilatorContinuousPositiveAirwayPressure();
			
			ventilator_CPAP.getFractionInspiredOxygen().setValue(parameters.get("FractionInspiredOxygen"));
			ventilator_CPAP.getDeltaPressureSupport().setValue(parameters.get("DeltaPressureSupport"), PressureUnit.cmH2O);
			ventilator_CPAP.getPositiveEndExpiratoryPressure().setValue(parameters.get("PositiveEndExpiratoryPressure"), PressureUnit.cmH2O);
			ventilator_CPAP.getSlope().setValue(parameters.get("Slope"), TimeUnit.s);	
			
			this.ventilator = ventilator_CPAP;
		}else{
			if(ventilator_EXTERNAL == null) ventilator_EXTERNAL = new SEMechanicalVentilation();
			
			if(parameters.containsKey("Volume")) ventilator_EXTERNAL.getFlow().setValue(parameters.get("Volume"), VolumePerTimeUnit.mL_Per_s);
			if(parameters.containsKey("Pressure")) ventilator_EXTERNAL.getPressure().setValue(parameters.get("Pressure"), PressureUnit.mmHg);
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
	
}
