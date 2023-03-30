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
        tarGzFileCreator.addFileToTarGz("main.cpp", "int main(){int a = 1;return 0;}");
        return tarGzFileCreator.getTarGzBytes();
    }
    @Test
    public void TestProjectBasic() throws IOException {
        Project project = new Project();
        project.setSourceCode(CppExampleFile());
        // 用户名不存在
        project.setUserId(10086);
        Assertions.assertThrows(Exception.class, () -> projectMapper.insert(project));

        // 创建新用户并插入项目
        User newUser = new User();
        newUser.setUsername("test");
        newUser.setPassword("test");
        userMapper.insert(newUser);
        int userId = newUser.getId();

        project.setUserId(userId);
        projectMapper.insert(project);
        project.setAnalyseResult("{\"test\": 1}");
        projectMapper.updateById(project);

        User user = userMapper.selectWithProjectById(userId);
        Assertions.assertEquals(1, user.getProjectList().size());
    }
}
