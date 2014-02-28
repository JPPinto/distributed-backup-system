package Client;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {

        if (args.length < 4) {
            System.out.println("Usage: java mcast_addr mcast_port");
            System.exit(0);
        }

        // Create multicast socket, Used only for the first received packet
        MulticastSocket mSocket = new MulticastSocket(Integer.parseInt(args[1]));

        InetAddress iAddress = InetAddress.getByName(args[0]);
        mSocket.joinGroup(iAddress);

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        /*Composing string which forms the server request/verify the correct usage of the server commands*/
        String request = "";
        if(args[2].equals("register"))
            request = args[2] + " " + args[3].replace(' ','_') + " " +  args[4];
        else
        if(args[2].equals("lookup"))
            request = args[2] + " " + args[3].replace(' ','_');
        else {
            System.out.println("Usage: \"register\" or \"lookup\" comand not found!");
            System.exit(-1);
        }

        /*Initial message receive in order to determine the IP and Port on which to send*/
        byte[] bub_initial = new byte[1000];
        DatagramPacket packet_initial;
        packet_initial = new DatagramPacket(bub_initial, bub_initial.length);
        mSocket.receive(packet_initial);

        // send request
        byte[] buf = request.getBytes();
        DatagramPacket packet;
        /*Using the first packet's address and port*/
        packet = new DatagramPacket(buf, buf.length, packet_initial.getAddress(), packet_initial.getPort());
        socket.send(packet);

        // get response
        byte[] buf_received = new byte[1000];
        packet = new DatagramPacket(buf_received, buf_received.length);
        socket.receive(packet);

        // display response/result
        String received = new String(packet.getData(), 0, packet.getLength());
        /*The server response becomes "ERROR" given the following conditions*/
        if(!received.matches("[0-9]+") && received.equals("NOT_FOUND"))
            received = "ERROR";
        System.out.println(request.replace('_', ' ') + " " +  received);

        mSocket.close();
    }
}
