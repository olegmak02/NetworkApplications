package com.lab4;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class Proxy implements Runnable {
    private Socket clientSocket;

    public Proxy(Socket client) {
        this.clientSocket = client;
    }

    @Override
    public void run() {
        System.out.println("Server accepted new connection.");
        try (
                InputStream clientInput = clientSocket.getInputStream();
                OutputStream clientOutput = clientSocket.getOutputStream();
        ) {
            byte[] requestBuffer = new byte[4096];
            int bytesRead = clientInput.read(requestBuffer);

            HttpReq httpRequest = HttpReq.parse(new String(requestBuffer, 0, bytesRead));
            String destination = httpRequest.getDestination();
            System.out.println("Request to " + destination + " is processing.");

            if (Cache.cachedFiles.containsKey(httpRequest.getUrl())) {
                byte[] resp = Cache.cachedFiles.get(httpRequest.getUrl());
                clientOutput.write(resp, 0, resp.length);
                System.out.println("Response was given from cache.");
            } else {
                try (Socket serverSocket = new Socket(destination, 80);
                     InputStream serverInput = serverSocket.getInputStream();
                     OutputStream serverOutput = serverSocket.getOutputStream()) {

                    System.out.println("Connection with server was opened.");

                    serverOutput.write(requestBuffer, 0, bytesRead);

                    byte[] responseBuffer = new byte[100000];
                    int serverBytesRead;
                    while ((serverBytesRead = serverInput.read(responseBuffer)) != -1 && serverInput.available() > 0 ) {
                        System.out.println("Response from required server was gotten.");
                        clientOutput.write(responseBuffer, 0, serverBytesRead);
                    }
                    if (httpRequest.getCachable() && HttpResp.isCachable(responseBuffer)) {
                        Cache.cachedFiles.put(httpRequest.getUrl(), responseBuffer);
                        System.out.println("Response was saved to cache.");
                    }
                    System.out.println("Response was given from server.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Problems with connection happened.");
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connection with client was closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
