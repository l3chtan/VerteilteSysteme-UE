package node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import util.Config;

public class AliveNotifier extends Thread {

	private DatagramSocket socket;
	private Config config;
	private DatagramPacket packet;
	private int alivePeriod;
	private String componentName;
	
	public AliveNotifier(DatagramSocket socket, Config config, String componentName) {
		this.socket = socket;
		this.config = config;
		alivePeriod = this.config.getInt("node.alive");
		this.componentName = componentName;
	}
	
	public void run() {
		
		String aliveMsg = "!alive " + componentName + " " + config.getInt("tcp.port");
		String register = aliveMsg + " " + config.getString("node.operators");
		byte[] buffer = register.getBytes();
		Boolean isRegistered = false;
		
		try {
			socket.setSoTimeout(5000);
			packet = new DatagramPacket(buffer, buffer.length,
					InetAddress.getByName(this.config.getString("controller.host")),
					this.config.getInt("controller.udp.port"));
			
			while(!isRegistered) {
				try {
					socket.send(packet);
					buffer = new byte[100];
					packet.setData(buffer);
					socket.receive(packet);
					System.out.println(new String(packet.getData()));
					if(new String(packet.getData()).contains("!registered")) {
						isRegistered = true;
					}
				} catch (SocketTimeoutException e) {
					System.out.println("No Answer from the Controller, try to register again.");
				}
			}
			
			buffer = new byte[100];
			buffer = aliveMsg.getBytes();
			packet.setData(buffer);
			
			while(true) {
				
				socket.send(packet);

				try {
					Thread.sleep(alivePeriod);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		} finally {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		}
	}
	
}
