package tests;

import javax.swing.SwingUtilities;

public class MainTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTest app = new AppTest();
            app.setVisible(true);
        });
    }
}
