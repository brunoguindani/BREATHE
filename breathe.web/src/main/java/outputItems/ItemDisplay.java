package outputItems;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class ItemDisplay extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	protected String title;
    protected String unit;
    protected double currentValue;
    
    protected Span titleLabel;
    protected Span valueLabel;

    public ItemDisplay(String title, String unit) {
        this.title = title;
        this.unit = unit;
        this.currentValue = 0.0;
        
        // Imposta il layout verticale
        setPadding(true);
        setSpacing(true);
        getStyle().set("background-color", "black");
        
        // Crea i componenti comuni
        titleLabel = new Span(title);
        titleLabel.getStyle().set("color", "white");
        valueLabel = new Span("" + currentValue);
        valueLabel.getStyle().set("color", "white");

        // Aggiungi i componenti al layout
        add(titleLabel, valueLabel);
    }

    // Metodo per aggiornare il valore e visualizzarlo
    public void updateValue(double newValue) {
        this.currentValue = newValue;
        valueLabel.setText("" + newValue);
    }

    // Metodi astratti per le sottoclassi
    public abstract void clear();

    public abstract void addPoint(double x, double y);
}
