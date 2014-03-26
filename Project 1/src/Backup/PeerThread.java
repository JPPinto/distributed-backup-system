package Backup;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 *
 * Peer Thread
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class PeerThread extends Thread {

	private int mCastPort;
	private String mCastAddress;
	private ArrayList<PBMessage> incoming_messages;

	private static final int packetSize = 65536;


	PeerThread(int port, String address) {
		incoming_messages = new ArrayList<PBMessage>();
		mCastPort = port;
		mCastAddress = address;
	}

	public void run() {

		MulticastSocket mSocket = null;
		InetAddress iAddress;

		try {
			DatagramPacket packet;

			mSocket = new MulticastSocket(mCastPort);

			/* Join the Multicast Group */
			iAddress = InetAddress.getByName(mCastAddress);
			mSocket.joinGroup(iAddress);

			while (true) {
				byte[] buf = new byte[packetSize];
				packet = new DatagramPacket(buf, buf.length);

				// receive the packets
				mSocket.receive(packet);

				incoming_messages.add(new PBMessage(packet.getData()));

				if (false) break;
			}

			mSocket.leaveGroup(iAddress);

		} catch (IOException e) {
			e.printStackTrace();

			mSocket.close();
		}


	}

	public void sendRequest(String request) throws IOException {

		PBMessage temp_message = new PBMessage(request.getBytes());
		DatagramSocket socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(mCastAddress);

		if (temp_message.type == PBMessage.PUTCHUNK) {

			DatagramPacket packet = new DatagramPacket(temp_message.raw_data, temp_message.raw_data.length, IPAddress, mCastPort);
			socket.send(packet);

			while (true) {

			}

		} else if (temp_message.type == PBMessage.STORED) {

			DatagramPacket packet = new DatagramPacket(temp_message.raw_data, temp_message.raw_data.length, IPAddress, mCastPort);
			socket.send(packet);
		}

		socket.close();
	}
}
