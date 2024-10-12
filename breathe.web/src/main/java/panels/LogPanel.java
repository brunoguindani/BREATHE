package panels;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import app.App;
import java.util.Timer;
import java.util.TimerTask;

@Route("logpanel")
public class LogPanel extends VerticalLayout {
    
    private TextArea resultArea;

    public LogPanel(App app) {
        // Set layout properties
        setWidth("550px");
        setHeight("600px");
        setPadding(false);
        setMargin(false);
        
        // Initialize the text area
        resultArea = new TextArea();
        resultArea.setReadOnly(true); // Make it non-editable
        resultArea.setWidth("100%");
        resultArea.setHeight("100%");
        resultArea.getStyle().set("background-color", "white");
        resultArea.getStyle().set("font-family", "monospace");
        resultArea.getStyle().set("font-size", "12px");
        resultArea.setValue(""); // Initialize with empty value

        // Add the text area to the layout
        add(resultArea);

    }

    public void append(String message) {
    	UI.getCurrent().access(() -> {
    		resultArea.setValue(resultArea.getValue() + message + "\n");
    	});
    }

}
