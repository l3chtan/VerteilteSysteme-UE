package client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
		DatagramPacket rc = new DatagramPacket(buffer,buffer.length);
		try {
			socket = new DatagramSocket();
			System.out.println(cmd.toString());
			socket.send(new DatagramPacket(cmd,cmd.length,InetAddress.getByName(host),port));
			System.out.println("sent");
			while(true){
				socket.receive(rc);
				writeOut(rc.getData());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
