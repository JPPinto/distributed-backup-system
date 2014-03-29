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
        super(GETCHUNK);
        receivedMessage = true;

        header = getHeaderFromMessage(inputData);

        // Decode header
        String[] splitHeader = header.split(" ");

        if (splitHeader.length == 4){
            if (!splitHeader[0].equals(GETCHUNK)){
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
        super(GETCHUNK);
        receivedMessage = false;

        String header = GETCHUNK + SEPARATOR +
                version + SEPARATOR +
                chunk.getFileId() + SEPARATOR +
                chunk.getChunkNo() + SEPARATOR +
				TERMINATOR + TERMINATOR + SEPARATOR;

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
