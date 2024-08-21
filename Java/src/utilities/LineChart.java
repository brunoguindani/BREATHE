package utilities;

import javax.swing.JPanel;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
public class LineChart extends JPanel {
    /**
	 * 
	 */
	private final List<Point> points = new ArrayList<>();
    private int maxXValue = 150;  
    private int maxYValue;  
    private int yStep;      
    private String title;

    public LineChart(String title) {
        this.title = title;
        this.setMaxY();
        setPreferredSize(new Dimension(600, 300));
        setBackground(Color.BLACK); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (title != null && !title.isEmpty()) {
            g2.setFont(g2.getFont().deriveFont(20f)); 
            g2.setColor(Color.WHITE); 
            int titleWidth = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - titleWidth) / 2, 30); 
        }

        g2.setColor(Color.WHITE); 
        g2.setStroke(new java.awt.BasicStroke(1.5f)); 
        g2.drawLine(50, 250, 550, 250); 
        g2.drawLine(50, 50, 50, 250);   

        g2.setColor(new Color(255, 255, 255, 80)); 
        g2.setStroke(new java.awt.BasicStroke(0.5f)); 

        for (int i = 50; i <= 550; i += 30) {
            g2.drawLine(i, 50, i, 250);
        }

        for (int i = 0; i <= maxYValue; i += yStep) {
            int y = 250 - (i * 200 / maxYValue);
            g2.drawLine(50, y, 550, y);
        }

        drawAxisLabels(g2);

        if (points.size() > 1) {
            int prevX = 50;
            g2.setColor(Color.GREEN);
            g2.setStroke(new java.awt.BasicStroke(2f));

            for (int i = Math.max(0, points.size() - maxXValue); i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2.drawLine(prevX, p1.y, p2.x - p1.x + prevX, p2.y);
                prevX = (int)(p2.x - p1.x + prevX);
            }
        }
    }

    private void drawAxisLabels(Graphics2D g2) {
        g2.setColor(Color.WHITE); 
        g2.setFont(g2.getFont().deriveFont(12f)); 

        for (int i = 0; i <= maxXValue; i += 10) {
            int x = 50 + (i * 3); 
            int y = 265; 
            g2.drawString(String.valueOf(i), x - 10, y + 15);
        }

        for (int i = 0; i <= maxYValue; i += yStep) {
            int x = 30; 
            int y = 250 - (i * 200 / maxYValue); 
            g2.drawString(String.valueOf(i), x - 10, y + 5);
        }
    }

    public void addPoint(int x, int y) {
    	points.add(new Point(x, y));
        System.out.println("Adding points: "+this.title+"("+ x + ";"+ y+")");
        repaint(); 
    }

    public void clear() {
        points.clear(); 
        repaint();
    }
    
    public void setMaxY(){
        switch(this.title){
            case "Heart Rate":
                this.maxYValue = 150;
                this.yStep = 20;  
                break;
            case "Total Lung Volume":
                this.maxYValue = 3000;
                this.yStep = 400;  
                break;
            case "Respiratory Rate":
                this.maxYValue = 30;
                this.yStep = 5;   
                break;
            case "ECG":
                this.maxYValue = 7000;
                this.yStep = 500;   
                break;
            default:
                this.maxYValue = 100;
                this.yStep = 20;  
                break;
        }
    }
    
    public int getMaxY() {
    	return this.maxYValue;
    }
}
