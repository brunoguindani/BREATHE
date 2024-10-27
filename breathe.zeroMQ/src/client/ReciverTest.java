package client;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;
import org.zeromq.ZContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class ReciverTest {
    private JFrame frame;
    private JButton connectButton, disconnectButton;
    private JTextArea outputArea;
    private ZMQ.Socket socketSub;
    private ZContext context;
    private boolean isConnected = false;
    private Thread communicationThread;

    private Map<String, Double> receivedDataMap;

    public ReciverTest() {
        receivedDataMap = new HashMap<>();

        frame = new JFrame("ZeroMQ Client");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
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

        outputArea = new JTextArea();
        panel.add(outputArea);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(40, 60, 300, 250);
        panel.add(scrollPane);

        connectButton.addActionListener(e -> {
            if (!isConnected) {
                connectToServer();
                connectButton.setEnabled(false);
                disconnectButton.setEnabled(true);
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


    private void connectToServer() {
        synchronized (this) {
            context = new ZContext();
//            socketPub = context.createSocket(SocketType.PUB);
//            socketPub.bind("tcp://*:5556");
            socketSub = context.createSocket(SocketType.SUB);
            socketSub.connect("tcp://localhost:5555");

        }
        
        outputArea.append("Connecting to server...\n");
        socketSub.subscribe("".getBytes(ZMQ.CHARSET));   

        synchronized (this) {
            isConnected = true;
        }

        communicationThread = new Thread(() -> {
            outputArea.append("Thread started. Entering while loop...\n");

            try {
                while (isConnected && !Thread.currentThread().isInterrupted()) {
                	
                    String receivedData = socketSub.recvStr(); 
                    storeData(receivedData);
                    outputArea.append(receivedData + "\n");
                }
            } catch (ZMQException ex) {
                if (ex.getErrorCode() == ZMQ.Error.ETERM.getCode()) {
                    outputArea.append("ZMQ context terminated.\n");
                } else if (ex.getErrorCode() == ZMQ.Error.EINTR.getCode()) {
                    outputArea.append("Socket interrupted during recv.\n");
                } else {
                    throw ex;
                }
            } finally {
                synchronized (this) {
                    if (socketSub != null) {
                        socketSub.close();
                        socketSub = null;
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

    private void storeData(String data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(data);
            
            JsonNode patientDataNode = rootNode.path("Patient Data");
            if (patientDataNode.isObject()) {

                patientDataNode.fieldNames().forEachRemaining(key -> {
                    try {
                        JsonNode valueNode = patientDataNode.get(key);
                        double value = valueNode.get("value").asDouble();
                        receivedDataMap.put(key, value);  
                    } catch (Exception e) {
                        outputArea.append("Errore nel parsing del valore per " + key + "\n");
                    }
                });
            }

            //JsonNode conditionsNode = rootNode.path("Conditions");
            // Could save conditions data

            JsonNode actionsNode = rootNode.path("Actions");
            actionsNode.fieldNames().forEachRemaining(actionKey -> {
                JsonNode actionNode = actionsNode.get(actionKey);
                actionNode.fieldNames().forEachRemaining(conditionKey -> {
                    //double severity = actionNode.get(conditionKey).asDouble();
                    // Could save actions Data as
                    // receivedDataMap.put(conditionKey, severity);
                });
            });

        } catch (Exception e) {
            outputArea.append("Errore nel parsing dei dati: " + e.getMessage() + "\n");
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReciverTest::new);
    }
}
