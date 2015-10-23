package controller;

import java.net.InetAddress;

public class Node {
	
	private InetAddress address;
	private int port;
	private String supportedOperators;
	private Boolean isAlive;
	private int usage;
	private long lastIsAlive;
	
	public Node(InetAddress address, int port, String supportedOperators) {
		this.address = address;
		this.port = port;
		this.supportedOperators = supportedOperators;
		isAlive = true;
		lastIsAlive = System.currentTimeMillis();
	}
	
	public synchronized void update() {
		isAlive = true;
		lastIsAlive = System.currentTimeMillis();
	}
	
	public synchronized Boolean isAlive() {
		return isAlive;
	}
	
	public synchronized String getSupportedOperators() {
		return supportedOperators;
	}
	
	public synchronized int getUsage() {
		return usage;
	}
	
	public synchronized void increaseUsage(int increase) {
		usage += increase;
	}
	
	public synchronized InetAddress getAddress() {
		return address;
	}

	public synchronized int getPort() {
		return port;
	}

	public synchronized Boolean checkTimeout(long currentTime, int timeout) {
		if(isAlive) {
			if((currentTime - lastIsAlive) > timeout) {
				isAlive = false;
				return true;
			}
		}
		return false;
	}
	
	public synchronized String toString() {
		
		String output = address + " Port: " + port + " ";
		if(isAlive) {
			output += "online ";
		} else {
			output += "offline ";
		}
		output += "Usage: " + usage + "\n";
		return output;
		
	}
	
}