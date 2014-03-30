package Backup;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

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
                exitButtonPressed();
            }
        });

        // call onCancel() when cross is clicked
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitButtonPressed();
            }
        });

        // call onCancel() on ESCAPE
        buttonsContentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitButtonPressed();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Buttons actions listeners
        backupFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backupButtonPressed();
            }
        });

        restoreFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreButtonPressed();
            }
        });

        deleteFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFileButtonPressed();
            }
        });

        freeSomeSpaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                freeSomeSpaceButtonPressed();
            }
        });


        // Start the peer
        peer.start();
        updateGui();
    }

    private void backupButtonPressed(){
        int returnVal = fc.showOpenDialog(Backup.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                log+="Backing up: ";
                log+=file.getAbsolutePath();
                log+="...\n";
                updateLogWindow();
                peer.sendPUTCHUNK(file.getAbsolutePath());


            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }
    }

    private void restoreButtonPressed(){

    }

    private void deleteFileButtonPressed(){

    }

    private void freeSomeSpaceButtonPressed(){

    }

    private void exitButtonPressed() {
        // Destroy the GUI interface
        dispose();

        //peer.saveDataBase();

        // Close all threads here

        // Bail out
        System.exit(0);
    }

    private void updateGui(){
        updateLogWindow();
        updateFileList();
    }

    private void updateLogWindow(){
        if(logTextPane != null){
            logTextPane.setText(log);
        }
    }

    private void updateFileList(){
        if (filesList != null){
            if (peer != null) {
                filesList.setEnabled(true);
                LocalDataBase dataBase1 = peer.getDataBase();
                Map<String, LocalFile> files = (Map<String, LocalFile>) dataBase1.getFiles();

                int i=0;
                Iterator it = files.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    filesList.add(new JLabel(pairs.getKey().toString() + " " + pairs.getValue().toString()), i);
                    i++;
                    it.remove();
                }


            } else {
                filesList.setEnabled(false);
            }
        }
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
