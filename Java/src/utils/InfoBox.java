package utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.RoundRectangle2D;

import com.kitware.pulse.cdm.properties.CommonUnits.Unit;

public class InfoBox extends ItemDisplay {
    private static final long serialVersionUID = 1L;
    
    private static final Color BACKGROUND_COLOR = Color.BLACK;  // Colore di sfondo scuro
    private static final Color BORDER_COLOR = Color.WHITE;  // Colore del bordo
    private static final Color TEXT_COLOR = Color.WHITE;    // Colore del testo

    public InfoBox(String title, Unit unit) {
        super(title, unit, new Dimension(150, 150));  // Imposta le dimensioni di InfoBox
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /*
        // Disegna uno sfondo arrotondato
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Disegna il bordo arrotondato
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth(), getHeight(), 20, 20);*/

        // Disegna il titolo al centro
        if (title != null && !title.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(16f));
            g2.setColor(TEXT_COLOR);
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 25);  // Posiziona il titolo
        }

        // Disegna il valore corrente al centro
        g2.setFont(g2.getFont().deriveFont(24f));
        g2.setColor(TEXT_COLOR);
        String valueStr = String.format("%.2f", currentValue);
        int valueWidth = g2.getFontMetrics().stringWidth(valueStr);
        g2.drawString(valueStr, (getWidth() - valueWidth) / 2, getHeight() / 2);  // Posiziona il valore

        // Disegna l'unità di misura sotto il valore
        if (unit != null) {
            g2.setFont(g2.getFont().deriveFont(14f));
            String unitStr = "(" + unit.toString() + ")";
            int unitWidth = g2.getFontMetrics().stringWidth(unitStr);
            g2.drawString(unitStr, (getWidth() - unitWidth) / 2, getHeight() / 2 + 30);  // Posiziona l'unità
        }
    }

    @Override
    public void addPoint(double x, double y) {
        currentValue = y;  
        repaint(); 
    }

    @Override
    public void clear() {
        currentValue = 0;  
        repaint(); 
    }
}
