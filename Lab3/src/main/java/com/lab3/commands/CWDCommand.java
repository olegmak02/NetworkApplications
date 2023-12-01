package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

public class CWDCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws FtpException {
        String dirName = "/";
        if (request.hasArgument()) {
            dirName = request.getArgument();
        }

        FileSystemView fsview = session.getFileSystemView();
        fsview.changeWorkingDirectory(dirName);
        session.write(new DefaultFtpReply(FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "Directory changed successfully."));
    }
}
