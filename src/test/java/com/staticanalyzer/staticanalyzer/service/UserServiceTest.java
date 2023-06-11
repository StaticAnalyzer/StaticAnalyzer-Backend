package com.staticanalyzer.staticanalyzer.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.staticanalyzer.staticanalyzer.config.user.UserProperties;
import com.staticanalyzer.staticanalyzer.entity.user.User;
import com.staticanalyzer.staticanalyzer.service.error.ServiceError;

@Transactional
@Rollback
@SpringBootTest
public class UserServiceTest {

    private static java.util.Random USER_RANDOM = new java.util.Random();

    private void generateRandomUserId(User user) {
        int randomId = USER_RANDOM.nextInt(32768);
        user.setId(randomId);
    }

    private String generateRandomString(int maxLength) {
        int randomLength = USER_RANDOM.nextInt(maxLength);
        char[] charArray = new char[randomLength];
        for (int i = 0; i < randomLength; i++) {
            int randomASCII = USER_RANDOM.nextInt(94) + 32;
            charArray[i] = (char) randomASCII;
        }
        return new String(charArray);
    }

    @Autowired
    private UserProperties userProperties;

    @Autowired
    private UserService userService;

    @org.junit.jupiter.api.Test
    public void testUserValidation() {
        User badFormattedUser = new User();
        // 非法格式用户
        generateRandomUserId(badFormattedUser);
        for (int i = 0; i < 100; i++) {
            badFormattedUser.setUsername(generateRandomString(16));
            badFormattedUser.setPassword(generateRandomString(16));
            try {
                userService.checkUserInfoFormat(badFormattedUser);
            } catch (ServiceError serviceError) {
                assertTrue(!badFormattedUser.getUsername().matches(userProperties.getUsernameFormat()) ||
                        !badFormattedUser.getPassword().matches(userProperties.getPasswordFormat()));
            }
        }
    }

    @org.junit.jupiter.api.Test
    public void testDatabaseUser() {
        User validUser = new User();
        generateRandomUserId(validUser);
        for (int i = 0; i < 100; i++) {
            validUser.setUsername(generateRandomString(16));
            validUser.setPassword(generateRandomString(16));
            try {
                userService.checkUserInfoFormat(validUser);
            } catch (ServiceError serviceError) {
                continue;
            }

            assertDoesNotThrow(() -> userService.createUser(validUser));

            User checkUser = new User();
            checkUser.setId(validUser.getId());
            checkUser.setUsername(validUser.getUsername());
            checkUser.setPassword(validUser.getPassword());

            assertEquals(userService.getUserById(validUser.getId()), checkUser);
            assertEquals(userService.getUserByName(validUser.getUsername()), checkUser);

            // 反转密码再测试
            String revertedPassword = new StringBuilder(checkUser.getPassword()).reverse().toString();
            checkUser.setPassword(revertedPassword);
            assertDoesNotThrow(() -> userService.updateUser(checkUser));

            assertEquals(userService.getUserById(validUser.getId()), checkUser);
            assertEquals(userService.getUserByName(validUser.getUsername()), checkUser);
        }
    }

}
