package Backup;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

class Backup extends JFrame {
    private LocalDataBase dataBase = null;
    private static final String dataBaseFileName = "database.bin";
    private JPanel buttonsContentPane;
    private final JFileChooser fc = new JFileChooser();

    private JButton buttonEXIT;
    private JButton backupFileButton;
    private JButton restoreFileButton;
    private JButton deleteFileButton;
    private JButton freeSomeSpaceButton;
    private JList list1;
    private JTextPane asdasdasdTextPane;

    public Backup() {
        loadDataBase();

        setName("Potato Backup");
        setTitle("Potato Backup");

        setContentPane(buttonsContentPane);
        getRootPane().setDefaultButton(buttonEXIT);

        buttonEXIT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        buttonsContentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        backupFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(Backup.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    // TODO Shove this into a thread
                    // Create temporary chunks
                    try {
                        PotatoBackup.readChunks(file, PotatoBackup.temporaryDirectory);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    // Send the files

                } else {
                    // Do nothing
                }
            }
        });
    }

    private void onExit() {
        // Destroy the GUI interface
        dispose();

        saveDataBase();

        // Close all threads here

        // Bail out
        System.exit(0);
    }

    private void loadDataBase(){
        dataBase = LocalDataBase.loadDataBaseFromFile(dataBaseFileName);

        // We failed to load the database
        if (dataBase == null) {
            dataBase = new LocalDataBase();
        }
    }

    private void saveDataBase(){
        LocalDataBase.saveDataBaseToFile(dataBase, dataBaseFileName);
    }

    public static void main(String[] args) {
        Backup dialog = new Backup();
        dialog.pack();
        dialog.setVisible(true);
        // Run threads

        //System.exit(0);
    }
}
