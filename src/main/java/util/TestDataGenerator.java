package util;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Utility class to generate test data for demonstrating mail functionality
 */
public class TestDataGenerator {
    private static final Logger logger = Logger.getLogger(TestDataGenerator.class.getName());
    
    /**
     * Generates sample emails for testing purposes
     */
    public static void generateSampleEmails() {
        logger.info("Generating sample test emails...");
        
        try {
            // Generate some sample inbox emails
            generateInboxEmails();
            
            // Generate some sample sent emails
            generateSentEmails();
            
            // Generate some sample drafts
            generateDraftEmails();
            
            logger.info("Sample test emails generated successfully");
            
        } catch (Exception e) {
            logger.severe("Failed to generate sample emails: " + e.getMessage());
        }
    }
    
    private static void generateInboxEmails() {
        // Sample encrypted email
        MailHistoryManager.MailRecord encryptedEmail = new MailHistoryManager.MailRecord(
            "test_encrypted_001",
            "alice@gmail.com",
            "user@gmail.com",
            "🔒 Confidential Project Update",
            "ENCRYPTED_MESSAGE_START\nVGhpcyBpcyBhIHNlY3JldCBtZXNzYWdlIGFib3V0IHRoZSBwcm9qZWN0IHVwZGF0ZS4=\nENCRYPTED_MESSAGE_END",
            new Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            "inbox",
            true,  // encrypted
            false, // not signed
            false, // unread
            true   // important
        );
        MailHistoryManager.addMailRecord("inbox", encryptedEmail);
        
        // Sample signed email
        MailHistoryManager.MailRecord signedEmail = new MailHistoryManager.MailRecord(
            "test_signed_002",
            "bob@gmail.com",
            "user@gmail.com",
            "✓ Contract Verification",
            "Please review the attached contract for the new project.\n\nBest regards,\nBob\n\n--- DIGITAL SIGNATURE ---\nSigned by: bob@gmail.com\nSignature: SIG_123456789\nTimestamp: " + new Date().toString(),
            new Date(System.currentTimeMillis() - 7200000), // 2 hours ago
            "inbox",
            false, // not encrypted
            true,  // signed
            false, // unread
            false  // not important
        );
        MailHistoryManager.addMailRecord("inbox", signedEmail);
        
        // Sample encrypted and signed email
        MailHistoryManager.MailRecord secureEmail = new MailHistoryManager.MailRecord(
            "test_secure_003",
            "charlie@gmail.com",
            "user@gmail.com",
            "🔒✓ Top Secret Information",
            "ENCRYPTED_MESSAGE_START\nSW1wb3J0YW50IGNvbmZpZGVudGlhbCBpbmZvcm1hdGlvbiBhYm91dCB0aGUgbmV3IGluaXRpYXRpdmUu\nENCRYPTED_MESSAGE_END\n\n--- DIGITAL SIGNATURE ---\nSigned by: charlie@gmail.com\nSignature: SIG_987654321\nTimestamp: " + new Date().toString(),
            new Date(System.currentTimeMillis() - 1800000), // 30 minutes ago
            "inbox",
            true,  // encrypted
            true,  // signed
            false, // unread
            true   // important
        );
        MailHistoryManager.addMailRecord("inbox", secureEmail);
        
        // Sample regular email
        MailHistoryManager.MailRecord regularEmail = new MailHistoryManager.MailRecord(
            "test_regular_004",
            "team@gmail.com",
            "user@gmail.com",
            "Weekly Team Meeting",
            "Hi everyone,\n\nOur weekly team meeting is scheduled for Friday at 2 PM.\n\nAgenda:\n- Project updates\n- Q4 planning\n- New initiatives\n\nSee you there!\nTeam Lead",
            new Date(System.currentTimeMillis() - 900000), // 15 minutes ago
            "inbox",
            false, // not encrypted
            false, // not signed
            false, // unread
            false  // not important
        );
        MailHistoryManager.addMailRecord("inbox", regularEmail);
    }
    
    private static void generateSentEmails() {
        MailHistoryManager.MailRecord sentEmail = new MailHistoryManager.MailRecord(
            "test_sent_001",
            "user@gmail.com",
            "client@gmail.com",
            "Project Proposal",
            "Dear Client,\n\nPlease find attached our project proposal for your review.\n\nBest regards,\nUser",
            new Date(System.currentTimeMillis() - 86400000), // 1 day ago
            "sent",
            false, // not encrypted
            false, // not signed
            true,  // read (sent emails are always read)
            false  // not important
        );
        MailHistoryManager.addMailRecord("sent", sentEmail);
        
        MailHistoryManager.MailRecord sentSecureEmail = new MailHistoryManager.MailRecord(
            "test_sent_002",
            "user@gmail.com",
            "partner@gmail.com",
            "🔒✓ Confidential Partnership Details",
            "ENCRYPTED_MESSAGE_START\nQ29uZmlkZW50aWFsIHBhcnRuZXJzaGlwIGRldGFpbHMgYW5kIHRlcm1zLg==\nENCRYPTED_MESSAGE_END\n\n--- DIGITAL SIGNATURE ---\nSigned by: user@gmail.com\nSignature: SIG_USER_001\nTimestamp: " + new Date().toString(),
            new Date(System.currentTimeMillis() - 43200000), // 12 hours ago
            "sent",
            true,  // encrypted
            true,  // signed
            true,  // read
            true   // important
        );
        MailHistoryManager.addMailRecord("sent", sentSecureEmail);
    }
    
    private static void generateDraftEmails() {
        MailHistoryManager.MailRecord draftEmail = new MailHistoryManager.MailRecord(
            "test_draft_001",
            "user@gmail.com",
            "manager@gmail.com",
            "Monthly Report - Draft",
            "Dear Manager,\n\nI'm working on the monthly report and will have it ready by...\n\n[DRAFT - NOT COMPLETED]",
            new Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            "drafts",
            false, // not encrypted
            false, // not signed
            true,  // read (drafts are always read)
            false  // not important
        );
        MailHistoryManager.addMailRecord("drafts", draftEmail);
    }
    
    /**
     * Clears all test data
     */
    public static void clearTestData() {
        MailHistoryManager.clearAllHistory();
        logger.info("All test data cleared");
    }
    
    /**
     * Generates test accounts for demonstration
     */
    public static void generateTestAccounts() {
        logger.info("Generating test accounts...");
        
        // Save some test accounts
        AccountManager.saveAccount("testuser@gmail.com", "testpass123", "localhost", "localhost", 3025, 3143);
        AccountManager.saveAccount("demo@gmail.com", "demopass456", "localhost", "localhost", 3025, 3143);
        AccountManager.saveAccount("alice@gmail.com", "alicepass789", "localhost", "localhost", 3025, 3143);
        
        logger.info("Test accounts generated");
    }
    
    /**
     * Demonstrates encryption and signature functionality
     */
    public static void demonstrateSecurityFeatures() {
        logger.info("Demonstrating security features...");
        
        try {
            String testEmail = "demo@gmail.com";
            
            // Generate key pair for demonstration
            if (!mail.SecureMailClient.hasKeyPair(testEmail)) {
                mail.SecureMailClient.generateKeyPair(testEmail);
                logger.info("Generated key pair for: " + testEmail);
            }
            
            // Test encryption and decryption
            String testMessage = "This is a test message for encryption demonstration.";
            logger.info("Original message: " + testMessage);
            
            // Process the message (this would normally happen during sending/receiving)
            mail.SecureMailClient.ProcessedMessage processed = mail.SecureMailClient.processReceivedMessage(testMessage);
            logger.info("Processed message: " + processed.getDisplayContent());
            logger.info("Is encrypted: " + processed.isEncrypted);
            logger.info("Is signed: " + processed.isSigned);
            logger.info("Signature verified: " + processed.signatureVerified);
            
        } catch (Exception e) {
            logger.severe("Failed to demonstrate security features: " + e.getMessage());
        }
    }
}
