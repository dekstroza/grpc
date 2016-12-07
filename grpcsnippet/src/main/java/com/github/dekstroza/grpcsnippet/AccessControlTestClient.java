package com.github.dekstroza.grpcsnippet;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class AccessControlTestClient {

    private static final Logger log = LoggerFactory.getLogger(AccessControlTestClient.class);
    private final ManagedChannel channel;
    private final RbacGrpc.RbacBlockingStub rbacBlockingStub;
    private final TbacGrpc.TbacBlockingStub tbacBlockingStub;

    public AccessControlTestClient(String hostname, int port) {
        this(ManagedChannelBuilder.forAddress(hostname, port).usePlaintext(true));
    }

    AccessControlTestClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        rbacBlockingStub = RbacGrpc.newBlockingStub(channel);
        tbacBlockingStub = TbacGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sendRbacAuthRequest() {
        RbacAuthRequest request = RbacAuthRequest.newBuilder().setName("John Doe").build();
        AuthResponse response;
        try {
            response = rbacBlockingStub.authenticate(request);
        } catch (StatusRuntimeException stre) {
            log.error("Exception while invoking rbac authentication request:", stre);
            return;
        }
        log.info("response.getMessage() => {} ", response.getMessage());
    }

    public void sendTbacAuthRequest() {
        TbacAuthRequest request = TbacAuthRequest.newBuilder().setName("John Doe").build();
        AuthResponse response;
        try {
            response = tbacBlockingStub.authenticate(request);
        } catch (StatusRuntimeException stre) {
            log.error("Exception while invoking tbac authentication request:", stre);
            return;
        }
        log.info("response.getMessage() => {}", response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        AccessControlTestClient client = new AccessControlTestClient("localhost", 50051);
        client.sendRbacAuthRequest();
        client.sendTbacAuthRequest();
        client.shutdown();
    }

}
