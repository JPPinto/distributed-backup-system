package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Putchunk class
 *
 * Syntax:
 * PUTCHUNK <Version> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF> <Body>
 */

import sun.plugin.dom.exception.InvalidStateException;

import static Backup.Utilities.*;

public class Msg_Putchunk extends PBMessage {
    private byte[] headerData;
    private byte[] chunkData;
    private int chunkNo;
    private int replicationDegree;
    private byte[] dataToBeSent;
    private Chunk receivedChunk;

    // Received message constructor
    Msg_Putchunk(byte[] inputData, int packetLenght) throws InvalidStateException {
        super(PUTCHUNK);
        receivedMessage = true;

        header = getHeaderFromMessage(inputData);

        // Decode header
        String[] splitHeader = header.split(" ");

        if (splitHeader.length == 5) {
            if (!splitHeader[0].equals(PUTCHUNK)) {
                throw new InvalidStateException("Invalid Message!");
            }

            if (!validateVersion(splitHeader[1])) {
                throw new InvalidStateException("Invalid Message Version!");
            }

            if (!validateFileId(splitHeader[2])) {
                throw new InvalidStateException("Invalid Message file ID!");
            }

            if (!validateChunkNo(Integer.parseInt(splitHeader[3]))) {
                throw new InvalidStateException("Invalid Message chunk number!");
            }

            if (!validateReplicationDeg(Integer.parseInt(splitHeader[4]))) {
                throw new InvalidStateException("Invalid Message replication degree!");
            }

            version = splitHeader[1];
            fileId = splitHeader[2];
            chunkNo = Integer.parseInt(splitHeader[3]);
            replicationDegree = Integer.parseInt(splitHeader[4]);
        } else {
            throw new InvalidStateException("Invalid Message too many fields: " + splitHeader.length);
        }

        chunkData = getBodyFromMessage(inputData, packetLenght);
        // Get the chunk data if it exists
        if (chunkData == null) {
            receivedChunk = new Chunk(fileId, chunkNo);
            //receivedChunk.write("pasta");

        } else {
            receivedChunk = new Chunk(fileId, chunkNo, chunkData);
            //receivedChunk.write("pasta");
        }

    }

    // Message to be sent constructor
    Msg_Putchunk(Chunk chunk, int repDegree) {
        super(PUTCHUNK);
        receivedMessage = false;
        fileId = chunk.getFileId();
        chunkNo = chunk.getChunkNo();
        replicationDegree = repDegree;

        String[] stringArray = new String[5];

        stringArray[0] = PUTCHUNK;
        stringArray[1] = version;
        stringArray[2] = fileId;
        stringArray[3] = Integer.toString(chunkNo);
        stringArray[4] = Integer.toString(replicationDegree);

        headerData = constructHeaderFromStringArray(stringArray);
        chunkData = chunk.getChunkData();

        //<Body>
        if (chunkData != null) {
            dataToBeSent = joinTwoArrays(headerData, chunkData);
        } else {
            dataToBeSent = headerData;
        }

    }

    public byte[] getData() {
        return dataToBeSent;
    }

    @Override
    public void saveChunk(String dir) {
        this.receivedChunk.write(dir);
    }

    @Override
    public int getIntAttribute(int type) {
        switch (type) {
            case 0:
                return chunkNo;
            case 1:
                return replicationDegree;
            default:
                System.out.println("Valid OPTION!");
        }
        return 0;
    }

    @Override
    public byte[] getData(int type) {
        switch (type) {
            case 0:
                return headerData;
            case 1:
                return chunkData;
            case 2:
                return getData();
            default:
                System.out.println("Valid OPTION!");
        }
        return new byte[0];
    }
}
