package com.staticanalyzer.staticanalyzer.service;

import org.springframework.stereotype.Service;

import net.devh.boot.grpc.client.inject.GrpcClient;

import com.google.protobuf.ByteString;

import com.staticanalyzer.algservice.AlgServiceGrpc.AlgServiceBlockingStub;
import com.staticanalyzer.algservice.AnalyseRequest;
import com.staticanalyzer.algservice.AnalyseResponse;

/**
 * 算法服务
 * 
 * @author WLLEGit
 * @since 0.0.1
 */
@Service
public class AlgorithmService {

    @GrpcClient("grpc-alg-server")
    private AlgServiceBlockingStub algServiceBlockingStub;

    /**
     * 递交分析请求
     * 
     * @param file
     * @param config
     * @return 分析结果
     * @see AnalyseResponse
     */
    public AnalyseResponse Analyse(byte[] file, String config) {
        AnalyseRequest justReturnRequest = AnalyseRequest.newBuilder()
                .setFile(ByteString.copyFrom(file))
                .setConfig(config)
                .build();
        return algServiceBlockingStub.analyse(justReturnRequest);
    }
}
