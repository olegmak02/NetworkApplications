package com.lab3;

import com.lab3.commands.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.DbUserManager;

public class Server {
    public static void main(String[] args) {
        FtpServerFactory serverFactory = new FtpServerFactory();
        NativeFileSystemFactory nativeFileSystemFactory = new NativeFileSystemFactory();
        nativeFileSystemFactory.setCreateHome(true);
        serverFactory.setFileSystem(nativeFileSystemFactory);

        ListenerFactory factory = new ListenerFactory();
        factory.setPort(21);

        serverFactory.addListener("default", factory.createListener());

        CommandFactoryFactory commandFactory = new CommandFactoryFactory();
        commandFactory.addCommand("USER", new UserCommand());
        commandFactory.addCommand("PASS", new PassCommand());
        commandFactory.addCommand("LIST", new ListCommand());
        commandFactory.addCommand("PORT", new PortCommand());
        commandFactory.addCommand("CWD", new CWDCommand());
        commandFactory.addCommand("NLST", new NLSTCommand());
        commandFactory.addCommand("DELE", new DeleCommand());
        commandFactory.addCommand("RMD", new RmdCommand());
        commandFactory.addCommand("MKD", new MKDCommand());
        commandFactory.addCommand("STOR", new StorCommand());
        commandFactory.addCommand("RETR", new RetrCommand());

        HikariConfig config = new HikariConfig();
        config.setPassword("pass");
        config.setUsername("postgres");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/lab3");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        HikariDataSource dataSource = new HikariDataSource(config);

        serverFactory.setUserManager(new DbUserManager(dataSource,
                "SELECT * FROM users",
                "SELECT * FROM users WHERE userid = '{userid}'",
                "INSERT INTO users (username, password, home_directory, enabled) VALUES (?, ?, ?, ?)",
                "UPDATE users (password, home_directory, enabled) SET (?, ?, ?, ?) WHERE username = ?",
                "DELETE FROM users WHERE username = ?",
                "SELECT userpassword from users WHERE userid = '{userid}'",
                "SELECT username FROM users WHERE username = 'admin'",
                new ClearTextPasswordEncryptor(),
                "admin"));

        serverFactory.setCommandFactory(commandFactory.createCommandFactory());

        try {
            FtpServer server = serverFactory.createServer();
            server.start();
            System.out.println("FTP Server started...");
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }
}