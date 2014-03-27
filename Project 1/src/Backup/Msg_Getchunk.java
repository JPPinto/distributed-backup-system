package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Getchunk extends PBMessage {

    public Msg_Getchunk(byte[] inputData){
        super("GETCHUNK");
    }
}
