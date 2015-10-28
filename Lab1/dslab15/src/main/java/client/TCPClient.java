package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TCPClient extends Thread {
	private Socket socket;
	private String host;
	private int port;
	
	public TCPClient(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public BufferedReader getReader() throws IOException{
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public BufferedWriter getWriter() throws IOException{
		return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public void run(){
		try {
			socket = new Socket(host,port);
			socket.setKeepAlive(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}