package core;

import java.util.logging.Logger;

import config.AppConfig;
import core.repository.AccountRepository;
import core.repository.EmailRepository;
import core.service.AuthService;
import core.service.CryptoService;
import core.service.KeyService;
import core.service.MailHistoryService;
import core.service.MailService;
import core.service.SecureMailService;
import infra.auth.DefaultAuthService;
import infra.crypto.DefaultCryptoService;
import infra.crypto.DefaultKeyService;
import infra.crypto.DefaultSecureMailService;
import infra.service.DefaultMailHistoryService;
import infra.db.DbConnectionManager;
import infra.db.PgAccountRepository;
import infra.db.PgEmailRepository;
import infra.mail.SmtpImapMailService;
import mail.MailClient;

/**
 * Service registry for dependency injection and service management
 */
public class ServiceRegistry {
    private static final Logger logger = Logger.getLogger(ServiceRegistry.class.getName());
    private static ServiceRegistry instance;
    private final AppConfig config;
    private MailService mailService;
    private AuthService authService;
    private AccountRepository accountRepository;
    private EmailRepository emailRepository;
    private CryptoService cryptoService;
    private KeyService keyService;
    private SecureMailService secureMailService;
    private MailHistoryService mailHistoryService;
    
    private ServiceRegistry() {
        System.out.println("   - REGISTRY: Initializing ServiceRegistry...");
        this.config = AppConfig.getInstance();
        System.out.println("   - REGISTRY: Config obtained, initializing services...");
        initializeServices();
        System.out.println("   - REGISTRY: ServiceRegistry initialized successfully");
    }
    
    public static ServiceRegistry getInstance() {
        if (instance == null) {
            instance = new ServiceRegistry();
        }
        return instance;
    }
    
    private void initializeServices() {
        try {
            // Initialize database connection if not in demo mode
            if (!config.isDemoMode()) {
                logger.info("Initializing database connection...");
                DbConnectionManager.initialize();
                
                // Initialize repositories
                this.accountRepository = new PgAccountRepository();
                this.emailRepository = new PgEmailRepository();
                
                // Initialize auth service
                this.authService = new DefaultAuthService(accountRepository);
                
                // Initialize mail history service
                this.mailHistoryService = new DefaultMailHistoryService(emailRepository);
                
                // Initialize crypto services
                this.cryptoService = new DefaultCryptoService();
                this.keyService = new DefaultKeyService();
                
                // Initialize mail service first for non-demo mode
                this.mailService = new SmtpImapMailService();
                this.secureMailService = new DefaultSecureMailService(mailService, cryptoService, keyService);
                
                logger.info("Database and crypto services initialized successfully");
            } else {
                logger.info("Running in demo mode - database services disabled");
            }
            
            // Initialize MailService based on app mode
            if (config.isDemoMode()) {
                // For demo mode, we'll create a wrapper around the existing MailClient
                this.mailService = new DemoMailServiceAdapter();
            } else {
                // For real modes (GUI_REMOTE, CLI_LOCAL), use SMTP/IMAP
                this.mailService = new SmtpImapMailService();
            }
            
        } catch (Exception e) {
            logger.severe("Failed to initialize services: " + e.getMessage());
            throw new RuntimeException("Service initialization failed", e);
        }
    }
    
    public MailService getMailService() {
        return mailService;
    }
    
    public AuthService getAuthService() {
        if (authService == null) {
            throw new IllegalStateException("AuthService not available in demo mode");
        }
        return authService;
    }
    
    public AccountRepository getAccountRepository() {
        if (accountRepository == null) {
            throw new IllegalStateException("AccountRepository not available in demo mode");
        }
        return accountRepository;
    }
    
    public EmailRepository getEmailRepository() {
        if (emailRepository == null) {
            throw new IllegalStateException("EmailRepository not available in demo mode");
        }
        return emailRepository;
    }
    
    public CryptoService getCryptoService() {
        if (cryptoService == null) {
            throw new IllegalStateException("CryptoService not available in demo mode");
        }
        return cryptoService;
    }
    
    public KeyService getKeyService() {
        if (keyService == null) {
            throw new IllegalStateException("KeyService not available in demo mode");
        }
        return keyService;
    }
    
    public SecureMailService getSecureMailService() {
        if (secureMailService == null) {
            throw new IllegalStateException("SecureMailService not available in demo mode");
        }
        return secureMailService;
    }
    
    public MailHistoryService getMailHistoryService() {
        if (mailHistoryService == null) {
            throw new IllegalStateException("MailHistoryService not available in demo mode");
        }
        return mailHistoryService;
    }
    
    public AppConfig getConfig() {
        return config;
    }
    
    /**
     * Shutdown all services
     */
    public void shutdown() {
        logger.info("Shutting down services...");
        if (!config.isDemoMode()) {
            DbConnectionManager.shutdown();
        }
    }
    
    /**
     * Adapter to wrap existing MailClient for demo mode
     */
    private static class DemoMailServiceAdapter implements MailService {
        @Override
        public void sendMail(String from, String password, String to, String subject, String body) throws Exception {
            MailClient.sendMail(from, to, subject, body);
        }
        
        @Override
        public java.util.List<jakarta.mail.Message> fetchInbox(String email, String password) throws Exception {
            return MailClient.fetchInbox(email, password);
        }
        
        @Override
        public boolean testConnection(String email, String password) {
            try {
                // In demo mode, always return true if demo server is running
                MailClient.startDemoServer();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
