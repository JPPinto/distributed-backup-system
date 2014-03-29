package Backup;

import sun.plugin.dom.exception.InvalidStateException;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Stored class
 *
 * Message syntax:
 * STORED <Version> <FileId> <ChunkNo> <CRLF><CRLF>
 */
public class Msg_Stored extends PBMessage {
    private byte[] data;
	private int chunkNo;

    // Received message constructor
    public Msg_Stored(byte[] inputData, int packetLenght) throws  InvalidStateException {
        super(PBMessage.STORED);
        receivedMessage = true;

        //Header
        data = inputData;
        header = getHeaderFromMessage(data);

        // Decode header
        String[] splitHeader = header.split(" ");

        if (splitHeader.length == 4) {											//Corrected size of string
            if (!splitHeader[0].equals(PBMessage.STORED)){
                throw new InvalidStateException("Invalid Message!");
            }

            if(!validateVersion(splitHeader[1])){
                throw new InvalidStateException("Invalid Message Version!");
            }

            if(!validateFileId(splitHeader[2])){
                throw new InvalidStateException("Invalid Message file ID!");
            }

            if(!validateChunkNo(Integer.parseInt(splitHeader[3]))){
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
    public Msg_Stored(String fId, int cNo){
        super(STORED);
        receivedMessage = false;
        chunkNo = cNo;

        String[] stringArray = new String[4];
        stringArray[0] = STORED;
        stringArray[1] = version;
        stringArray[2] = fId;
        stringArray[3] = Integer.toString(chunkNo);

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
