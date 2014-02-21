package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
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

                /* receive request */
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                /* figure out response */
                String input, response = "AD";
                input = buf.toString();

                /* Split input */
                String[] commandArray = input.split(" ");
                int numberOfArgs = Array.getLength(commandArray);

                /* DEBUG */
                System.out.println("Input: " + input);
                System.out.println("Number of args: " + Integer.toString(numberOfArgs));

                /* DEBUG END */

                buf = response.getBytes();

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

    /**
     * Returns -1 if plate already exists, else returns the number of vehicles in the database.
     */
    private String registerPlate(String plate){

        return "-1";
    }

    /**
     * Returns NOT_FOUND if plate doesn't exist, else returns the driver name.
     */
    private String lookupPlate(String plate){

        return "NOT_FOUND";
    }

    private void initializeConnection() throws IOException {
        socket = new DatagramSocket(60000);
    }

    private void closeConnection(){
        socket.close();
    }
}
