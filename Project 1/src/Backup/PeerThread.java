package Backup;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 *
 * Peer Thread
 */

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Vector;

import static Backup.PBMessage.*;
import static Backup.Utilities.backupDirectory;
import static Backup.Utilities.chunkDataSize;

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

		loadDataBase();
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

			} else if (msg.getType() == REMOVED) {

				packet = new DatagramPacket(msg.getData(0), msg.getData(0).length, IPAddress, mcast_port);
				System.out.println("SEND: " + msg.getType() + " " + msg.version + " " + msg.fileId + " " + msg.getIntAttribute(0));
				socket.send(packet);

			}
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void freeDiskSpace(int kbytes) {

		File backup_folder = new File(backupDirectory);
		File[] chunks_on_backup = backup_folder.listFiles();

		int num_chunks_to_remove = kbytes / chunkDataSize;

		if ((kbytes % chunkDataSize) > 0)
			num_chunks_to_remove++;

		if (num_chunks_to_remove > chunks_on_backup.length) {
			System.out.println("Amount of memory to free is too high!");
			return;
		}

		for (int i = 0; i < num_chunks_to_remove;i++) {
			if (chunks_on_backup[i].isFile() && chunks_on_backup[i].getName().length() > 66) {
				Chunk temp_chunk = Chunk.loadChunk(chunks_on_backup[i].getPath());
				try {
					sendREMOVED(temp_chunk.getFileId(), temp_chunk.getChunkNo());
				} catch (IOException e) {
					e.printStackTrace();
				}
				chunks_on_backup[i].delete();
				//System.out.println("Chunk Nº " + temp_chunk.getChunkNo() + " with FileID " + temp_chunk.getFileId() + " has been REMOVED");
				continue;
			}
			num_chunks_to_remove++;
		}

		System.out.println("SPACE RECLAIMED: " + (chunkDataSize * num_chunks_to_remove));
	}

	public static void main(String[] args) throws IOException {

		PeerThread peer;

		if (args.length != 6) {
			peer = new PeerThread("224.0.0.0", 60000, "225.0.0.0", 60001, "226.0.0.0", 60002);
		} else {
			peer = new PeerThread(args[0], Integer.getInteger(args[1]), args[2], Integer.getInteger(args[3]), args[4], Integer.getInteger(args[5]));
		}

		peer.start();
	}

	private void loadDataBase() {
		dataBase = LocalDataBase.loadDataBaseFromFile(dataBaseFileName);

		// We failed to load the database
		if (dataBase == null) {
			System.out.println("Failed to load DataBase.");
			dataBase = new LocalDataBase();
		}
	}

	public void saveDataBase() {
		LocalDataBase.saveDataBaseToFile(dataBase, dataBaseFileName);
	}

	public void run() {
		socMCReceiver.start();
		socMDBReceiver.start();
		socMDRReceiver.start();
	}

	public void sendPUTCHUNK(String filepath, int rep_degree) throws IOException, InterruptedException {

		LocalFile local_file;
		File f = new File(filepath);

		if (!f.exists()) {
			System.out.println("File: " + f.getName() + " does NOT EXIST!");
			return;
		}

		if (dataBase.getFiles().containsValue(f)) {
			local_file = dataBase.getFiles().get(Utilities.getHashFromFile(f));
		} else {
			local_file = new LocalFile(f, rep_degree);
		}

		int time_multiplier, retransmission_count;

		Utilities.readChunks(f, Utilities.temporaryDirectory);
		System.out.println("Starting to send FILE: " + filepath + "...");

		File[] chunksToSend = Utilities.listFiles(Utilities.temporaryDirectory);


		for (File file : chunksToSend) {
			if (file.isFile() && file.getName().length() > 66) {
				if (file.getName().substring(0, 64).equals(Utilities.getHashFromFile(f))) {
					Chunk temp_chunk = Chunk.loadChunk(file.getPath());
					PBMessage temp_putchunk = new Msg_Putchunk(temp_chunk, rep_degree);

					socMCReceiver.clearCountStores();

					sendRequest(temp_putchunk, addressMDB, portMDB);

					time_multiplier = 1;
					retransmission_count = 1;

					sleep(500 * time_multiplier);

					int repDegree = temp_putchunk.getIntAttribute(1);
					while (retransmission_count < 6 && socMCReceiver.stores < repDegree) {


						System.out.println("RETRANSMITTING... (Nº of Retransmittion: " + retransmission_count + ")");
						sendRequest(temp_putchunk, addressMC, portMC);

						time_multiplier *= 2;
						retransmission_count++;

						sleep(500 * time_multiplier);
					}

					if (retransmission_count == 6) {
						for (File c : chunksToSend)
							if (c.isFile())
								c.delete();
						System.out.println("FAILED TO BACKUP FILE " + filepath + " DUE TO ERROR SENDING CHUNK " + temp_chunk.getChunkNo() + ", WITH ONLY " + socMCReceiver.stores + " STORED MESSAGES RECEIVED");
						return;
					}

					//if (local_file.getChunks_rep().get(temp_chunk.getChunkNo()) != null) {
					//	local_file.addChunkRepDegree(local_file.getChunks_rep().get(temp_chunk.getChunkNo()) + socMCReceiver.stores);
					//} else {
					local_file.addChunkRepDegree(socMCReceiver.stores);
					//}
				}
			}
		}

		dataBase.addFileToDatabase(local_file);
		System.out.println("File " + filepath + " backup complete with " + local_file.getNumberOfChunks() + " chunks sent.");

		for (File file : chunksToSend) {
			if (file.isFile()) {
				file.delete();
			}
		}
	}

	public void sendGETCHUNK(String filehash, String dir) throws IOException, InterruptedException {

		if (!dataBase.getFiles().containsKey(filehash)) {
			System.out.println("File: " + filehash + " does NOT EXIST in the Data Base!");
			return;
		}

		LocalFile local_file = dataBase.getFiles().get(filehash);

		int time_multiplier, retransmission_count;
		System.out.println("Starting to recover FILE: " + local_file.getFileName() + "...");

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
				System.out.println("FAILED TO RECOVER FILE " + local_file.getFileName() + " DUE TO ERROR GETTING CHUNK " + i);
				return;
			}
		}

		local_file.restoreFileFromChunks(dir);
		System.out.println("File " + local_file.getFileName() + " recovery complete with " + i + " chunks received.");
	}

	public void sendDELETE(String filehash) throws IOException {

		if (!dataBase.getFiles().containsKey(filehash)) {
			System.out.println("File: " + filehash + " does NOT EXIST!");
			return;
		}

		dataBase.removeFileFromBackup(filehash);

		PBMessage temp_delete = new Msg_Delete(filehash);
		sendRequest(temp_delete, addressMC, portMC);
	}

	public void sendREMOVED(String fId, int chunkNo) throws IOException {

		PBMessage temp_removed = new Msg_Removed(fId, chunkNo);
		sendRequest(temp_removed, addressMC, portMC);

	}

	public LocalDataBase getDataBase() {
		return dataBase;
	}
}


