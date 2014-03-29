package Backup;

import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Delete class
 *
 * Syntax:
 * DELETE <FileId> <CRLF><CRLF>
 */
public class Msg_Delete extends PBMessage {
    byte[] data;

    // Received message constructor
    public Msg_Delete(byte[] inputData, int packetLenght){
        super(DELETE);
        receivedMessage = true;

        header = getHeaderFromMessage(inputData);
    }

    // Message to be sent constructor
    public Msg_Delete(String fId){
        super(DELETE);
        receivedMessage = false;

        String[] stringArray = new String[2];
        stringArray[0] = DELETE;
        stringArray[1] = fId;

        data = constructHeaderFromStringArray(stringArray);
    }


	@Override
	public void saveChunk(String dir){
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
