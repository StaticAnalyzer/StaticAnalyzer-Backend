package com.staticanalyzer.staticanalyzer.service;

import net.devh.boot.grpc.client.inject.GrpcClient;

import com.google.protobuf.ByteString;

import com.staticanalyzer.algservice.AlgServiceGrpc.AlgServiceBlockingStub;
import com.staticanalyzer.algservice.JustReturnRequest;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {
    @GrpcClient("grpc-alg-server")
    private AlgServiceBlockingStub algServiceBlockingStub;

    public String JustReturn(byte[] file, String config) {
        JustReturnRequest justReturnRequest = JustReturnRequest.newBuilder()
                .setFile(ByteString.copyFrom(file))
                .setConfig(config)
                .build();
        return algServiceBlockingStub.justReturn(justReturnRequest).getResult();
    }
}
