package util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Manages account storage and validation
 */
public class AccountManager {
    private static final Logger logger = Logger.getLogger(AccountManager.class.getName());
    private static final String ACCOUNTS_FILE = "data/accounts.dat";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    
    private static final Map<String, AccountInfo> accounts = new HashMap<>();
    
    public static class AccountInfo implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private final String email;
        private final String password;
        private final String smtpHost;
        private final String imapHost;
        private final int smtpPort;
        private final int imapPort;
        private final Date lastLogin;
        private final boolean isActive;
        
        public AccountInfo(String email, String password, String smtpHost, String imapHost, 
                          int smtpPort, int imapPort, Date lastLogin, boolean isActive) {
            this.email = email;
            this.password = password;
            this.smtpHost = smtpHost;
            this.imapHost = imapHost;
            this.smtpPort = smtpPort;
            this.imapPort = imapPort;
            this.lastLogin = lastLogin;
            this.isActive = isActive;
        }
        
        // Getters
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getSmtpHost() { return smtpHost; }
        public String getImapHost() { return imapHost; }
        public int getSmtpPort() { return smtpPort; }
        public int getImapPort() { return imapPort; }
        public Date getLastLogin() { return lastLogin; }
        public boolean isActive() { return isActive; }
    }
    
    static {
        loadAccounts();
    }
    
    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        // Check basic email format
        if (!emailPattern.matcher(trimmedEmail).matches()) {
            return false;
        }
        
        // Check for Gmail requirement (if specified)
        if (!trimmedEmail.endsWith("@gmail.com")) {
            logger.warning("Email validation: Email must be a Gmail address (@gmail.com)");
            return false;
        }
        
        return true;
    }
    
    /**
     * Saves account information to local storage
     */
    public static boolean saveAccount(String email, String password, String smtpHost, 
                                    String imapHost, int smtpPort, int imapPort) {
        if (!isValidEmail(email)) {
            logger.warning("Invalid email format: " + email);
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            logger.warning("Password cannot be empty");
            return false;
        }
        
        try {
            AccountInfo account = new AccountInfo(
                email.toLowerCase().trim(),
                password,
                smtpHost,
                imapHost,
                smtpPort,
                imapPort,
                new Date(),
                true
            );
            
            accounts.put(email.toLowerCase().trim(), account);
            saveAccountsToFile();
            
            logger.info("Account saved successfully: " + email);
            return true;
            
        } catch (Exception e) {
            logger.severe("Failed to save account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves account information
     */
    public static AccountInfo getAccount(String email) {
        return accounts.get(email.toLowerCase().trim());
    }
    
    /**
     * Gets all saved accounts
     */
    public static Collection<AccountInfo> getAllAccounts() {
        return accounts.values();
    }
    
    /**
     * Removes an account
     */
    public static boolean removeAccount(String email) {
        AccountInfo removed = accounts.remove(email.toLowerCase().trim());
        if (removed != null) {
            saveAccountsToFile();
            logger.info("Account removed: " + email);
            return true;
        }
        return false;
    }
    
    /**
     * Updates last login time
     */
    public static void updateLastLogin(String email) {
        AccountInfo account = accounts.get(email.toLowerCase().trim());
        if (account != null) {
            AccountInfo updatedAccount = new AccountInfo(
                account.getEmail(),
                account.getPassword(),
                account.getSmtpHost(),
                account.getImapHost(),
                account.getSmtpPort(),
                account.getImapPort(),
                new Date(),
                account.isActive()
            );
            accounts.put(email.toLowerCase().trim(), updatedAccount);
            saveAccountsToFile();
        }
    }
    
    /**
     * Loads accounts from file
     */
    private static void loadAccounts() {
        try {
            Path filePath = Paths.get(ACCOUNTS_FILE);
            if (!Files.exists(filePath)) {
                logger.info("No accounts file found, starting with empty accounts");
                return;
            }
            
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(filePath))) {
                @SuppressWarnings("unchecked")
                Map<String, AccountInfo> loadedAccounts = (Map<String, AccountInfo>) ois.readObject();
                accounts.putAll(loadedAccounts);
                logger.info("Loaded " + accounts.size() + " accounts from file");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to load accounts: " + e.getMessage());
        }
    }
    
    /**
     * Saves accounts to file
     */
    private static void saveAccountsToFile() {
        try {
            Path filePath = Paths.get(ACCOUNTS_FILE);
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(filePath))) {
                oos.writeObject(accounts);
                logger.info("Accounts saved to file");
            }
        } catch (Exception e) {
            logger.severe("Failed to save accounts: " + e.getMessage());
        }
    }
    
    /**
     * Validates login credentials
     */
    public static boolean validateLogin(String email, String password) {
        AccountInfo account = getAccount(email);
        if (account == null) {
            return false;
        }
        
        boolean isValid = account.getPassword().equals(password);
        if (isValid) {
            updateLastLogin(email);
        }
        
        return isValid;
    }
    
    /**
     * Gets validation error message for email
     */
    public static String getEmailValidationError(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required";
        }
        
        String trimmedEmail = email.trim();
        
        if (!emailPattern.matcher(trimmedEmail).matches()) {
            return "Invalid email format. Please enter a valid email address";
        }
        
        if (!trimmedEmail.endsWith("@gmail.com")) {
            return "Email must be a Gmail address (@gmail.com)";
        }
        
        return null; // No error
    }
}
