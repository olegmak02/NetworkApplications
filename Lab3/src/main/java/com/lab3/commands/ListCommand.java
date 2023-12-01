package com.lab3.commands;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.command.impl.listing.DirectoryLister;
import org.apache.ftpserver.command.impl.listing.LISTFileFormater;
import org.apache.ftpserver.command.impl.listing.ListArgument;
import org.apache.ftpserver.command.impl.listing.ListArgumentParser;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedDataTransferFtpReply;
import org.apache.ftpserver.impl.LocalizedFtpReply;

import java.io.File;
import java.io.IOException;

public class ListCommand extends AbstractCommand {
    @Override
    public void execute(FtpIoSession ftpIoSession, FtpServerContext ftpServerContext, FtpRequest ftpRequest) throws IOException, FtpException {
        if (ftpIoSession.getUser() == null) {
            ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_530_NOT_LOGGED_IN, "Authentication required."));
        }

        ListArgument parsedArg = ListArgumentParser.parse(ftpRequest
                .getArgument());

        DirectoryLister lister = new DirectoryLister();
        String dirList = lister.listFiles(parsedArg,
                ftpIoSession.getFileSystemView(), new LISTFileFormater());
        ftpIoSession.write(new DefaultFtpReply(FtpReply.REPLY_150_FILE_STATUS_OKAY, "LIST executed successfully."));

        try {
            ftpIoSession.getDataConnection().openConnection().transferToClient(ftpIoSession.getFtpletSession(), dirList);
            FtpFile file = ftpIoSession.getFileSystemView().getFile(parsedArg.getFile());

            ftpIoSession.write(LocalizedDataTransferFtpReply.translate(ftpIoSession, ftpRequest, ftpServerContext,
                    FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "LIST",
                    null, file, dirList.length()));
            ftpIoSession.getDataConnection().closeDataConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
