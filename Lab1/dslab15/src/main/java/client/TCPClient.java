package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient extends Thread {
	private Socket socket;
	private String host;
	private int port;
	private BufferedReader reader;
	private BufferedWriter writer;
	private boolean close;
	private OutputStream out;
	
	public TCPClient(String host, int port, OutputStream out) throws UnknownHostException, IOException{
		this.host = host;
		this.port = port;
		this.out = out;
		socket = null;
		close = false;
	}
	
	public BufferedReader getReader() throws IOException{
		if(socket == null) return null;
		return reader;
	}
	
	public BufferedWriter getWriter() throws IOException{
		if(socket == null) return null;
		return writer;
	}
	
	public void close(){
		close = true;
	}
	
	public void run(){
		try {
			socket = new Socket(host,port);
//			socket.setKeepAlive(true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			while(!close){
				writeOut(reader.readLine());
			}
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			System.out.println("run");
	}

	private synchronized void writeOut(String msg){
		try {
			out.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}