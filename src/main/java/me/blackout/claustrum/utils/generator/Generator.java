package me.blackout.claustrum.utils.generator;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Generator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "1234567890";

    private static final String SYMBOL = "!@#$%^&*()-_=+[]{};:,.<>?";
    private static final String AMBIGUOUS = "0O1lI|`'\"";

    /**
     * Password Generator
     * */
    public static String generate() {
        return generate(16, true, true, true, true , false);
    }

    public static String generate(int length, boolean includeUpper, boolean includeLower,
                                  boolean includeDigits, boolean includeSymbol, boolean excludeAmbiguity) {

        // String pool
        StringBuilder fullPool = new StringBuilder();
        List<String> reqPool = new ArrayList<>();

        if (!includeUpper && !includeLower && !includeDigits && !includeSymbol) {
            throw new IllegalStateException("At least one character class must be set to true");
        }

        // Appending characters to big pool
        if (includeUpper) {
            String pool = strip(UPPER, excludeAmbiguity);
            fullPool.append(pool);
            reqPool.add(pool);
        }

        if (includeLower) {
            String pool = strip(LOWER, excludeAmbiguity);
            fullPool.append(pool);
            reqPool.add(pool);
        }

        if (includeDigits) {
            String pool = strip(DIGITS, excludeAmbiguity);
            fullPool.append(pool);
            reqPool.add(pool);
        }

        if (includeSymbol) {
            String pool = strip(SYMBOL, excludeAmbiguity);
            fullPool.append(pool);
            reqPool.add(pool);
        }

        // Check required pool size
        if (length < reqPool.size()) {
            throw new IllegalArgumentException(
                    "Length (" + length + ") must be at least " + reqPool.size()
                            + " to guarantee one character from each enabled class.");
        }

        char[] result = new char[length];
        int index = 0;

        for (String pool : reqPool) {
            result[index++] = pool.charAt(SECURE_RANDOM.nextInt(pool.length()));
        }

        String combined = fullPool.toString();
        while (index < length) {
            result[index++] = combined.charAt(SECURE_RANDOM.nextInt(combined.length()));
        }

        for (int i = result.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(result.length);
            char temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }

        return new String(result);
    }

    private static String strip(String pool, boolean excludeAmbiguity) {
        if (!excludeAmbiguity) return pool;
        StringBuilder sb = new StringBuilder();
        for (char c : pool.toCharArray()) {
            if (AMBIGUOUS.indexOf(c) == -1) sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Password Strength check (Using a honest password checker, no check-match against entire dictionary or leaked password like zxcvbn), Maybe used in later versions but not for current
     * */
    public static double estimateEntropy(String password) {
        if (password == null || password.isEmpty()) return 0;

        int poolSize = checkPoolSize(password);
        double rawBits = password.length() * (Math.log(poolSize) / Math.log(2));

        double penalty = patternPenalty(password);
        return Math.max(0, rawBits - penalty);
    }

    // Strength Label
    public static String strengthLabel(double bits) {
        if (bits < 28) return "Very Weak";
        if (bits < 36) return "Weak";
        if (bits < 60) return "Reasonable";
        if (bits < 90) return "Strong";
        return "Very Strong";
    }

    // check for any pattern and penalize
    private static double patternPenalty(String password) {
        char[] chars = password.toCharArray();
        double penalty = 0;

        // Check for repeated letters and penalize
        int repetition = 1;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == chars[i - 1]) {
                repetition++;
                if (repetition >= 3) penalty += 4;
            } else {
                repetition = 1;
            }
        }

        // Check for sequence like 'abcd' and '4321'
        int seq = 1;
        for (int i = 1; i < chars.length; i++) {
            boolean ascending = chars[i] == chars[i - 1] + 1;
            boolean descending = chars[i] == chars[i - 1] - 1;
            if (ascending || descending) {
                seq++;
                if (seq >= 3) penalty += 4;
            } else {
                seq = 1;
            }
        }

        // for common keyboard pattern, massively penalize for those
        String lower = password.toLowerCase();
        String[] patterns = {"qwerty", "asdf", "zxcv", "uiop", "ghjkl", "1234", "09876"};
        for (String pattern : patterns) {
            if (lower.contains(pattern)) penalty += 10;
        }

        return penalty;
    }

    private static int checkPoolSize(String password) {
        boolean upper = false, lower = false, digits = false, symbols = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digits = true;
            else symbols = true;
        }

        int poolSize = 0;
        if (upper) poolSize += UPPER.length();
        if (lower) poolSize += LOWER.length();
        if (digits) poolSize += DIGITS.length();
        if (symbols) poolSize += SYMBOL.length();

        return Math.max(poolSize, 1);
    }
}
