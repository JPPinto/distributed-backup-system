package Server;

import java.io.IOException;

/**
 * SDIS Lab 01
 * Eduardo Fernandes
 * José Pinto
 */
public class Server {
    public static void main(String[] args) throws IOException {
        new ServerThread().start();
    }
}
