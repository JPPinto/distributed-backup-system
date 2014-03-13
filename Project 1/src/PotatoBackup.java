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
    /* Buffer size for hashing operations */
    private static int bufferSize = 65536;

    public static void main(String[] args) {
        System.out.println("Welcome to Potato Backup");


        try {
            listFiles();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Convert byte array to hex string Stack Overflow*/
    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /* Calculate file SHA256 sum */
    public static String hash(File inputFile) throws IOException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(inputFile);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] buffer = new byte[bufferSize];

        int sizeRead;

        while ((sizeRead = bufferedInputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, sizeRead);
        }

        bufferedInputStream.close();

        byte[] hash = digest.digest();

        /* Convert to string */
        String hexString = toHex(hash);
        return hexString;
    }

    private static void listFiles() throws NoSuchAlgorithmException, IOException {
        // Directory path here
        String path = ".";

        String fileName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();


        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {
                String hashT = hash(listOfFiles[i]);

                fileName = listOfFiles[i].getName();
                System.out.print(hashT + "  ");
                System.out.println(fileName);

            }

        }
    }
}
