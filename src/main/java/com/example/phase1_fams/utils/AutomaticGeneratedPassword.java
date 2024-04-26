package com.example.phase1_fams.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class AutomaticGeneratedPassword {
    private static final SecureRandom RANDOM = new SecureRandom();
    // Define the characters that can be used in the password
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int PASSWORD_LENGTH = 10; // Default length, can be adjusted

    public static String generateRandomPassword(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }

    // Overloaded method to use the default password length
    public static String generateRandomPassword() {
        return generateRandomPassword(PASSWORD_LENGTH);
    }
}
