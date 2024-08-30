package zeroMQ;

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
    
    private String selectedMode;
    private double volume;
    private double pressure;
    
    ArrayList<String> data;
    
    public void connect() throws Exception {
        context = new ZContext();  // Inizializzazione del contesto
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

            if (receivedData.equals("disconnect")) {
                System.out.println("Disconnect message received.");
                connectionStable = false;
                //socket.send("Disconnect acknowledged".getBytes(ZMQ.CHARSET), 0);

            } else if (receivedData.equals("requestData")) {
                // Risponde con i dati attualmente disponibili
                sendData();
                connectionStable = true;

            } else if (receivedData.startsWith("Volume")) {
                selectedMode = "Volume";
                volume = Double.parseDouble(receivedData.split(": ")[1]);
                socket.send("Volume received".getBytes(ZMQ.CHARSET), 0);
                connectionStable = true;

            } else if (receivedData.startsWith("Pressure")) {
                selectedMode = "Pressure";
                pressure = Double.parseDouble(receivedData.split(": ")[1]);
                socket.send("Pressure received".getBytes(ZMQ.CHARSET), 0);
                connectionStable = true;
            } else {
                socket.send("Unknown command".getBytes(ZMQ.CHARSET), 0);
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
            // Concatenate all elements in the data ArrayList into a single string
            StringBuilder dataBuilder = new StringBuilder();
            for (String datum : data) {
                dataBuilder.append(datum).append(";"); // Use a semicolon or another delimiter of your choice
            }
            
            // Remove the last delimiter
            if (dataBuilder.length() > 0) {
                dataBuilder.setLength(dataBuilder.length() - 1);
            }
            
            // Send the concatenated data string over the socket
            socket.send(dataBuilder.toString().getBytes(ZMQ.CHARSET), 0);
        } else {
            // Handle the case where data is null or empty
            socket.send("No data available".getBytes(ZMQ.CHARSET), 0);
        }
    }
    
    
    public boolean isConnectionStable() {
        return connectionStable;
    }

    public String getSelectedMode() {
        return selectedMode;
    }

    public double getVolume() {
        System.out.println(volume);
        return volume;
    }

    public double getPressure() {
        return pressure;
    }
    
    public void setSimulationData(ArrayList<String> data){
    	this.data = data;
    }
}
