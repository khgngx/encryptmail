package ui.cli;

import java.util.Scanner;
import java.util.logging.Logger;

import config.AppConfig;
import core.ServiceRegistry;
import core.service.KeyService;
import core.service.MailService;
import core.service.SecureMailService;
import jakarta.mail.Message;

/**
 * Command Line Interface for the mail client
 */
public class CliMain {
    private static final Logger logger = Logger.getLogger(CliMain.class.getName());
    private static final Scanner scanner = new Scanner(System.in);
    
    private final ServiceRegistry serviceRegistry;
    private final MailService mailService;
    private final AppConfig config;
    private SecureMailService secureMailService;
    private KeyService keyService;
    private String currentUser;
    private String currentPassword;
    
    public CliMain() {
        this.serviceRegistry = ServiceRegistry.getInstance();
        this.mailService = serviceRegistry.getMailService();
        this.config = serviceRegistry.getConfig();
        
        // Initialize secure services if not in demo mode
        if (!config.isDemoMode()) {
            try {
                this.secureMailService = serviceRegistry.getSecureMailService();
                this.keyService = serviceRegistry.getKeyService();
            } catch (IllegalStateException e) {
                logger.warning("Secure mail services not available: " + e.getMessage());
            }
        }
    }
    
    public static void main(String[] args) {
        // Set CLI_LOCAL mode for CLI app
        System.setProperty("app.mode", "CLI_LOCAL");
        
        CliMain cli = new CliMain();
        cli.run();
    }
    
    public void run() {
        System.out.println("=== Secure Mail Client CLI ===");
        System.out.println("Mode: " + config.getAppMode());
        System.out.println("SMTP: " + config.getSmtpHost() + ":" + config.getSmtpPort());
        System.out.println("IMAP: " + config.getImapHost() + ":" + config.getImapPort());
        System.out.println();
        
        if (!login()) {
            System.out.println("Login failed. Exiting...");
            return;
        }
        
        showMainMenu();
    }
    
    private boolean login() {
        System.out.println("=== Login ===");
        
        if (config.isCliLocalMode()) {
            // In CLI_LOCAL mode, use system user
            currentUser = config.getCurrentSystemUser();
            String emailAddress = config.generateEmailAddress(currentUser);
            System.out.println("System user detected: " + currentUser);
            System.out.println("Email address: " + emailAddress);
            System.out.print("Enter password (or press Enter for no password): ");
            currentPassword = scanner.nextLine();
            if (currentPassword.trim().isEmpty()) {
                currentPassword = ""; // No password for local mode
            }
            currentUser = emailAddress; // Use full email address
        } else {
            System.out.print("Email: ");
            currentUser = scanner.nextLine();
            System.out.print("Password: ");
            currentPassword = scanner.nextLine();
        }
        
        // Test connection
        System.out.println("Testing connection...");
        boolean connected = mailService.testConnection(currentUser, currentPassword);
        
        if (connected) {
            System.out.println("Login successful!");
            return true;
        } else {
            System.out.println("Login failed. Please check your credentials and server configuration.");
            return false;
        }
    }
    
    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Inbox");
            System.out.println("2. Sent");
            System.out.println("3. Compose");
            System.out.println("4. Keys");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    showInbox();
                    break;
                case "2":
                    showSent();
                    break;
                case "3":
                    composeMail();
                    break;
                case "4":
                    manageKeys();
                    break;
                case "5":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void showInbox() {
        System.out.println("\n=== Inbox ===");
        try {
            var messages = mailService.fetchInbox(currentUser, currentPassword);
            
            if (messages.isEmpty()) {
                System.out.println("No messages in inbox.");
                return;
            }
            
            System.out.println("Found " + messages.size() + " message(s):");
            for (int i = 0; i < messages.size(); i++) {
                Message msg = messages.get(i);
                System.out.printf("%d. From: %s | Subject: %s | Date: %s%n", 
                    i + 1, 
                    msg.getFrom()[0].toString(),
                    msg.getSubject(),
                    msg.getSentDate());
            }
            
            System.out.print("Enter message number to read (or 0 to go back): ");
            String choice = scanner.nextLine();
            
            try {
                int msgNum = Integer.parseInt(choice);
                if (msgNum > 0 && msgNum <= messages.size()) {
                    showMessage(messages.get(msgNum - 1));
                }
            } catch (NumberFormatException e) {
                // Invalid input, go back to menu
            }
            
        } catch (Exception e) {
            System.out.println("Error fetching inbox: " + e.getMessage());
            logger.severe("Error fetching inbox: " + e.getMessage());
        }
    }
    
    private void showMessage(Message message) {
        try {
            System.out.println("\n=== Message Details ===");
            System.out.println("From: " + message.getFrom()[0].toString());
            System.out.println("Subject: " + message.getSubject());
            System.out.println("Date: " + message.getSentDate());
            System.out.println("Content:");
            System.out.println("---");
            System.out.println(message.getContent().toString());
            System.out.println("---");
            
            // Process secure message if available
            if (secureMailService != null) {
                try {
                    SecureMailService.ProcessedMessage processed = 
                        secureMailService.processReceivedMessage(message.getContent().toString(), currentUser);
                    
                    System.out.println("\n--- Security Status ---");
                    System.out.println("Encrypted: " + (processed.isEncrypted() ? "YES" : "NO"));
                    System.out.println("Signed: " + (processed.isSigned() ? "YES" : "NO"));
                    if (processed.isSigned()) {
                        System.out.println("Signature Valid: " + (processed.isSignatureVerified() ? "YES" : "NO"));
                        System.out.println("Sender: " + processed.getSenderEmail());
                    }
                    System.out.println("--- End Security Status ---");
                } catch (Exception e) {
                    System.out.println("Error processing secure message: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error reading message: " + e.getMessage());
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void showSent() {
        System.out.println("\n=== Sent Messages ===");
        System.out.println("(Sent messages functionality will be implemented with database integration)");
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    private void composeMail() {
        System.out.println("\n=== Compose Mail ===");
        
        System.out.print("To: ");
        String to = scanner.nextLine();
        
        if (config.isCliLocalMode() && !to.contains("@")) {
            // Auto-complete with hostname for local users
            to = to + "@" + config.getHostname();
            System.out.println("Auto-completed to: " + to);
        }
        
        System.out.print("Subject: ");
        String subject = scanner.nextLine();
        
        System.out.println("Body (type 'END' on a new line to finish):");
        StringBuilder body = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            body.append(line).append("\n");
        }
        
        System.out.print("Encrypt message? (y/n): ");
        boolean encrypt = scanner.nextLine().toLowerCase().startsWith("y");
        
        System.out.print("Sign message? (y/n): ");
        boolean sign = scanner.nextLine().toLowerCase().startsWith("y");
        
        try {
            String finalBody = body.toString();
            
            // Send secure mail if encryption/signing requested and service available
            if ((encrypt || sign) && secureMailService != null) {
                try {
                    secureMailService.sendSecureMail(currentUser, currentPassword, to, subject, finalBody, encrypt, sign);
                    System.out.println("Secure mail sent successfully!");
                    if (encrypt) System.out.println("✓ Message was encrypted");
                    if (sign) System.out.println("✓ Message was digitally signed");
                } catch (Exception e) {
                    System.out.println("Error sending secure mail: " + e.getMessage());
                    System.out.println("Falling back to plain text...");
                    mailService.sendMail(currentUser, currentPassword, to, subject, finalBody);
                    System.out.println("Plain mail sent successfully!");
                }
            } else {
                if ((encrypt || sign) && secureMailService == null) {
                    System.out.println("Secure mail features not available in demo mode.");
                    System.out.println("Sending as plain text...");
                }
                mailService.sendMail(currentUser, currentPassword, to, subject, finalBody);
                System.out.println("Mail sent successfully!");
            }
            
        } catch (Exception e) {
            System.out.println("Error sending mail: " + e.getMessage());
            logger.severe("Error sending mail: " + e.getMessage());
        }
    }
    
    private void manageKeys() {
        System.out.println("\n=== Key Management ===");
        
        if (keyService == null) {
            System.out.println("Key management not available in demo mode.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        while (true) {
            System.out.println("\n1. Check key pair status");
            System.out.println("2. Generate new key pair");
            System.out.println("3. Export public key");
            System.out.println("4. Back to main menu");
            System.out.print("Choose option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    checkKeyPairStatus();
                    break;
                case "2":
                    generateKeyPair();
                    break;
                case "3":
                    exportPublicKey();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void checkKeyPairStatus() {
        boolean hasKeys = keyService.hasKeyPair(currentUser);
        System.out.println("\nKey pair status for " + currentUser + ": " + 
                          (hasKeys ? "EXISTS" : "NOT FOUND"));
        
        if (hasKeys) {
            try {
                String publicKey = keyService.exportPublicKey(currentUser);
                System.out.println("Public key preview: " + publicKey.substring(0, Math.min(50, publicKey.length())) + "...");
            } catch (Exception e) {
                System.out.println("Error reading public key: " + e.getMessage());
            }
        }
    }
    
    private void generateKeyPair() {
        System.out.print("Key size (2048 or 4096, default 2048): ");
        String sizeInput = scanner.nextLine();
        int keySize = 2048;
        
        try {
            if (!sizeInput.trim().isEmpty()) {
                keySize = Integer.parseInt(sizeInput);
                if (keySize != 2048 && keySize != 4096) {
                    System.out.println("Invalid key size, using 2048");
                    keySize = 2048;
                }
            }
            
            System.out.println("Generating " + keySize + "-bit RSA key pair...");
            keyService.generateKeyPair(currentUser, keySize);
            System.out.println("Key pair generated successfully!");
            
        } catch (Exception e) {
            System.out.println("Error generating key pair: " + e.getMessage());
        }
    }
    
    private void exportPublicKey() {
        try {
            String publicKey = keyService.exportPublicKey(currentUser);
            System.out.println("\nYour public key:");
            System.out.println("--- BEGIN PUBLIC KEY ---");
            System.out.println(publicKey);
            System.out.println("--- END PUBLIC KEY ---");
            System.out.println("\nShare this public key with others to receive encrypted messages.");
            
        } catch (Exception e) {
            System.out.println("Error exporting public key: " + e.getMessage());
        }
    }
}
