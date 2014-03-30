package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.LocalDataBase class
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LocalDataBase implements Serializable{
    private Map<String, LocalFile> files = new HashMap();

    public boolean addFileToDatabase(LocalFile fileToAdd) {

        /* Check if the file already exists in the database */
        if (getFileFromId(fileToAdd.getFileHash()) == null) {
            files.put(fileToAdd.getFileHash(), fileToAdd);
            return true;

        } else {
            return false;
        }

    }

    public LocalFile getFileFromId(String hash){
        return files.get(hash);
    }


    /***
     * Calls the file chunk delete and removes the file from the database
     */
    public boolean removeFileFromBackup(String hash){
        LocalFile temp = getFileFromId(hash);

        /* File doesn't exist */
        if (temp == null) {
            return false;
        } else {
            return removeFileFromBackup(temp);
        }
    }

    public boolean removeFileFromBackup(LocalFile fileToDelete){

        // Remove from data base
        files.remove(fileToDelete.getFileHash());
        return true;
    }

    public static LocalDataBase loadDataBaseFromFile(String fn){
        try {
            FileInputStream fileIn = new FileInputStream(fn);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            LocalDataBase temp = (LocalDataBase) in.readObject();
            in.close();
            fileIn.close();

            return temp;

        } catch(IOException i) {
            return null;
        } catch (ClassNotFoundException i) {
            return null;
        }
    }
}
