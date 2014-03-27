package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Stored extends PBMessage {

    public Msg_Stored(byte[] inputData){
        super("STORED");
    }

    @Override
    public byte[] getData(int type){
        return null;
    }
}
