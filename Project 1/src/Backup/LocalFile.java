package Backup;

import java.util.Date;

import java.io.IOException;
import java.io.Serializable;
import java.io.*;
import java.nio.file.Files;
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
class LocalFile implements Serializable {
    private String fileName;
    private String fileHash;
    private long fileSize; // File size in bytes
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

    LocalFile(File input) throws IOException {
        fileSize = input.length();
        fileName = input.getName();
        fileHash = Utilities.getHashFromFile(input);
        // TODO missing dates

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
            currentChunk = Chunk.loadChunk(currentChunkName);

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

    public int getNumberOfChunks(){
        if (fileSize % 64000 == 0){
            return (int) ((fileSize/6400) + 1);
        } else {
            return (int) ((fileSize/6400));
        }
    }
}
