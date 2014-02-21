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
 */
public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean serverIsRunning = false;

    publc ServerThread() {

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
