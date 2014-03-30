package Backup;

import java.util.Date;

import java.io.IOException;
import java.io.Serializable;
import java.io.*;

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
    private Date lastModificationDate;
    private int desiredReplicationDegree;
    private int currentReplicationDegree;


    LocalFile(String fileName, String fileHash, Date lastModificationDate){
        if (fileName.length() <1) {
            throw new IllegalStateException("Invalid file name!");
        }

        this.fileName = fileName;
        this.fileHash = fileHash;
        this.lastModificationDate = lastModificationDate;
    }

    LocalFile(File input) throws IOException {
        fileSize = input.length();
        fileName = input.getName();
        fileHash = Utilities.getHashFromFile(input);

        long lastMod = input.lastModified();
        lastModificationDate = new Date(lastMod);
    }

    public boolean restoreFileFromChunks(String new_file_location) throws IOException {

        final String filePath = new_file_location + fileName;

        // Get buffered output stream for file writing
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        // The Chunk Joiner
        int currentChunkNo = 0;
        String currentChunkName;
        Chunk currentChunk = null;

		int i = 0;
        do {
			System.out.println("Chunk Nº: " + i);
			i++;
            // Load chunk from file
            currentChunkName = PotatoBackup.backupDirectory + "/"+ fileHash + "-" + currentChunkNo + Chunk.chunkFileExtension;
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
            System.out.println("The restored file hash matches!");
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

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public int getNumberOfChunks(){
        if (fileSize % 64000 == 0){
            return (int) ((fileSize/64000) + 1);
        } else {
            return (int) ((fileSize/64000) + 1);
        }
    }
}
