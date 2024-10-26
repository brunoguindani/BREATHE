package server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ZeroServer {
    private ZContext context;
    private ZMQ.Socket socketPub, socketSub;
    private Thread receiveThread;
    private boolean running = false;
    private boolean connectionStable = false;
    private boolean disconnecting = false;
    
    private String selectedMode = null;
    private double volume;
    private double pressure;
    

    private String data;

    public void connect() throws Exception {
        context = new ZContext();
        socketPub = context.createSocket(SocketType.PUB);
        socketPub.bind("tcp://*:5555");
        socketSub = context.createSocket(SocketType.SUB);
        socketSub.connect("tcp://localhost:5556");
        socketSub.subscribe("Client-".getBytes(ZMQ.CHARSET)); 
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
            byte[] reply = socketSub.recv();
            String receivedData = new String(reply, ZMQ.CHARSET);
            receivedData = receivedData.trim().replace("Client", "").trim();
            String messageJson = receivedData.trim();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(messageJson);

            switch (jsonNode.get("message").asText()) {
                case "disconnect":
                    socketPub.send("Server-{\"message\":\"Disconnected client\"}".getBytes(ZMQ.CHARSET), 0);
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
                    if (jsonNode.get("ventilatorType").asText().equals("Volume")) {
                        connectionStable = true;
                        disconnecting = false;
                        selectedMode = "Volume";
                        volume = Double.parseDouble(jsonNode.get("value").asText());
                        socketPub.send("Server {\"message\":\"Volume received\"}".getBytes(ZMQ.CHARSET), 0);
                    } else if (jsonNode.get("ventilatorType").asText().equals("Pressure")) {
                        connectionStable = true;
                        disconnecting = false;
                        selectedMode = "Pressure";
                        pressure = Double.parseDouble(jsonNode.get("value").asText());
                        socketPub.send("Server {\"message\":\"Pressure received\"}".getBytes(ZMQ.CHARSET), 0);
                    } else {
                    	socketPub.send("Server {\"message\":\"Unknown command\"}".getBytes(ZMQ.CHARSET), 0);
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
        if (socketPub != null) {
        	socketPub.close();
        }
        if (socketSub != null) {
        	socketSub.close();
        }
        if (context != null) {
            context.close();
        }
    }

    private void sendData() {
    	if (data != null && !data.isEmpty()) {
    		String toSend = "Server " + data;
    	    socketPub.send(toSend.getBytes(ZMQ.CHARSET), 0);  
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

    public void setSimulationData(String data) {
        try {
			this.data = data;
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
