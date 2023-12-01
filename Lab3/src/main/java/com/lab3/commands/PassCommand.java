package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

import java.io.IOException;

public class PassCommand implements Command {
    @Override
    public void execute(FtpIoSession ftpIoSession, FtpServerContext ftpServerContext, FtpRequest ftpRequest) throws IOException, FtpException {
        String username = ftpIoSession.getUserArgument();
        if (username == null || username.isEmpty()) {
            ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "Login with USER first."));
            return;
        }

        String password = ftpRequest.getArgument();
        User user = ftpServerContext.getUserManager().authenticate(new UsernamePasswordAuthentication(username, password));
        if (user != null) {
            ftpIoSession.setUser(user);
            FileSystemView fileSystemView = ftpServerContext.getFileSystemManager().createFileSystemView(user);
            ftpIoSession.setLogin(fileSystemView);
            ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_230_USER_LOGGED_IN, "Login successful."));
        } else {
            ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_530_NOT_LOGGED_IN, "Authentication failed."));
        }
    }
}
