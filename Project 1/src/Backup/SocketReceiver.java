package Backup;

import sun.plugin.dom.exception.InvalidStateException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import static Backup.PBMessage.*;

/**
 * Created by Jose on 26-03-2014.
 */
public class SocketReceiver extends Thread {

	private HashSet<PBMessage> received;
	private String mcast_adrr;
	private int mcast_port;
	private Vector<String> addrs;
	private Vector<Integer> ports;
	public int stores;

	SocketReceiver(Vector<String> a, Vector<Integer> p, int type) {
		addrs = a;
		ports = p;
		mcast_adrr = a.get(type);
		mcast_port = p.get(type);
		received = new HashSet<PBMessage>();
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

					PBMessage temp_message = createMessageFromType(packet.getData(), packet.getLength());

					if (temp_message != null) {
						if (received.add(temp_message)) {

							countStores(temp_message);

							ProtocolHandler temp_handler = new ProtocolHandler(addrs, ports, temp_message, packet);
							temp_handler.run();
						}
					} else {
						System.out.println("MESSAGE DISCARDED!");
					}

				} catch (InvalidStateException e) {
					System.out.println("MESSAGE DISCARDED!");
					e.printStackTrace();
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

	public void countStores(PBMessage msg){
		if(msg.getType() == STORED)
			stores++;
	}

	public void clearCount() {
		stores = 0;
	}
}
