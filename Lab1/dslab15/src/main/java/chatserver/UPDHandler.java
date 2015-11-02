package chatserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import util.Config;

public class UPDHandler extends Handler {
	private DatagramPacket packet;
	private DatagramSocket dSoc;

	public UPDHandler(DatagramSocket dSoc, DatagramPacket packet/*, HashMap<String, User> users*/) {
//		super(users);
		this.dSoc = dSoc;
		this.packet = packet;
	}

	@Override
	public void run() {
		byte[] data = packet.getData();
//		String answer = "";
		try {
			System.out.println("----------------------------------");
			System.out.println("data: "+data.toString());
			System.out.println("----------------------------------");

			if(data.toString().equals("!list")){
				data = "Online users:\n".getBytes();
				dSoc.send(new DatagramPacket(data,data.length));

				for(User u: getOnline()){
					data = u.getName().getBytes();
					dSoc.send(new DatagramPacket(data,data.length));
//					answer += String.format("* %s\n", u.getName());
				}
			} else {
				data = "unknown request".getBytes();
				dSoc.send(new DatagramPacket(data,data.length));
//				answer = "unknown request";
			}

//			DatagramPacket p = new DatagramPacket(answer.getBytes(),answer.length());
//			dSoc.send(p);
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
