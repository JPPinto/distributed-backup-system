package Backup;

import java.io.Serializable;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Chunk class
 */

public class Chunk implements Serializable {
    private static int chunkDataSize = 64000;

    private String fileId;
    private int chunkNo;
    private byte chunkData[];

    public Chunk(String fId, int cNo) throws IllegalStateException {
        setFileId(fId);
        setChunkNo(cNo);
        chunkData = null;
    }

    public Chunk(String fId, int cNo, byte[] data) throws IllegalStateException {
        setFileId(fId);
        setChunkNo(cNo);
        storeData(data);
    }

    private void setFileId(String fId){
        if (fId.length() == 64){
            fileId = fId;
        } else {
            throw new IllegalStateException("Invalid file hash!");
        }

    }

    private void setChunkNo(int chunkNoIn){
        if (chunkNoIn < 0 || chunkNoIn > 999999) {
            throw new IllegalStateException("Invalid chunk number!");
        } else {
            chunkNo = chunkNoIn;
        }
    }

    public void storeData(byte[] in){
        if (in.length <= chunkDataSize) {
            chunkData = in;
        } else {
            throw new IllegalStateException("Data too big for chunk!");
        }
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileId() {
        return fileId;
    }

    public byte[] getChunkData(){
        return chunkData;
    }

    public String getChunkFileName(){
        return fileId + "-" + chunkNo + ".bin";
    }
}
