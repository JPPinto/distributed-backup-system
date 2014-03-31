package Backup;



/**
 * SDIS TP1
 * <p/>
 * Eduardo Fernandes
 * José Pinto
 * <p/>
 * Backup.Msg_Removed class
 * <p/>
 * Message syntax:
 * REMOVED <Version> <FileId> <ChunkNo> <CRLF><CRLF>
 */

public class Msg_Removed extends PBMessage {
    private byte[] data;
    private int chunkNo;

    // Received message constructor
    public Msg_Removed(byte[] inputData, int packetLenght) {
        super(REMOVED);
        receivedMessage = true;
        data = inputData;

        header = getHeaderFromMessage(inputData);

        // Decode header
        String[] splitHeader = header.split(" ");

        if (splitHeader.length == 4) {
            if (!splitHeader[0].equals(REMOVED)) {
                throw new IllegalAccessError("Invalid Message!");
            }

            if (!validateVersion(splitHeader[1])) {
                throw new IllegalAccessError("Invalid Message Version!");
            }

            if (!Utilities.validateFileId(splitHeader[2])) {
                throw new IllegalAccessError("Invalid Message file ID!");
            }

            if (!Utilities.validateChunkNo(Integer.parseInt(splitHeader[3]))) {
                throw new IllegalAccessError("Invalid Message chunk number!");
            }

            version = splitHeader[1];
            fileId = splitHeader[2];
            chunkNo = Integer.parseInt(splitHeader[3]);
        } else {
            throw new IllegalAccessError("Invalid Message!");
        }
    }

    // Message to be sent constructor
    public Msg_Removed(String fId, int cNo) {
        super(REMOVED);
        receivedMessage = false;
        fileId = fId;
        chunkNo = cNo;

        String[] stringArray = new String[4];
        stringArray[0] = REMOVED;
        stringArray[1] = version;
        stringArray[2] = fId;
        stringArray[3] = Integer.toString(cNo);

        data = constructHeaderFromStringArray(stringArray);
    }

    @Override
    public void saveChunk(String dir) {
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
