package zeroMQ;
//Hello World client in Java
//Connects REQ socket to tcp://localhost:5555
//Sends "Hello" to server, expects "World" back

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class ZeroClient
	{
	public static void main(String[] args)
	{
	    try (ZContext context = new ZContext()) {
	        System.out.println("Connecting to hello world server");
	        
	  		//  Socket to talk to server
	        ZMQ.Socket socket = context.createSocket(SocketType.REQ);
	        socket.connect("tcp://localhost:5555");
	
	        //for (int requestNbr = 0; requestNbr != 100; requestNbr++) {
	        while(true) {
	            String request = "30";
	            System.out.println("Sending Volume " + request);
	            socket.send(request.getBytes(ZMQ.CHARSET), 0);
	
	            byte[] reply = socket.recv(0);
	            System.out.println(
	                "Received " + new String(reply, ZMQ.CHARSET)
	            );
	        }
	        
	    }
	}
	
	private static void sendPressure() {
		
	}
	
	private static void sendVolume() {
		
	}
}
