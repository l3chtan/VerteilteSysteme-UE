package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class TCPReader extends Thread {

	private BufferedReader reader;
	private PrintWriter writer;
	private String lstMsg;

	public TCPReader(BufferedReader reader, OutputStream out){
		this.reader = reader;
		writer = new PrintWriter(out,true);
		lstMsg = "No message received!";
	}
	
	public String getLstMsg(){
		return lstMsg;
	}
	
	private synchronized void write(String msg){
		writer.println(msg);
	}
	
	public void run(){
		String msg = "";
		while(true){
			try {
				msg = reader.readLine();
				if(msg == null) return;
				System.out.println("reader");
				write(msg);
				if(msg.contains(":")){
					lstMsg = msg;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
	}

}
