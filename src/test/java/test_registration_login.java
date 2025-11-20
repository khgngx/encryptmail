// Quick test script để verify registration và login
// Chạy: java -cp "target\classes;lib\*" test_registration_login

import config.AppConfig;
import core.ServiceRegistry;
import core.service.AuthService;
import core.model.Account;

public class test_registration_login {
    public static void main(String[] args) {
        System.out.println("=== TESTING REGISTRATION & LOGIN ===");
        
        try {
            // Initialize
            AppConfig config = AppConfig.getInstance();
            ServiceRegistry registry = ServiceRegistry.getInstance();
            AuthService authService = registry.getAuthService();
            
            String testEmail = "khang@gmail.com";
            String testPassword = "123456";
            
            System.out.println("1. Testing registration...");
            try {
                Account account = authService.register(
                    testEmail, testPassword,
                    config.getSmtpHost(), config.getSmtpPort(),
                    config.getImapHost(), config.getImapPort()
                );
                System.out.println("✓ Registration successful: " + account.getEmail());
                System.out.println("  - ID: " + account.getId());
                System.out.println("  - Plain password: " + account.getPlainPassword());
            } catch (Exception e) {
                System.out.println("✗ Registration failed: " + e.getMessage());
            }
            
            System.out.println("\n2. Testing login...");
            try {
                var loginResult = authService.login(testEmail, testPassword);
                if (loginResult.isPresent()) {
                    Account account = loginResult.get();
                    System.out.println("✓ Login successful: " + account.getEmail());
                    System.out.println("  - ID: " + account.getId());
                    System.out.println("  - Active: " + account.isActive());
                } else {
                    System.out.println("✗ Login failed: No account returned");
                }
            } catch (Exception e) {
                System.out.println("✗ Login failed: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== TEST COMPLETED ===");
    }
}
