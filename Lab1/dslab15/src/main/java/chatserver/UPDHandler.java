package chatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import util.Config;

public class UPDHandler extends Handler {
	private DatagramPacket packet;

	public UPDHandler(DatagramPacket packet/*, HashMap<String, User> users*/) {
//		super(users);
		this.packet = packet;
	}

	@Override
	public void run() {
		byte[] data = packet.getData();
		String answer = "";
		try {
			DatagramSocket dSoc = new DatagramSocket(packet.getSocketAddress());

			if(data.toString().equals("!list")){
				answer += "Online users:\n";

				for(User u: getOnline()){
					answer += String.format("* %s\n", u.getName());
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
