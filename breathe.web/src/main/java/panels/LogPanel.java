package panels;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import app.App;
import java.util.Timer;
import java.util.TimerTask;

public class LogPanel extends VerticalLayout {

    private StringBuilder logBuilder;
    private TextArea resultArea;
    private App app;
    private Timer timer;
    private boolean hasNewData = false;

    public LogPanel(App app) {
        this.app = app;

        setWidthFull();
        setHeight("50vh");
        getStyle().set("background-color", "white");

        logBuilder = new StringBuilder();

        resultArea = new TextArea("Log Area");
        resultArea.setWidth("100%");
        resultArea.setHeight("100%");
        resultArea.setReadOnly(true);

        add(resultArea);

        // Inizializza il Timer per aggiornare periodicamente il TextArea
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Aggiorna l'interfaccia solo se ci sono nuovi dati
                if (hasNewData) {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        resultArea.setValue(logBuilder.toString());
                        hasNewData = false;
                    }));
                }
            }
        }, 0, 200); // Aggiorna ogni 200 millisecondi
    }

    public void append(String log) {
        logBuilder.append(log);
        hasNewData = true; // Segnala che ci sono nuovi dati
    }
}
