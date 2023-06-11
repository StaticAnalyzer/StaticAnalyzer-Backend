package com.staticanalyzer.staticanalyzer.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.service.UserService;

/**
 * 用户功能测试
 */
@SpringBootTest
@Transactional
@Rollback
public class UserServiceTest {

    @Autowired
    private UserService userService;

    //@Test
    public void TestUserBasic() {
        User badUser = new User();
        badUser.setId(8);
        badUser.setUsername("test");
        badUser.setPassword("8888");
        assertDoesNotThrow(() -> userService.checkUserInfoFormat(badUser));

        User newUser = new User();
        newUser.setId(1000);
        newUser.setUsername("nju");
        newUser.setPassword("88888888");
        assertDoesNotThrow(() -> userService.checkUserInfoFormat(newUser));

        userService.createUser(newUser);
        User byId = userService.getUserById(newUser.getId());
        User byUsername = userService.getUserByName(newUser.getUsername());
        assertEquals(byId, newUser);
        assertEquals(byUsername, newUser);

        newUser.setPassword("66666666");
        assertDoesNotThrow(() -> userService.checkUserInfoFormat(newUser));
        userService.updateUser(newUser);
        byId = userService.getUserById(newUser.getId());
        byUsername = userService.getUserByName(newUser.getUsername());
        assertEquals(byId, newUser);
        assertEquals(byUsername, newUser);
    }
}
