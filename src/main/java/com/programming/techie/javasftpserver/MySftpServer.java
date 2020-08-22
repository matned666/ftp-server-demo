package com.programming.techie.javasftpserver;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ftp.server.ApacheMinaFtplet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Configuration
public class MySftpServer {

    private final ApacheMinaFtplet apacheMinaFtplet;

    private final FtpServerProperties ftpServerProperties;

    public MySftpServer(ApacheMinaFtplet apacheMinaFtplet, FtpServerProperties ftpServerProperties) {
        this.apacheMinaFtplet = apacheMinaFtplet;
        this.ftpServerProperties = ftpServerProperties;
    }

    @Bean
    public WritePermission writePermission() {
        return new WritePermission();
    }

    @Bean
    public BaseUser user(WritePermission writePermission){
        BaseUser user = new BaseUser();
        user.setName("user");
        user.setPassword("secret");
        user.setHomeDirectory("ftproot");
        user.setAuthorities(List.of(writePermission));
        return user;
    }

    @Bean
    public UserManager userManagerFactory(BaseUser user) throws FtpException {
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//        userManagerFactory.setFile(new File("myusers.properties"));
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager um = userManagerFactory.createUserManager();
        um.save(user);
        return um;
    }

    @Bean
    public NativeFileSystemFactory nativeFileSystemFactory(BaseUser user) throws FtpException {
        NativeFileSystemFactory nativeFileSystemFactory = new NativeFileSystemFactory();
        nativeFileSystemFactory.setCreateHome(true);
        nativeFileSystemFactory.createFileSystemView(user);
        return nativeFileSystemFactory;
    }

    @Bean
    public FtpServer ftpServer(UserManager userManager, NativeFileSystemFactory nativeFileSystemFactory){
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();

        // set the port of the listener
        factory.setPort(ftpServerProperties.getPort());

        // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setFtplets(new HashMap<>(Collections.singletonMap("springFtplet", apacheMinaFtplet)));

        serverFactory.setUserManager(userManager);
        serverFactory.setFileSystem(nativeFileSystemFactory);

        return serverFactory.createServer();
    }


}
