package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Stored extends PBMessage {

	private int chunkNo;

    public Msg_Stored(byte[] inputData){
        super("STORED");
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
