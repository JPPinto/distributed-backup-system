package Gui;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

class Backup extends JDialog {
    private JPanel contentPane;
    private final JFileChooser fc = new JFileChooser();

    private JButton buttonEXIT;
    private JButton backupFileButton;
    private JButton restoreFileButton;
    private JButton deleteFileButton;
    private JButton freeSomeSpaceButton;

    public Backup() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonEXIT);

        buttonEXIT.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
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
                    //This is where a real application would open the file.
                } else {
                    // Do nothing
                }
            }
        });
    }

    private void onExit() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Backup dialog = new Backup();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
