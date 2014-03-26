package Backup;

import java.util.Date;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.LocalFile class
 */
class LocalFile {
    private String fileName;
    private String fileHash;
    private Date creationDate;
    private Date modificationDate;

    LocalFile(String fileName, String fileHash, Date creationDate, Date modificationDate){
        if (fileName.length() <1) {
            throw new IllegalStateException("Invalid file name!");
        }

        this.fileName = fileName;
        this.fileHash = fileHash;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    public void restoreFile(){
        // TODO Get all chunks

        // TODO Create output stream

        // TODO Join the chunks
        int currentChunkNo = 0;
        int numberOfChunks = 99;
        String currentChunkName = null;
        Chunk currentChunk = null;

        while (numberOfChunks < currentChunkNo){
            // Load chunk from file
            currentChunkName = fileHash + "-" + currentChunk;
            currentChunk = new Chunk(currentChunkName);

            // Write chunk data to output stream


            // On to the next one
            currentChunkNo++;
        }

    }

    public String getFileHash(){
        return fileHash;
    }

    public String getFileName() {
        return fileName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }
}
