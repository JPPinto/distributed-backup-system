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
import java.util.Random;

public class PeerThread extends Thread {

	private int portMC;
	private String addressMC;
	private int portMDB;
	private String addressMDB;
	//private int portMDR;
	//private String addressMDR;
	private SocketReceiver socMCReceiver;
	private SocketReceiver socMDBReceiver;
	private int storesWaiting;
	private Random rand;

	public static final int packetSize = 65536;
	public static final int CRLF = 218;

	PeerThread(String aMC, int pMC, String aMDB, int pMDB/*, String aMDR, int pMDR*/) {
		addressMC = aMC;
		addressMDB = aMDB;
		//addressMDR = aMDR;
		portMC = pMC;
		portMDB = pMDB;
		//portMDR = pMDR;
		socMCReceiver = new SocketReceiver(addressMC, portMC);
		socMDBReceiver = new SocketReceiver(addressMDB, portMDB);
		rand = new Random();
		storesWaiting = 0;
	}

	public void run() {
		socMCReceiver.start();
		socMDBReceiver.start();

		boolean running = true;
		try {
			while (running) {

				for (Map.Entry<String, PBMessage> entry : socMCReceiver.received.entrySet()) {

					if (entry.getValue().getType() == PBMessage.STORED) {
						handleProtocol((Msg_Stored) entry.getValue());
					}

					socMCReceiver.received.remove(entry.getKey());    // Remove message from queue
				}

				for (Map.Entry<String, PBMessage> entry : socMDBReceiver.received.entrySet()) {

					if (entry.getValue().getType() == PBMessage.PUTCHUNK) {
						handleProtocol((Msg_Putchunk) entry.getValue());
					}

					socMDBReceiver.received.remove(entry.getKey());    // Remove message from queue
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void handleProtocol(PBMessage msg) throws InterruptedException {

		if (msg.getType().equals("PUTCHUNK")) {

			Chunk currentChunk = new Chunk(msg.fileId, msg.getIntAttribute(0), msg.getData(1));
			currentChunk.write(PotatoBackup.backupDirectory);

			int randomNum = rand.nextInt((400 - 0) + 1) + 0;

			PBMessage message = new Msg_Stored(msg.fileId, msg.getIntAttribute(0));
			sleep(randomNum);
			sendRequest(message, addressMC, portMC);

			System.out.println("HANDLED PUTCHUNK!");
		} else if (msg.getType().equals("DELETE")) {
			System.out.println("HANDLED DELETE!");
		} else if (msg.getType().equals("STORED")) {
			storesWaiting++;
			System.out.println("HANDLED STORED!");
		} else if (msg.getType().equals("REMOVED")) {
			System.out.println("HANDLED REMOVED!");
		} else if (msg.getType().equals("CHUNK")) {
			System.out.println("HANDLED CHUNK!");
		} else if (msg.getType().equals("GETCHUNK")) {
			System.out.println("HANDLED GETCHUNK!");
		}

	}


	public void sendRequest(PBMessage msg, String mcast_addr, int mcast_port) {

		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(mcast_addr);
			DatagramPacket packet;

			if (msg.getType() == PBMessage.PUTCHUNK) {

				packet = new DatagramPacket(msg.getData(2), msg.getData(2).length, IPAddress, mcast_port);
				socket.send(packet);

			} else if (msg.getType() == PBMessage.STORED) {

				packet = new DatagramPacket(msg.getData(0), msg.getData(0).length, IPAddress, mcast_port);
				socket.send(packet);
			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*public void sendRequest(String msg, String mcast_addr, int mcast_port) {

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

	public void closeThreads() {
		socMCReceiver.interrupt();
		this.interrupt();
	}*/

	public void sendPUTCHUNK(String filepath) throws IOException, InterruptedException {

		int time_multiplier, retransmission_count;
		PotatoBackup.readChunks(new File(filepath), PotatoBackup.temporaryDirectory);
		System.out.println("DONE!");

		File[] chucksToSend = PotatoBackup.listFiles(PotatoBackup.temporaryDirectory);

		//Chunk temp_chunk = Chunk.loadChunk(chucksToSend[0].getPath());


		for (File file : chucksToSend) {
			if (file.isFile()) {
				Chunk temp_chunk = Chunk.loadChunk(file.getPath());
				PBMessage temp_putchunk = new Msg_Putchunk(temp_chunk, 1);

				sendRequest(temp_putchunk, addressMDB, portMDB);

				time_multiplier = 1;
				retransmission_count = 1;
				while (retransmission_count < 6) {

					sleep(500 * time_multiplier);

					if (storesWaiting >= temp_putchunk.getIntAttribute(1)) {    //STORES received == replication degree
						System.out.println("CONFIRMED CHUNK Nº: " + temp_chunk.getChunkNo());  //Debug Purposes
						storesWaiting = 0;
						break;
					}

					System.out.println("RETRANSMITTING... (Nº of Retransmittion: " + retransmission_count + ")");
					sendRequest(temp_putchunk, addressMC, portMC);
					time_multiplier *= 2;
					retransmission_count++;
				}

				if (time_multiplier == 6){
					System.out.println("FAILED TO BACKUP FILE " + filepath + " WITH REPLICATION DEGREE OF: " + temp_putchunk.getIntAttribute(1));
					break;
				}
			}
		}

		for (File file : chucksToSend) {
			if (file.isFile()) {
				file.delete();
			}
		}

		System.out.println("DONE!");
	}

	public static void main(String[] args) throws IOException {

		PeerThread peer = new PeerThread("224.0.0.0", 60000, "225.0.0.0", 60001);

		peer.start();

		try {
			//Give time to start the threads
			sleep(1000);

			//PBMessage message = new Msg_Stored("2ACE2D72832ACE2D72832ACE2D72832ACE2D72832ACE2D72832ACE2D72831234",1); //Works


			PotatoBackup.readChunks(new File("./binary.test"), PotatoBackup.temporaryDirectory);
			System.out.println("DONE!");
			File[] chucksToSend = PotatoBackup.listFiles(PotatoBackup.temporaryDirectory);

			PBMessage message = new Msg_Putchunk(Chunk.loadChunk(chucksToSend[0].getPath()), 1);
			System.out.println("SENDING...");
			peer.sendRequest(message, peer.addressMDB, peer.portMDB);

			//peer.sendPUTCHUNK("./binary.test");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


