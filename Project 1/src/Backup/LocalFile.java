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
