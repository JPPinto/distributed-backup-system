package Backup; /**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.PBMessage class
 */
import sun.plugin.dom.exception.InvalidStateException;

import java.util.Arrays;
import java.util.regex.Pattern;

import static Backup.Utilities.convertByteArrayToSring;

abstract class PBMessage {

	//Types of Messages
	protected static final String PUTCHUNK = "PUTCHUNK";
	protected static final String STORED = "STORED";
	protected static final String DELETE = "DELETE";
	protected static final String CHUNK = "CHUNK";
	protected static final String GETCHUNK = "GETCHUNK";
	protected static final String REMOVED = "REMOVED";
    protected static final String SEPARATOR = " ";
    protected static final String CRLF = "/r/n";
    // Constants
    protected static final byte TERMINATOR = (byte) Integer.parseInt("DA", 16);
    protected String version = "1.0";
    // Message Type
    protected String messageType;
    protected String header;
    public String fileId;
    protected boolean receivedMessage;


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

	public abstract int getIntAttribute(int type);

	abstract public byte[] getData(int type);

    public static PBMessage createMessageFromType(byte[] data) {
        String type = getType(data);

        if(type.equals("PUTCHUNK")){
            //System.out.println("PROCESSING, MESSAGE TYPE: PUTCHUNK");
            return new Msg_Putchunk(data);

        } else if(type.equals("DELETE")){
            //System.out.println("PROCESSING, MESSAGE TYPE: DELETE");
            return new Msg_Delete(data);

        } else if(type.equals("STORED")){
            //System.out.println("PROCESSING, MESSAGE TYPE: STORED");
            return new Msg_Stored(data);

        } else if(type.equals("REMOVED")){
            //System.out.println("PROCESSING, MESSAGE TYPE: REMOVED");
            return new Msg_Removed(data);

        } else if(type.equals("CHUNK")){
            //System.out.println("PROCESSING, MESSAGE TYPE: CHUNK");
            return new Msg_Chunk(data);

        } else if(type.equals("GETCHUNK")){
            //System.out.println("PROCESSING, MESSAGE TYPE: GETCHUNK");
            return new Msg_Getchunk(data);
        }
        return null;
    }

    private static boolean validateMsgType(String m) {
        Pattern p = Pattern.compile("DELETE|CHUNK|PUTCHUNK|GETCHUNK|STORED|REMOVED");

        return p.matcher(m).matches();
    }

    protected boolean validateVersion(String v) {

        /* Make use of pattern to check if input string (e.g.: 1.0, 2.4, 1.0.1)*/
        Pattern p = Pattern.compile("[0-9]+(\\.[0-9])+");

        return p.matcher(v).matches();
    }

    protected boolean validateFileId(String f) {
        // 64 ASCII (Hex A to F) character sequence
        Pattern p = Pattern.compile("[0-9a-fA-F]{64}");

        return p.matcher(f).matches();
    }

    /*
     * Validate chunk number
     */
    protected boolean validateChunkNo(int chunkNo) {
        return !(chunkNo < 0 || chunkNo > 999999);
    }

    /*
     * Validate replication degree (NOT COMPLETED check upper bond)?
     */
    protected boolean validateReplicationDeg(int replicationDeg) {
        return !(replicationDeg < 0 || replicationDeg > 9);
    }

    public boolean getMessageReceivedBool(){
        return receivedMessage;
    }

    public static String getHeaderFromMessage(byte[] inputData) throws InvalidStateException {
        int it = 0;
        int terminators = 0;

        byte[] headerData = null;

        while (true) {
            if (it >= inputData.length){
                throw new InvalidStateException("Message Error!");
            }

            /* 0xDA */
            if(inputData[it] == PBMessage.TERMINATOR) {
                if (terminators == 0){
                    // -1 ignore space + 0xDA
                    headerData = Arrays.copyOfRange(inputData, 0, (it - 1));
                }
                terminators++;
            }

            if (terminators == 2) {
            /* Advance the last terminator */
                it++;
                break;
            }

            it++;
        }

        return Utilities.convertByteArrayToSring(headerData);
    }

    public static byte[] getBodyFromMessage(byte[] inputData) throws InvalidStateException {
        int it = 0;
        int terminators = 0;

        while (true) {
            if (it >= inputData.length){
                throw new InvalidStateException("Message Error!");
            }

            /* 0xDA */
            if(inputData[it] == TERMINATOR) {
                terminators++;
            }

            if (terminators == 2) {
            /* Advance the last terminator */
                it++;
                break;
            }

            it++;
        }

        // Get the body data if it exists
        if (it == inputData.length || (it + 1) == inputData.length){
            // No body found
            return null;

        } else {
            // Advance space between 0xDA and the body
            it++;
            return Arrays.copyOfRange(inputData, it, inputData.length);
        }

    }
}
