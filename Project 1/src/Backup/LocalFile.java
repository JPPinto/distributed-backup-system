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
public class LocalFile {
    String fileName;
    Date creationDate, modificationDate;

    LocalFile(String fN){
        if (fN.length() <1) {
            throw new IllegalStateException("Invalid file name!");
        }

        fileName = fN;
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
