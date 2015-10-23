package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientListener extends Thread {

	private ServerSocket serverSocket;
	private NodeHandler nodeHandler;
	private ConcurrentSkipListMap<String, User> users;
	private ExecutorService threadPool = Executors.newFixedThreadPool(100);
	private ArrayList<Socket> sockets = new ArrayList<Socket>();
	
	public ClientListener(ServerSocket serverSocket, ConcurrentSkipListMap<String, User> users, NodeHandler nodeHandler) {
		this.serverSocket = serverSocket;
		this.users = users;
		this.nodeHandler = nodeHandler;
	}
	
	public void run() {
		
			Socket socket;
			try {
				while(true) {
					socket = serverSocket.accept();
					threadPool.execute(new ClientConnection(socket, users, nodeHandler, sockets));
				}
			} catch (IOException e) {
				System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			} finally {
				
				threadPool.shutdown();
				synchronized(sockets) {
					for(Socket s: sockets) {
						if(s != null && !s.isClosed()) {
							try {
								s.close();
							} catch (IOException e) {}
						}
					}
				}
				try {
					if(!threadPool.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
						threadPool.shutdownNow();
					}
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			
				if(serverSocket != null && !serverSocket.isClosed()) {
					try {
						serverSocket.close();
					} catch (IOException e) {}
				}
		}
	}
	
}
