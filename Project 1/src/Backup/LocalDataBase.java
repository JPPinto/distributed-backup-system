package Backup;

/**
 * SDIS TP1
 *
 * Eduardo Fernandes
 * José Pinto
 *
 * Backup.LocalDataBase class
 */
import java.util.HashMap;
import java.util.Map;

public class LocalDataBase {
    private Map<String, LocalFile> files = new HashMap();

    public boolean addFileToDatabase(LocalFile fileToAdd) {

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

    public boolean removeFile(String hash){
        LocalFile temp = getFileFromId(hash);

        /* File doesn't exist */
        if (temp == null) {
            return false;
        } else {
            return removeFile(temp);
        }
    }

    public boolean removeFile(LocalFile fileToDelete){
        // Delete file
        // fileToDelete.delete();

        // Remove from data base
        files.remove(fileToDelete.getFileHash());
        return true;
    }
}
