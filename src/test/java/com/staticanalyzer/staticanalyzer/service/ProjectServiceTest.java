package com.staticanalyzer.staticanalyzer.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.staticanalyzer.algservice.AnalyseResponse;

import com.staticanalyzer.staticanalyzer.entity.file.SrcFile;
import com.staticanalyzer.staticanalyzer.entity.file.SrcFileAnalysis;
import com.staticanalyzer.staticanalyzer.entity.project.Project;
import com.staticanalyzer.staticanalyzer.entity.project.ProjectVO;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.utils.TarGzTest;
import com.staticanalyzer.staticanalyzer.utils.TarGzUtils;

@Transactional
@Rollback
@SpringBootTest
public class ProjectServiceTest {

    private static String TARGET_TARGZ_PATH = "cpython.tar.gz";
    private static String TARGET_OUTPUT_PATH = "cpython_out.json";

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @org.junit.jupiter.api.Test
    public void testParseAnalyseResponse() throws java.io.IOException {
        byte[] tarGzBytes = TarGzTest.readAllBytesFromResource(TARGET_TARGZ_PATH);
        String responseJson = new String(TarGzTest.readAllBytesFromResource(TARGET_OUTPUT_PATH));

        java.util.Map<String, SrcFile> files = TarGzUtils.decompress(tarGzBytes);
        java.util.Map<String, SrcFileAnalysis> analyses = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, SrcFile> entry : files.entrySet())
            analyses.put(entry.getKey(), new SrcFileAnalysis(entry.getValue()));

        java.util.Random random = new java.util.Random();
        Project handcraftedProject = new Project();
        handcraftedProject.setId(random.nextInt(32768) + 1024);
        handcraftedProject.setSourceCode(tarGzBytes);
        handcraftedProject.setAnalyseResult(responseJson);

        AnalyseResponse response = handcraftedProject.resolveAnalyseResponse();
        assertNotNull(response);
        assertDoesNotThrow(() -> projectService.parseAnalyseResponse(analyses, response));

        System.out.println("Project " + handcraftedProject.getId() + " passed analysis test!");
    }

    @org.junit.jupiter.api.Test
    public void testGetProjectInfo() {
        User testUser = userService.getUserByName("test0");
        java.util.List<ProjectVO> projects = projectService.getProjectInfo(testUser.getId());

        assertEquals(projects.size(), 2);
        System.out.println("Query project info test passed!");
    }

    @org.junit.jupiter.api.Test
    public void testFetchFromCache() {
        User testUser = userService.getUserByName("test0");
        java.util.List<ProjectVO> projects = projectService.getProjectInfo(testUser.getId());

        assertEquals(projects.size(), 2);
        for (ProjectVO proj : projects) {
            System.out.println("Files of project " + proj.getId() + ":");
            java.util.Map<String, SrcFileAnalysis> files = projectService.fetchFromCache(proj.getId());
            for (String fileName : files.keySet())
                System.out.println(fileName);
        }
    }

}
