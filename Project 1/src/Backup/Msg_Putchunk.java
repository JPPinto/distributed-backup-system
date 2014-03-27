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

        while (true) {
            /* 0xDA */
            if(inputData[it] == TERMINATOR) {
                // -2 ignore space + 0xDA
                headerData = Arrays.copyOfRange(inputData, 0, (it - 2));
                terminators++;
            }

            if (terminators == 2) {
            /* Advance the last terminator */
                it++;
                break;
            }

            it++;
        }

        String messageHeader = convertByteArrayToSring(headerData);

        // Receive data
        it++;
        chunkData = Arrays.copyOfRange(inputData, it, inputData.length);

        // Decode header
        String[] splitHeader = messageHeader.split(" ");

        for (int i=0; i < splitHeader.length; i++){
            System.out.println(splitHeader[i]);
        }
    }
}
