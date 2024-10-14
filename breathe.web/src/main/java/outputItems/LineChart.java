package outputItems;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route
public class LineChart extends ItemDisplay {

    private final List<Point> points = new ArrayList<>();
    private int maxXValue = 400;  
    private int maxYValue;  
    private int minYValue = 0;  
    private int yStep;       
    private Div chartContainer;
    private Div yLabelContainer;

    public LineChart(String title, String unit) {
        super(title, unit);  // Call the parent constructor
        setMaxY();
        
        chartContainer = new Div();
        chartContainer.setWidth("450px");
        chartContainer.setHeight("300px");
        chartContainer.getStyle().set("position", "relative");
        chartContainer.getStyle().set("border", "1px solid black");

        yLabelContainer = new Div();
        yLabelContainer.setWidth("50px");
        yLabelContainer.getStyle().set("position", "absolute");
        yLabelContainer.getStyle().set("bottom", "50px");
        
        Button addButton = new Button("Add Point", e -> addRandomPoint());
        Button clearButton = new Button("Clear", e -> clear());
        add(chartContainer, yLabelContainer);
        
        drawChart();
    }

    private void addRandomPoint() {
    	double x = 0;
    	if(!points.isEmpty()) x = ((points.get(points.size()-1).x) - 60)*50/maxXValue + 1; 
    	double y = 50;
    	if(!points.isEmpty()) y = 0.01 + (1 + (250 - (points.get(points.size()-1).y)) / 250.0) * (maxYValue - minYValue) + minYValue;

        addPoint(x, y);
    }

    @Override
    public void addPoint(double x, double y) {
        if (minYValue > y) {
            minYValue = (int) Math.floor(y);
        }
        if (maxYValue < y) {
            maxYValue = (int) Math.floor(y);
        }

        int x1 = (int) (x * maxXValue/50 + 60);
        int y1 = (int) (250 + (1 - (y - minYValue) / (maxYValue - minYValue)) * (500 - 250));
        
        points.add(new Point(x1, y1));
        for(Point point: points) {
            //System.out.print(point.x + "," + point.y + " - ");  	
        }
        //System.out.println("");
        drawChart();
    }

    private void setMaxY() {
        switch (this.title) {
            case "Heart Rate":
                this.maxYValue = 100;
                this.minYValue = 0;
                this.yStep = 20;  
                break;
            case "Total Lung Volume":
                this.maxYValue = 100;
                this.minYValue = 0;
                this.yStep = 20;  
                break;
            default:
                this.maxYValue = 100;
                this.minYValue = 0;
                this.yStep = 20;  
                break;
        }
    }

    @Override
    public void clear() {
        points.clear(); 
        setMaxY();
        drawChart();
    }

    private void drawChart() {
        chartContainer.removeAll(); 
        yLabelContainer.removeAll();

        // Disegna gli assi
        Div xAxis = new Div();
        xAxis.getStyle().set("position", "absolute");
        xAxis.getStyle().set("bottom", "50px");
        xAxis.getStyle().set("left", "50px");
        xAxis.getStyle().set("width", "400px");
        xAxis.getStyle().set("height", "1px");
        xAxis.getStyle().set("background-color", "white");
        chartContainer.add(xAxis);
        
        Div yAxis = new Div();
        yAxis.getStyle().set("position", "absolute");
        yAxis.getStyle().set("left", "50px");
        yAxis.getStyle().set("bottom", "50px");
        yAxis.getStyle().set("width", "1px");
        yAxis.getStyle().set("height", "200px");
        yAxis.getStyle().set("background-color", "white");
        chartContainer.add(yAxis);

        // Disegna le linee della griglia orizzontali
        for (int i = maxYValue; i >= minYValue; i -= yStep) {
            int yGridPosition = 250 - ((i - minYValue) * 200 / (maxYValue - minYValue));
            
            // Disegna la linea della griglia orizzontale
            Div gridLine = new Div();
            gridLine.getStyle().set("position", "absolute");
            gridLine.getStyle().set("background-color", "lightgray");
            gridLine.getStyle().set("height", "1px");
            gridLine.getStyle().set("width", "400px");
            gridLine.getStyle().set("left", "50px");
            gridLine.getStyle().set("bottom", (yGridPosition - 0) + "px");
            chartContainer.add(gridLine);

            // Disegna le etichette sull'asse Y usando Span
            Span span = new Span(String.valueOf(maxYValue - i));
            span.getStyle().set("position", "absolute");
            span.getStyle().set("color", "white");
            span.getStyle().set("bottom", (yGridPosition - 0) + "px");
            span.getStyle().set("left", "10px");
            chartContainer.add(span);
        }

        // Disegna le linee della griglia verticale
        for (int i = 0; i < maxXValue; i += 40) {  // Vertical lines every 10 units
            int xGridPosition = (i + 50); // Convert X value to pixel position

            // Disegna la linea della griglia verticale
            Div verticalGridLine = new Div();
            verticalGridLine.getStyle().set("position", "absolute");
            verticalGridLine.getStyle().set("background-color", "lightgray");
            verticalGridLine.getStyle().set("height", "250px"); // Adjust to the height of your chart
            verticalGridLine.getStyle().set("width", "1px");
            verticalGridLine.getStyle().set("left", xGridPosition + "px");
            verticalGridLine.getStyle().set("bottom", "50px"); // Align with the bottom of the chart
            chartContainer.add(verticalGridLine);
        }

        if (points.size() > 1) {
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                
                int startX = p1.x;
                int startY = p1.y;

                // Disegna il punto di partenza
                Div startPoint = new Div();
                startPoint.getStyle().set("position", "absolute");
                startPoint.getStyle().set("background-color", "green");
                startPoint.getStyle().set("width", "8px");  // Dimensione del punto
                startPoint.getStyle().set("height", "8px");
                startPoint.getStyle().set("left", (startX - 8) + "px");  // Centra il punto
                startPoint.getStyle().set("bottom", (startY - 250 - 8) + "px");  // Centra il punto
                chartContainer.add(startPoint);
            }
        }
    }

    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
