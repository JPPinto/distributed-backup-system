package Backup; /**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.PBMessage class
 */
import java.util.regex.Pattern;

abstract class PBMessage {
    // Constants
    protected static final byte TERMINATOR = (byte) Integer.parseInt("DA", 16);
    protected String version = "1.0";
    // Message Type
    protected String messageType;
    public String fileId;
    protected boolean validMessage;


    public PBMessage(String kind) {
        messageType = kind;
    }

    public String getType() {
        return messageType;
    }

    public static String getType(byte[] bs) {
        int i = 0;
        String type = "";

        while (i < 14) {
            type += (char) (bs[i] & 0xFF);
            i++;
        }

        String[] str = type.split(" ");


        if (validateMsgType(str[0])) {
            return str[0];
        }

        return "INVALID_MSG";
    }

    public static PBMessage createMessageFromType(byte[] data){
        String type = getType(data);

        if(type.equals("PUTCHUNK")){
            System.out.println("MESSAGE TYPE: PUTCHUNK");
            return new Msg_Putchunk(data);

        } else if(type.equals("DELETE")){
            System.out.println("MESSAGE TYPE: DELETE");
            return new Msg_Delete(data);

        } else if(type.equals("STORED")){
            System.out.println("MESSAGE TYPE: STORED");
            return new Msg_Stored(data);

        } else if(type.equals("REMOVED")){
            System.out.println("MESSAGE TYPE: REMOVED");
            return new Msg_Removed(data);

        } else if(type.equals("CHUNK")){
            System.out.println("MESSAGE TYPE: CHUNK");
            return new Msg_Chunk(data);

        } else if(type.equals("GETCHUNK")){
            System.out.println("MESSAGE TYPE: GETCHUNK");
            return new Msg_Getchunk(data);
        }
        return null;
    }

    private static boolean validateMsgType(String m) {
        System.out.println(m);
        Pattern p = Pattern.compile("DELETE|CHUNK|PUTCHUNK|GETCHUNK|STORED|REMOVED");

        return p.matcher(m).matches();
    }

    private boolean validateVersion(String v) {

        /* Make use of pattern to check if input string (e.g.: 1.0, 2.4, 1.0.1)*/
        Pattern p = Pattern.compile("[0-9]+(\\.[0-9])+");

        return p.matcher(v).matches();
    }

    private boolean validateFileId(String f) {
        // 64 ASCII (Hex A to F) character sequence
        Pattern p = Pattern.compile("[0-9A-F]{64}");

        return p.matcher(f).matches();
    }

    /*
     * Validate chunk number
     */
    private boolean validateChunkNo(int chunkNo) {
        return !(chunkNo < 0 || chunkNo > 999999);
    }

    /*
     * Validate replication degree (NOT COMPLETED check upper bond)?
     */
    private boolean validateReplicationDeg(int replicationDeg) {
        return !(replicationDeg < 0 || replicationDeg > 9);
    }

}
