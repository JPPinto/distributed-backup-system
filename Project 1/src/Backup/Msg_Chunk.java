package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Chunk extends PBMessage {


    public Msg_Chunk(String fId, int cNo, byte[] chunkData){
        super("CHUNK");

    }


}
