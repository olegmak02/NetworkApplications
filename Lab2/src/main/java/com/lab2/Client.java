package com.lab2;

import com.lab2.panels.MainPanel;
import com.lab2.panels.StartPanel;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;

public class Client extends JFrame {
    private MainPanel mainPanel;
    private StartPanel startPanel;
    private FTPClient ftpClient;
    public Client() {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        startPanel = new StartPanel(ftpClient, this);
        mainPanel = new MainPanel(ftpClient);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    e.getWindow().dispose();
                    System.exit(0);
                }
            }
        });

        setSize(800, 600);
        setResizable(false);

        setStartPanel();

        setVisible(true);
    }

    public static void main(String[] args) {
        Client frame = new Client();
    }

    public void setMainPanel() {
            this.setContentPane(this.mainPanel);
            this.setVisible(true);
            this.mainPanel.initPage();
    }

    public void setStartPanel() {
        this.setContentPane(this.startPanel);
        this.setVisible(true);
    }
}