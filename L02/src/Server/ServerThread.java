package Server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

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
    // Params
    private String serverPort, mcastAddress, mcastPort;

    private byte[] helloMessage = ("HELLO").getBytes();
    private InetSocketAddress destAddress = null;

    protected DatagramSocket socket = null;
    protected boolean serverIsRunning = false;

    private HashMap<String, String> dataBase = new HashMap<String, String>();

    public ServerThread(String srvc_port, String mcast_addr, String mcast_port) {
        serverPort = srvc_port;
        mcastAddress = mcast_addr;
        mcastPort = mcast_port;

        try {
            initializeConnection();
        } catch (IOException e) {
            System.out.println("Could not create server.");
        }

        serverIsRunning = true;
    }

    public void run(){
        while(serverIsRunning) {
            try {
                sleep(1000);
                byte[] buf = new byte[256];

                /* receive request */
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String input, response;
                input = new String(buf, "UTF-8");

                /* Split input */
                String[] commandArray = input.split(" ");
                int numberOfArgs = Array.getLength(commandArray);

                /* DEBUG */
                System.out.println("Input: " + input);
                System.out.println("Number of args: " + Integer.toString(numberOfArgs));
                /* DEBUG END */

                /* figure out response */
                switch (numberOfArgs){
                    case 2:
                        if (commandArray[0].equals("lookup")) {
                            /*Isolating plate number from unknown caracter from empty buf positions*/
                            commandArray[1] = commandArray[1].substring(0,8);
                            response = lookupPlate(commandArray[1]);
                            break;
                        }

                    case 3:
                        if (commandArray[0].equals("register")) {
                            commandArray[1] = commandArray[1].replace('_', ' ');
                            /*Isolating plate number from unknown characters from empty buf positions*/
                            commandArray[2] = commandArray[2].substring(0,8);
                            response = registerPlate(commandArray[2], commandArray[1]); //Order of arguments was wrong - changed
                            break;
                        }

                    default:
                        response = "INVALID_INPUT";
                        break;
                }

                /* DEBUG */
                System.out.println("Response: " + response);
                /* DEBUG END */

                buf = response.getBytes();

                /* send the response to the client at "address" and "port" */
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
                serverIsRunning = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        closeConnection();
    }

    /**
     * Returns -1 if plate already exists, else returns the number of vehicles in the database.
     */
    private String registerPlate(String plate, String owner){
        if(dataBase.containsKey(plate))
            return "-1";
        dataBase.put(plate, owner);
        return Integer.toString(dataBase.size());
    }

    /**
     * Returns NOT_FOUND if plate doesn't exist, else returns the owners name.
     */
    private String lookupPlate(String plate){
        if(!dataBase.containsKey(plate))
            return "NOT_FOUND";
        String owner = dataBase.get(plate);
        return owner;
    }

    private void initializeConnection() throws IOException {
        destAddress = new InetSocketAddress(mcastAddress, Integer.parseInt(mcastPort));
        socket = new DatagramSocket(60000);
    }

    private void closeConnection(){
        socket.close();
    }
}
