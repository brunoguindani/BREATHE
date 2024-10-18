package outputItems;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.page.Push;

import java.util.ArrayList;
import java.util.List;

@Route
@Push
public class LineChart extends ItemDisplay {
    private static final long serialVersionUID = 1L;

    private Div chartContainer; 
    private List<Double> xPoints; // List to hold x points
    private List<Double> yPoints; // List to hold y points

    public LineChart(String title, String unit) {
        super(title, unit);
        xPoints = new ArrayList<>();
        yPoints = new ArrayList<>();
        initializeChart(title);
    }

    private void initializeChart(String title) {
        chartContainer = new Div();
        chartContainer.setId(title); // Set ID for the chart container
        chartContainer.getElement().getStyle().set("width", "150px");
        chartContainer.getElement().getStyle().set("height", "50px");

        // Add the container to the UI
        add(chartContainer);

        // Initialize the chart in JavaScript
        getElement().executeJs("initLineChart($0)", chartContainer.getElement());
    }

    @Override
    public void addPoint(double x, double y) {
        // Add the new point to the lists
        xPoints.add(x);
        yPoints.add(y);

        // Update the chart with the new point
        getElement().executeJs("updateLineChart($0, $1)", 
            chartContainer.getElement(), 
            x, 
            y);
    }
    

    @Override
    public void clear() {
        // Clear the points and reset the chart
        xPoints.clear();
        yPoints.clear();
    }
}
