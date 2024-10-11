package panels;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import app.App;

@Route("log-panel")
public class LogPanel extends VerticalLayout {

    private StringBuilder logBuilder; // Utilizza StringBuilder per costruire i log
    private TextArea resultArea; // Usa TextArea per visualizzare i log
    private App app;

    public LogPanel(App app) {
        this.app = app;

        // Impostazioni del layout principale
        setWidthFull();  // Imposta la larghezza al 100%
        setHeight("50vh");  // Imposta l'altezza al 50% della viewport
        getStyle().set("background-color", "white");  // Colore di sfondo

        // Inizializza il StringBuilder
        logBuilder = new StringBuilder();

        // Creazione del TextArea
        resultArea = new TextArea("Log Area");
        resultArea.setWidth("100%");  // Imposta la larghezza al 100%
        resultArea.setHeight("100%"); // Imposta l'altezza al 100%
        resultArea.setReadOnly(true); // Imposta come solo lettura

        // Aggiungi il TextArea al layout
        add(resultArea);
    }

    public void append(String log) {
        logBuilder.append(log);

        resultArea.setValue(logBuilder.toString()); 
    }
}
