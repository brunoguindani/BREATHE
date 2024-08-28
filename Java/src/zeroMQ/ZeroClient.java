package zeroMQ;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import javax.swing.*;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    
    public ZeroClient() {
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

        connectButton = new JButton("Connetti");
        connectButton.setBounds(50, 20, 100, 25);
        connectButton.setBackground(new Color(0, 122, 255)); 
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        panel.add(connectButton);

        disconnectButton = new JButton("Disconnetti");
        disconnectButton.setBounds(200, 20, 120, 25);
        disconnectButton.setBackground(new Color(255, 59, 48));
        disconnectButton.setForeground(Color.WHITE);
        disconnectButton.setFocusPainted(false);
        disconnectButton.setEnabled(false);
        panel.add(disconnectButton);

        outputArea = new JTextArea();
        outputArea.setBounds(50, 120, 300, 180);
        panel.add(outputArea);

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
            }
        });
    }


    private void disconnectFromServer() {
        if (isConnected) {
            try {
                // Send a disconnect message to the server
                socket.send("disconnect".getBytes(ZMQ.CHARSET), 0);
                // Wait for a short period to ensure the message is sent
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                outputArea.append("Disconnecting from server...\n");
                isConnected = false;
                if (communicationThread != null && communicationThread.isAlive()) {
                    communicationThread.interrupt();
                }
                if (socket != null) {
                    socket.close();
                }
                if (context != null) {
                    context.close();
                }
                outputArea.append("Disconnected.\n");
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

        communicationThread = new Thread(() -> {
            while (isConnected) {
                double value = 0;
                if (selectedOption.equals("Volume"))
                    value = Math.random();
                else
                    value = Math.random() * 20;
                String request = selectedOption + ": " + value;
                outputArea.append("Sending " + request + "\n");
                socket.send(request.getBytes(ZMQ.CHARSET), 0);

                byte[] reply = socket.recv(0);
                outputArea.append("Received: " + new String(reply, ZMQ.CHARSET) + "\n");

                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        communicationThread.start();
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ZeroClient::new);
    }
}
