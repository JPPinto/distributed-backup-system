package Backup;

import java.util.Arrays;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Putchunk extends PBMessage {
    private byte[] raw_data;

    Msg_Putchunk(byte[] inputData){
        super("PUTCHUNK");

        int it = 0;
        String messageHeader = "";
        raw_data = inputData;

        while (true) {
            messageHeader = messageHeader + String.valueOf(inputData[it]);
            it++;

            /* Stop on first 0xD 0xA */
            if(inputData[it] == TERMINATOR) {
                System.out.println("FOUND IT");
                break;
            }

        }

        System.out.println("Decode em");
        /* Decode message header */
        validMessage = false; //decodeHeaderString(messageHeader);

        /* Get data block */
        if (validMessage) {
            it++;

            if (inputData[it] == 0xDA) {
                /* Data might be present */

                it++;
                byte[] data = Arrays.copyOfRange(inputData, it, inputData.length);

            }
        }
    }
}
