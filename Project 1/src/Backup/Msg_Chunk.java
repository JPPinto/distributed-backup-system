package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import static Backup.Utilities.joinTwoArrays;

/**
 * SDIS TP1
 * <p/>
 * Eduardo Fernandes
 * José Pinto
 * <p/>
 * Backup.Msg_Chunk class
 * <p/>
 * Syntax:
 * CHUNK <Version> <FileId> <ChunkNo> <CRLF><CRLF> <Body>
 */
public class Msg_Chunk extends PBMessage {
    private byte[] data;
    private byte[] headerData;
    private int chunkNo;
    private Chunk chunk;

    // Received message constructor
    public Msg_Chunk(byte[] inputData, int packetLenght) {
        super(PBMessage.CHUNK);
        receivedMessage = true;
        data = inputData;

        header = getHeaderFromMessage(inputData);

        // Decode header
        String[] splitHeader = header.split(" ");

        if (splitHeader.length == 4) {
            if (!splitHeader[0].equals(CHUNK)) {
                throw new InvalidStateException("Invalid Message!");
            }

            if (!validateVersion(splitHeader[1])) {
                throw new InvalidStateException("Invalid Message Version!");
            }

            if (!Utilities.validateFileId(splitHeader[2])) {
                throw new InvalidStateException("Invalid Message file ID!");
            }

            if (!Utilities.validateChunkNo(Integer.parseInt(splitHeader[3]))) {
                throw new InvalidStateException("Invalid Message chunk number!");
            }

            version = splitHeader[1];
            fileId = splitHeader[2];
            chunkNo = Integer.parseInt(splitHeader[3]);
        } else {
            throw new InvalidStateException("Invalid Message!");
        }

        byte[] body = getBodyFromMessage(inputData, packetLenght);
        // Get the chunk data if it exists
        if (body == null) {
            chunk = new Chunk(fileId, chunkNo);
        } else {
            chunk = new Chunk(fileId, chunkNo, body);
        }
    }

    // Message to be sent constructor
    public Msg_Chunk(Chunk c) {
        super(PBMessage.CHUNK);
        receivedMessage = false;
        chunk = c;
        fileId = chunk.getFileId();
        version = "1.0";
        chunkNo = chunk.getChunkNo();

        String[] stringArray = new String[4];
        stringArray[0] = CHUNK;
        stringArray[1] = version;
        stringArray[2] = chunk.getFileId();
        stringArray[3] = Integer.toString(chunk.getChunkNo());

        headerData = constructHeaderFromStringArray(stringArray);
        data = joinTwoArrays(headerData, chunk.getChunkData());
    }

    @Override
    public void saveChunk(String dir) {
        chunk.write(dir);
    }

    @Override
    public int getIntAttribute(int type) {
        return chunkNo;
    }

    @Override
    public byte[] getData(int type) {
        return data;
    }
}
