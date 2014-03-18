package Client;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {

        if (args.length < 4) {
            System.out.println("Usage: java mcast_addr mcast_port command param1 (param2)");
            System.exit(0);
        }

        /*Parse Input Arguments*/
        int port = Integer.parseInt(args[1]);
        String address = args[0];

        DatagramPacket packet_initial;

        // Create multicast socket, Used only for the first received packet
        MulticastSocket mSocket = new MulticastSocket(port);

        /*Joining Multicast Group*/
        InetAddress iAddress = InetAddress.getByName(address);
        mSocket.joinGroup(iAddress);

        /*Initial message receive in order to determine the IP and Port on which to send*/
        byte[] buf_initial = new byte[512];
        packet_initial = new DatagramPacket(buf_initial, buf_initial.length);

        // receive the packets
        mSocket.receive(packet_initial);
        // print connection
        String str = new String(packet_initial.getData());
        String mPort[] = str.split(":");
        System.out.print("Connect to MultiCast Server: " + "Port" + ":" + mPort[1] + "\n");

        mSocket.leaveGroup(iAddress);
        mSocket.close();

        /*Composing string which forms the server request/verify the correct usage of the server commands*/
        String request = getRequest(args);

        //Unicast, UDP request
        String received = sendRequest(mPort[1], request);

        /*The server response becomes "ERROR" given the following conditions
        * Handles and prints the server response */
        printServerResponse(request, received);
    }

    private static String sendRequest(String port, String request) throws IOException {

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");

        // send request
        byte[] buf = request.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, IPAddress, Integer.parseInt(port.substring(0,5)));
        socket.send(packet);

        // get response
        byte[] buf_received = new byte[1024];
        packet = new DatagramPacket(buf_received, buf_received.length);

        socket.receive(packet);

        socket.close();

        // display response/result
        return new String(packet.getData(), 0, packet.getLength());
    }

    private static void printServerResponse(String request, String received) {
        if(received.equals("NOT_FOUND") || received.equals("-1"))
            received = "ERROR";
        System.out.println(request.replace('_', ' ') + " " +  received);
    }

    private static String getRequest(String[] args) {
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
        return request;
    }
}
