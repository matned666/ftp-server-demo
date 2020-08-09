package com.programming.techie.javasftpserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.integration.ftp.server.ApacheMinaFtplet;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class MySftpServer {

    private Log log = LogFactory.getLog(MySftpServer.class);

    private final ApacheMinaFtplet apacheMinaFtplet;

    public MySftpServer(ApacheMinaFtplet apacheMinaFtplet) {
        this.apacheMinaFtplet = apacheMinaFtplet;
    }

    @PostConstruct
    public void startServer() throws FtpException {
        start();
    }

    private void start() throws FtpException {

        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();

        // set the port of the listener
        factory.setPort(2221);

            // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setFtplets(new HashMap<>(Collections.singletonMap("springFtplet", apacheMinaFtplet)));

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//        userManagerFactory.setFile(new File("myusers.properties"));
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager um = userManagerFactory.createUserManager();
        BaseUser user = new BaseUser();
        user.setName("user");
        user.setPassword("secret");
        user.setHomeDirectory("ftproot");

        WritePermission writePermission = new WritePermission();
        user.setAuthorities(List.of(writePermission));

        um.save(user);

        serverFactory.setUserManager(um);

        NativeFileSystemFactory nativeFileSystemFactory = new NativeFileSystemFactory();
        nativeFileSystemFactory.setCreateHome(true);
        nativeFileSystemFactory.createFileSystemView(user);

        serverFactory.setFileSystem(nativeFileSystemFactory);

        // start the server
        FtpServer server = serverFactory.createServer();
        server.start();

   }
}
