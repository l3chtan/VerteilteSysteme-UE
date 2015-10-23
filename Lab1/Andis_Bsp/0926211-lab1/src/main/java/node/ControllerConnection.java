package node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import util.Config;

public class ControllerConnection implements Runnable {

	private Socket socket;
	private Config config;
	private String supportedOperators;
	private Logger logger;
	private ArrayList<Socket> sockets;
	
	public ControllerConnection(Socket socket, Config config, Logger logger, ArrayList<Socket> sockets) {
		this.socket = socket;
		this.config = config;
		this.logger = logger;
		supportedOperators = this.config.getString("node.operators");
		this.sockets = sockets;
		synchronized(this.sockets) {
			this.sockets.add(this.socket);
		}
	}
	
	public void run() {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
			
			String request = reader.readLine();
			String[] parts = request.split("\\s");
			
			String response = "";
			if(parts.length == 4 && request.startsWith("!compute")) {
				if(supportedOperators.contains(parts[2])) {
					int a = Integer.parseInt(parts[1].trim());
					int b = Integer.parseInt(parts[3].trim());
					if(parts[2].equals("+")) {
						response += a + b;
					} else if(parts[2].equals("-")) {
						response += a - b;
					} else if(parts[2].equals("*")) {
						response += a * b;
					} else if(parts[2].equals("/")) {
						if(b != 0) {
							response += a / b;
						} else {
							response = "Error: division by 0";
						}
					}
				} else {
					response = "Error: operator is not supported";
				}
			} else {
				response = "Error: Invalid Command";
			}
			
			logger.log(request + "\n" + response);
			
			writer.println(response);
			
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		} finally {
			if(socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {}
			}
			synchronized(sockets) {
				if(sockets.contains(socket)) {
					sockets.remove(socket);
				}
			}
		}
		
	}
	
}
