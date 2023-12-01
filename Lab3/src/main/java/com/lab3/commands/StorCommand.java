package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.*;
import org.apache.ftpserver.util.IoUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.SocketException;

public class StorCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        try {
            String fileName = request.getArgument();
            if (fileName == null) {
                session
                        .write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "Wrong arguments."));
                return;
            }

            DataConnectionFactory connFactory = session.getDataConnection();
            if (connFactory instanceof IODataConnectionFactory) {
                InetAddress address = ((IODataConnectionFactory) connFactory)
                        .getInetAddress();
                if (address == null) {
                    session.write(new DefaultFtpReply(FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS, "PORT or PASV must be issued first"));
                    return;
                }
            }

            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception ex) {
                System.out.println("Exception getting file object");
            }

            if (file == null) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,"File does not exist."));
                return;
            }

            fileName = file.getAbsolutePath();

            if (!file.isWritable()) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,"You don't have permissions."));
                return;
            }

            session.write(new DefaultFtpReply(FtpReply.REPLY_150_FILE_STATUS_OKAY, "STOR " + fileName));

            DataConnection dataConnection;
            try {
                dataConnection = session.getDataConnection().openConnection();
            } catch (Exception e) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "Cant open data connection."));
                return;
            }

            long skipLen = session.getFileOffset();
            boolean failure = false;
            OutputStream outStream = null;
            try {
                outStream = file.createOutputStream(skipLen);

                if(outStream != null) {
                    outStream.close();
                }
            } catch (SocketException ex) {
                failure = true;
                session.write(new DefaultFtpReply(FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED,"Connection closed."));
            } catch (IOException ex) {
                failure = true;
                session.write(new DefaultFtpReply(FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN, "STOR wasn't successful."));
            } finally {
                IoUtils.close(outStream);
            }

            if (!failure) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "STOR was successfully executed."));
            }
        } finally {
            session.resetState();
            session.getDataConnection().closeDataConnection();
        }
    }
}
