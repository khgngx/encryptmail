package app;

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import mail.MockMailClient;
import ui.LoginPanel;
import ui.MainApplication;

public class MainApp {
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    private static JFrame loginFrame;
    private static MainApplication mainApplication;

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            logger.warning(() -> "Could not set system look and feel: " + e.getMessage());
        }

        // Start demo mail server
        try {
            MockMailClient.startDemoServer();
        } catch (Exception e) {
            logger.severe(() -> "Failed to start demo mail server: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "Failed to start mail server: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Initialize test data for demonstration (comment out for production)
        initializeTestData();
        
        // Show login window
        showLoginWindow();
    }

    private static void showLoginWindow() {
        loginFrame = new JFrame("Secure Mail Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        LoginPanel loginPanel = new LoginPanel(new LoginPanel.LoginCallback() {
            @Override
            public void onLoginSuccess() {
                loginFrame.dispose();
                showMainApplication();
            }

            @Override
            public void onLoginError(String message) {
                // Error already shown in LoginPanel
            }
        });

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);
    }

    private static void showMainApplication() {
        SwingUtilities.invokeLater(() -> {
            mainApplication = new MainApplication();
            mainApplication.showApplication();
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
