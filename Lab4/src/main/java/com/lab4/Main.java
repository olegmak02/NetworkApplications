package com.lab4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int proxyPort = 8080;
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(proxyPort)) {
            System.out.println("Proxy server is running on port " + proxyPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new Proxy(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}