package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

import static Backup.Utilities.convertByteArrayToSring;


public class Msg_Putchunk extends PBMessage {
    private byte[] headerData;
    private byte[] chunkData;
    private int chunkNo;
    private int replicationDegree;
    private byte[] dataToBeSent;

    // Received message constructor
    Msg_Putchunk(byte[] inputData) throws InvalidStateException {
        super("PUTCHUNK");

        int it = 0;
        int terminators = 0;

        while (true) {
            if (it >= inputData.length){
                throw new InvalidStateException("Message Error!");
            }

            /* 0xDA */
            if(inputData[it] == TERMINATOR) {
                if (terminators == 0){
                    // -2 ignore space + 0xDA
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

        if (splitHeader.length == 5){
            version = splitHeader[1];
            fileId  = splitHeader[2];
            chunkNo = Integer.parseInt(splitHeader[3]);
            replicationDegree =  Integer.parseInt(splitHeader[4]);
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
    Msg_Putchunk(Chunk chunk, int repDegree){
        super(PBMessage.PUTCHUNK);

        // PUTCHUNK <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF> <CRLF>
        String header = PBMessage.PUTCHUNK + PBMessage.SEPARATOR +
                chunk.getFileId() + PBMessage.SEPARATOR +
                chunk.getChunkNo() + PBMessage.SEPARATOR +
                repDegree + PBMessage.SEPARATOR +
                PBMessage.CRLF + PBMessage.SEPARATOR +
                PBMessage.CRLF + PBMessage.SEPARATOR;

        //dataToBeSent =

        //<Body>
        if (chunk.getChunkData() != null){
            
        }

    }

    @Override
	public byte[] getData(int type){
		switch (type){
			case 0:
				return headerData;
			case 1:
				return chunkData;
			default:
				System.out.println("Valid OPTION!");
		}
		return new byte[0];
	}
}
