package chatserver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class User {
	private final String name;
	private final int password;
	private String registry; 
	private boolean online;
	private BufferedWriter writer;
	private Socket socket;

	public User(String name, int password){
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}
	public String getRegistry() {
		return registry;
	}
	public void setRegistry(String registry) {
		this.registry = registry;
	}
	public int getPassword() {
		return password;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public void write(String msg) throws IOException {
		writer.write(msg);
	}
	public void setWriter(BufferedWriter writer) {
		this.writer = writer;
	}

	public String getStatus() {
		if(online) return "online";
		return "offline";
	}	
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void closeSocket() throws IOException{
		if(socket != null){
			if(!socket.isClosed()) socket.close();
		}
	}
}
