package outputItems;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.page.Push;

@Route
@Push
public class LineChart extends ItemDisplay {
    private static final long serialVersionUID = 1L;

    private Div chartContainer; 

    public LineChart(String title, String unit) {
        super(title, unit);
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
        getElement().executeJs("initLineChart($0)", title);
    }

    @Override
    public void addPoint(double x, double y) {

        // Update the chart with the new point
        getElement().executeJs("updateLineChart($0, $1, $2)", 
            title, 
            x, 
            y);
    }

    @Override
    public void clear() {
    }
}
