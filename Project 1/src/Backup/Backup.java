package Backup;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private JButton refreshFileListButton;
    private String log;

    private ArrayList arl;
    private ArrayList arlH;

    PeerThread peer;

    public Backup(String[] args) {
        log = "";

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
        refreshFileListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateGui();
            }
        });

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
        //updateGui();
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
                peer.sendPUTCHUNK(file.getAbsolutePath(), 1);


            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            updateGui();
        }
    }

    private void restoreButtonPressed(){
        int selectedFile = filesList.getSelectedIndex();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(arl.get(selectedFile).toString()));

        int retrieval = fileChooser.showSaveDialog(Backup.this);

        if (retrieval == JFileChooser.APPROVE_OPTION) {
            try {
                log+="Restoring: ";
                log+=fileChooser.getSelectedFile().getAbsolutePath();
                log+="...\n";
                peer.sendGETCHUNK(arlH.get(selectedFile).toString() ,fileChooser.getSelectedFile().getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //peer.sendGETCHUNK("./binary2.test");
    }

    private void deleteFileButtonPressed(){
        int selectedFile = filesList.getSelectedIndex();

    }

    private void freeSomeSpaceButtonPressed(){
        //peer.
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
        repaint();
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

                filesList.clearSelection();

                Map<String, LocalFile> dataBase = peer.getDataBase().getFiles();

                arl = new ArrayList();
                arlH = new ArrayList();

                for (Map.Entry<String, LocalFile> pairs : dataBase.entrySet()) {
                    String hash = pairs.getKey();
                    arlH.add(hash);
                    arl.add(peer.getDataBase().getFileNameFromId(hash));
                }

                filesList.setListData(arl.toArray());

            } else {
                filesList.setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        final String [] arg = args;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Backup dialog = new Backup(arg);
                dialog.pack();
                dialog.setVisible(true);
            }
        });


        //System.exit(0);
    }
}
