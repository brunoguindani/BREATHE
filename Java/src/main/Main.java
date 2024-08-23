package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import app.App;

public class Main {
    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
        	try { 
//        		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        		UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        		UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//        		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
        	
            App app = new App();
            app.setVisible(true);
        });
    }
}
