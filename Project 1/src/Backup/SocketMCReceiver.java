package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jose on 26-03-2014.
 */
public class SocketMCReceiver extends Thread {

	public HashMap<String, PBMessage> received;
	public String mcast_adrr;
	public int mcast_port;

	SocketMCReceiver(String ma, int mp) {
		mcast_adrr = ma;
		mcast_port = mp;
		received = new HashMap<String, PBMessage>();
	}

	public void run() {

		MulticastSocket mSocket = null;
		InetAddress iAddress;

		try {
			DatagramPacket packet;

			mSocket = new MulticastSocket(mcast_port);

			/* Join the Multicast Group */
			iAddress = InetAddress.getByName(mcast_adrr);
			mSocket.joinGroup(iAddress);

			while (true) {

				byte[] buf = new byte[PeerThread.packetSize];
				packet = new DatagramPacket(buf, buf.length);

				// receive the packets
				mSocket.receive(packet);

				try {
				PBMessage temp_message = PBMessage.createMessageFromType(packet.getData());

				if (!received.containsKey(packet.getAddress().getHostAddress())) {
					received.put(packet.getAddress().getHostAddress(), temp_message);
				}

				System.out.println("RECEIVED FROM " + packet.getAddress().getHostAddress() + " TYPE: " + temp_message.getType());

				} catch(InvalidStateException e){
					e.printStackTrace();
					System.out.println("KEEP GOING");
				}

				if (false) break;
			}

			mSocket.leaveGroup(iAddress);

		} catch (IOException e) {
			e.printStackTrace();
			mSocket.close();
		}
		mSocket.close();
	}

	public int numStoredByIP() {

		int num = 0;

		for (Map.Entry<String, PBMessage> entry : received.entrySet()) {

			if (entry.getValue().getType() == "STORED")
				num++;
		}
		return num;
	}

	public int numPutByIP() {
		int num = 0;

		for (Map.Entry<String, PBMessage> entry : received.entrySet()) {

			if (entry.getValue().getType() == "PUTCHUNK")
				num++;
		}
		return num;
	}
}
