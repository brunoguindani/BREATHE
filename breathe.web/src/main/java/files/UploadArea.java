package files;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class UploadArea extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	
	private final Upload uploadField;
    private final Span errorField;

    public UploadArea(File uploadFolder) {
        uploadField = new Upload(createFileReceiver(uploadFolder));
        uploadField.setMaxFiles(1);
        uploadField.setMaxFileSize(50 * 1024 * 1024);
        uploadField.setDropLabel(new Span("Drop file here"));
        
        uploadField.setAcceptedFileTypes("application/json");

        errorField = new Span();
        errorField.setVisible(false);
        errorField.getStyle().set("color", "red");

        uploadField.addFailedListener(e -> showErrorMessage(e.getReason().getMessage()));
        uploadField.addFileRejectedListener(e -> showErrorMessage(e.getErrorMessage()));

        add(uploadField, errorField);
    }

    public Upload getUploadField() {
        return uploadField;
    }

    public void hideErrorField() {
        errorField.setVisible(false);
    }

    private Receiver createFileReceiver(File uploadFolder) {
        return (MultiFileReceiver) (filename, mimeType) -> {
            File file = new File(uploadFolder, filename);
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }
        };
    }

    private void showErrorMessage(String message) {
        errorField.setVisible(true);
        errorField.setText(message);
    }
}