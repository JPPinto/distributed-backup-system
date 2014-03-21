package Backup;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Entry point
 */
public class PotatoBackup {
    /* Buffer size for hashing operations 1MiB*/
    private static int bufferSize = 1048576;
    private static int chunkDataSize = 64000;

    /**
     * Entry Point
     * @param args Arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Potato Backup");

        try {
            processFileList(listFiles("."));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert byte array to hex string (Stack Overflow)
     * @param bytes Byte array to be converted to hex string
     */
    public static String convertByteArrayToHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
     * Calculate file SHA256 sum
     * @param inputFile Input file
     * @return Returns file SHA-256 hash.
     * */
    public static String getHashFromFile(File inputFile) throws IOException {
        // Get buffered stream from file
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] buffer = new byte[bufferSize];

            int sizeRead;

            while ((sizeRead = bufferedInputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
            }

            bufferedInputStream.close();

            byte[] hash = digest.digest();

            /* Convert to string */
            return convertByteArrayToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-256 Algorithm Not found! Aborting execution.");
            System.exit(-1);
        }

        // Make the compiler happy, since we never get to return the empty string
        return "";
    }

    /**
     * Reads a file and creates chunks
     * @param inputFile Input file
     **/
    public static void readChunks(File inputFile) throws IOException {
        // Get fileId
        String fileID = getHashFromFile(inputFile);

        // Get buffered stream from file
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        long fileSize = inputFile.length();

        byte[] buffer = new byte[chunkDataSize];

        int sizeRead;
        int currentChunkNumber = 0;

        while ((sizeRead = bufferedInputStream.read(buffer)) != -1) {
            Chunk currentChunk = new Chunk(fileID, currentChunkNumber, buffer);
            currentChunk.save("./backup");

            // Last chunk if file size not a multiple
            if (sizeRead < chunkDataSize) {
                // It's the final chunkdown
                // TODO SOMETHING
            } else {
                currentChunkNumber++;
            }

        }
        bufferedInputStream.close();

        if (fileSize % chunkDataSize == 0) {
            System.out.println("An empty chunk is needed");
            Chunk finalChunk = new Chunk(fileID, currentChunkNumber);
            finalChunk.save("./backup");
        }

    }

    public static void writeFileFromChunks(File output) throws IOException {
        // Get buffered stream from file
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        byte[] buffer = new byte[chunkDataSize];

        int sizeWritten;
        int currentChunkNumber = 0;

        while (true) {
            bufferedOutputStream.write(buffer);

            break;
        }
        bufferedOutputStream.close();
    }

    /**
     * Lists local files
     * @param path Folder path
     */
    private static File[] listFiles(String path) throws IOException {
        File folder = new File(path);
        return folder.listFiles();
    }

    private static void processFileList(File[] listOfFiles) throws IOException {
        assert listOfFiles != null;

        String fileName;

        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile()) {
                String hashT = getHashFromFile(listOfFile);
                fileName = listOfFile.getName();
                System.out.print(hashT + "  ");
                System.out.println(fileName);

                readChunks(listOfFile);

            }

        }
    }
}
