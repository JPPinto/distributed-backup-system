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

        /*Composing string which forms the server request/verify the correct usage of the server commands*/
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
        DatagramPacket packet;
        packet = new DatagramPacket(buf, buf.length, address, Integer.parseInt(args[1]));
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response/result
        String received = new String(packet.getData(), 0, packet.getLength());
        /*The server response becomes "ERROR" given the following conditions*/
        if(!received.matches("[0-9]+") && received.equals("NOT_FOUND"))
            received = "ERROR";
        System.out.println(request.replace('_', ' ') + " " +  received);

        socket.close();
    }
}
