package Backup;

/**
 * Created by Jose on 27-03-2014.
 */
public class Msg_Putchunk extends PBMessage {
	@Override
	public String getType(){
		return "PUTCHUNK";
	}
}
