package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 *
 * Server Thread
 *
 * register
 * to register the association of a plate number to the owner. Returns -1 if the plate
 * number has already been registered; otherwise, returns the number of vehicles in the database.
 *
 * lookup
 * to obtain the owner of a given plate number. Returns the owner's name or the string
 * NOT_FOUND if the plate number was never registered.
 */
public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean serverIsRunning = false;

    public ServerThread() {

        try {
            initializeConnection();
        } catch (IOException e) {
            System.out.println("Cannot create server");
        }

        serverIsRunning = true;
    }

    public void run(){
        while(serverIsRunning) {
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // figure out response
                String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = "Hello";

                buf = dString.getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                serverIsRunning = false;
            }
        }

        closeConnection();
    }

    private void initializeConnection() throws IOException {
        socket = new DatagramSocket(60000);
    }

    private void closeConnection(){
        socket.close();
    }
}
