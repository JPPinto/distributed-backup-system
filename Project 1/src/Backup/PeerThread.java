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
import java.security.cert.CRL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PeerThread extends Thread {

	private int mCastPort;
	private String mCastAddress;
	private SocketReceiver socReceiver;

	public static final int packetSize = 65536;
	public static final String CRLF = "\r\n";

	PeerThread(int port, String address) {
		mCastPort = port;
		mCastAddress = address;
		socReceiver = new SocketReceiver(address, port);

	}

	public void run() {
		boolean running = true;
		socReceiver.run();


		while (running) {

			for (Map.Entry<String, PBMessage> entry : socReceiver.received_putchunk.entrySet()) {

				PBMessage temp_message = entry.getValue();

				// TODO Guardar chunk

				String temp_request = new String("STORED " + temp_message.version + " " + temp_message.fileId + " " + temp_message.chunkNo + " " + CRLF);
				sendRequest(temp_request);								// Responde to message

				socReceiver.received_putchunk.remove(entry.getKey());  	// Remove message from queue
			}

		}
	}

	public void sendRequest(String request) {

		PBMessage temp_message = new PBMessage(request.getBytes());

		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(mCastAddress);

			if (temp_message.type == PBMessage.PUTCHUNK) {

				DatagramPacket packet = new DatagramPacket(temp_message.raw_data, temp_message.raw_data.length, IPAddress, mCastPort);
				socket.send(packet);

				while (true) {
				/*TO COMPLETE*/
				}

			} else if (temp_message.type == PBMessage.STORED) {

				DatagramPacket packet = new DatagramPacket(temp_message.raw_data, temp_message.raw_data.length, IPAddress, mCastPort);
				socket.send(packet);
			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main (String[] args) throws IOException {

		System.out.println("STORED " + 0xDA);
		BufferedInputStream y = new BufferedInputStream(System.in);
		y.read();
	}
}


