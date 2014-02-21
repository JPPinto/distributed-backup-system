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

        if (args.length < 1) {
            System.out.println("Usage: java Client ");
            System.exit(0);
        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        String request = "";
        if(args[2].equals("register"))
            request = args[2] + " " + args[3].replace(' ','_') + " " +  args[4];
        else
        if(args[2].equals("lookup"))
            request = args[2] + " " + args[3].replace(' ','_');
        else {
            System.out.println("Usage: no \"register\" or \"lookup\" comand found!");
            System.exit(-1);
        }

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
        System.out.println(received);

        socket.close();
    }
}
