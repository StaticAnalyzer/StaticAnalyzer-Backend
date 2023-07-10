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

    private static String GOOD_USERNAME = "gooduser";
    private static String GOOD_PASSWORD = "12345678";

    @org.junit.jupiter.api.Test
    public void testSignature() {
        User goodUser = new User();
        goodUser.setUsername(GOOD_USERNAME);
        goodUser.setPassword(GOOD_PASSWORD);

        userService.createUser(goodUser);
        User databaseUser = userService.getUserByName(GOOD_USERNAME);
        assertDoesNotThrow(() -> userService.checkSignature(
                userService.getSignature(databaseUser.getId()), databaseUser.getId()));

        System.out.println("User " + databaseUser.getId() + " passed signature test!");
    }

    @org.junit.jupiter.api.Test
    public void testFormat() {
        User badUser = new User();
        // 非法格式用户
        generateRandomUserId(badUser);
        for (int i = 0; i < 100; i++) {
            badUser.setUsername(generateRandomString(16));
            badUser.setPassword(generateRandomString(16));
            try {
                userService.checkUserInfoFormat(badUser);
            } catch (ServiceError serviceError) {
                assertTrue(!badUser.getUsername().matches(userProperties.getUsernameFormat()) ||
                        !badUser.getPassword().matches(userProperties.getPasswordFormat()));
            }

            System.out.println("Bad user " + badUser.getUsername() + "with password " + badUser.getPassword()
                    + " passed format test!");
        }
    }

    @org.junit.jupiter.api.Test
    public void testUpdate() {
        User randomUser = new User();
        for (int i = 0; i < 20; i++) {
            randomUser.setUsername(generateRandomString(16));
            randomUser.setPassword(generateRandomString(16));
            try {
                userService.checkUserInfoFormat(randomUser);
            } catch (ServiceError serviceError) {
                continue;
            }

            assertDoesNotThrow(() -> userService.createUser(randomUser));
            assertDoesNotThrow(() -> userService.getUserByName(randomUser.getUsername()));

            User checkUser = userService.getUserByName(randomUser.getUsername());
            // 反转密码再测试
            String revertedPassword = new StringBuilder(checkUser.getPassword()).reverse().toString();
            checkUser.setPassword(revertedPassword);
            assertDoesNotThrow(() -> userService.updateUser(checkUser));

            assertEquals(userService.getUserById(checkUser.getId()).getUsername(), checkUser.getUsername());
            assertEquals(userService.getUserByName(checkUser.getUsername()).getId(), checkUser.getId());
            System.out.println("Random user " + checkUser.getId() + " passed update test!");
        }
    }

}
