package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Getchunk class
 */
public class Msg_Getchunk extends PBMessage {

    public Msg_Getchunk(byte[] inputData){
        super("GETCHUNK");
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
