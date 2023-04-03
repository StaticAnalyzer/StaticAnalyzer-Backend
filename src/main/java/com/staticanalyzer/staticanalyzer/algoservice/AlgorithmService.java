package com.staticanalyzer.staticanalyzer.algoservice;

import com.google.protobuf.ByteString;
import com.staticanalyzer.algservice.AlgServiceGrpc.AlgServiceBlockingStub;
import com.staticanalyzer.algservice.JustReturnRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AlgorithmService {
    @GrpcClient("grpc-alg-server")
    private AlgServiceBlockingStub algServiceBlockingStub;

    public String JustReturn(byte[] file, String config){
        JustReturnRequest justReturnRequest = JustReturnRequest.newBuilder()
                .setFile(ByteString.copyFrom(file))
                .setConfig(config)
                .build();
        return algServiceBlockingStub.justReturn(justReturnRequest).getResult();
    }
}