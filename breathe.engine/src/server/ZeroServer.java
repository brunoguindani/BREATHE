package server;

import java.util.ArrayList;

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
    

    private ArrayList<String> data;

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
            System.out.println("Received: [" + receivedData + "]");

            switch (receivedData) {
                case "disconnect":
                    System.out.println("Disconnect message received.");
                    socket.send("Disconnected client.".getBytes(ZMQ.CHARSET), 0);
                    connectionStable = false;
                    disconnecting = true;
                    selectedMode = null;
                    break;

                case "requestData":
                    connectionStable = true;
                    disconnecting = false;
                    sendData();
                    break;

                default:
                    if (receivedData.startsWith("Volume")) {
                        connectionStable = true;
                        disconnecting = false;
                        selectedMode = "Volume";
                        volume = Double.parseDouble(receivedData.split(": ")[1]);
                        socket.send("Volume received".getBytes(ZMQ.CHARSET), 0);
                    } else if (receivedData.startsWith("Pressure")) {
                        connectionStable = true;
                        disconnecting = false;
                        selectedMode = "Pressure";
                        pressure = Double.parseDouble(receivedData.split(": ")[1]);
                        socket.send("Pressure received".getBytes(ZMQ.CHARSET), 0);
                    } else {
                        socket.send("Unknown command".getBytes(ZMQ.CHARSET), 0);
                    }
                    break;
            }
        }
    }

    private void stopReceiving() {
        running = false;		
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
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
            StringBuilder dataBuilder = new StringBuilder();
            for (String datum : data) {
                dataBuilder.append(datum).append(";");
            }
            if (dataBuilder.length() > 0) {
                dataBuilder.setLength(dataBuilder.length() - 1);
            }
            String tosend = "Patient Data:" + dataBuilder.toString();
            socket.send(tosend.getBytes(ZMQ.CHARSET), 0);
        } else {
            socket.send("No data available".getBytes(ZMQ.CHARSET), 0);
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

    public void setPressure(double p) {
        pressure = p;
    }
    
    public void setVolume(double v) {
    	volume = v;
    }

    public double getPressure() {
        return pressure;
    }

    public void setSimulationData(ArrayList<String> data) {
        this.data = data;
    }
    
}
