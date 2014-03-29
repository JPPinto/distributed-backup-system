package Backup;

import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Created by Jose on 29/03/2014.
 */
public class ProtocolHandler extends Thread{

	private int portMC;
	private String addressMC;
	private int portMDB;
	private String addressMDB;
	//private int portMDR;
	//private String addressMDR;
	private SocketReceiver socMCReceiver;
	private SocketReceiver socMDBReceiver;
	private int storesWaiting;
	private Queue<PBMessage> store;
	private Queue<PBMessage> puts;
	private Random rand;

	ProtocolHandler(String aMC, int pMC, String aMDB, int pMDB/*, String aMDR, int pMDR*/) {
		addressMC = aMC;
		addressMDB = aMDB;
		//addressMDR = aMDR;
		portMC = pMC;
		portMDB = pMDB;
		//portMDR = pMDR;
		rand = new Random();
	}

	public void run(){


	}

	public void addElement(Set<Map.Entry<String,PBMessage>> s){

	}


	public void handleProtocol(PBMessage msg) throws InterruptedException {

		if (msg.getType().equals("PUTCHUNK")) {
			msg.saveChunk(PotatoBackup.backupDirectory);
			//				rand.nextInt((MAX-MIN) + 1) + MIN;
			int randomNum = rand.nextInt((400 - 0) + 1);

			PBMessage message = new Msg_Stored(msg.fileId, msg.getIntAttribute(0));
			sleep(randomNum);
			//sendRequest(message, addressMC, portMC);

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
}
