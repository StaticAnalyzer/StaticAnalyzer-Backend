package com.staticanalyzer.staticanalyzer.utils.auth;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    private final Pattern patternUsername;
    private final Pattern patternPassword;

    public AuthUtils(
            @Value("${min-username-length}") int minUsernameLength,
            @Value("${max-username-length}") int maxUsernameLength,
            @Value("${min-password-length}") int minPasswordLength,
            @Value("${max-password-length}") int maxPasswordLength) {
        String regexUsername = String.format("[0-9a-zA-Z_-]{%d,%d}", minUsernameLength, maxUsernameLength);
        String regexPassword = String.format(".{%d,%d}", minPasswordLength, maxPasswordLength);
        patternUsername = Pattern.compile(regexUsername);
        patternPassword = Pattern.compile(regexPassword);
    }

    public boolean verifyUsername(String username) {
        return patternUsername.matcher(username).matches();
    }

    public boolean verifyPassword(String password) {
        return patternPassword.matcher(password).matches();
    }
}
