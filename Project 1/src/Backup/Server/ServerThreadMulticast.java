package Backup.Server;

import java.io.IOException;
import java.net.*;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 * <p/>
 * Server Thread
 * <p/>
 * register
 * to register the association of a plate number to the owner. Returns -1 if the plate
 * number has already been registered; otherwise, returns the number of vehicles in the database.
 * <p/>
 * lookup
 * to obtain the owner of a given plate number. Returns the owner's name or the string
 * NOT_FOUND if the plate number was never registered.
 */
public class ServerThreadMulticast extends Thread {
	// Params
	private String mcastAddress, mcastPort;
	public final int PORT = 60000;
	public final String adr = new String("230.0.0.1"); 	//any class D address
	public final String adr2 = new String("231.0.0.1"); //any class D address
	public final String adr3 = new String("232.0.0.1"); //any class D address

	protected boolean serverIsRunning = false;

	public ServerThreadMulticast(String mcast_addr, String mcast_port) {
		mcastAddress = mcast_addr;
		mcastPort = mcast_port;
		serverIsRunning = true;
	}

	public void run() {

		InetAddress address;
		DatagramPacket packet;
		DatagramSocket socket;

		try {
			address = InetAddress.getByName(mcastAddress);
			socket = new DatagramSocket();

			byte[] data;

			while (true) {
				Thread.sleep(1000);

				String str = new String("PORT:60001");
				data = str.getBytes();
				packet = new DatagramPacket(data, str.length(), address, Integer.parseInt(mcastPort));
				System.out.println("Sending <" + str + ">");
				socket.send(packet);
			}

		} catch (IOException e) {
			serverIsRunning = false;
			e.printStackTrace();

		} catch (InterruptedException e) {
			serverIsRunning = false;
			e.printStackTrace();
		}
	}
}

