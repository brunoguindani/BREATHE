package tests;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class LineChartPanelTest extends JPanel {
    private final java.util.List<Point> points = new ArrayList<>();
    private int maxXValue = 150;  // Valore massimo dell'asse X
    private int maxYValue = 100; // Valore massimo dell'asse Y
    private String title;

    public LineChartPanelTest(String title) {
        this.title = title;
        setPreferredSize(new Dimension(600, 300));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Disegna titolo
        if (title != null && !title.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(16f)); // Imposta la dimensione del font del titolo
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 30); // Centra il titolo in alto
        }

        // Disegna assi
        g2.drawLine(50, 250, 500, 250); // Asse X
        g2.drawLine(50, 50, 50, 250);   // Asse Y

        // Disegna etichette sugli assi
        drawAxisLabels(g2);

        // Disegna il grafico a linee
        if (points.size() > 1) {
        	int prevX = 50;
            for (int i = Math.max(0,points.size()-maxXValue); i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                //g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            	g2.drawLine(prevX, p1.y, p2.x - p1.x + prevX, p2.y);
            	prevX = p2.x - p1.x + prevX;           
            }
        }
    }

    private void drawAxisLabels(Graphics2D g2) {
        // Etichette asse X
    	/*
        for (int i = 0; i <= maxXValue; i++) {
            int x = 50 + (i * 30); // Spaziatura delle etichette
            int y = 265; // Posizione dell'etichetta lungo l'asse Y
            g2.drawString(String.valueOf(i), x, y);
        }*/

        // Etichette asse Y
        for (int i = 0; i <= maxYValue; i += 20) {
            int x = 30; // Posizione dell'etichetta lungo l'asse X
            int y = 250 - (i * 2); // Scala l'etichetta per adattarla al grafico
            g2.drawString(String.valueOf(i), x, y);
        }
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
        repaint(); // Aggiorna il grafico
    }
    
    public void clear() {
    	 points.clear(); // Clear the list of points
    	 repaint();  
    }
}
