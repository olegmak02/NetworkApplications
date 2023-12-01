package com.lab1.client.panel;

import com.lab1.client.Client;
import com.lab1.client.MessageBuilder;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class StartPanel extends JPanel {
    public StartPanel(Client client) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel currentUsername = new JLabel("Введений нікнейм");
        JTextArea usernameArea = new JTextArea();
        usernameArea.setEditable(false);
        JLabel label = new JLabel("Введіть нікнейм");
        JTextField textField = new JTextField(15);
        textField.setText(client.getUsername());

        JLabel label2 = new JLabel("Введіть IP-адресу серверу");
        JTextField textField2 = new JTextField(30);
        textField2.setText(client.getAddress());

        JLabel label3 = new JLabel("Введіть порт серверу");
        JTextField textField3 = new JTextField(6);
        textField3.setText(String.valueOf(client.getPort()));

        JButton button2 = new JButton("Підключитись");
        JTextArea errorArea = new JTextArea();
        errorArea.setEditable(false);
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                usernameArea.setText(textField.getText());
                client.setUsername(textField.getText());
                client.setAddress(textField2.getText());
                try {
                    client.setPort(Integer.parseInt(textField3.getText()));
                } catch (NumberFormatException ex) {
                    errorArea.setText("Невалідний порт. Число має бути від 0 до 65353.");
                }

                if (!client.getAddress().trim().equals("") &&
                        !client.getUsername().trim().equals("")) {
                    try {
                        client.setConnection();
                        client.setMainPanel();
                    } catch (IOException ioException) {
                        errorArea.setText("Не вдалось з'єднатись із сервером.");
                        return;
                    }

                    try {
                        client.send(new MessageBuilder().type("[INIT]").info(textField.getText()).build());
                    } catch (IOException ioException) {
                        errorArea.setText("Не вдалось відправити запит.");
                    }
                } else {
                    errorArea.setText("Всі поля мають бути заповнені.");
                }
            }
        });

        this.add(currentUsername);
        this.add(usernameArea);
        this.add(label);
        this.add(textField);
        this.add(label2);
        this.add(textField2);
        this.add(label3);
        this.add(textField3);
        this.add(button2);
        this.add(errorArea);
    }
}
