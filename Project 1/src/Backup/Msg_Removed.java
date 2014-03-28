package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Removed class
 *
 * Message syntax:
 * REMOVED <Version> <FileId> <ChunkNo> <CRLF><CRLF>
 */

public class Msg_Removed extends PBMessage {
    private byte[] data;

    // Received message constructor
    public Msg_Removed(byte[] inputData){
        super(REMOVED);
        receivedMessage = true;
        data = inputData;

        header = getHeaderFromMessage(inputData);
    }

    // Message to be sent constructor
    public Msg_Removed(String fId, int cNo){
        super(REMOVED);
        receivedMessage = false;

        String header = REMOVED + SEPARATOR +
                version + SEPARATOR +
                fId + SEPARATOR +
                cNo + SEPARATOR +
                CRLF + CRLF;

        data = Utilities.convertStringToByteArray(header);
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
