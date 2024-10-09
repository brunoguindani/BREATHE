package interfaces;

import java.util.List;

import data.Condition;

public interface GuiCallback {
	
    void stabilizationComplete(boolean enable);
    
    void logStringData(String data);
    
    void logItemDisplayData(String data, double x, double y);
    
    void logPressureExternalVentilatorData(double pressure);
    
    void logVolumeExternalVentilatorData(double volume);
    
    void setInitialCondition(List<Condition> list);
}