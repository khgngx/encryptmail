package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Application configuration manager supporting different modes
 */
public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static AppConfig instance;
    private Properties properties;
    
    public enum AppMode {
        GUI_REMOTE,  // GUI app connecting to remote mail server
        CLI_LOCAL,   // CLI app using local mail server with system users
        DEMO         // Demo mode with mock data
    }
    
    private AppConfig() {
        System.out.println("   - CONFIG: Initializing AppConfig...");
        loadConfig();
        System.out.println("   - CONFIG: AppConfig initialized successfully");
    }
    
    public static AppConfig getInstance() {
        System.out.println("   - CONFIG: Getting AppConfig instance...");
        if (instance == null) {
            System.out.println("   - CONFIG: Creating new AppConfig instance...");
            instance = new AppConfig();
        } else {
            System.out.println("   - CONFIG: Using existing AppConfig instance");
        }
        return instance;
    }
    
    private void loadConfig() {
        System.out.println("     - CONFIG: Creating Properties object...");
        properties = new Properties();
        
        // Try to load from application.properties
        System.out.println("     - CONFIG: Loading application.properties...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) {
                System.out.println("     - CONFIG: Found application.properties, loading...");
                properties.load(is);
                System.out.println("     - CONFIG: Properties loaded successfully");
                System.out.println("     - CONFIG: App mode from file: " + properties.getProperty("app.mode", "DEMO"));
                logger.info("Loaded configuration from application.properties");
            } else {
                System.err.println("     - CONFIG: No application.properties found, using defaults");
                logger.info("No application.properties found, using defaults");
            }
        } catch (IOException e) {
            System.err.println("     - CONFIG: Failed to load application.properties: " + e.getMessage());
            e.printStackTrace();
            logger.warning("Failed to load application.properties: " + e.getMessage());
        }
        
        // Override with system properties if available
        System.out.println("     - CONFIG: Applying system property overrides...");
        properties.putAll(System.getProperties());
        System.out.println("     - CONFIG: Final app mode: " + properties.getProperty("app.mode", "DEMO"));
    }
    
    public AppMode getAppMode() {
        String mode = properties.getProperty("app.mode", "DEMO");
        try {
            return AppMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warning("Invalid app mode: " + mode + ", defaulting to DEMO");
            return AppMode.DEMO;
        }
    }
    
    public boolean isDemoMode() {
        return getAppMode() == AppMode.DEMO;
    }
    
    public boolean isGuiRemoteMode() {
        return getAppMode() == AppMode.GUI_REMOTE;
    }
    
    public boolean isCliLocalMode() {
        return getAppMode() == AppMode.CLI_LOCAL;
    }
    
    // Mail server configuration
    public String getSmtpHost() {
        AppMode mode = getAppMode();
        switch (mode) {
            case GUI_REMOTE:
                return properties.getProperty("mail.smtp.host.remote", "your-server-ip");
            case CLI_LOCAL:
                return properties.getProperty("mail.smtp.host.local", "localhost");
            case DEMO:
            default:
                return properties.getProperty("mail.smtp.host.demo", "localhost");
        }
    }
    
    public int getSmtpPort() {
        AppMode mode = getAppMode();
        switch (mode) {
            case GUI_REMOTE:
                return Integer.parseInt(properties.getProperty("mail.smtp.port.remote", "587"));
            case CLI_LOCAL:
                return Integer.parseInt(properties.getProperty("mail.smtp.port.local", "25"));
            case DEMO:
            default:
                return Integer.parseInt(properties.getProperty("mail.smtp.port.demo", "3025"));
        }
    }
    
    public String getImapHost() {
        AppMode mode = getAppMode();
        switch (mode) {
            case GUI_REMOTE:
                return properties.getProperty("mail.imap.host.remote", "your-server-ip");
            case CLI_LOCAL:
                return properties.getProperty("mail.imap.host.local", "localhost");
            case DEMO:
            default:
                return properties.getProperty("mail.imap.host.demo", "localhost");
        }
    }
    
    public int getImapPort() {
        AppMode mode = getAppMode();
        switch (mode) {
            case GUI_REMOTE:
                return Integer.parseInt(properties.getProperty("mail.imap.port.remote", "993"));
            case CLI_LOCAL:
                return Integer.parseInt(properties.getProperty("mail.imap.port.local", "143"));
            case DEMO:
            default:
                return Integer.parseInt(properties.getProperty("mail.imap.port.demo", "3143"));
        }
    }
    
    // Database configuration
    public String getDbUrl() {
        return properties.getProperty("db.url", "jdbc:postgresql://localhost:5432/securemail");
    }
    
    public String getDbUser() {
        return properties.getProperty("db.user", "securemail");
    }
    
    public String getDbPassword() {
        return properties.getProperty("db.password", "secret");
    }
    
    // Get current system user for CLI mode
    public String getCurrentSystemUser() {
        return System.getProperty("user.name");
    }
    
    public String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "localhost";
        }
    }
    
    // Generate email address based on mode
    public String generateEmailAddress(String username) {
        AppMode mode = getAppMode();
        switch (mode) {
            case GUI_REMOTE:
                return username + "@" + properties.getProperty("mail.domain.remote", "yourdomain.com");
            case CLI_LOCAL:
                return username + "@" + getHostname();
            case DEMO:
            default:
                return username + "@localhost";
        }
    }
}
