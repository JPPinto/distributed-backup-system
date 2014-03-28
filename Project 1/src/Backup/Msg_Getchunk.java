package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Getchunk class
 *
 * Syntax:
 * GETCHUNK <Version> <FileId> <ChunkNo> <CRLF><CRLF>
 */
public class Msg_Getchunk extends PBMessage {

    public Msg_Getchunk(byte[] inputData){
        super(PBMessage.GETCHUNK);
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
