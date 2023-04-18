package com.staticanalyzer.staticanalyzer.service;

import com.staticanalyzer.algservice.AnalyseRequest;
import com.staticanalyzer.algservice.AnalyseResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;

import com.google.protobuf.ByteString;

import com.staticanalyzer.algservice.AlgServiceGrpc.AlgServiceBlockingStub;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {
    @GrpcClient("grpc-alg-server")
    private AlgServiceBlockingStub algServiceBlockingStub;

    public AnalyseResponse Analyse(byte[] file, String config) {
        AnalyseRequest justReturnRequest = AnalyseRequest.newBuilder()
                .setFile(ByteString.copyFrom(file))
                .setConfig(config)
                .build();
        return algServiceBlockingStub.analyse(justReturnRequest);
    }
}
