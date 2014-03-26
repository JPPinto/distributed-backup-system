package Backup;

import java.io.*;
import java.util.regex.Pattern;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.Chunk class
 */

class Chunk implements Serializable {
    private static final long serialVersionUID = -1665082267372566163L;
    private static final int chunkDataSize = 64000;
    private static final String chunkFileExtension = ".bin";

    private String fileId;
    private int chunkNo;
    private byte chunkData[];

    /**
     * Constructor for a chunk with data
     * */
    public Chunk(String fId, int cNo, byte[] data) throws IllegalStateException {
        setFileId(fId);
        setChunkNo(cNo);
        storeData(data);
    }

    /**
     * Constructor for a chunk with no data
     * */
    public Chunk(String fId, int cNo) throws IllegalStateException {
        this(fId, cNo, null);
    }

    /**
     * Constructor for chunk loading
     * @param fileName File name for loading
     * */
    public Chunk(String fileName){
        loadChunk(fileName);
    }

    /**
     * Sets the file id (sha256 hash)
     * @param fId file hash
     * */
    private void setFileId(String fId){
        Pattern hashPattern = Pattern.compile("[0-9A-F]{64}");

        if (fId.length() == 64 && hashPattern.matcher(fId).matches()){
            fileId = fId;
        } else {
            System.out.println(fId);
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
    void storeData(byte[] in){
        if (in.length <= chunkDataSize) {
            chunkData = in;
        } else {
            throw new IllegalStateException("Data too big for chunk!");
        }
    }

    /**
     * Loads chunk from file
     * @param chunkFileName output folder name
     * */
    public static Chunk loadChunk(String chunkFileName){
        try {
            FileInputStream fileIn = new FileInputStream(chunkFileName + chunkFileExtension);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            Chunk temp = (Chunk) in.readObject();
            in.close();
            fileIn.close();

            return temp;

        } catch(IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException i) {
            i.printStackTrace();
        }

        return null;
    }

    /**
     * Saves the chunk to a file
     * @param outputFolderName output folder name
     * */
    public void write(String outputFolderName){
        try {
            FileOutputStream fileOut = new FileOutputStream(outputFolderName + "/" + getChunkFileName());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch(IOException i) {
            i.printStackTrace();
        }
    }

    public boolean isTheFinalChunk(){
        if (chunkData == null || chunkData.length < chunkDataSize){
            return true;
        }

        return false;
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
    String getChunkFileName(){
        return fileId + "-" + chunkNo + chunkFileExtension;
    }
}
