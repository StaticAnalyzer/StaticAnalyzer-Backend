package com.staticanalyzer.staticanalyzer.entity;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staticanalyzer.staticanalyzer.entity.analysis.AnalysisProblem;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.service.ProjectService;

import java.util.List;

@SpringBootTest
@Transactional
@Rollback
public class ProjectTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private ProjectService projectService;

    @Test
    public void TestProjectBasic() throws JsonProcessingException {
        List<AnalysisProblem> analysisProblems = projectService.getProblems(1);
        String analysisProblemsJson = objectMapper.writeValueAsString(analysisProblems);
        System.out.println(analysisProblemsJson);
    }
}
