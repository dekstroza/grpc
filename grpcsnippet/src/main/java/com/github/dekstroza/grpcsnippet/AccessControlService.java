package com.github.dekstroza.grpcsnippet;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class AccessControlService {

    private Server server;
    private static final Logger log = LoggerFactory.getLogger(AccessControlService.class);

    private void start(final int port) throws IOException {
        server = ServerBuilder.forPort(port).addService(new RbacAuthServiceImpl()).addService(new TbacAuthServiceImpl()).build().start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.error("*** shutting down gRPC server since JVM is shutting down");
                AccessControlService.this.stop();
                log.error("*** server shut down");
            }
        });

    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final AccessControlService rpcServer = new AccessControlService();
        rpcServer.start(50051);
        rpcServer.blockUntilShutdown();
    }
}
