import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Jose on 07-03-2014.
 */

public class ServerThread extends Thread {

    private HashMap<String, String> dataBase = new HashMap<String, String>();

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(345);
             Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String inputLine, outputLine;

            /* Wait for input */
            while ((inputLine = in.readLine()) != null) {
                /* Show the input (DEBUG) */
                System.out.println("Input: " + inputLine);
                /* Process the input */
                outputLine = getResponce(inputLine);
                /* Print the response (DEBUG) */
                System.out.println("Response: "); //+ outputLine);
                /* Send the response */
                out.println(outputLine);
            }

            /* Close sockets */
            serverSocket.close();

        } catch (IOException e) {
            System.out.println("Error with sockets: ");
            e.printStackTrace();
        }
    }

    public String getResponce(String s){

        String response;

        /* Split input */
        String[] commandArray = s.split(" ");
        int numberOfArgs = Array.getLength(commandArray);

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
        return response;
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
}
