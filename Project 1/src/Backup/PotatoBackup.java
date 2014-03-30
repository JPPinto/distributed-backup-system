package Backup;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

import static Backup.Utilities.getHashFromFile;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Entry point
 */
class PotatoBackup {
    /* Buffer size for hashing operations 1MiB */
    private static final int chunkDataSize = 64000;

    /* Path constants */
    public static final String backupDirectory = "./backup";
	public static final String recoveryDirectory = "./recovery";
    public static final String temporaryDirectory = "./temporary";

    /**
     * Entry Point
     * @param args Arguments
     */
    public static void main(String[] args) {
        int suchChoice;

        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Welcome to Potaturu Backupuru");
            System.out.println("Choose an optionuru:\n" +
                    "1) List all files and create chunks\n" +
                    "2) Decode sample message\n" +
                    "9) Exit");

            suchChoice = in.nextInt();
            switch (suchChoice) {
                default:
                    System.out.println("Invalid choice!");

                case 1:
                    try {
                        processFileList(listFiles("."));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2:
                    try {
                        decodeTestMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case 9:
                    System.exit(0);
                    break;

            }

        }

    }

    public static void decodeTestMessage() throws IOException{
        FileInputStream fileIn = new FileInputStream("message.test");
        BufferedInputStream in = new BufferedInputStream(fileIn);

        // Nasty hack for testing purposes only
        byte [] testMessage = new byte[(int) Files.size(new File("message.test").toPath())];

        int sizeRead = 0;
        while ((sizeRead = in.read(testMessage)) != -1) {
            // Reading where
        }

        in.close();
        fileIn.close();
        PBMessage tempMessage = PBMessage.createMessageFromType(testMessage, testMessage.length);

    }

    /**
     * Reads a file and creates chunks
     * @param inputFile Input file
     **/
    public static void readChunks(File inputFile,String directory) throws IOException {
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

            byte[] realData = Arrays.copyOfRange(buffer, 0, sizeRead);

            Chunk currentChunk = new Chunk(fileID, currentChunkNumber, realData);
            currentChunk.write(directory);

            currentChunkNumber++;

        }
        bufferedInputStream.close();

        if (fileSize % chunkDataSize == 0) {
            System.out.println("An empty chunk is needed");
            Chunk finalChunk = new Chunk(fileID, currentChunkNumber);
            finalChunk.write(directory);
        }
    }

    /**
     * Write file from chunks
     * @param output
     * @throws IOException
     */
    public static void writeFileFromChunks(File output) throws IOException {
        // Get buffered stream from file
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

        int currentChunkNumber = 0;
        int numberOfChunksToWrite = 0;
        String chunkFileName = "";
        Chunk currentChunk = null;

        for (; currentChunkNumber < numberOfChunksToWrite; currentChunkNumber++) {
            // currentChunk.loadChunk(chunkFileName);


            bufferedOutputStream.write(currentChunk.getChunkData());

            currentChunkNumber++;
            break;
        }
        bufferedOutputStream.close();
    }

    private static File[] listChunksByHash() {
        File folder = new File(temporaryDirectory);
        return folder.listFiles();
    }

    /**
     * Lists local files
     * @param path Folder path
     */
    public static File[] listFiles(String path) {
        File folder = new File(path);
        return folder.listFiles();
    }

    private static void processFileList(File[] listOfFiles) throws IOException {
        assert listOfFiles != null;

        String fileName;

        for (File file : listOfFiles) {

            if (file.isFile()) {
                String hashT = getHashFromFile(file);
                fileName = file.getName();
                System.out.print(hashT + "  ");
                System.out.println(fileName);

                readChunks(file, backupDirectory);

            }

        }
    }
}
