package Backup;

import java.io.*;
import java.util.Date;

import static Backup.Utilities.getHashFromFile;

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

    public boolean restoreFileFromChunks(String restoredFileLocation) throws IOException {
        // TODO Check if all chunks exist


        final String filePath = restoredFileLocation + fileName;

        // Get buffered output stream for file writing
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        // The Chunk Joiner
        int currentChunkNo = 0;
        String currentChunkName;
        Chunk currentChunk = null;

        do {
            // Load chunk from file
            currentChunkName = fileHash + "-" + currentChunk;
            currentChunk = new Chunk(currentChunkName);

            // Check if this is the final chunk
            if (currentChunk.isTheFinalChunk()){
                // Check if the final chunk still has data
                if (currentChunk.getChunkData() != null){
                    bufferedOutputStream.write(currentChunk.getChunkData());
                }

                break;
            }

            // Write chunk data to output stream
            bufferedOutputStream.write(currentChunk.getChunkData());

            // On to the next one
            currentChunkNo++;

            // Every 100nth chunk flush the buffer
            if (currentChunkNo % 100 == 0) {
                bufferedOutputStream.flush();
                fileOutputStream.flush();
            }
        } while (true);

        bufferedOutputStream.close();
        fileOutputStream.close();

        // Check hash on the restored file
        File destination = new File(filePath);
        String writtenFileHash = getHashFromFile(destination);

        if (fileHash.equals(writtenFileHash)){
            return true;
        } else {
            System.out.println("The restored file hash doesn't match the original file hash.");
            return false;
        }
    }

    public boolean deleteFileChunks(){
        // TODO Delete chunks

        return true;
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
