package app;

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import config.AppConfig;
import mail.MockMailClient;
import ui.modern.ModernLoginPanel;
import ui.modern.ModernMainApplication;
import ui.theme.ThemeManager;

public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    private static JFrame loginFrame;

    public static void main(String[] args) {
        System.out.println("=== SECURE MAIL CLIENT STARTUP DEBUG ===");
        System.out.println("Step 1: Starting main method...");
        
        // Set system look and feel
        System.out.println("Step 2: Setting Look and Feel...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("✓ Look and Feel set successfully");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("✗ Look and Feel failed: " + e.getMessage());
            logger.warning(() -> "Could not set system look and feel: " + e.getMessage());
        }

        // Get application configuration
        System.out.println("Step 3: Loading application configuration...");
        AppConfig config = null;
        try {
            config = AppConfig.getInstance();
            System.out.println("✓ Config loaded successfully");
            System.out.println("   - App Mode: " + config.getAppMode());
            System.out.println("   - Demo Mode: " + config.isDemoMode());
            logger.info("Starting application in mode: " + config.getAppMode());
        } catch (Exception e) {
            System.err.println("✗ Failed to load config: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Start demo mail server only in DEMO mode
        System.out.println("Step 4: Checking demo mode...");
        if (config.isDemoMode()) {
            System.out.println("   - Demo mode detected, starting mock mail server...");
            try {
                MockMailClient.startDemoServer();
                System.out.println("✓ Demo mail server started successfully");
                logger.info("Demo mail server started");
            } catch (Exception e) {
                System.err.println("✗ Failed to start demo mail server: " + e.getMessage());
                e.printStackTrace();
                logger.severe(() -> "Failed to start demo mail server: " + e.getMessage());
                JOptionPane.showMessageDialog(null, 
                    "Failed to start mail server: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            System.out.println("   - Not in demo mode, skipping mock server");
        }

        // Initialize test data only in DEMO mode or if explicitly enabled
        System.out.println("Step 5: Checking test data initialization...");
        if (config.isDemoMode() || Boolean.parseBoolean(System.getProperty("app.generate.testdata", "false"))) {
            System.out.println("   - Initializing test data...");
            initializeTestData();
        } else {
            System.out.println("   - Skipping test data initialization");
        }
        
        // Show login window
        System.out.println("Step 6: Showing login window...");
        showLoginWindow();
        System.out.println("✓ Application startup completed");
    }

    private static void showLoginWindow() {
        System.out.println("   - Creating login window on EDT...");
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("   - EDT: Initializing theme manager...");
                // Initialize theme manager
                ThemeManager themeManager = ThemeManager.getInstance();
                themeManager.applyTheme();
                System.out.println("   - EDT: Theme applied successfully");
                
                System.out.println("   - EDT: Creating login frame...");
                loginFrame = new JFrame("Secure Mail Client - Login");
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.setSize(450, 650);
                loginFrame.setLocationRelativeTo(null);
                loginFrame.setResizable(false);
                System.out.println("   - EDT: Login frame configured");

                System.out.println("   - EDT: Creating login panel...");
                ModernLoginPanel loginPanel = new ModernLoginPanel(new ModernLoginPanel.LoginCallback() {
                    @Override
                    public void onLoginSuccess(String email, String password) {
                        System.out.println("   - LOGIN SUCCESS: " + email);
                        loginFrame.dispose();
                        // Truyền cả email và password sang ModernMainApplication để sync IMAP với hMail
                        showModernMainApplication(email, password);
                    }

                    @Override
                    public void onLoginError(String message) {
                        System.err.println("   - LOGIN ERROR: " + message);
                        // Error already shown in LoginPanel
                    }
                });
                System.out.println("   - EDT: Login panel created successfully");

                loginFrame.add(loginPanel);
                loginFrame.setVisible(true);
                System.out.println("   - EDT: Login window displayed successfully");
            } catch (Exception e) {
                System.err.println("   - EDT ERROR: Failed to create login window: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static void showModernMainApplication(String email, String password) {
        System.out.println("   - MAIN APP: Creating main application for user: " + email);
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("   - MAIN APP EDT: Initializing ModernMainApplication...");
                ModernMainApplication modernApp = new ModernMainApplication(email, password);
                System.out.println("   - MAIN APP EDT: ModernMainApplication created successfully");
                
                System.out.println("   - MAIN APP EDT: Making application visible...");
                modernApp.setVisible(true);
                System.out.println("   - MAIN APP EDT: Main application displayed successfully");
            } catch (Exception e) {
                System.err.println("   - MAIN APP EDT ERROR: Failed to create main application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Initializes test data for demonstration purposes
     */
    private static void initializeTestData() {
        try {
            logger.info("Initializing test data for demonstration...");
            
            // Generate test accounts
            util.TestDataGenerator.generateTestAccounts();
            
            // Generate sample emails
            util.TestDataGenerator.generateSampleEmails();
            
            // Demonstrate security features
            util.TestDataGenerator.demonstrateSecurityFeatures();
            
            logger.info("Test data initialization completed");
            
        } catch (Exception e) {
            logger.warning("Failed to initialize test data: " + e.getMessage());
        }
    }
}
