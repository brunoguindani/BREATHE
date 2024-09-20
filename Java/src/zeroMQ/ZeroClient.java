package zeroMQ;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZContext;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class ZeroClient {
    private JFrame frame;
    private JButton connectButton, disconnectButton;
    private JRadioButton volumeButton, pressureButton;
    private JTextArea outputArea;

    private ZMQ.Socket socket;
    private ZContext context;
    private String selectedOption;
    private boolean isConnected = false;
    private Thread communicationThread;
    @SuppressWarnings("unused")
	private boolean canDisconnect = false;

    private Map<String, Double> receivedDataMap;

    private int temp = 0;

    public ZeroClient() {
        receivedDataMap = new HashMap<>();

        frame = new JFrame("ZeroMQ Client");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (disconnectButton.isEnabled()) {
                    disconnectFromServer();
                }
                frame.dispose();
            }
        });

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        connectButton = new JButton("Connect");
        connectButton.setBounds(50, 20, 100, 25);
        connectButton.setBackground(new Color(0, 122, 255));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        panel.add(connectButton);

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setBounds(200, 20, 120, 25);
        disconnectButton.setBackground(new Color(255, 59, 48));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setFocusPainted(false);
        disconnectButton.setEnabled(false);
        panel.add(disconnectButton);

        outputArea = new JTextArea();
        outputArea.setBounds(50, 120, 300, 180);
        panel.add(outputArea);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 120, 300, 180);
        panel.add(scrollPane);

        volumeButton = new JRadioButton("Volume");
        volumeButton.setBounds(50, 60, 100, 25);
        volumeButton.setSelected(true);
        panel.add(volumeButton);

        pressureButton = new JRadioButton("Pressure");
        pressureButton.setBounds(160, 60, 100, 25);
        panel.add(pressureButton);

        ButtonGroup group = new ButtonGroup();
        group.add(volumeButton);
        group.add(pressureButton);

        connectButton.addActionListener(e -> {
            if (!isConnected) {
                selectedOption = volumeButton.isSelected() ? "Volume" : "Pressure";
                connectToServerWithTimer();
                volumeButton.setEnabled(false);
                pressureButton.setEnabled(false);
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
            }
        });

        disconnectButton.addActionListener(e -> {
            if (isConnected) {
                disconnectFromServer();
                //connectToServerWithTimer();
                //disconnectFromServer();
                volumeButton.setEnabled(true);
                pressureButton.setEnabled(true);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                outputArea.append("Disconnected.\n");
                frame.dispose(); 
            }
        });

        outputArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        });
    }

    private void connectToServerWithTimer() {
        synchronized (this) {
            context = new ZContext();
            socket = context.createSocket(SocketType.REQ);
        }

        outputArea.append("Connecting to server...\n");

        try {
            socket.connect("tcp://localhost:5555");
        } catch (ZMQException ex) {
            outputArea.append("Failed to connect to server: " + ex.getMessage() + "\n");
            return; // Esci se la connessione fallisce
        }

        synchronized (this) {
            isConnected = true;
            canDisconnect = false;
        }

        // Ricrea il thread di comunicazione
        communicationThread = new Thread(() -> {
            outputArea.append("Thread started. Entering while loop...\n"); // Log di debug
            try {
                while (isConnected && !Thread.currentThread().isInterrupted()) {
                    synchronized (this) {
                        canDisconnect = false;
                    }

                    outputArea.append("Thread is running inside the while loop...\n"); // Log di debug

                    socket.send("requestData".getBytes(ZMQ.CHARSET), 0);
                    outputArea.append("Request Sent\n"); // Log di debug
                    
                    byte[] reply = socket.recv(0);  
                    String receivedData = new String(reply, ZMQ.CHARSET);
                    outputArea.append("Received: " + receivedData + "\n");

                    outputArea.append("Data Received\n"); // Log di debug
                    
                    storeData(receivedData);

                    double value = selectedOption.equals("Volume") ? processVolume() : processPressure();

                    String request = selectedOption + ": " + value;
                    outputArea.append("Sending: " + request + "\n");
                    socket.send(request.getBytes(ZMQ.CHARSET), 0);

                    reply = socket.recv(0);
                    outputArea.append("Received: " + new String(reply, ZMQ.CHARSET) + "\n");

                    synchronized (this) {
                        canDisconnect = true;
                    }

                    Thread.sleep(200);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); // Reimposta lo stato di interruzione del thread
                outputArea.append("Thread was interrupted.\n"); // Log di debug
            } catch (ZMQException ex) {
                if (ex.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                    outputArea.append("ZMQ context terminated.\n");
                } else if (ex.getErrorCode() == ZMQ.Error.EINTR.getCode()) {
                    outputArea.append("Socket interrupted during recv.\n");
                } else {
                    throw ex;  // Propaga eccezioni non gestite
                }
            } finally {
                synchronized (this) {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                    if (context != null) {
                        context.close();
                        context = null;
                    }
                }
                outputArea.append("Resources cleaned up after interruption.\n");
            }
        });

        communicationThread.start();
    }

    private void disconnectFromServer() {
        outputArea.append("Disconnecting from server...\n");

        new Thread(() -> {
            synchronized (this) {
                isConnected = false; // Segnala al thread di comunicazione di fermarsi
            }
           
            try {
                // Invia il messaggio di disconnessione al server
                if (socket != null) {
                    socket.send("disconnect".getBytes(ZMQ.CHARSET), 0);
                    outputArea.append("Disconnect message sent to server.\n");
                }
            } catch (ZMQException ex) {
                outputArea.append("Failed to send disconnect message: " + ex.getMessage() + "\n");
            }

            // Attendi la terminazione del thread di comunicazione
            if (communicationThread != null) {
                try {
                    communicationThread.join(); // Attendi la fine del thread
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Pulizia delle risorse
            synchronized (this) {
                if (socket != null) {
                    socket.close();
                    socket = null; // Azzera il riferimento per sicurezza
                }
                if (context != null) {
                    context.close();
                    context = null; // Azzera il riferimento per sicurezza
                }
            }

            outputArea.append("Disconnected from server and cleaned up resources.\n");
        }).start();
    }



    private void storeData(String data) {
        String[] pairs = data.split(";");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                try {
                    String key = keyValue[0].trim();
                    double value = Double.parseDouble(keyValue[1].trim());
                    receivedDataMap.put(key, value);
                } catch (NumberFormatException e) {
                    System.err.println("Error converting value: " + keyValue[1]);
                }
            }
        }
    }

    private double processPressure() {
        // Parameters
        int respiratoryRate = 12; // RR in cicli al minuto
        double ieRatio = 2.0 / 3.0; // I:E ratio
        double pinsp = 20.0; // Pressure during inspiration (e.g., in cm H2O)
        double peep = 5.0; // Positive End-Expiratory Pressure (e.g., in cm H2O)

        // Calculate total cycle duration in seconds
        double totalCycleDuration = 60.0 / respiratoryRate; // in seconds
        double inspiratoryTime = totalCycleDuration * (ieRatio / (1 + ieRatio)); // duration of inspiration
        double expiratoryTime = totalCycleDuration * (1 / (1 + ieRatio)); // duration of expiration

        // Simulate one complete cycle (inspiration followed by expiration)
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime % (long) (totalCycleDuration * 1000); // elapsed time in the current cycle

        double pressure;
        if (elapsedTime < inspiratoryTime * 1000) {
            // Inspiration phase
            pressure = pinsp; // Pressure during inspiration
        } else {
            // Expiration phase
            pressure = peep; // Pressure during expiration
        }

        return pressure; // Return the current pressure value
    }


    private double processVolume() {
        double volume = 0;
        if (receivedDataMap.getOrDefault("TotalLungVolume", 0.0) < 2000) {
            volume = 20;
        }

        volume = temp + 1;
        temp++;
        if (temp > 10) temp = 0;

        return volume;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ZeroClient::new);
    }
}
