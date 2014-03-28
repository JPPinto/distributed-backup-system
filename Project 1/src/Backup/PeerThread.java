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
		socReceiver.start();
		boolean running = true;

		while (running) for (Map.Entry<String, PBMessage> entry : socReceiver.received.entrySet()) {

			PBMessage temp_message = entry.getValue();

			handleProtocol(temp_message);

			socReceiver.received.remove(entry.getKey());    // Remove message from queue
		}
	}

	public void handleProtocol(PBMessage msg){

		if(msg.getType().equals("PUTCHUNK")){
			//TODO Guardar chunk
			Chunk currentChunk = new Chunk(msg.fileId, msg.getIntAttribute(0), msg.getData(1));
			//currentChunk.write(backupdirectory);
			sendRequest(PBMessage.STORED + msg.version + PBMessage.SEPARATOR + msg.fileId + PBMessage.SEPARATOR + PBMessage.TERMINATOR + PBMessage.SEPARATOR + +PBMessage.TERMINATOR, addressMC, portMC);
			System.out.println("HANDLED PUTCHUNK!");
		} else
			if(msg.getType().equals("DELETE")){
				System.out.println("HANDLED DELETE!");
		} else
			if(msg.getType().equals("STORED")){
				System.out.println("HANDLED STORED!");
		} else
			if(msg.getType().equals("REMOVED")){
				System.out.println("HANDLED REMOVED!");
		} else
			if(msg.getType().equals("CHUNK")){
				System.out.println("HANDLED CHUNK!");
		} else
			if(msg.getType().equals("GETCHUNK")){
				System.out.println("HANDLED GETCHUNK!");
		}

	}


	public void sendRequest(PBMessage msg, String mcast_addr, int mcast_port) {

		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(mcast_addr);
			DatagramPacket packet;

			if (msg.getType() == PBMessage.PUTCHUNK) {

				//packet = new DatagramPacket(msg.raw_data, msg.raw_data.length, IPAddress, mcast_port);
				//socket.send(packet);


			} else if (msg.getType() == PBMessage.STORED) {

				//packet = new DatagramPacket(msg.getData(2), msg.getData(2).length, IPAddress, mcast_port);
				//socket.send(packet);
			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendRequest(String msg, String mcast_addr, int mcast_port) {

		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(mcast_addr);
			DatagramPacket packet;


			packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, IPAddress, mcast_port);
			socket.send(packet);

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeThreads(){
		socReceiver.interrupt();
		this.interrupt();
	}

	public static void main (String[] args) throws IOException {

		PeerThread peer = new PeerThread("224.0.0.0", 60000, "225.0.0.0", 60001);

		peer.start();

		String msg = "STORED 1.0 1 1 \r\n \r\n";
		String msg2 = "PUTCHUNK 1.0 1 1 ";

		//PBMessage message = PBMessage.createMessageFromType(msg.getBytes());

		try {
			//Give time to start the threads
			sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		peer.sendRequest(msg,peer.addressMC, peer.portMC);
		peer.sendRequest(msg2,peer.addressMC, peer.portMC);
	}
}


