package com.staticanalyzer.staticanalyzer.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class AlgorithmServiceTest {

    @Autowired
    private AlgorithmService algorithmService;

    @Test
    public void TestConnection() {
        /* 暂时迁移至resouces/test */
    }
}
