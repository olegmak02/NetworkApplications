package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;

import java.io.IOException;

public class RmdCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        String fileName = request.getArgument();
        if (fileName == null) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "Wrong syntax."));
            return;
        }

        FtpFile file = null;
        try {
            file = session.getFileSystemView().getFile(fileName);
        } catch (Exception ex) {
            System.out.println("File doesn't exist.");
            return;
        }

        if (file == null) {
            System.out.println("File doesn't exist.");
            return;
        }

        if (!file.isDirectory()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "File is not directory"));
            return;
        }

        FtpFile cwd = session.getFileSystemView().getWorkingDirectory();
        if(file.equals(cwd)) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "Directory is busy."));
            return;
        }

        if (!file.isRemovable()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "You don't have permissions."));
            return;
        }

        if (file.delete()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "Deleted successfully."));
        } else {
            session.write(new DefaultFtpReply(FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "Deletion wasn't successful."));
        }
    }
}
