package Backup;

import java.util.Arrays;

import static Backup.PotatoBackup.convertByteArrayToHex;

public class Msg_Putchunk extends PBMessage {
    private byte[] headerData;
    private byte[] chunkData;

    Msg_Putchunk(byte[] inputData){
        super("PUTCHUNK");

        int it = 0;
        int terminators = 0;
        String messageHeader = "";

        while (true) {
            messageHeader = messageHeader + String.valueOf(inputData[it]);
            it++;

            /* 0xDA */
            if(inputData[it] == TERMINATOR) {
                headerData = Arrays.copyOfRange(inputData, 0, it - 1);
                System.out.println("FOUND IT");
                terminators++;
            }

            if (terminators == 2) {
                break;
            }

        }

        // Receive data
        it++;
        chunkData = Arrays.copyOfRange(inputData, it, inputData.length);

        // Decode header
        String header = convertByteArrayToHex(headerData);

        String[] splitHeader = header.split(" ");
    }
}
