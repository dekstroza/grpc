package com.github.dekstroza.grpcsnippet;

import io.grpc.stub.StreamObserver;

/**
 * Actual imlpementation of access control via rbac would go here
 */
public class RbacAuthServiceImpl extends RbacGrpc.RbacImplBase {

    @Override
    public void authenticate(RbacAuthRequest request, StreamObserver<AuthResponse> responseObserver) {
        AuthResponse response = AuthResponse.newBuilder().setMessage("Access denied.").build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
