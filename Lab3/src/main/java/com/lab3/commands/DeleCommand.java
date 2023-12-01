package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFileActionFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;

import java.io.IOException;

public class DeleCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        String fileName = request.getArgument();
        if (fileName == null) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "Syntax error."));
            return;
        }

        FtpFile file = null;

        try {
            file = session.getFileSystemView().getFile(fileName);
        } catch (Exception ex) {
            System.out.println("Could not give file.");
        }

        if (file == null) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "Request action not taken."));
            return;
        }

        fileName = file.getAbsolutePath();

        if (file.isDirectory()) {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context,
                    FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                    "DELE.invalid", fileName, file));
            return;
        }

        if (!file.isRemovable()) {
            session.write(LocalizedFileActionFtpReply.translate(session, request, context,
                    FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN,
                    "DELE.permission", fileName, file));
            return;
        }

        if (file.delete()) {
            session.write(new DefaultFtpReply(FtpReply.REPLY_250_REQUESTED_FILE_ACTION_OKAY, "Deleted successfully."));

            ServerFtpStatistics ftpStat = (ServerFtpStatistics) context
                    .getFtpStatistics();
            ftpStat.setDelete(session, file);

        } else {
            session.write(new DefaultFtpReply(FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN, "File wasn't deleted."));
        }
    }
}
