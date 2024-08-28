package zeroMQ;

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
                // Gestisce il messaggio di disconnessione
                System.out.println("Disconnect message received.");
                connectionStable = false;
                socket.send("Disconnect acknowledged".getBytes(ZMQ.CHARSET), 0);
                
            } else if (receivedData.startsWith("Volume")) {
                selectedMode = "Volume";
                volume = Double.parseDouble(receivedData.split(": ")[1]);
                socket.send("Volume received".getBytes(ZMQ.CHARSET), 0);
                connectionStable = true;
            } else {
                selectedMode = "Pressure";
                pressure = Double.parseDouble(receivedData.split(": ")[1]);
                socket.send("Pressure received".getBytes(ZMQ.CHARSET), 0);
                connectionStable = true;
            } 
            
        }
    }

    public void stopReceiving() {
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
}
