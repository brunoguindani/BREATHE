package outputItems;

import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.*;
import com.github.appreciated.apexcharts.config.chart.builder.ZoomBuilder;
import com.github.appreciated.apexcharts.config.stroke.Curve;
import com.github.appreciated.apexcharts.config.xaxis.TickPlacement;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route
public class LineChart extends ItemDisplay {
    private static final long serialVersionUID = 1L;
    
    private ApexCharts apexChart; 
    private List<Double> xData = new ArrayList<>(); 
    private List<Double> yData = new ArrayList<>(); 
    
    //change!!!
    private static final int UPDATE_THRESHOLD = 10;
    private int pointCount = 0;

    public LineChart(String title, String unit) {
        super(title, unit);
        initializeChart();
    }

    private void initializeChart() {
        apexChart = ApexChartsBuilder.get()
            .withChart(ChartBuilder.get()
                .withZoom(ZoomBuilder.get().withEnabled(false).build())
                .build())
            .withStroke(StrokeBuilder.get()
                .withCurve(Curve.STRAIGHT)
                .build())
            .withGrid(GridBuilder.get()
                .withShow(false)
                .build())
            .withXaxis(XAxisBuilder.get()
                .withMin(0.0)    
                .withMax(400.0)  
                .withCategories() 
                .withTickPlacement(TickPlacement.BETWEEN)
                .build())
            .withYaxis(YAxisBuilder.get()
                .withShow(false) 
                .build())
            .withSeries(new Series<>("Data", yData.toArray())) 
            .build();

        apexChart.setWidth("600px");  
        apexChart.setHeight("250px"); 

        add(apexChart);
    }

    @Override
    public void addPoint(double x, double y) {
        if (xData.size() >= 400) {
            xData.remove(0);
            yData.remove(0);
        }

        xData.add(x);
        yData.add(y);
        updateValue(y);

        pointCount++;
        if (pointCount >= UPDATE_THRESHOLD) {
            updateChart();
            pointCount = 0;
        }

    }

    private void updateChart() {
        String[] categories = xData.stream().map(String::valueOf).toArray(String[]::new);
        
        apexChart.updateSeries(new Series<>("Data", yData.toArray()));
        apexChart.setXaxis(XAxisBuilder.get().withCategories(categories).build());
    }

    @Override
    public void clear() {
        xData.clear();
        yData.clear();
        pointCount = 0;
        updateChart();
    }
}
