package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;

public class ClientConnection implements Runnable {

	private Socket clientSocket;
	private NodeHandler nodeHandler;
	private ConcurrentSkipListMap<String, User> users;
	private Socket socket;
	private ArrayList<Socket> sockets;
	
	public ClientConnection(Socket clientSocket, ConcurrentSkipListMap<String, User> users, NodeHandler nodeHandler, ArrayList<Socket> sockets) {
		this.clientSocket = clientSocket;
		this.users = users;
		this.nodeHandler = nodeHandler;
		this.sockets = sockets;
		synchronized(this.sockets) {
			this.sockets.add(this.clientSocket);
		}
	}
	
	@Override
	public void run() {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
			
			String request;
			User loggedInUser = null;
			while(true) {
				
				request = reader.readLine();
				String[] parts = request.split("\\s");
				String response = "You are not logged in!";
				
				switch(parts[0]) {
					case "!login":
						if(parts.length == 3) {
							response = "Wrong username or password.";
							if(users.containsKey(parts[1])) {
								User user = users.get(parts[1]);
								if(user.isLoggedIn()) {
									response = "You are already logged in!";
								} else if(user.login(parts[1], parts[2])) {
									loggedInUser = users.get(parts[1]);
									response = "Successfully logged in.";
								}
							}
						} else {
							response = "Usage: !login <username> <password>";
						}
						break;
					case "!credits":
						if(loggedInUser != null) {
							if(loggedInUser.isLoggedIn()) {
								response = "You have " + loggedInUser.getCredits() + " credits left.";
							}
						}
						break;
					case "!buy":
						if(loggedInUser != null) {
							if(loggedInUser.isLoggedIn()) {
								if(parts.length == 2) {
									loggedInUser.increaseCredits(Integer.parseInt(parts[1].trim()));
									response = "You now have " + loggedInUser.getCredits() + " credits.";
								}
							}
						}
						break;
					case "!list":
						if(loggedInUser != null) {
							if(loggedInUser.isLoggedIn()) {
								response = nodeHandler.getAllSupportedOperators();
							}
						}
						break;
					case "!compute":
						if(loggedInUser != null) {
							if(loggedInUser.isLoggedIn()) {
								int[] credits = { 0 };
								response = compute(request, loggedInUser, credits);
								loggedInUser.decreaseCredits(credits[0]);
							}
						}
						break;
					case "!logout":
						if(loggedInUser != null) {
							if(loggedInUser.isLoggedIn()) {
								loggedInUser.logout();
								loggedInUser = null;
								response = "Successfully logged out.";
							}
						}
						break;
				}
				
				writer.println(response);
				
			}
			
		} catch (IOException e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		} catch (NullPointerException e) {
			System.out.println("Client disconnected.");
		} finally {
			if(clientSocket != null && !clientSocket.isClosed()) {
				try {
					clientSocket.close();
				} catch (IOException e) {}
			}
			synchronized(sockets) {
				if(sockets.contains(clientSocket)) {
					sockets.remove(clientSocket);
				}
			}
		}
	}
	
	private String compute(String request, User user, int[] credits) {
		
		String response = "";
		String[] parts = request.split("\\s");
		if(parts.length >= 4 && request.startsWith("!compute")) {

			Node node = nodeHandler.getBestNodeForOperator(parts[2]);
			
			try {
			
				if(node != null) {
					socket = new Socket(node.getAddress(), node.getPort());
				} else {
					response = "There is currently no Node available for the requested Operator.";
					socket = null;
				}
				
				if(socket != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
					
					String calculate = "";
					for(int i = 0; i < 4; i++) {
						calculate += parts[i] + " ";
					}
					request = "";
					for(int i = 4; i < parts.length; i++) {
						request += parts[i] + " ";
					}
					if(user.getCredits() >= 50) {
						writer.println(calculate);
						response = reader.readLine();
						credits[0] += 50;
						node.increaseUsage(50);
						
						if(response.contains("Error")) {
							return response;
						} else {
							if(parts.length >= 6) {
								response = compute("!compute " + response + " " + request, user, credits);
							}
						}
						
					} else {
						response = "Not enough credits.";
					}
					
					
				}
				
			} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			} finally {
				if(socket != null && !socket.isClosed()) {
					try {
						socket.close();
					} catch (IOException e) {}
				}
			}
			
		}
		
		return response;
	}
}
