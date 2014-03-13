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

    public Chunk(String fId, int cNo) throws IllegalStateException {
        if (cNo < 0 || cNo > 999999) {
            throw new IllegalStateException("Invalid chunk number!");
        }

        fileId = fId;
        chunkNo = cNo;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileId() {
        return fileId;
    }
}
