package panels;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import app.App;
import java.util.LinkedList;
/*
    CLASS NOT USED
*/

@Route("logpanel")
public class LogPanel extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    
	private TextArea resultArea;
    private LinkedList<String> logLines; 
    private static final int MAX_LINES = 30; 

    public LogPanel(App app) {
		getStyle().set("border", "1px solid #ccc"); 
        setPadding(false);
        setMargin(false);

        resultArea = new TextArea();
        resultArea.setReadOnly(true); 
        resultArea.getStyle().set("background-color", "white");
        resultArea.getStyle().set("font-family", "monospace");
        resultArea.getStyle().set("font-size", "12px");
        resultArea.setValue(""); 

        logLines = new LinkedList<>();

        add(resultArea);
    }

    public void append(String message) {
        logLines.add(message);

        if (logLines.size() > MAX_LINES) {
            logLines.removeFirst();
        }

        resultArea.setValue(String.join("\n", logLines));
    }
}
