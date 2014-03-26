package Backup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

/**
 * Created by Jose on 26-03-2014.
 */
public class SocketReceiver extends Thread {

	public HashMap<String, PBMessage> received_putchunk;
	public HashMap<String, PBMessage> received_stored;
	public String mcast_adrr;
	public int mcast_port;

	SocketReceiver(String ma, int mp) {
		mcast_adrr = ma;
		mcast_port = mp;
		received_putchunk = new HashMap<String, PBMessage>();
		received_stored = new HashMap<String, PBMessage>();
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

				PBMessage temp_message = new PBMessage(packet.getData());

				if (!received_stored.containsKey(packet.getAddress()) && temp_message.type == PBMessage.STORED) {
					received_stored.put(packet.getAddress().toString(), temp_message);
				}

				if (!received_putchunk.containsKey(packet.getAddress()) && temp_message.type == PBMessage.PUTCHUNK) {
					received_putchunk.put(packet.getAddress().toString(), temp_message);
				}

				System.out.println("RECEIVED FROM " + packet.getAddress().toString() + " TYPE: " + temp_message.type);

				if (false) break;
			}

			mSocket.leaveGroup(iAddress);

		} catch (IOException e) {
			e.printStackTrace();

			mSocket.close();
		}
	}

	public int numStoredByIP() {
		return received_stored.size();
	}

	public int numPutByIP() {
		return received_putchunk.size();
	}
}
