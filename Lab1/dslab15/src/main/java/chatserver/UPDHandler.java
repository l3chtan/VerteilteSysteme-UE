package chatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UPDHandler extends Thread {
	private DatagramPacket packet;

	public UPDHandler(DatagramPacket packet) {
		this.packet = packet;
	}

	@Override
	public void run() {
		byte[] data = packet.getData();
		String answer = "";
		try {
			DatagramSocket dSoc = new DatagramSocket(packet.getSocketAddress());

			if(data.toString().equals("!list")){
				//TODO get list of people who are online
				answer = ""; 
			} else {
				answer = "unknown request";
			}

			DatagramPacket p = new DatagramPacket(answer.getBytes(),answer.length());
			dSoc.send(p);
			dSoc.close();

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
