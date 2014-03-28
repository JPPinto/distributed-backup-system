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

    public Msg_Delete(byte[] inputData){
        super(PBMessage.DELETE);
    }

	@Override
	public int getIntAttribute(int type){
		return 0;
	}

    @Override
    public byte[] getData(int type){
        return null;
    }
}
