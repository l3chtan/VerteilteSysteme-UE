package node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import util.Config;

public class ListenerThread extends Thread {
	
	private ServerSocket serverSocket;
	private Config config;
	private Logger logger;
	private ExecutorService threadPool = Executors.newFixedThreadPool(100);
	private ArrayList<Socket> sockets = new ArrayList<Socket>();
	
	public ListenerThread(ServerSocket serverSocket, Config config, Logger logger) {
		this.serverSocket = serverSocket;
		this.config = config;
		this.logger = logger;
	}
	
	public void run() {
		
			Socket socket = null;
			
			try {
				while(true) {
					socket = serverSocket.accept();
					threadPool.execute(new ControllerConnection(socket, config, logger, sockets));
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
				if(socket != null && !socket.isClosed()) {
					try {
						socket.close();
					} catch (IOException e) {}
				}
			}
			
		
	}
	
}
