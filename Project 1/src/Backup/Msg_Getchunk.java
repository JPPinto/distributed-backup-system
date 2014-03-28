package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

import static Backup.Utilities.convertByteArrayToSring;
import static Backup.Utilities.convertStringToByteArray;
import static Backup.Utilities.joinTwoArrays;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Getchunk class
 *
 * Syntax:
 * GETCHUNK <Version> <FileId> <ChunkNo> <CRLF><CRLF>
 */
public class Msg_Getchunk extends PBMessage {
    private byte[] data;
    private int chunkNo;

    // Received message constructor
    public Msg_Getchunk(byte[] inputData){
        super(PBMessage.GETCHUNK);
        receivedMessage = true;

        int it = 0;
        int terminators = 0;

        // Split body from header
        while (true) {
            if (it >= inputData.length){
                throw new InvalidStateException("Message Error!");
            }

            /* 0xDA */
            if(inputData[it] == TERMINATOR) {
                if (terminators == 0){
                    // -1 ignore space + 0xDA
                    data = Arrays.copyOfRange(inputData, 0, (it - 1));
                }
                terminators++;
            }

            if (terminators == 2) {
            /* Advance the last terminator */
                it++;
                break;
            }

            it++;
        }

        String messageHeader = convertByteArrayToSring(data);

        // Decode header
        String[] splitHeader = messageHeader.split(" ");

        if (splitHeader.length == 4){
            if (!splitHeader[0].equals(PBMessage.GETCHUNK)){
                throw new InvalidStateException("Invalid Message!");
            }

            if(!validateVersion(splitHeader[1])){
                throw new InvalidStateException("Invalid Message Version!");
            }

            if(!validateFileId(splitHeader[2])){
                throw new InvalidStateException("Invalid Message file ID!");
            }

            if(!validateChunkNo(Integer.parseInt(splitHeader[3]))){
                throw new InvalidStateException("Invalid Message chunk number!");
            }

            version = splitHeader[1];
            fileId  = splitHeader[2];
            chunkNo = Integer.parseInt(splitHeader[3]);
        } else {
            throw new InvalidStateException("Invalid Message!");
        }
    }

    // Message to be sent constructor
    public Msg_Getchunk(Chunk chunk){
        super(PBMessage.GETCHUNK);
        receivedMessage = false;

        String header = PBMessage.GETCHUNK + PBMessage.SEPARATOR +
                version + PBMessage.SEPARATOR +
                chunk.getFileId() + PBMessage.SEPARATOR +
                chunk.getChunkNo() + PBMessage.SEPARATOR +
                PBMessage.CRLF + PBMessage.CRLF + PBMessage.SEPARATOR;

        data = convertStringToByteArray(header);

    }

    @Override
	public int getIntAttribute(int type){
		return 0;
	}

    @Override
    public byte[] getData(int type){
        return data;
    }
}
