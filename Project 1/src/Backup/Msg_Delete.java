package Backup;

import sun.plugin.dom.exception.InvalidStateException;

/**
 * SDIS TP1
 * <p/>
 * Eduardo Fernandes
 * José Pinto
 * <p/>
 * Backup.Msg_Delete class
 * <p/>
 * Syntax:
 * DELETE <FileId> <CRLF><CRLF>
 */
public class Msg_Delete extends PBMessage {
    byte[] data;

    // Received message constructor
    public Msg_Delete(byte[] inputData, int packetLenght) {
        super(DELETE);
        receivedMessage = true;

        header = getHeaderFromMessage(inputData);

        // Decode header
        String[] splitHeader = header.split(" ");

        if (splitHeader.length == 2) {                                            //Corrected size of string
            if (!splitHeader[0].equals(DELETE)) {
                throw new InvalidStateException("Invalid Message!");
            }

            if (!Utilities.validateFileId(splitHeader[1])) {
                throw new InvalidStateException("Invalid Message file ID!");
            }

            fileId = splitHeader[1];
        } else {
            throw new InvalidStateException("Invalid Message!");
        }
    }

    // Message to be sent constructor
    public Msg_Delete(String fId) {
        super(DELETE);
        receivedMessage = false;
        fileId = fId;

        String[] stringArray = new String[2];
        stringArray[0] = DELETE;
        stringArray[1] = fId;

        data = constructHeaderFromStringArray(stringArray);
    }


    @Override
    public void saveChunk(String dir) {
    }

    @Override
    public int getIntAttribute(int type) {
        return 0;
    }

    @Override
    public byte[] getData(int type) {
        return data;
    }
}
