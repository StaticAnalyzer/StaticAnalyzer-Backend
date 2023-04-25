package com.staticanalyzer.staticanalyzer.entity;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.service.ProjectService;

@SpringBootTest
@Transactional
@Rollback
public class ProjectTest {

    @Autowired
    private ProjectService projectService;

    @Test
    public void TestProjectBasic() {
        Project project = projectService.create(1000, new byte[10], "<algorithm>test</algorithm>");
        assertTrue(project != null);
    }
}
