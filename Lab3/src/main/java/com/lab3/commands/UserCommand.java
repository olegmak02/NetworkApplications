package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

import java.io.IOException;

public class UserCommand implements Command {
    @Override
    public void execute(FtpIoSession ftpIoSession, FtpServerContext ftpServerContext, FtpRequest ftpRequest) throws IOException, FtpException {
        String username = ftpRequest.getArgument();
        if (username != null && !username.isEmpty()) {
            UserManager userManager = ftpServerContext.getUserManager();

            if (username.equals("anonymous")) {
                ftpIoSession.setUserArgument(username);
                User user = ftpServerContext.getUserManager().authenticate(new UsernamePasswordAuthentication(username, ""));
                if (user != null) {
                    ftpIoSession.setUser(user);
                    FileSystemView fileSystemView = ftpServerContext.getFileSystemManager().createFileSystemView(user);
                    ftpIoSession.setLogin(fileSystemView);
                    ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_230_USER_LOGGED_IN, "Login successful."));
                } else {
                    ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_530_NOT_LOGGED_IN, "Authentication failed."));
                }
                return;
            }

            if (userManager.doesExist(username)) {
                ftpIoSession.setUserArgument(username);
                ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_331_USER_NAME_OKAY_NEED_PASSWORD, "Username accepted, provide password."));
            } else {
                ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_530_NOT_LOGGED_IN, "User does not exist."));
            }
        } else {
            ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "Invalid username."));
        }
    }
}
