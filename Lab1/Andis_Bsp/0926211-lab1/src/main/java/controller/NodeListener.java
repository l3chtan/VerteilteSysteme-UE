package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NodeListener extends Thread {
	
	private DatagramSocket datagramSocket;
	private NodeHandler nodeHandler;
	
	public NodeListener(DatagramSocket datagramSocket, NodeHandler nodeHandler) {
		this.datagramSocket = datagramSocket;
		this.nodeHandler = nodeHandler;
	}
	
	public void run() {
		
		byte[] buffer;
		DatagramPacket packet;
		try {
			while(true) {
				buffer = new byte[100];
				packet = new DatagramPacket(buffer, buffer.length);
				
				datagramSocket.receive(packet);
				
				String request = new String(packet.getData());
				
				String[] parts = request.split("\\s");
				
				
				if(request.startsWith("!alive")) {
					
					int port = Integer.parseInt(parts[2].trim());
					if(parts.length == 3) {
						nodeHandler.isAlive(parts[1], packet.getAddress(), port);
					}
					if(parts.length == 4) {
						nodeHandler.register(parts[1], packet.getAddress(), port, parts[3]);
						String response = "!registered";
						buffer = new byte[100];
						buffer = response.getBytes();
						packet.setData(buffer);
						datagramSocket.send(packet);
						System.out.println("Node " + packet.getAddress() + " connected.");
					}
				}
				
			}	
		} catch (IOException e) {
			System.out.println("Error occurred while waiting for/handling packets: " +  e.getMessage());
		} finally {
			if (datagramSocket != null && !datagramSocket.isClosed())
				datagramSocket.close();
		}
		
	}
}
