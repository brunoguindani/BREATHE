package outputItems;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class InfoBox extends ItemDisplay {
	private static final long serialVersionUID = 1L;
	
	
	private Span unitLabel;

    public InfoBox(String title, String unit) {
        super(title, unit);
        
        // Imposta il layout principale
        setWidth("150px"); // Larghezza fissa
        setHeight("100px"); // Altezza fissa
        getStyle().set("background-color", "#282c34"); // Colore di sfondo scuro
        getStyle().set("border", "1px solid #61dafb"); // Bordo blu
        getStyle().set("border-radius", "8px"); // Angoli arrotondati
        getStyle().set("padding", "10px"); // Padding per spazio interno
        getStyle().set("box-shadow", "0 2px 10px rgba(0, 0, 0, 0.5)"); // Ombra per profondità

        // Configura l'etichetta dell'unità di misura
        unitLabel = new Span("(" + unit + ")");
        unitLabel.getStyle().set("color", "#61dafb"); // Colore dell'unità
        unitLabel.getStyle().set("font-size", "14px");
        
        // Aggiorna lo stile delle etichette del titolo e del valore
        titleLabel.getStyle().set("font-size", "16px");
        titleLabel.getStyle().set("color", "white"); // Colore del titolo

        valueLabel.getStyle().set("font-size", "24px");
        valueLabel.getStyle().set("color", "#61dafb"); // Colore del valore
        
        // Layout orizzontale per titolo e valore
        HorizontalLayout layout = new HorizontalLayout(titleLabel, valueLabel);
        layout.setAlignItems(Alignment.CENTER); // Allinea verticalmente al centro
        layout.setSpacing(true); // Spaziatura tra titolo e valore

        // Aggiungi al layout principale
        add(layout, unitLabel);
    }

    @Override
    public void addPoint(double x, double y) {
        y = Math.round(y * 1000.0) / 1000.0; 
        updateValue(y);
    }

    @Override
    public void clear() {
        updateValue(0);
    }
}
