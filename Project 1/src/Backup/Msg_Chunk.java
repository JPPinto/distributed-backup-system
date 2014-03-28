package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

import static Backup.Utilities.convertByteArrayToSring;
import static Backup.Utilities.joinTwoArrays;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Chunk class
 *
 * Syntax:
 * CHUNK <Version> <FileId> <ChunkNo> <CRLF><CRLF> <Body>
 */
public class Msg_Chunk extends PBMessage {
    private byte[] data;
    private byte[] headerData;
    private byte[] chunkData;
    int chunkNo;

    // Received message constructor
    public Msg_Chunk(byte[] inputData){
        super(PBMessage.CHUNK);
        receivedMessage = true;
        inputData = data;

        int it = 0;
        int terminators = 0;

        while (true) {
            if (it >= inputData.length){
                throw new InvalidStateException("Message Error!");
            }

            /* 0xDA */
            if(inputData[it] == TERMINATOR) {
                if (terminators == 0){
                    // -1 ignore space + 0xDA
                    headerData = Arrays.copyOfRange(inputData, 0, (it - 1));
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

        String messageHeader = convertByteArrayToSring(headerData);

        // Decode header
        String[] splitHeader = messageHeader.split(" ");

        if (splitHeader.length == 4){
            if (!splitHeader[0].equals(PUTCHUNK)){
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

            if(!validateReplicationDeg(Integer.parseInt(splitHeader[4]))){
                throw new InvalidStateException("Invalid Message replication degree!");
            }

            version = splitHeader[1];
            fileId  = splitHeader[2];
            chunkNo = Integer.parseInt(splitHeader[3]);
        } else {
            throw new InvalidStateException("Invalid Message!");
        }

        // Get the chunk data if it exists
        if (it == inputData.length || (it + 1) == inputData.length){
            Chunk receivedChunk = new Chunk(fileId, chunkNo);
            //receivedChunk.write("pasta");

        } else {
            // Advance space between 0xDA and the body
            it++;
            chunkData = Arrays.copyOfRange(inputData, it, inputData.length);

            Chunk receivedChunk = new Chunk(fileId, chunkNo, chunkData);
            //receivedChunk.write("pasta");
        }
    }

    // Message to be sent constructor
    public Msg_Chunk(Chunk chunk){
        super(PBMessage.CHUNK);
        receivedMessage = false;

        String header = CHUNK + SEPARATOR +
                version + SEPARATOR +
                chunk.getFileId() + SEPARATOR +
                chunk.getChunkNo() + SEPARATOR +
                CRLF + CRLF + SEPARATOR;

        byte[] headerData = Utilities.convertStringToByteArray(header);

        data = joinTwoArrays(headerData, chunk.getChunkData());

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
