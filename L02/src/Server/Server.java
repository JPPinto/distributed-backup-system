package Server;

import java.io.IOException;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */
public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: client <srvc_port> <mcast_addr> <mcast_port>");
            System.exit(0);
        }

        new ServerThread(args[0], args[1], args[2]).start();
    }
}
