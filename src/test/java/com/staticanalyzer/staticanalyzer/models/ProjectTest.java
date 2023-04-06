package com.staticanalyzer.staticanalyzer.models;

import com.staticanalyzer.staticanalyzer.entities.Project;
import com.staticanalyzer.staticanalyzer.entities.User;
import com.staticanalyzer.staticanalyzer.mapper.ProjectMapper;
import com.staticanalyzer.staticanalyzer.mapper.UserMapper;
import com.staticanalyzer.staticanalyzer.utils.TarGzFileCreator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
@Transactional
@Rollback
public class ProjectTest {
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper;

    private byte[] CppExampleFile() throws IOException {
        TarGzFileCreator tarGzFileCreator = new TarGzFileCreator();
        tarGzFileCreator.addFileToTarGz("main.cpp", "int main(){printf(\"hello world\");}");
        return tarGzFileCreator.getTarGzBytes();
    }

    @Test
    public void TestProjectBasic() throws IOException {
        Project project = new Project();
        project.setSourceCode(CppExampleFile());
        project.setConfig("{\"test\":\"some_config\"}");
        project.setUserId(10086);
        Assertions.assertThrows(Exception.class, () -> projectMapper.insert(project));

        User newUser = new User();
        newUser.setUsername("test");
        newUser.setPassword("test");
        userMapper.insert(newUser);

        project.setUserId(newUser.getId());
        projectMapper.insert(project);
        project.setAnalyseResult("{\"test\": 1}");
        projectMapper.updateById(project);

        User user = userMapper.selectWithProjectIdById(newUser.getId());
        System.out.println(user.getProjectIdList());
        Assertions.assertEquals(1, user.getProjectIdList().size());
    }
}
