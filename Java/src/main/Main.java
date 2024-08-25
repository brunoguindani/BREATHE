package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import app.App;

public class Main {
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
        	try { 
        		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
        	
            App app = new App();
            app.setVisible(true);
        });
    }
}
