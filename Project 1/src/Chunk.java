/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Chunk class
 */
public class Chunk {
    private String fileId;
    private int chunkNo;
    private byte chunkData[];

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileId() {
        return fileId;
    }
}
