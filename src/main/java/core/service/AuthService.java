package core.service;

import java.util.Optional;

import core.model.Account;

/**
 * Authentication service interface
 */
public interface AuthService {
    
    /**
     * Login with email and password
     * @param email user email
     * @param password plain text password
     * @return account if login successful
     */
    Optional<Account> login(String email, String password);
    
    /**
     * Register a new account
     * @param email user email
     * @param password plain text password
     * @param smtpHost SMTP server host
     * @param smtpPort SMTP server port
     * @param imapHost IMAP server host
     * @param imapPort IMAP server port
     * @return created account
     */
    Account register(String email, String password, String smtpHost, int smtpPort, 
                    String imapHost, int imapPort);
    
    /**
     * Validate email format
     * @param email email to validate
     * @return true if valid
     */
    boolean isValidEmail(String email);
    
    /**
     * Check if email already exists
     * @param email email to check
     * @return true if exists
     */
    boolean emailExists(String email);
    
    /**
     * Hash password
     * @param password plain text password
     * @return hashed password
     */
    String hashPassword(String password);
    
    /**
     * Verify password against hash
     * @param password plain text password
     * @param hash stored hash
     * @return true if password matches
     */
    boolean verifyPassword(String password, String hash);
}
