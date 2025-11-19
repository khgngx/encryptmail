package infra.auth;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import config.AppConfig;
import core.model.Account;
import core.repository.AccountRepository;
import core.service.AuthService;

/**
 * Default implementation of AuthService
 */
public class DefaultAuthService implements AuthService {
    private static final Logger logger = Logger.getLogger(DefaultAuthService.class.getName());
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    
    private final AccountRepository accountRepository;
    private final AppConfig config;
    
    public DefaultAuthService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.config = AppConfig.getInstance();
    }
    
    @Override
    public Optional<Account> login(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        
        Optional<Account> accountOpt = accountRepository.findByEmail(email.toLowerCase().trim());
        if (accountOpt.isEmpty()) {
            logger.warning("Login attempt for non-existent email: " + email);
            return Optional.empty();
        }
        
        Account account = accountOpt.get();
        if (!account.isActive()) {
            logger.warning("Login attempt for inactive account: " + email);
            return Optional.empty();
        }
        
        if (verifyPassword(password, account.getPasswordHash())) {
            // Update last login time
            accountRepository.updateLastLogin(account.getId());
            logger.info("Successful login: " + email);
            return Optional.of(account);
        } else {
            logger.warning("Failed login attempt: " + email);
            return Optional.empty();
        }
    }
    
    @Override
    public Account register(String email, String password, String smtpHost, int smtpPort, 
                           String imapHost, int imapPort) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        if (password == null || password.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        String hashedPassword = hashPassword(password);
        
        Account account = new Account(
            email.toLowerCase().trim(),
            hashedPassword,
            smtpHost,
            smtpPort,
            imapHost,
            imapPort
        );
        
        Account savedAccount = accountRepository.save(account);
        logger.info("Account registered: " + email);
        
        return savedAccount;
    }
    
    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        // Check basic email format
        if (!emailPattern.matcher(trimmedEmail).matches()) {
            return false;
        }
        
        // In CLI_LOCAL mode, check if it matches hostname pattern
        // if (config.isCliLocalMode()) {
        //     String expectedDomain = "@" + config.getHostname();
        //     if (!trimmedEmail.endsWith(expectedDomain)) {
        //         logger.warning("Email validation: Email must end with " + expectedDomain + " in CLI_LOCAL mode");
        //         return false;
        //     }
        // }
        
        return true;
    }
    
    @Override
    public boolean emailExists(String email) {
        return accountRepository.existsByEmail(email.toLowerCase().trim());
    }
    
    @Override
    public String hashPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));
            
            // Combine salt and hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            logger.severe("Failed to hash password: " + e.getMessage());
            throw new RuntimeException("Password hashing failed", e);
        }
    }
    
    @Override
    public boolean verifyPassword(String password, String hash) {
        try {
            byte[] combined = Base64.getDecoder().decode(hash);
            
            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            
            // Extract stored hash (remaining bytes)
            byte[] storedHash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedHash, 0, storedHash.length);
            
            // Hash the provided password with the same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));
            
            // Compare hashes
            return MessageDigest.isEqual(storedHash, hashedPassword);
            
        } catch (Exception e) {
            logger.severe("Failed to verify password: " + e.getMessage());
            return false;
        }
    }
}
