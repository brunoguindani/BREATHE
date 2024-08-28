package zeroMQ;
//Hello World server in Java
//Binds REP socket to tcp://*:5555
//Expects "Hello" from client, replies with "World"

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ZeroServer {
    ZMQ.Socket socket;
    private Thread receiveThread;
    private boolean running = false;
    private double volume;
    
    public void connect() throws Exception {
        ZContext context = new ZContext();
        //  Socket to talk to clients
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
            System.out.println("Gionny");
            byte[] reply = socket.recv(0);
            String newValue = new String(reply, ZMQ.CHARSET);
            System.out.println("Received: [" +  newValue + "]");
            volume = Double.parseDouble(newValue);
            String response = "volume";
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
        }
    }
    
    public void stopReceiving() {
        running = false;
        receiveThread.interrupt();
    }

    public void close() {
        stopReceiving();
        socket.close();
    }
    
    public double getVolume() {
    	return volume;
    }
}
