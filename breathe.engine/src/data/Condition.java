package data;

import java.util.HashMap;
import java.util.Map;

import com.kitware.pulse.cdm.bind.Physiology.eLungCompartment;
import com.kitware.pulse.cdm.conditions.SECondition;
import com.kitware.pulse.cdm.patient.conditions.*;
import com.kitware.pulse.cdm.properties.CommonUnits.VolumeUnit;

import utils.Pair;

public class Condition {
	
	/*
	 * To add a new condition:
	 * - add it when creating the Condition object client side
	 * - add the proper line in "generateSECondition"
	 */
	
	private String name;
	private Map<String, Double> parameters; 
	private SECondition condition; 
	
	/*
	 * Constructor from Parameters
	 */
	@SafeVarargs
	public Condition(String name, Pair<String, Double>... pairs) {
		
	    this.name = name;

	    //Receive a list of parameters as pairs String Double
	    //Doesn't check for duplicates cause it is completely handled client side
	    this.parameters = new HashMap<>(); 
	    for (Pair<String, Double> pair : pairs) {
	        this.parameters.put(pair.getKey(), pair.getValue());
	    }
	    
	    generateSECondition(); //generate and save SECondition object
	}
	
	/*
	 * Create the SECondition object
	 */
	private void generateSECondition() {

		switch (this.name) {
		    case "Chronic Anemia":
		        SEChronicAnemia condition = new SEChronicAnemia();	                        
		        condition.getReductionFactor().setValue(parameters.get("ReductionFactor"));	 
		        this.condition = condition;
		        break;
		        
		    case "ARDS":
		        SEAcuteRespiratoryDistressSyndrome ARDS = new SEAcuteRespiratoryDistressSyndrome();   
		        ARDS.getSeverity(eLungCompartment.LeftLung).setValue(parameters.get("LeftLungSeverity"));
		        ARDS.getSeverity(eLungCompartment.RightLung).setValue(parameters.get("RightLungSeverity"));	                        
		        this.condition = ARDS;
		        break;
		        
		    case "COPD":
		        SEChronicObstructivePulmonaryDisease COPD = new SEChronicObstructivePulmonaryDisease();
		        COPD.getBronchitisSeverity().setValue(parameters.get("BronchitisSeverity"));
		        COPD.getEmphysemaSeverity(eLungCompartment.LeftLung).setValue(parameters.get("LeftLungEmphysemaSeverity"));
		        COPD.getEmphysemaSeverity(eLungCompartment.RightLung).setValue(parameters.get("RightLungEmphysemaSeverity"));                        
		        this.condition = COPD;
		        break;   
		        
		    case "Pericardial Effusion":
		        SEChronicPericardialEffusion CPE = new SEChronicPericardialEffusion();	                        
		        CPE.getAccumulatedVolume().setValue(parameters.get("AccumulatedVolume"), VolumeUnit.mL);                        
		        this.condition = CPE;
		        break; 
		        
		    case "Renal Stenosis":
		        SEChronicRenalStenosis Stenosis = new SEChronicRenalStenosis();   
		        Stenosis.getLeftKidneySeverity().setValue(parameters.get("LeftKidneySeverity"));
		        Stenosis.getRightKidneySeverity().setValue(parameters.get("RightKidneySeverity"));                  
		        this.condition = Stenosis;
		        break;	  
		        
		    case "Chronic Ventricular Systolic Disfunction":
		        SEChronicVentricularSystolicDysfunction VSD = new SEChronicVentricularSystolicDysfunction();   
		        this.condition = VSD;
		        break;	
		        
		    case "Impaired Alveolar Exchange":
		        SEImpairedAlveolarExchange IAE = new SEImpairedAlveolarExchange();                           
		        this.condition = IAE;
		        break;		  
	
		    case "Pneumonia":
		        SEPneumonia Pneumonia = new SEPneumonia();   
		        Pneumonia.getSeverity(eLungCompartment.LeftLung).setValue(parameters.get("LeftLungSeverity"));
		        Pneumonia.getSeverity(eLungCompartment.RightLung).setValue(parameters.get("RightLungSeverity"));                        
		        this.condition = Pneumonia;
		        break;
		        
		    case "Pulmonary Fibrosis":
		        SEPulmonaryFibrosis fibrosis = new SEPulmonaryFibrosis();	                        
		        fibrosis.getSeverity().setValue(parameters.get("Severity"));	                        
		        this.condition = fibrosis;
		        break;
		        
		    case "Pulmonary Shunt":
		        SEPulmonaryShunt shunt = new SEPulmonaryShunt();	                        
		        shunt.getSeverity().setValue(parameters.get("Severity"));                      
		        this.condition = shunt;
		        break;	                        
        }
	}
	
	/*
	 * Return SECondition object
	 */
	public SECondition getCondition(){
		return condition;
	}

}
