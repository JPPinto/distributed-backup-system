package Backup;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

class Backup extends JFrame {
    private static final String dataBaseFileName = "database.bin";
    static PeerThread peer;
    private final JFileChooser fc = new JFileChooser();
    private LocalDataBase dataBase = null;
    private JPanel buttonsContentPane;
    private JButton buttonEXIT;
    private JButton backupFileButton;
    private JButton restoreFileButton;
    private JButton deleteFileButton;
    private JButton freeSomeSpaceButton;
    private JList filesList;
    private JTextArea logTextPane;
    private JButton refreshFileListButton;
    private JComboBox repDegree;
    private JSpinner spinnerSpace;
    private ArrayList arrayListFileName;
    private ArrayList arrayListFileHash;

    public Backup(String[] args) {

        if (args.length != 6) {
            peer = new PeerThread("239.0.0.1", 8765, "239.0.0.1", 8766, "239.0.0.1", 8767);
        } else {
            peer = new PeerThread(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), args[4], Integer.parseInt(args[5]));
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

        DefaultCaret caret = (DefaultCaret) logTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //redirectConsoleTo(logTextPane);
    }

    public static void main(String[] args) {
        final String[] arg = args;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Backup dialog = new Backup(arg);
                // Start the peer
                peer.start();
                dialog.pack();
                dialog.setVisible(true);
                dialog.updateGui();
            }
        });

    }

    /* Stack overflow */
    private void redirectConsoleTo(final JTextArea textarea) {
        PrintStream out = new PrintStream(new ByteArrayOutputStream() {
            public synchronized void flush() throws IOException {
                textarea.setText(toString());
            }
        }, true);

        System.setErr(out);
        System.setOut(out);
    }

    private void backupButtonPressed() {
        int returnVal = fc.showOpenDialog(Backup.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            int temp = Integer.parseInt(repDegree.getSelectedItem().toString());
            try {
                peer.sendPUTCHUNK(file.getAbsolutePath(), temp);


            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            updateGui();
        }
    }

    private void restoreButtonPressed() {
        if (filesList == null || arrayListFileName == null) {
            JOptionPane.showMessageDialog(null, "No files exist!");
            return;
        }

        if (filesList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "No file selected!");
            return;
        }

        int selectedFile = filesList.getSelectedIndex();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(arrayListFileName.get(selectedFile).toString()));

        int retrieval = fileChooser.showSaveDialog(Backup.this);

        if (retrieval == JFileChooser.APPROVE_OPTION) {
            try {
                peer.sendGETCHUNK(arrayListFileHash.get(selectedFile).toString(), fileChooser.getSelectedFile().getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
            }

            updateGui();
        }
    }

    private void deleteFileButtonPressed() {
        if (filesList == null || arrayListFileName == null) {
            JOptionPane.showMessageDialog(null, "No files exist!");
            return;
        }

        if (filesList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "No file selected!");
            return;
        }

        int selectedFile = filesList.getSelectedIndex();
        try {
            peer.sendDELETE(arrayListFileHash.get(selectedFile).toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        updateGui();

    }

    private void freeSomeSpaceButtonPressed() {
        int size = Integer.parseInt(spinnerSpace.getValue().toString());

        if (size < 0) {
            JOptionPane.showMessageDialog(null, "Invalid value in space reclaim!");
        } else {
            peer.freeDiskSpace(size);
            updateGui();
        }

    }

    private void exitButtonPressed() {
        // Destroy the GUI interface
        dispose();

        peer.saveDataBase();

        // Close all threads here

        // Bail out
        System.exit(0);
    }

    private void updateGui() {
        updateFileList();
        repaint();
    }

    private void updateFileList() {
        if (filesList != null) {
            if (peer != null) {
                filesList.setEnabled(true);

                filesList.clearSelection();

                Map<String, LocalFile> dataBase = peer.getDataBase().getFiles();

                arrayListFileName = new ArrayList();
                arrayListFileHash = new ArrayList();

                for (Map.Entry<String, LocalFile> pairs : dataBase.entrySet()) {
                    String hash = pairs.getKey();
                    arrayListFileHash.add(hash);
                    arrayListFileName.add(peer.getDataBase().getFileNameFromId(hash));
                }

                filesList.setListData(arrayListFileName.toArray());

            } else {
                filesList.setEnabled(false);
            }
        }
    }
}
