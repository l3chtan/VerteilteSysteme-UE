package client;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPClient extends Thread {
	
	private DatagramSocket socket;
	
	public void run(){
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
