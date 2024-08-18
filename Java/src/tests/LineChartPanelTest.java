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
    private final List<Point> points = new ArrayList<>();
    private int maxXValue = 150;  // Valore massimo dell'asse X
    private int maxYValue;  // Valore massimo dell'asse Y
    private int yStep;      // Step per l'asse Y
    private String title;

    public LineChartPanelTest(String title) {
        this.title = title;
        this.setMaxY();
        setPreferredSize(new Dimension(600, 300));
        setBackground(Color.BLACK);  // Imposta sfondo nero
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Disegna il titolo
        if (title != null && !title.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(20f)); // Font più grande e visibile
            g2.setColor(Color.WHITE); // Colore bianco per il titolo
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 30); // Centra il titolo in alto
        }

        // Disegna assi
        g2.setColor(Color.WHITE); // Colore bianco per gli assi
        g2.setStroke(new java.awt.BasicStroke(1.5f)); // Spessore più evidente per gli assi
        g2.drawLine(50, 250, 550, 250); // Asse X
        g2.drawLine(50, 50, 50, 250);   // Asse Y

        // Disegna la griglia
        g2.setColor(new Color(255, 255, 255, 80)); // Colore bianco semi-trasparente per la griglia
        g2.setStroke(new java.awt.BasicStroke(0.5f)); // Spessore sottile per la griglia

        // Griglia verticale
        for (int i = 50; i <= 550; i += 30) {
            g2.drawLine(i, 50, i, 250);
        }

        // Griglia orizzontale (calcolata dinamicamente)
        for (int i = 0; i <= maxYValue; i += yStep) {
            int y = 250 - (i * 200 / maxYValue); // Scala la griglia in base a maxY
            g2.drawLine(50, y, 550, y);
        }

        // Disegna etichette sugli assi
        drawAxisLabels(g2);

        // Disegna il grafico a linee
        if (points.size() > 1) {
            int prevX = 50;
            g2.setColor(Color.GREEN); // Colore bianco per la linea del grafico
            g2.setStroke(new java.awt.BasicStroke(2f)); // Spessore della linea

            for (int i = Math.max(0, points.size() - maxXValue); i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2.drawLine(prevX, p1.y, p2.x - p1.x + prevX, p2.y);
                prevX = p2.x - p1.x + prevX;
            }
        }
    }

    private void drawAxisLabels(Graphics2D g2) {
        g2.setColor(Color.WHITE); // Colore bianco per le etichette
        g2.setFont(g2.getFont().deriveFont(12f)); // Font più piccolo e leggibile

        // Etichette asse X
        for (int i = 0; i <= maxXValue; i += 10) {
            int x = 50 + (i * 3); // Spaziatura delle etichette
            int y = 265; // Posizione dell'etichetta lungo l'asse Y
            g2.drawString(String.valueOf(i), x - 10, y + 15);
        }

        // Etichette asse Y (calcolate dinamicamente)
        for (int i = 0; i <= maxYValue; i += yStep) {
            int x = 30; // Posizione dell'etichetta lungo l'asse X
            int y = 250 - (i * 200 / maxYValue); // Scala l'etichetta per adattarla al grafico
            g2.drawString(String.valueOf(i), x - 10, y + 5);
        }
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
        repaint(); // Aggiorna il grafico
    }

    public void clear() {
        points.clear(); // Pulisce la lista dei punti
        repaint();
    }
    
    public void setMaxY(){
        switch(this.title){
            case "Heart Rate":
                this.maxYValue = 150;
                this.yStep = 20;  // Imposta il salto per l'asse Y
                break;
            case "Total Lung Volume":
                this.maxYValue = 3000;
                this.yStep = 400;  // Imposta il salto per l'asse Y
                break;
            case "Respiratory Rate":
                this.maxYValue = 30;
                this.yStep = 5;   // Imposta il salto per l'asse Y
                break;
            default:
                this.maxYValue = 100;
                this.yStep = 20;  // Imposta il salto di default
                break;
        }
    }
    
    public int getMaxY() {
    	return this.maxYValue;
    }
}
