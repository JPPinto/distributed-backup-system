package Backup;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import static java.lang.Thread.sleep;

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
    private JList filesList;
    private JTextPane logTextPane;
    private String log;

    PeerThread peer;

    public Backup(String[] args) {
        log = "";
        //peer.loadDataBase();

        if (args.length != 6) {
            peer = new PeerThread("224.0.0.0", 60000, "225.0.0.0", 60001, "226.0.0.0", 60002);
        } else {
            peer = new PeerThread(args[0], Integer.getInteger(args[1]), args[2], Integer.getInteger(args[3]), args[4], Integer.getInteger(args[5]));
        }

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

                    try {
                        log+="Backing up: ";
                        log+=file.getAbsolutePath();
                        log+="...\n";
                        peer.sendPUTCHUNK(file.getAbsolutePath());

                        logTextPane.setText(log);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                } else {
                    // Do nothing
                }
            }
        });

        // Start the peer
        peer.start();
    }

    private void onExit() {
        // Destroy the GUI interface
        dispose();

        //peer.saveDataBase();

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

    public static void main(String[] args) {
        Backup dialog = new Backup(args);
        dialog.pack();
        dialog.setVisible(true);
        // Run threads

        //System.exit(0);
    }
}
