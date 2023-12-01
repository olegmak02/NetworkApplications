package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.impl.listing.*;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;

import java.io.IOException;

public class NLSTCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        session.getDataConnection();

        session.write(LocalizedFtpReply.translate(session, request, context,
                FtpReply.REPLY_150_FILE_STATUS_OKAY, "NLST", null));

        DataConnection dataConnection;
        try {
            dataConnection = session.getDataConnection().openConnection();
        } catch (Exception e) {
            session.write(LocalizedFtpReply.translate(session, request, context,
                    FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "NLST",
                    null));
            return;
        }

        ListArgument parsedArg = ListArgumentParser.parse(request
                .getArgument());

        FileFormater formater = new NLSTFileFormater();

        String s = new DirectoryLister().listFiles(
                parsedArg, session.getFileSystemView(), formater);

        dataConnection.transferToClient(session.getFtpletSession(), "..\r\n" + s);


        session.write(LocalizedFtpReply.translate(session, request, context,
                FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "NLST",
                null));

        session.getDataConnection().closeDataConnection();

    }
}
