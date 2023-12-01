package com.lab2.panels;

import com.lab2.Client;
import org.apache.commons.net.ftp.FTPClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class StartPanel extends JPanel {
    public StartPanel(FTPClient ftp, Client client) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel addressLabel = new JLabel("Введіть адресу FTP-сервера");
        JTextField addressField = new JTextField(20);

        JLabel portLabel = new JLabel("Введіть порт FTP-сервера");
        JTextField portField = new JTextField(20);

        JLabel loginLabel = new JLabel("Введіть логін (за замовчуванням anonymous)");
        JTextField loginField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Введіть пароль");
        JTextField passwordField = new JTextField(20);

        JLabel errorLabel = new JLabel("Інформаційне поле");
        JTextArea errorArea = new JTextArea();
        errorArea.setEditable(false);

        JButton button = new JButton("Підключитись до серверу");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {


                try {
                    if (portField.getText() != null && !portField.getText().isEmpty()) {
                        ftp.connect(addressField.getText(), Integer.parseInt(portField.getText()));
                    }
                    ftp.connect(addressField.getText());

                    if (loginField.getText() == null || loginField.getText().isEmpty()) {
                        ftp.login("anonymous", "");
                    } else {
                        ftp.login(loginField.getText(), passwordField.getText());
                    }
                    ftp.changeWorkingDirectory("/");
                    client.setMainPanel();
                } catch (IOException ex) {
                    errorArea.setText("Не вдалось приєднатись до серверу");
                    errorArea.setVisible(true);
                }

            }
        });

        add(addressLabel);
        add(addressField);
        add(portLabel);
        add(portField);
        add(loginLabel);
        add(loginField);
        add(passwordLabel);
        add(passwordField);
        add(button);
        add(errorLabel);
        add(errorArea);
    }
}
