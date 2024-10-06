package data;

import java.util.ArrayList;

import com.kitware.pulse.cdm.actions.SEAdvanceTime;
import com.kitware.pulse.cdm.properties.CommonUnits.TimeUnit;
import com.kitware.pulse.cdm.scenario.SEScenario;

import utils.Pair;


public class Scenario {
	
	
	public Scenario() { }
	
    //Scenario will be exported to scenario.
    //an existing patient must be selected.
    public void createScenario(String patientFile,String scenarioName, ArrayList<Pair<Action, Integer>> actions) {
        SEScenario sce = new SEScenario();

        sce.setName(scenarioName);
        sce.setEngineState(patientFile);

        int seconds = 0;
        SEAdvanceTime adv = new SEAdvanceTime();
        adv.getTime().setValue(1, TimeUnit.s);
        if(actions != null) {
	        for (Pair<Action, Integer> action : actions) {
	            int target = action.getValue();
	
	            while (seconds < target) {
	                sce.getActions().add(adv);
	                seconds++;
	            }
	
	            sce.getActions().add(action.getKey().getAction());
	        }
        }

        sce.writeFile("./scenario/" + scenarioName + ".json");
    }
}
