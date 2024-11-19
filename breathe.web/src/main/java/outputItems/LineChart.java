package outputItems;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.page.Push;

@Route
@Push
public class LineChart extends ItemDisplay {
    private static final long serialVersionUID = 1L;

    private final Chart chart; 
    private final ListSeries series; 

    public LineChart(String title, String unit) {
        super(title, unit);

        chart = new Chart(ChartType.LINE);
        chart.setWidthFull();
        chart.setHeight("300px");
        chart.getConfiguration().getChart().setStyledMode(true);
        
        series = new ListSeries();
        chart.getConfiguration().addSeries(series);

        add(chart);
    }

    @Override
    public void addPoint(double x, double y) {
        series.addData(y);
    }

    @Override
    public void clear() {
    }
}
