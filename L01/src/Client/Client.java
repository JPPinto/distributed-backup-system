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

        Scanner in = new Scanner(System.in);

        if (args.length != 1) {
            System.out.println("Usage: java Client ");
            return;
        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        String request = "";
        if(args[1].equals("REGISTER"))
            request = args[1] + args[2] + args[3];
        else
        if(args[1].equals("LOOKUP"))
            request = args[1] + args[2];

        // send request
        byte[] buf = request.getBytes();
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);

        socket.close();
    }
}
