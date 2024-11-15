package outputItems;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.page.Push;

@Route
@Push
public class LineChart extends ItemDisplay {
	/*
	 * Item connected to javascript to display dynamic line charts
	 */
    private static final long serialVersionUID = 1L;


    public LineChart(String title, String unit) {
        super(title, unit);
        initializeChart(title); 
    }

    private void initializeChart(String title) {
    }

    @Override
    public void addPoint(double x, double y) {
    }

    @Override
    public void clear() {
    }
}
