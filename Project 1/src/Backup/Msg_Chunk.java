package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Msg_Chunk class
 *
 * Syntax:
 * CHUNK <Version> <FileId> <ChunkNo> <CRLF><CRLF> <Body>
 */
public class Msg_Chunk extends PBMessage {
    private byte[] data;
    //CHUNK <Version> <FileId> <ChunkNo> <CRLF><CRLF> <Body>

    // Received message constructor
    public Msg_Chunk(byte[] inputData){
        super(PBMessage.CHUNK);
        receivedMessage = true;
        inputData = data;

    }

    // Message to be sent constructor
    public Msg_Chunk(){
        super(PBMessage.CHUNK);
        receivedMessage = false;

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
