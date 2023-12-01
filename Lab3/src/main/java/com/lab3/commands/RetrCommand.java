package com.lab3.commands;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.*;
import org.apache.ftpserver.util.IoUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;

public class RetrCommand implements Command {
    @Override
    public void execute(FtpIoSession session, FtpServerContext context, FtpRequest request) throws IOException, FtpException {
        try {
            long skipLen = session.getFileOffset();

            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
                        "Wrong syntax."));
                return;
            }

            FtpFile file = null;
            try {
                file = session.getFileSystemView().getFile(fileName);
            } catch (Exception ex) {
                System.out.println("Exception getting file object");
            }

            if (file == null) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "File does not exist."));
                return;
            }
            fileName = file.getAbsolutePath();

            if (!file.doesExist()) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "File does not exist."));
                return;
            }

            if (!file.isFile()) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "Chosen file is directory."));
                return;
            }

            if (!file.isReadable()) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "You don't have permissions."));
                return;
            }

            DataConnectionFactory connFactory = session.getDataConnection();
            if (connFactory instanceof IODataConnectionFactory) {
                InetAddress address = ((IODataConnectionFactory) connFactory)
                        .getInetAddress();
                if (address == null) {
                    session.write(new DefaultFtpReply(
                            FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS,
                            "PORT or PASV must be issued first"));
                    return;
                }
            }

            session.write(new DefaultFtpReply(FtpReply.REPLY_150_FILE_STATUS_OKAY, "RETR is successful."));

            boolean failure = false;
            InputStream is = null;

            DataConnection dataConnection;
            try {
                dataConnection = session.getDataConnection().openConnection();
            } catch (Exception e) {
                session.write(new DefaultFtpReply(FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "Cant open data connection."));
                return;
            }

            long transSz = 0L;
            try {

                InputStream inputStream;
                if (session.getDataType() == DataType.ASCII) {
                    int c;
                    long offset = 0L;
                    inputStream = new BufferedInputStream(file.createInputStream(0L));
                    while (offset++ < skipLen) {
                        if ((c = inputStream.read()) == -1) {
                            throw new IOException("Cannot skip");
                        }
                        if (c == '\n') {
                            offset++;
                        }
                    }
                } else {
                    inputStream = file.createInputStream(skipLen);
                }

                transSz = dataConnection.transferToClient(session.getFtpletSession(), inputStream);
                if (is != null) {
                    is.close();
                }

            } catch (SocketException ex) {
                failure = true;
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED,
                        "RETR", fileName, file, transSz));
            } catch (IOException ex) {
                failure = true;
                session
                        .write(LocalizedDataTransferFtpReply
                                .translate(
                                        session,
                                        request,
                                        context,
                                        FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
                                        "RETR", fileName, file, transSz));
            } finally {
                IoUtils.close(is);
            }

            if (!failure) {
                session.write(LocalizedDataTransferFtpReply.translate(session, request, context,
                        FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "RETR",
                        fileName, file, transSz));

            }
        } finally {
            session.resetState();
            session.getDataConnection().closeDataConnection();
        }
    }
}
