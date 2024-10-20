package outputItems;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class InfoBox extends ItemDisplay {
    private static final long serialVersionUID = 1L;
    
    protected Span titleLabel;
    protected Span valueLabel;

    public InfoBox(String title, String unit) {
        super(title, unit);
        
        // Imposta il layout principale
        getStyle().set("padding", "0px"); // Padding per spazio interno
        setWidth("15vw");
        
        // Aggiorna lo stile delle etichette del titolo e del valore
        titleLabel = new Span(title + " (" + unit + ") ");
        titleLabel.getStyle().set("font-size", "16px");
        titleLabel.getStyle().set("color", "#ccc"); // Colore del titolo

        valueLabel = new Span();
        valueLabel.getStyle().set("font-size", "20px");
        valueLabel.getStyle().set("color", "#ccc"); // Colore del valore
        
        // Layout verticale per titolo e valore
        VerticalLayout layout = new VerticalLayout(titleLabel, valueLabel);
        
        // Aggiorna il layout principale
        layout.getStyle().set("border", "1px solid #ccc"); // Bordo
        layout.getStyle().set("border-radius", "8px"); // Angoli arrotondati
        layout.getStyle().set("background-color", "#222"); // Colore di sfondo
        layout.getStyle().set("padding", "2px"); // Padding interno
        layout.getStyle().set("margin", "2px"); // Margine esterno

        // Allineamento orizzontale e verticale
        layout.setAlignItems(Alignment.CENTER); // Centra verticalmente
        layout.setSpacing(false);
        
        // Aggiungi al layout principale
        addPoint(0.00, 0.00);
        add(layout);
    }

    @Override
    public void addPoint(double x, double y) {
        y = Math.round(y * 100.0) / 100.0; 
        valueLabel.setText(y + "");
        valueLabel.getElement().getStyle().set("font-style", "italic");  
    }

    @Override
    public void clear() {
        valueLabel.setText("0.0");
    }
}
