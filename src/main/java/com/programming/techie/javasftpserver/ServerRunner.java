package com.programming.techie.javasftpserver;

import org.apache.ftpserver.FtpServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ServerRunner implements CommandLineRunner {

    private final FtpServer ftpServer;

    public ServerRunner(FtpServer ftpServer) {
        this.ftpServer = ftpServer;
    }


    @Override
    public void run(String... args) throws Exception {
        ftpServer.start();
    }
}
