package interfaces;


public interface GuiCallback {
	
    void showStartingButton(boolean enable);
    
    void logStringData(String data);
    
    void logItemDisplayData(String data, double x, double y);
}