package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PortCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        if (session.getUser() == null) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_530_NOT_LOGGED_IN, "Authentication required."));
        }

        String[] parts = request.getArgument().split(",");
        if (parts.length == 6) {
            InetSocketAddress address = new InetSocketAddress(String.join(".", parts[0], parts[1], parts[2], parts[3]), Integer.parseInt(parts[4]) * 256 + Integer.parseInt(parts[5]));
            session.getDataConnection().initActiveDataConnection(address);
            session.write(new DefaultFtpReply(FtpReply.REPLY_200_COMMAND_OKAY, "Port command successful."));
        }
    }
}
