package interfaces;

import java.util.List;

import data.Condition;

public interface GuiCallback {
	
	/*
	 * INTERFACE TO LET SIMULATIONWORKER CALL GUI METHODS
	 */
	
    void stabilizationComplete(boolean enable);
    
    void logStringData(String data);
    
    void minilogStringData(String data);
    
    void logItemDisplayData(String data, double x, double y);
    
    void logPressureExternalVentilatorData(double pressure);
    
    void logVolumeExternalVentilatorData(double volume);
    
    void setInitialCondition(List<Condition> list);

	void setVentilatorsCondition(List<Condition> list);
    
}