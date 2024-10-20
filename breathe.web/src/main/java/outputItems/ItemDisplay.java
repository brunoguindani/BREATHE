package outputItems;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class ItemDisplay extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	protected String title;
    protected String unit;
    protected double currentValue;


    public ItemDisplay(String title, String unit) {
        this.title = title;
        this.unit = unit;
        this.currentValue = 0.0;
        
        // Imposta il layout verticale
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "transparent");
    }

    // Metodi astratti per le sottoclassi
    public abstract void clear();

    public abstract void addPoint(double x, double y);
}
