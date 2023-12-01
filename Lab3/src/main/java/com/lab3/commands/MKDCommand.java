package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;

import java.io.IOException;

public class MKDCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        String fileName = request.getArgument();
        if (fileName == null) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "Wrong syntax."));
            return;
        }

        FtpFile file;
        try {
            file = session.getFileSystemView().getFile(fileName);
        } catch (Exception ex) {
            System.out.println("File doesn't exist.");
            return;
        }

        if (file == null) {
            System.out.println("Directory doesn't exist.");
            return;
        }

        fileName = file.getAbsolutePath();
        if (!file.isWritable()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "You don't have permissions."));
            return;
        }

        if (file.doesExist()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "Directory already exists."));
            return;
        }

        if (file.mkdir()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_257_PATHNAME_CREATED, "MKD " + fileName + " successfully executed."));
        } else {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "MKD " + fileName + " wasn't executed successfully."));
        }
    }
}
