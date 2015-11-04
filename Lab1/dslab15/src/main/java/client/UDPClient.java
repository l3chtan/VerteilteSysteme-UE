package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class UDPClient extends Thread {
	
	private DatagramSocket socket;
	private int port;
	private String host;
	private byte[] cmd;
	private OutputStream out;
	
	public UDPClient(String host, int port, OutputStream out){
		this.host = host;
		this.port = port;
		this.out = out;
		cmd = "!list".getBytes();
	}
	
	private synchronized void writeOut(byte[] msg){
		try {
			out.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		byte[] buffer = new byte[2048];
		String answer = "";
		DatagramPacket rc = new DatagramPacket(buffer,buffer.length);
		try {
			socket = new DatagramSocket();
//			System.out.println(new String(cmd));
			socket.send(new DatagramPacket(cmd,cmd.length,InetAddress.getByName(host),port));

			socket.setSoTimeout(1000);
			while(true){
				try{
					socket.receive(rc);
					answer += String.format("%s\n", new String(rc.getData()));
//					writeOut(rc.getData());
					if(new String(rc.getData()).startsWith("No users online")){
						writeOut(answer.getBytes());
						break;
					}
				} catch (SocketTimeoutException e){
					socket.close();
					writeOut(answer.getBytes());
					return;
				}
			}
		} catch (IOException e) {
			socket.close();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
