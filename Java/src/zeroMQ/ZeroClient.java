package zeroMQ;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
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
    private boolean canDisconnect = false;
    
    private Map<String, Double> receivedDataMap;
    
    public ZeroClient() {
    	receivedDataMap = new HashMap<>();
    	
        frame = new JFrame("ZeroMQ Client");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevent default close operation
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Call the disconnect method before closing the window
            	if(disconnectButton.isEnabled())
            		disconnectFromServer();
                frame.dispose(); // Close the application window
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
        outputArea.setLineWrap(true); // Attiva il line wrap
        outputArea.setWrapStyleWord(true); // Avvolge le parole intere
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
                volumeButton.setEnabled(true);
                pressureButton.setEnabled(true);
                connectButton.setEnabled(true);
                disconnectButton.setEnabled(false);
                outputArea.append("Disconnected.\n");
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


    private void disconnectFromServer() {
        outputArea.append("Disconnecting from server...\n");
        isConnected = false;       	
    	if(canDisconnect) {
            try {
                // Send a disconnect message to the server
                socket.send("disconnect".getBytes(ZMQ.CHARSET), 0);        		
                // Wait for a short period to ensure the message is sent
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                if (communicationThread != null && communicationThread.isAlive()) {
                    communicationThread.interrupt();
                }
                if (socket != null) {
                    socket.close();
                }
                if (context != null) {
                    context.close();
                }
            }
    	}
    }
    
    
    //Non utilizzato per ora
    private void connectToServerWithTimer() {
        context = new ZContext();
        outputArea.append("Connecting to server...\n");

        socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://localhost:5555");
        isConnected = true;
        canDisconnect = false;

        communicationThread = new Thread(() -> {
            while (isConnected) {
            	canDisconnect = false;
            
                // Il client richiede i dati
                socket.send("requestData".getBytes(ZMQ.CHARSET), 0);
                byte[] reply = socket.recv(0);
                String receivedData = new String(reply, ZMQ.CHARSET);
                outputArea.append("Received: " + receivedData + "\n");
                
                // Processa i dati ricevuti e li salva nella mappa
                storeData(receivedData);
                
                // Con questi dati, decide quale valore di pressione o volume inviare
                double value = 0;
                if (selectedOption.equals("Volume"))
                    value = processVolume();
                else
                    value = processPressure();
                
                String request = selectedOption + ": " + value;
                outputArea.append("Sending: " + request + "\n");
                socket.send(request.getBytes(ZMQ.CHARSET), 0);

                // Riceviamo la conferma del server dopo aver inviato il valore
                reply = socket.recv(0);
                outputArea.append("Received: " + new String(reply, ZMQ.CHARSET) + "\n");

                try {
                    Thread.sleep(200);
                    canDisconnect = true;
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

            }
        });
        communicationThread.start();
    }

    

	private void storeData(String data) {
        String[] pairs = data.split(";");  // Divide i dati ricevuti in coppie chiave-valore
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");  // Divide ogni coppia in chiave e valore
            if (keyValue.length == 2) {
                try {
                    String key = keyValue[0].trim();
                    double value = Double.parseDouble(keyValue[1].trim());
                    receivedDataMap.put(key, value);  // Salva nella mappa
                } catch (NumberFormatException e) {
                    System.err.println("Errore nella conversione del valore: " + keyValue[1]);
                }
            }
        }
    }
    
	//Logica nel caso festisca la pressione
    private double processPressure() {
    	double pressure = 0;
		return pressure;
	}

    //Logica nel caso che gestisca il volume
	private double processVolume() {
		double volume = 0;
		
		if(receivedDataMap.get("TotalLungVolume") < 2000)
			volume = 20;
		
		return volume;
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ZeroClient::new);
    }
}
