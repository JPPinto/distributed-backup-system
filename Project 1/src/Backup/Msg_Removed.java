package Backup;

import sun.plugin.dom.exception.InvalidStateException;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Removed class
 *
 * Message syntax:
 * REMOVED <Version> <FileId> <ChunkNo> <CRLF><CRLF>
 */

public class Msg_Removed extends PBMessage {
    private byte[] data;
	private int chunkNo;

    // Received message constructor
    public Msg_Removed(byte[] inputData, int packetLenght){
        super(REMOVED);
        receivedMessage = true;
        data = inputData;

        header = getHeaderFromMessage(inputData);

		// Decode header
		String[] splitHeader = header.split(" ");

		if (splitHeader.length == 4){
			if (!splitHeader[0].equals(REMOVED)){
				throw new InvalidStateException("Invalid Message!");
			}

			if(!validateVersion(splitHeader[1])){
				throw new InvalidStateException("Invalid Message Version!");
			}

			if(!Utilities.validateFileId(splitHeader[2])){
				throw new InvalidStateException("Invalid Message file ID!");
			}

			if(!Utilities.validateChunkNo(Integer.parseInt(splitHeader[3]))){
				throw new InvalidStateException("Invalid Message chunk number!");
			}

			version = splitHeader[1];
			fileId  = splitHeader[2];
			chunkNo = Integer.parseInt(splitHeader[3]);
		} else {
			throw new InvalidStateException("Invalid Message!");
		}
    }

    // Message to be sent constructor
    public Msg_Removed(String fId, int cNo){
        super(REMOVED);
        receivedMessage = false;
		fileId = fId;
		chunkNo = cNo;

        String[] stringArray = new String[4];
        stringArray[0] = REMOVED;
        stringArray[1] = version;
        stringArray[2] = fId;
        stringArray[3] = Integer.toString(cNo);

        data = constructHeaderFromStringArray(stringArray);
    }

	@Override
	public void saveChunk(String dir){
	}

	@Override
	public int getIntAttribute(int type){
		return 0;
	}

    @Override
    public byte[] getData(int type){
        return data;
    }
}
