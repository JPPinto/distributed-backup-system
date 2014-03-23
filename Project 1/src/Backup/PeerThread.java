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

public class PeerThread extends Thread{

	private int mCastPort;
	private String mCastAddress;

	private static final int packetSize = 65536;


	PeerThread(int port, String address){
		mCastPort = port;
		mCastAddress = address;
	}

	public void run(){

		MulticastSocket mSocket = null;
		InetAddress iAddress;

		try{
			DatagramPacket packet;

			mSocket = new MulticastSocket(mCastPort);

			/* Join the Multicast Group */
			iAddress = InetAddress.getByName(mCastAddress);
			mSocket.joinGroup(iAddress);

			byte[] buf = new byte[packetSize];
			packet = new DatagramPacket(buf, buf.length);

			// receive the packets
			mSocket.receive(packet);

            PBPacket receivedPacket = new PBPacket(packet.getData());

			mSocket.leaveGroup(iAddress);

		}catch(IOException e){
			e.printStackTrace();

			mSocket.close();
		}


	}

    public String sendRequest(String request) throws IOException {

		byte[] buf = request.getBytes();

        DatagramSocket socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(mCastAddress);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, IPAddress, mCastPort);

        // send request
        socket.send(packet);

        // get response
        byte[] buf_received = new byte[1024];
        packet = new DatagramPacket(buf_received, buf_received.length);

        socket.receive(packet);

        socket.close();

        // return response/result
        return new String(packet.getData(), 0, packet.getLength());
    }
}
