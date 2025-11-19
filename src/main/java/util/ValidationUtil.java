package util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation and security checks
 */
public class ValidationUtil {
    
    // Email validation pattern (RFC 5322 compliant)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Password strength pattern (at least 8 chars, 1 upper, 1 lower, 1 digit)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"
    );
    
    private ValidationUtil() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Validate email address format
     * @param email email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Check length limits
        if (email.length() > 254) { // RFC 5321 limit
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate password strength
     * @param password password to validate
     * @return true if password meets strength requirements
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Sanitize string input to prevent injection attacks
     * @param input input string
     * @return sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove potential HTML/script chars
                   .replaceAll("\\p{Cntrl}", ""); // Remove control characters
    }
    
    /**
     * Validate subject line
     * @param subject email subject
     * @return true if valid subject
     */
    public static boolean isValidSubject(String subject) {
        if (subject == null) {
            return false;
        }
        
        String trimmed = subject.trim();
        return trimmed.length() > 0 && trimmed.length() <= 998; // RFC 5322 limit
    }
    
    /**
     * Validate email body content
     * @param body email body
     * @return true if valid body
     */
    public static boolean isValidBody(String body) {
        if (body == null) {
            return false;
        }
        
        // Check for reasonable size limit (10MB)
        return body.length() <= 10 * 1024 * 1024;
    }
    
    /**
     * Check if string contains only safe characters for file names
     * @param filename filename to check
     * @return true if safe filename
     */
    public static boolean isSafeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        // Check for dangerous patterns
        String trimmed = filename.trim();
        if (trimmed.contains("..") || trimmed.contains("/") || trimmed.contains("\\")) {
            return false;
        }
        
        // Check for reserved names (Windows)
        String[] reserved = {"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", 
                           "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", 
                           "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
        
        for (String res : reserved) {
            if (trimmed.equalsIgnoreCase(res)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Get password strength description
     * @param password password to check
     * @return strength description
     */
    public static String getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Empty";
        }
        
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // Character variety
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
        
        switch (score) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
            case 3:
                return "Weak";
            case 4:
                return "Medium";
            case 5:
                return "Strong";
            case 6:
                return "Very Strong";
            default:
                return "Unknown";
        }
    }
}
