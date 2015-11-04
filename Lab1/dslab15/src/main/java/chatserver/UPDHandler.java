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

	public UPDHandler(DatagramSocket dSoc, DatagramPacket packet) {
		this.dSoc = dSoc;
		this.packet = packet;
	}

	@Override
	public void run() {
		byte[] data = packet.getData();
		System.out.println("data: "+new String(data));
		try {

			if(new String(data).startsWith("!list")){
				if(!getOnline().isEmpty()){
					data = "Online users:".getBytes();
					packet.setData(data);
					dSoc.send(packet);

					for(User u: getOnline()){
						data = u.getName().getBytes();
						packet.setData(data);
						dSoc.send(packet);
					}
					return;
				} else {
					data = "No users online\n".getBytes();
				}
			} else {
				data = "unknown request".getBytes();
			}

			packet.setData(data);
			dSoc.send(packet);

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
