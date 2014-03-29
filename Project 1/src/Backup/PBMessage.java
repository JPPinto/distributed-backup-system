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

abstract class PBMessage {

	//Types of Messages
	protected static final String PUTCHUNK = "PUTCHUNK";
	protected static final String STORED = "STORED";
	protected static final String DELETE = "DELETE";
	protected static final String CHUNK = "CHUNK";
	protected static final String GETCHUNK = "GETCHUNK";
	protected static final String REMOVED = "REMOVED";
	protected static final String SEPARATOR = " ";
	// Constants
    protected static final byte TERMINATOR_BYTE_1 = (byte) Integer.parseInt("D", 16);
    protected static final byte TERMINATOR_BYTE_2 = (byte) Integer.parseInt("A", 16);
	protected static String TERMINATOR = "\r\n";

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

	public abstract void saveChunk(String dir);

	public static PBMessage createMessageFromType(byte[] data, int lenght) {
		String type = getType(data);

		try {
			if (type.equals("PUTCHUNK")) {
				//System.out.println("PROCESSING, MESSAGE TYPE: PUTCHUNK");
				return new Msg_Putchunk(data, lenght);

			} else if (type.equals("DELETE")) {
				//System.out.println("PROCESSING, MESSAGE TYPE: DELETE");
				return new Msg_Delete(data, lenght);

			} else if (type.equals("STORED")) {
				//System.out.println("PROCESSING, MESSAGE TYPE: STORED");
				return new Msg_Stored(data, lenght);

			} else if (type.equals("REMOVED")) {
				//System.out.println("PROCESSING, MESSAGE TYPE: REMOVED");
				return new Msg_Removed(data, lenght);

			} else if (type.equals("CHUNK")) {
				//System.out.println("PROCESSING, MESSAGE TYPE: CHUNK");
				return new Msg_Chunk(data, lenght);

			} else if (type.equals("GETCHUNK")) {
				//System.out.println("PROCESSING, MESSAGE TYPE: GETCHUNK");
				return new Msg_Getchunk(data, lenght);
			}
		}catch(InvalidStateException e){
			e.printStackTrace();
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

	public boolean getMessageReceivedBool() {
		return receivedMessage;
	}

	public static String getHeaderFromMessage(byte[] inputData) throws InvalidStateException {
		int it = 0;

		byte[] headerData = null;

		while (true) {
			if (it >= inputData.length) {
				throw new InvalidStateException("Message Error!");
			}

            /* 0xD 0xA */
			if (inputData[it] == PBMessage.TERMINATOR_BYTE_1 && inputData[it + 1] == PBMessage.TERMINATOR_BYTE_2 &&
                    inputData[it+2] == PBMessage.TERMINATOR_BYTE_1 && inputData[it + 3] == PBMessage.TERMINATOR_BYTE_2) {
				// ignore first 0xD
				headerData = Arrays.copyOfRange(inputData, 0, (it));
				break;
			}
			it++;
		}

		return Utilities.convertByteArrayToSring(headerData);
	}

	public static byte[] getBodyFromMessage(byte[] inputData, int length) throws InvalidStateException {
        System.out.println(Utilities.convertByteArrayToHex(inputData));

		int it = 0;
		int terminators = 0;

		while (true) {
			if (it >= length) {
				throw new InvalidStateException("Message Error!");
			}

            /* 0xD 0xA */
            if (inputData[it] == PBMessage.TERMINATOR_BYTE_1 && inputData[it + 1] == PBMessage.TERMINATOR_BYTE_2 &&
                    inputData[it+2] == PBMessage.TERMINATOR_BYTE_1 && inputData[it + 3] == PBMessage.TERMINATOR_BYTE_2) {
                //
                it+=4;
                break;
            }

			it++;
		}

		// Get the body data if it exists
		if (it == length) {
			// No body found
			return null;

		} else {
			return Arrays.copyOfRange(inputData, it, length);
		}

	}

    protected static byte[] constructHeaderFromStringArray(String[] in){
        String resultS="";
        boolean first = true;

        for (String a :in){
            if(!first){
                resultS+=" ";
            } else {
                first = false;
            }

            resultS+=a;
        }

        resultS+=TERMINATOR;
        resultS+=TERMINATOR;

        return(Utilities.convertStringToByteArray(resultS));
    }
}
