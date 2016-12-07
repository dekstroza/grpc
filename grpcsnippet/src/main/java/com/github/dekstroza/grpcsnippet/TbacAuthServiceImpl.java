package com.github.dekstroza.grpcsnippet;

import io.grpc.stub.StreamObserver;

/**
 * Actual implementation of access control via tbac would go here
 */
public class TbacAuthServiceImpl extends TbacGrpc.TbacImplBase {

    @Override
    public void authenticate(TbacAuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        AuthResponse response = AuthResponse.newBuilder().setMessage("Access denied.").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
