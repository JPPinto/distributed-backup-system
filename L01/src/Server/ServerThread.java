package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramSocket;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */
public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean serverIsRunning = true;

    public ServerThread() throws IOException{
        this("Server.ServerThread");
    }

    publc ServerThread(String name) throws IOException {
        super(name);

        initializeConnection();
    }



    public void run(){
        while(serverIsRunning) {

        }

        closeConnection();
    }

    private void initializeConnection(){
        socket = new DatagramSocket(60000);
    }

    private void closeConnection(){
        socket.close();
    }
}
