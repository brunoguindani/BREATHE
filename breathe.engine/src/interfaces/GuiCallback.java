package interfaces;


public interface GuiCallback {
	
    void stabilizationComplete(boolean enable);
    
    void logStringData(String data);
    
    void logItemDisplayData(String data, double x, double y);
    
    void logPressureExternalVentilatorData(double pressure);
    
    void logVolumeExternalVentilatorData(double volume);

}