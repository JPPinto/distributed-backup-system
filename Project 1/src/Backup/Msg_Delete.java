package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Delete extends PBMessage {

    public Msg_Delete(byte[] inputData){
        super("DELETE");
    }

    @Override
    public byte[] getData(int type){
        return null;
    }
}
