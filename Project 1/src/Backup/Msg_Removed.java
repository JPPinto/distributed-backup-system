package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Removed extends PBMessage {

    public Msg_Removed(byte[] inputData){
        super("REMOVED");
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
