package Backup.Peer;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */

import java.io.*;
import java.net.*;

public class Peer extends Thread{

	private int mcast_port;
	private String mcast_addr;

	private static final int MIN_PACKET_SIZE = 1024;

	Peer(int p, String ad){
		mcast_port = p;
		mcast_addr = ad;
	}

	public void run(){

		MulticastSocket mSocket = null;
		InetAddress iAddress;

		try{
			DatagramPacket packet;

			mSocket = new MulticastSocket(mcast_port);

			/*Joining Multicast Group*/
			iAddress = InetAddress.getByName(mcast_addr);
			mSocket.joinGroup(iAddress);

			byte[] buf = new byte[1024];
			packet = new DatagramPacket(buf, buf.length);

			while(true){
				// receive the packets
				mSocket.receive(packet);

				String str = new String(packet.getData());

				if(false){
					break;
				}
			}

			mSocket.leaveGroup(iAddress);

		}catch(IOException e){
			e.printStackTrace();

			mSocket.close();
		}


	}

    public String sendRequest(String request) throws IOException {

		byte[] buf = request.getBytes();

        DatagramSocket socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(mcast_addr);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, IPAddress, mcast_port);

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
