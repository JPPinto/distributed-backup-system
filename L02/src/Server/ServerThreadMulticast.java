package Server;

import java.io.IOException;
import java.net.*;

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
public class ServerThreadMulticast extends Thread {
    // Params
    private String serverPort, mcastAddress, mcastPort;

    private byte[] helloMessage = ("HELLO").getBytes();
    private InetSocketAddress destinationAddress = null;
    private DatagramPacket helloPacket = null;

    protected MulticastSocket socket = null;
    protected boolean serverIsRunning = false;


    public ServerThreadMulticast(String srvc_port, String mcast_addr, String mcast_port) {
        serverPort = srvc_port;
        mcastAddress = mcast_addr;
        mcastPort = mcast_port;

        try {
            //initializeConnection();

            socket = new MulticastSocket(Integer.parseInt(serverPort));
            socket.setTimeToLive(1);

            destinationAddress = new InetSocketAddress(mcastAddress, Integer.parseInt(mcastPort));

            helloPacket = new DatagramPacket(helloMessage, helloMessage.length, destinationAddress);
        } catch (IOException e) {
            System.out.println("Could not create server.");
            e.printStackTrace();
        }

        serverIsRunning = true;
    }

    public void run(){
        while(serverIsRunning) {
            try {
                sleep(1000);
                socket.send(helloPacket);

            } catch (IOException e) {
                serverIsRunning = false;
                e.printStackTrace();

            } catch (InterruptedException e) {
                serverIsRunning = false;
                e.printStackTrace();
            }
        }

        closeConnection();
    }

    private void initializeConnection() throws IOException {
        socket = new MulticastSocket(Integer.parseInt(serverPort));
        socket.setTimeToLive(1);

        destinationAddress = new InetSocketAddress(mcastAddress, Integer.parseInt(mcastPort));
    }

    private void closeConnection(){
        socket.close();
    }
}
