package Backup;

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
    public Msg_Delete(byte[] inputData){
        super(DELETE);
        receivedMessage = true;
    }

    // Message to be sent constructor
    public Msg_Delete(String fId){
        super(DELETE);
        receivedMessage = false;

        String header = DELETE + SEPARATOR +
                fId + SEPARATOR +
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
