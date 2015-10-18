package chatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import util.Config;

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

			//maybe integrate status list into user.properties?
			if(data.toString().equals("!list")){
				Config cfg = new Config("list");
				for(String s: cfg.listKeys()){
					if(cfg.getString(s).equals("online")){
						answer += String.format("%s %s\n",s,cfg.getString(s));
					}
				}
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
