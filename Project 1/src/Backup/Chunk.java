package Backup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.regex.Pattern;

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

    /**
     * Sets the file id (sha256 hash)
     * @param fId file hash
     * */
    private void setFileId(String fId){
        Pattern hashPattern = Pattern.compile("\\[0-9A-F]{64}");

        if (fId.length() == 64 && hashPattern.matcher(fId).matches()){
            fileId = fId;
        } else {
            throw new IllegalStateException("Invalid file hash!");
        }
    }

    /**
     * Sets the chunk number
     * @param chunkNoIn chunk number
     * */
    private void setChunkNo(int chunkNoIn){
        if (chunkNoIn < 0 || chunkNoIn > 999999) {
            throw new IllegalStateException("Invalid chunk number!");
        } else {
            chunkNo = chunkNoIn;
        }
    }

    /**
     * Sets the chunk data
     * @param in chunk data
     * */
    public void storeData(byte[] in){
        if (in.length <= chunkDataSize) {
            chunkData = in;
        } else {
            throw new IllegalStateException("Data too big for chunk!");
        }
    }

    /**
     * Saves the chunk to a file
     * @param fileName file name
     * */
    public void save(String fileName){
        // TODO use the getChunkFileName Method
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Returns the chunk number
     * @return chunk number
     * */
    public int getChunkNo() {
        return chunkNo;
    }

    /**
     * Returns the file hash
     * @return original file hash
     * */
     public String getFileId() {
        return fileId;
    }

    /**
     * Returns the chunk data
     * @return chunk data
     * */
    public byte[] getChunkData(){
        return chunkData;
    }

    /**
     * Returns the chunk file name
     * @return chunk file name
     * */
    public String getChunkFileName(){
        return fileId + "-" + chunkNo + ".bin";
    }
}
