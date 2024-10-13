package panels;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import app.App;
import java.util.LinkedList;

@Route("logpanel")
public class LogPanel extends VerticalLayout {

    private TextArea resultArea;
    private LinkedList<String> logLines; // Usare una LinkedList per gestire le righe
    private static final int MAX_LINES = 50; // Numero massimo di righe

    public LogPanel(App app) {
        // Set layout properties
        setWidth("550px");
        setHeight("600px");
        setPadding(false);
        setMargin(false);

        // Inizializza il text area
        resultArea = new TextArea();
        resultArea.setReadOnly(true); // Rendi non modificabile
        resultArea.setWidth("100%");
        resultArea.setHeight("100%");
        resultArea.getStyle().set("background-color", "white");
        resultArea.getStyle().set("font-family", "monospace");
        resultArea.getStyle().set("font-size", "12px");
        resultArea.setValue(""); // Inizializza con valore vuoto

        // Inizializza la lista delle righe di log
        logLines = new LinkedList<>();

        // Aggiungi il text area al layout
        add(resultArea);
    }

    public void append(String message) {
        // Aggiungi il messaggio alla lista
        logLines.add(message);

        // Se la lista supera il numero massimo di righe, rimuovi la prima
        if (logLines.size() > MAX_LINES) {
            logLines.removeFirst();
        }

        // Aggiorna il contenuto del TextArea solo una volta
        resultArea.setValue(String.join("\n", logLines));
    }
}
