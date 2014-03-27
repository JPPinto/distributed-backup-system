package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

import static Backup.PotatoBackup.convertByteArrayToHex;

public class Msg_Putchunk extends PBMessage {
    private byte[] headerData;
    private byte[] chunkData;
    private int chunkNo;
    private int replicationDegree;

    Msg_Putchunk(byte[] inputData) throws InvalidStateException {
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

        // Decode header
        String[] splitHeader = messageHeader.split(" ");

        if (splitHeader.length == 5){
            version = splitHeader[1];
            fileId  = splitHeader[2];
            chunkNo = Integer.getInteger(splitHeader[3]);
            replicationDegree =  Integer.getInteger(splitHeader[4]);
        } else {
            throw new InvalidStateException("Invalid Message!");
        }

        // Get the chunk data if it exists
        if (it == inputData.length || (it + 1) == inputData.length){
            return;
        } else {
            // Advance space between 0xDA and the body
            it++;
            chunkData = Arrays.copyOfRange(inputData, it, inputData.length);
        }

    }
}
