package com.lab1.client;

import com.lab1.client.panel.MainPanel;
import com.lab1.client.panel.StartPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame {
    private String username;
    private String address;
    private int port;
    private Socket socket;
    private MainPanel mainPanel;
    private StartPanel startPanel;

    public Client() {
        super("Client");
        this.setSize(1000, 700);
        this.mainPanel = new MainPanel(this);
        this.startPanel = new StartPanel(this);
        this.setResizable(false);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try {
                    send(new MessageBuilder().type("[END]").build());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.getWindow().dispose();
            }
        });
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.setStartPanel();
        client.setVisible(true);
    }

    public void setConnection() throws IOException {
        this.socket = new Socket(getAddress(), getPort());
    }

    public void setMainPanel() {
        this.getContentPane().removeAll();
        this.add(this.mainPanel);
        this.startPanel.revalidate();
        this.startPanel.repaint();
    }

    public void setStartPanel() {
        this.getContentPane().removeAll();
        this.add(this.startPanel);
        this.startPanel.revalidate();
        this.startPanel.repaint();
    }

    public void send(String message) throws IOException {
        if (socket == null)
            return;

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(message);
            bw.flush();
        } catch (IOException e) {
            throw e;
        }
    }

    public String receive() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            StringBuilder res = new StringBuilder();
            String line = br.readLine();
            while (!line.equals("")) {
                res.append(line);
                line = br.readLine();
            }
            return res.toString();
        } catch (IOException e) {
            throw e;
        }
    }
}
