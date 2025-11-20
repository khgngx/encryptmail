package util;

import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import core.ServiceRegistry;
import core.model.Account;
import core.model.Email;
import core.repository.AccountRepository;
import core.repository.EmailRepository;
import core.service.AuthService;

/**
 * Utility to migrate data from .dat files to PostgreSQL
 */
public class DataMigrationUtil {
    private static final Logger logger = Logger.getLogger(DataMigrationUtil.class.getName());
    
    /**
     * Migrate all data from files to database
     */
    public static void migrateAllData() {
        logger.info("Starting data migration from files to database...");
        
        try {
            ServiceRegistry registry = ServiceRegistry.getInstance();
            
            if (registry.getConfig().isDemoMode()) {
                logger.info("Skipping migration in demo mode");
                return;
            }
            
            // Migrate accounts
            migrateAccounts(registry);
            
            // Migrate mail history
            migrateMailHistory(registry);
            
            logger.info("Data migration completed successfully");
            
        } catch (Exception e) {
            logger.severe("Data migration failed: " + e.getMessage());
            throw new RuntimeException("Migration failed", e);
        }
    }
    
    /**
     * Migrate accounts from accounts.dat to database
     */
    private static void migrateAccounts(ServiceRegistry registry) {
        Path accountsFile = Paths.get("data/accounts.dat");
        if (!Files.exists(accountsFile)) {
            logger.info("No accounts.dat file found, skipping account migration");
            return;
        }
        
        try {
            AccountRepository accountRepo = registry.getAccountRepository();
            AuthService authService = registry.getAuthService();
            logger.info("Using auth service: " + authService.getClass().getSimpleName());
            
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(accountsFile))) {
                @SuppressWarnings("unchecked")
                Map<String, AccountManager.AccountInfo> oldAccounts = 
                    (Map<String, AccountManager.AccountInfo>) ois.readObject();
                
                int migrated = 0;
                for (AccountManager.AccountInfo oldAccount : oldAccounts.values()) {
                    // Check if account already exists
                    if (accountRepo.existsByEmail(oldAccount.getEmail())) {
                        logger.info("Account already exists, skipping: " + oldAccount.getEmail());
                        continue;
                    }
                    
                    // Create new account
                    Account newAccount = new Account(
                        oldAccount.getEmail(),
                        oldAccount.getPassword(), // Already hashed in old format
                        oldAccount.getSmtpHost(),
                        oldAccount.getSmtpPort(),
                        oldAccount.getImapHost(),
                        oldAccount.getImapPort()
                    );
                    
                    if (oldAccount.getLastLogin() != null) {
                        newAccount.setLastLoginAt(oldAccount.getLastLogin().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                    }
                    
                    newAccount.setActive(oldAccount.isActive());
                    
                    accountRepo.save(newAccount);
                    migrated++;
                    
                    logger.info("Migrated account: " + oldAccount.getEmail());
                }
                
                logger.info("Migrated " + migrated + " accounts from accounts.dat");
            }
            
        } catch (Exception e) {
            logger.severe("Failed to migrate accounts: " + e.getMessage());
            throw new RuntimeException("Account migration failed", e);
        }
    }
    
    /**
     * Migrate mail history from mail_history.dat to database
     */
    private static void migrateMailHistory(ServiceRegistry registry) {
        Path historyFile = Paths.get("data/mail_history.dat");
        if (!Files.exists(historyFile)) {
            logger.info("No mail_history.dat file found, skipping mail history migration");
            return;
        }
        
        try {
            EmailRepository emailRepo = registry.getEmailRepository();
            AccountRepository accountRepo = registry.getAccountRepository();
            
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(historyFile))) {
                @SuppressWarnings("unchecked")
                Map<String, List<MailHistoryManager.MailRecord>> oldHistory = 
                    (Map<String, List<MailHistoryManager.MailRecord>>) ois.readObject();
                
                int migrated = 0;
                for (Map.Entry<String, List<MailHistoryManager.MailRecord>> entry : oldHistory.entrySet()) {
                    String folder = entry.getKey();
                    List<MailHistoryManager.MailRecord> records = entry.getValue();
                    
                    for (MailHistoryManager.MailRecord oldRecord : records) {
                        // Find account by email (assuming from/to contains account email)
                        String accountEmail = determineAccountEmail(oldRecord);
                        if (accountEmail == null) {
                            logger.warning("Cannot determine account for mail record: " + oldRecord.getId());
                            continue;
                        }
                        
                        var accountOpt = accountRepo.findByEmail(accountEmail);
                        if (accountOpt.isEmpty()) {
                            logger.warning("Account not found for email: " + accountEmail);
                            continue;
                        }
                        
                        Long accountId = accountOpt.get().getId();
                        
                        // Create new email record
                        Email newEmail = new Email(
                            accountId,
                            folder,
                            oldRecord.getFrom(),
                            oldRecord.getTo(),
                            oldRecord.getSubject(),
                            oldRecord.getBody()
                        );
                        
                        newEmail.setEncrypted(oldRecord.isEncrypted());
                        newEmail.setSigned(oldRecord.isSigned());
                        newEmail.setSignatureOk(oldRecord.isSigned()); // Assume old signed messages are valid
                        newEmail.setRead(oldRecord.isRead());
                        newEmail.setImportant(oldRecord.isImportant());
                        newEmail.setCreatedAt(oldRecord.getTimestamp().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                        
                        emailRepo.save(newEmail);
                        migrated++;
                    }
                }
                
                logger.info("Migrated " + migrated + " email records from mail_history.dat");
            }
            
        } catch (Exception e) {
            logger.severe("Failed to migrate mail history: " + e.getMessage());
            throw new RuntimeException("Mail history migration failed", e);
        }
    }
    
    /**
     * Determine account email from mail record
     */
    private static String determineAccountEmail(MailHistoryManager.MailRecord record) {
        // For sent emails, account is the sender
        if ("sent".equals(record.getFolder())) {
            return record.getFrom();
        }
        // For inbox/drafts, account is the recipient
        else {
            return record.getTo();
        }
    }
    
    /**
     * Backup existing .dat files after successful migration
     */
    public static void backupDataFiles() {
        try {
            String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            Path accountsFile = Paths.get("data/accounts.dat");
            if (Files.exists(accountsFile)) {
                Files.move(accountsFile, Paths.get("data/accounts_backup_" + timestamp + ".dat"));
                logger.info("Backed up accounts.dat");
            }
            
            Path historyFile = Paths.get("data/mail_history.dat");
            if (Files.exists(historyFile)) {
                Files.move(historyFile, Paths.get("data/mail_history_backup_" + timestamp + ".dat"));
                logger.info("Backed up mail_history.dat");
            }
            
        } catch (Exception e) {
            logger.warning("Failed to backup data files: " + e.getMessage());
        }
    }
}
