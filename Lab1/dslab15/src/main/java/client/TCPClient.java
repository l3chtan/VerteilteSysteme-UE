package client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
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
	private PrintWriter writer;
	private boolean close;
	private OutputStream out;
	private InputStream in;
	
	public TCPClient(String host, int port, OutputStream out) throws UnknownHostException, IOException{
		this.host = host;
		this.port = port;
		this.out = out;
		socket = null;
		close = false;
	}
	
	public BufferedReader getReader() throws IOException{
		return reader;
	}
	
	public PrintWriter getWriter() throws IOException{
		return writer;
	}
	
	public void close(){
		close = close && false;
	}
	
	public void run(){
		try {
			socket = new Socket(host,port);
//			socket.setKeepAlive(true);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			writer = new PrintWriter(new OutputStreamWriter(out),true);
			while(!close){
//				writeOut(reader.readLine());
			}
			in.close();
			out.close();
			socket.close();
			close = false;

		} catch (IOException e) {
			System.out.println("here is the problem");
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