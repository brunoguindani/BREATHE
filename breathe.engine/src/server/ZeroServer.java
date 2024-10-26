package server;

import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ZeroServer {
    private ZContext context;
    private ZMQ.Socket socket;
    private Thread receiveThread;
    private boolean running = false;
    private boolean connectionStable = false;
    private boolean disconnecting = false;
    
    private String selectedMode = null;
    private double volume;
    private double pressure;
    

    private JsonNode data;

    public void connect() throws Exception {
        context = new ZContext();
        socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://*:5555");
    }

    public void startReceiving() {
        running = true;
        receiveThread = new Thread(() -> {
            try {
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
    }

    public void receive() throws Exception {
        while (running && !Thread.currentThread().isInterrupted()) {
            byte[] reply = socket.recv(0);
            String receivedData = new String(reply, ZMQ.CHARSET);
            String messageJson = receivedData.trim();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            
            switch (jsonNode.get("message").asText()) {
                case "disconnect":
                    socket.send("{\"message\":\"Disconnected client\"}".getBytes(ZMQ.CHARSET), 0);
                    connectionStable = false;
                    disconnecting = true;
                    selectedMode = null;
                    break;

                case "requestData":
                    connectionStable = true;
                    disconnecting = false;
                    sendData();
                    break;

                case "input":
                	System.out.println(jsonNode.get("ventilatorType").asText());
                    if (jsonNode.get("ventilatorType").asText().equals("Volume")) {
                        connectionStable = true;
                        disconnecting = false;
                        selectedMode = "Volume";
                        volume = Double.parseDouble(jsonNode.get("value").asText());
                        socket.send("{\"message\":\"Volume received\"}".getBytes(ZMQ.CHARSET), 0);
                    } else if (jsonNode.get("ventilatorType").asText().equals("Pressure")) {
                        connectionStable = true;
                        disconnecting = false;
                        selectedMode = "Pressure";
                        pressure = Double.parseDouble(jsonNode.get("value").asText());
                        socket.send("{\"message\":\"Pressure received\"}".getBytes(ZMQ.CHARSET), 0);
                    } else {
                        socket.send("{\"message\":\"Unknown command\"}".getBytes(ZMQ.CHARSET), 0);
                    }
                    break;
            }
        }
    }

    private void stopReceiving() {
        running = false;		
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
            selectedMode = null;
        }
    }

    public void close() {
        stopReceiving();
        if (socket != null) {
            socket.close();
        }
        if (context != null) {
            context.close();
        }
    }

    private void sendData() {
    	if (data != null && !data.isEmpty()) {
    	    String tosend = data.toString();
    	    socket.send(tosend.getBytes(ZMQ.CHARSET), 0);  
    	} 
    }

    public boolean isConnectionStable() {
        return connectionStable;
    }
    
    public boolean isDisconnecting() {
    	return disconnecting;
    }

    public String getSelectedMode() {
        return selectedMode;
    }

    public double getVolume() {
        return volume;
    }

    public double getPressure() {
        return pressure;
    }

    public void setSimulationData(ArrayList<String> data) {
        try {
			this.data = convertToJson(data);
    	    System.out.println(this.data.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public JsonNode convertToJson(ArrayList<String> data) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode patientDataNode = objectMapper.createObjectNode();

        ObjectNode currentSection = patientDataNode;
        String[] lines = data.toArray(new String[0]);
        ObjectNode conditionNode = null; // Nodo per le condizioni

        for (String line : lines) {
            line = line.trim();

            if (line.contains(": ")) {
                String[] keyValue = line.split(": ", 2);
                String key = keyValue[0].replaceAll("[\\n\\t]", "").trim();
                String value = keyValue[1].trim();

                // Verifica se il valore rappresenta un oggetto condizionale (es. contiene "\n\t")
                if (value.contains("\n\t")) {
                    // Crea un nuovo nodo per la condizione
                    conditionNode = objectMapper.createObjectNode();
                    currentSection.set(key, conditionNode); // Aggiungi alla sezione corrente
                    currentSection = conditionNode; // Passa al nodo della condizione

                    // Separa le severità
                    String[] severities = value.split("\n\t");
                    for (String severity : severities) {
                        String[] severityKeyValue = severity.split(": ", 2);
                        if (severityKeyValue.length == 2) {
                            String severityKey = severityKeyValue[0].replaceAll("[\\n\\t]", "").trim();
                            String severityValue = severityKeyValue[1].trim();
                            try {
                                double numericValue = Double.parseDouble(severityValue);
                                conditionNode.put(severityKey, numericValue);
                            } catch (NumberFormatException e) {
                                conditionNode.put(severityKey, severityValue);
                            }
                        }
                    }
                } else {
                    // Se non è una condizione, gestiscilo normalmente
                    try {
                        double numericValue = Double.parseDouble(value);
                        currentSection.put(key, numericValue);
                    } catch (NumberFormatException e) {
                        currentSection.put(key, value);
                    }
                }
            } 
            else if (!line.isEmpty() && conditionNode != null) {
                // Gestisci linee non vuote sotto la condizione
                ObjectNode newSection = objectMapper.createObjectNode();
                currentSection.set(line.replaceAll("[\\n\\t]", "").trim(), newSection);
                currentSection = newSection; // Passa al nuovo sotto-nodo
            }
        }

        ObjectNode root = objectMapper.createObjectNode();
        root.set("Patient Data", patientDataNode);

        return root;
    }

    
}
