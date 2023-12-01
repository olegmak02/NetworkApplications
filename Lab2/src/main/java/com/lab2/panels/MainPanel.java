package com.lab2.panels;

import com.lab2.Client;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;

public class MainPanel extends JPanel {
    private FTPClient ftp;
    private DefaultTableModel model;
    public MainPanel(FTPClient ftp) {
        this.ftp = ftp;

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton binMode = new JRadioButton("Бінарний режим передачі");
        binMode.setSelected(true);
        JRadioButton symbMode = new JRadioButton("Символьний режим передачі");
        buttonGroup.add(binMode);
        buttonGroup.add(symbMode);

        JTable table = new JTable();
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);

        JScrollPane output = new JScrollPane(table);
        output.setVisible(true);

        JButton copyFileOnServer = new JButton("Завантажити файл на сервер");
        copyFileOnServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                JDialog dialog = new JDialog();
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setResizable(false);
                dialog.setModal(true);
                JLabel pathLabel = new JLabel("Введіть шлях для завантаження файлу на сервер");
                JTextArea pathInput = new JTextArea(1, 32);
                JButton sendButton = new JButton("Завантажити");
                sendButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            if (binMode.isSelected())
                                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                            else
                                ftp.setFileType(FTP.ASCII_FILE_TYPE);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                        File localFile = new File(pathInput.getText());
                        if (localFile.isFile()) {
                            try (FileInputStream inputStream = new FileInputStream(localFile)) {
                                String fullFilename = concatPath(ftp.printWorkingDirectory(), localFile.getName());
                                ftp.storeFile(fullFilename, inputStream);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        dialog.dispose();
                    }
                });

                dialog.setSize(400, 400);
                JPanel panel = new JPanel();
                panel.add(pathLabel);
                panel.add(pathInput);
                panel.add(sendButton);
                dialog.setContentPane(panel);
                dialog.setVisible(true);
            }
        });

        JButton copyFileFromServer = new JButton("Завантажити файл із сервера");
        copyFileFromServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog dialog = new JDialog();
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setResizable(false);
                dialog.setModal(true);
                JLabel pathLabel = new JLabel("Введіть шлях для завантаження файлу");
                JTextArea pathInput = new JTextArea(1, 32);
                JButton sendButton = new JButton("Завантажити");
                sendButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            if (binMode.isSelected())
                                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                            else
                                ftp.setFileType(FTP.ASCII_FILE_TYPE);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                        String path = pathInput.getText();
                        int row = table.getSelectedRow();
                        if (row < 0) {
                            dialog.dispose();
                            return;
                        }
                        String filename = (String) model.getValueAt(row, 0);
                        if (path != null && !path.isEmpty() && !isDirectory(filename)) {
                            File downloadFile = new File(concatPath(path, filename));
                            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile))) {
                                String fullFilename = concatPath(ftp.printWorkingDirectory(), filename);
                                ftp.retrieveFile(fullFilename, outputStream);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        dialog.dispose();
                    }
                });

                dialog.setSize(400, 400);
                JPanel panel = new JPanel();
                panel.add(pathLabel);
                panel.add(pathInput);
                panel.add(sendButton);
                dialog.setContentPane(panel);
                dialog.setVisible(true);
            }
        });

        JLabel fileName = new JLabel("Введіть назву нової директорії");
        JTextArea newDirectoryName = new JTextArea(1, 30);
        JButton createDirectory = new JButton("Створити директорію");
        createDirectory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String newDirectory = concatPath(ftp.printWorkingDirectory(), newDirectoryName.getText());
                    ftp.makeDirectory(newDirectory);
                    displayFiles(ftp.listNames());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton removeDirectory = new JButton("Видалити директорію");
        removeDirectory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int row = table.getSelectedRow();
                    if (row < 0)
                        return;
                    String name = (String) model.getValueAt(row, 0);
                    if (isDirectory(name)) {
                        ftp.removeDirectory(concatPath(ftp.printWorkingDirectory(), name));
                    }
                    displayFiles(ftp.listNames());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton removeFile = new JButton("Видалити файл");
        removeFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int row = table.getSelectedRow();
                    if (row < 0)
                        return;
                    String name = (String) model.getValueAt(row, 0);
                    if (!isDirectory(name)) {
                        ftp.deleteFile(concatPath(ftp.printWorkingDirectory(), name));
                    }
                    displayFiles(ftp.listNames());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton exListButton = new JButton("Отримати файли(назви)");
        exListButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    displayFiles(ftp.listNames());
                } catch (IOException ex) {
                    model.addRow(new String[] {"Не вдалось отримати файли"});
                }
            }
        });

        JButton listButton = new JButton("Отримати файли(детально)");
        listButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    displayFilesDetailed(ftp.listFiles());
                } catch (IOException ex) {
                    model.addRow(new String[] {"Не вдалось отримати файли"});
                }
            }
        });

        JButton changeDir = new JButton("Перейти до обраної директорії");
        changeDir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int row = table.getSelectedRow();
                    if (row < 0)
                        return;
                    String name = (String) model.getValueAt(row, 0);
                    if (isDirectory(name)) {
                        ftp.changeWorkingDirectory(concatPath(ftp.printWorkingDirectory(), name));
                    } else if (name.equals(".."))
                        ftp.changeToParentDirectory();
                    displayFiles(ftp.listNames());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        this.add(binMode);
        this.add(symbMode);
        this.add(output);
        this.add(listButton);
        this.add(exListButton);
        this.add(copyFileOnServer);
        this.add(copyFileFromServer);
        this.add(fileName);
        this.add(newDirectoryName);
        this.add(createDirectory);
        this.add(removeDirectory);
        this.add(removeFile);
        this.add(changeDir);
    }

    public void displayFilesDetailed(FTPFile[] files) {
        model.setColumnCount(0);
        model.addColumn("Права");
        model.addColumn("Кількість посилань");
        model.addColumn("Власник");
        model.addColumn("Група");
        model.addColumn("Розмір файлу");
        model.addColumn("Час модифікації");
        model.addColumn("Назва");
        model.setRowCount(0);
        for (FTPFile file: files) {
            model.addRow(file.toFormattedString().split("\\s+"));
        }
    }

    public void displayFiles(String[] files) {
        model.setColumnCount(0);
        model.addColumn("Файл");
        model.setRowCount(0);
        for (String name: files) {
            model.addRow(new String[] {name});
        }
    }

    public String concatPath(String dir, String filename) {
        if (dir.charAt(dir.length() - 1) != '/') {
            dir = dir + "/";
        }
        return dir + filename;
    }

    public boolean isDirectory(String name) {
        try {
            return Arrays.stream(ftp.listDirectories()).anyMatch(d -> d.getName().equals(name));
        } catch (IOException e) {
            return false;
        }
    }

    public void initPage() {
        try {
            displayFiles(ftp.listNames());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
