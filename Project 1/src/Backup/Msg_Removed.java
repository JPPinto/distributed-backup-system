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

    public Msg_Removed(byte[] inputData){
        super(PBMessage.REMOVED);
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
