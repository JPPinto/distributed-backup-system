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
import java.util.Map;

public class PeerThread extends Thread {

	private int portMC;
	private String addressMC;
	private int portMDB;
	private String addressMDB;
	//private int portMDR;
	//private String addressMDR;
	private SocketMCReceiver socReceiver;

	public static final int packetSize = 65536;
	public static final int CRLF = 218;

	PeerThread(String aMC, int pMC, String aMDB, int pMDB/*, String aMDR, int pMDR*/) {
		addressMC = aMC;
		addressMDB = aMDB;
		//addressMDR = aMDR;
		portMC = pMC;
		portMDB = pMDB;
		//portMDR = pMDR;
		socReceiver = new SocketMCReceiver(addressMC, portMC);
	}

	public void run() {
		boolean running = true;
		socReceiver.run();


		while (running) for (Map.Entry<String, PBMessage> entry : socReceiver.received_putchunk.entrySet()) {

			PBMessage temp_message = entry.getValue();

			// TODO Guardar chunk

			String temp_request = "STORED " + temp_message.version + " " + temp_message.fileId + " " + temp_message.chunkNo + PBMessage.TERMINATOR;
			sendRequest(temp_request);                                // Responde to message

			socReceiver.received_putchunk.remove(entry.getKey());    // Remove message from queue
		}
	}

	public void sendRequest(String request) {

		PBMessage temp_message = new PBMessage(request.getBytes());

		try {
			DatagramSocket socketMDB = new DatagramSocket();
			DatagramSocket socketMC = new DatagramSocket();
			InetAddress IPAddressMDB = InetAddress.getByName(addressMDB);
			InetAddress IPAddressMC = InetAddress.getByName(addressMC);

			if (temp_message.type == PBMessage.PUTCHUNK) {

				DatagramPacket packet = new DatagramPacket(temp_message.raw_data, temp_message.raw_data.length, IPAddressMDB, portMDB);
				socketMDB.send(packet);

				while (true) {
					/*TO COMPLETE*/
				}

			} else if (temp_message.type == PBMessage.STORED) {

				DatagramPacket packet = new DatagramPacket(temp_message.raw_data, temp_message.raw_data.length, IPAddressMC, portMC);
				socketMC.send(packet);
			}

			socketMDB.close();
			socketMC.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main (String[] args) throws IOException {

		PeerThread peer = new PeerThread("224.0.0.0", 60000, "225.0.0.0", 60001);

		peer.start();

		peer.sendRequest("STORED 1.0 1 1\r\n");

		BufferedInputStream y = new BufferedInputStream(System.in);
		y.read();
	}
}


