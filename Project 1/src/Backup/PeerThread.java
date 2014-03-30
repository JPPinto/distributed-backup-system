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
import java.util.Random;
import java.util.Vector;

import static Backup.PBMessage.*;
import static Backup.PotatoBackup.*;

public class PeerThread extends Thread {

	public static final int packetSize = 65536;
	public static final int CRLF = 218;
	public static final int SOCKET_MC = 0;
	public static final int SOCKET_MDB = 1;
	public static final int SOCKET_MDR = 2;
	private static final String dataBaseFileName = "database.bin";

	private int portMC;
	private String addressMC;
	private int portMDB;
	private String addressMDB;
	private int portMDR;
	private String addressMDR;
	private SocketReceiver socMCReceiver;
	private SocketReceiver socMDBReceiver;
	private SocketReceiver socMDRReceiver;
	private int storesWaiting;
	private Random rand;
	private Vector<String> addrs;
	private Vector<Integer> ports;
	private LocalDataBase dataBase;

	PeerThread(String aMC, int pMC, String aMDB, int pMDB, String aMDR, int pMDR) {
		addrs = new Vector<String>();
		ports = new Vector<Integer>();

		addressMC = aMC;
		addrs.add(aMC);

		addressMDB = aMDB;
		addrs.add(aMDB);

		addressMDR = aMDR;
		addrs.add(aMDR);

		portMC = pMC;
		ports.add(pMC);

		portMDB = pMDB;
		ports.add(pMDB);

		portMDR = pMDR;
		ports.add(pMDR);

		socMCReceiver = new SocketReceiver(addrs, ports, SOCKET_MC);
		socMDBReceiver = new SocketReceiver(addrs, ports, SOCKET_MDB);
		socMDRReceiver = new SocketReceiver(addrs, ports, SOCKET_MDR);
		rand = new Random();
		storesWaiting = 0;

		loadDataBase();
	}

	private void loadDataBase() {
		dataBase = LocalDataBase.loadDataBaseFromFile(dataBaseFileName);

		// We failed to load the database
		if (dataBase == null) {
			dataBase = new LocalDataBase();
		}
	}

	private void saveDataBase() {
		LocalDataBase.saveDataBaseToFile(dataBase, dataBaseFileName);
	}

	public void run() {
		socMCReceiver.start();
		socMDBReceiver.start();
		socMDRReceiver.start();
	}

	public static void sendRequest(PBMessage msg, String mcast_addr, int mcast_port) {

		try {
			DatagramSocket socket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(mcast_addr);
			DatagramPacket packet;

			if (msg.getType() == PUTCHUNK) {

				packet = new DatagramPacket(msg.getData(2), msg.getData(2).length, IPAddress, mcast_port);
				System.out.println("SEND: " + msg.getType() + " " + msg.version + " " + msg.fileId + " " + msg.getIntAttribute(0) + " " + msg.getIntAttribute(1));
				socket.send(packet);

			} else if (msg.getType() == STORED) {

				packet = new DatagramPacket(msg.getData(0), msg.getData(0).length, IPAddress, mcast_port);
				System.out.println("SEND: " + msg.getType() + " " + msg.version + " " + msg.fileId + " " + msg.getIntAttribute(0));
				socket.send(packet);

			} else if (msg.getType() == GETCHUNK) {

				packet = new DatagramPacket(msg.getData(0), msg.getData(0).length, IPAddress, mcast_port);
				System.out.println("SEND: " + msg.getType() + " " + msg.version + " " + msg.fileId + " " + msg.getIntAttribute(0));
				socket.send(packet);

			} else if (msg.getType() == CHUNK) {

				packet = new DatagramPacket(msg.getData(0), msg.getData(0).length, IPAddress, mcast_port);
				System.out.println("SEND: " + msg.getType() + " " + msg.version + " " + msg.fileId + " " + msg.getIntAttribute(0));
				socket.send(packet);

			} else if (msg.getType() == DELETE) {

				packet = new DatagramPacket(msg.getData(0), msg.getData(0).length, IPAddress, mcast_port);
				System.out.println("SEND: " + msg.getType() + " " + msg.fileId);
				socket.send(packet);

			}
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendPUTCHUNK(String filepath) throws IOException, InterruptedException {

		File f = new File(filepath);

		if (!f.exists()) {
			if (!dataBase.getFiles().containsValue(f)) {
				System.out.println("File: " + f.getName() + " does NOT EXIST in the Data Base!");
				return;
			}
			System.out.println("File: " + f.getName() + " does NOT EXIST!");
			return;
		}

		LocalFile local_file = new LocalFile(f);
		dataBase.addFileToDatabase(local_file);

		int time_multiplier, retransmission_count;

		readChunks(f, PotatoBackup.temporaryDirectory);
		System.out.println("Starting to send FILE: " + filepath + "...");

		File[] chunksToSend = listFiles(PotatoBackup.temporaryDirectory);

		for (File file : chunksToSend) {
			if (file.isFile() && file.getName().length() > 66) {
				if (file.getName().substring(0, 64).equals(Utilities.getHashFromFile(f))) {
					Chunk temp_chunk = Chunk.loadChunk(file.getPath());
					PBMessage temp_putchunk = new Msg_Putchunk(temp_chunk, 1);

					socMCReceiver.clearCountStores();

					sendRequest(temp_putchunk, addressMDB, portMDB);

					time_multiplier = 1;
					retransmission_count = 1;

					sleep(500 * time_multiplier);

					int repDegree = temp_putchunk.getIntAttribute(1);
					while (retransmission_count < 7 && socMCReceiver.stores < repDegree) {

						if (retransmission_count != 6) {
							System.out.println("RETRANSMITTING... (Nº of Retransmittion: " + retransmission_count + ")");
							sendRequest(temp_putchunk, addressMC, portMC);
						}

						time_multiplier *= 2;
						retransmission_count++;

						sleep(500 * time_multiplier);
					}

					if (retransmission_count == 6) {
						System.out.println("FAILED TO BACKUP FILE " + filepath + " DUE TO ERROR SENDING CHUNK " + temp_chunk.getChunkNo() + ", WITH ONLY " + socMCReceiver.stores + "STORED MESSAGES");
						return;
					}
				}
			}
		}

		System.out.println("File " + filepath + " backup complete with " + local_file.getNumberOfChunks() + " chunks sent.");

		for (File file : chunksToSend) {
			if (file.isFile()) {
				file.delete();
			}
		}
	}

	public void sendGETCHUNK(String filepath) throws IOException, InterruptedException {

		File f = new File(filepath);

		if (!dataBase.getFiles().containsKey(Utilities.getHashFromFile(f))) {
			System.out.println("File: " + f.getName() + " does NOT EXIST in the Data Base!");
			return;
		}

		LocalFile local_file = dataBase.getFiles().get(Utilities.getHashFromFile(f));

		int time_multiplier, retransmission_count;
		System.out.println("Starting to recover FILE: " + filepath + "...");

		int i;
		int num_chunks_to_recover = local_file.getNumberOfChunks();
		for (i = 0; i < num_chunks_to_recover; i++) {

			socMCReceiver.clearCountStores();

			PBMessage temp_getChunk = new Msg_Getchunk(new Chunk(local_file.getFileHash(), i));
			sendRequest(temp_getChunk, addressMC, portMC);

			time_multiplier = 1;
			retransmission_count = 1;

			sleep(500 * time_multiplier);

			while (retransmission_count < 7 && socMDRReceiver.messages_chuck < 1) {

				if (retransmission_count != 6) {
					System.out.println("RETRANSMITTING... (Nº of Retransmittion: " + retransmission_count + ")");
					sendRequest(temp_getChunk, addressMC, portMC);
				}

				time_multiplier *= 2;
				retransmission_count++;

				sleep(500 * time_multiplier);
			}

			if (retransmission_count == 6) {
				System.out.println("FAILED TO RECOVER FILE " + filepath + " DUE TO ERROR GETTING CHUNK " + i);
				return;
			}
		}

		local_file.restoreFileFromChunks("./");
		System.out.println("File " + filepath + " recovery complete with " + i + " chunks received.");
	}

	public void sendDELETE(String filepath) throws IOException {
		File file = new File(filepath);

		if (!file.exists()) {
			System.out.println("File: " + filepath + " does NOT EXIST!");
			return;
		}


		String sha_num = Utilities.getHashFromFile(file);
		dataBase.removeFileFromBackup(sha_num);

		PBMessage temp_delete = new Msg_Delete(sha_num);
		sendRequest(temp_delete, addressMC, portMC);
	}

	public void sendREMOVED() throws IOException {

	}

	public LocalDataBase getDataBase() {
		return dataBase;
	}

	public static void main(String[] args) throws IOException {

		PeerThread peer;

		if (args.length != 6) {
			peer = new PeerThread("224.0.0.0", 60000, "225.0.0.0", 60001, "226.0.0.0", 60002);
		} else {
			peer = new PeerThread(args[0], Integer.getInteger(args[1]), args[2], Integer.getInteger(args[3]), args[4], Integer.getInteger(args[5]));
		}

		peer.start();

		try {
			//Give time to start the threads
			sleep(1000);

			peer.sendPUTCHUNK("./binary2.test");
			System.out.println("Backup done Now for the Recovery!");
			sleep(1000);
			peer.sendGETCHUNK("./binary2.test");
			System.out.println("Recovery done Now for the Delete!");
			peer.sendDELETE("./binary2.test");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		peer.saveDataBase();
	}
}


