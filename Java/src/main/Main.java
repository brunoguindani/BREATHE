package main;

import javax.swing.SwingUtilities;

import app.App;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}
