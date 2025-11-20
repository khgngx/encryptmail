package util;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.mail.*;

import config.AppConfig;

/**
 * Tool để test kết nối với hMailServer
 */
public class MailServerTester {
    private static final Logger logger = Logger.getLogger(MailServerTester.class.getName());
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("HMAIL SERVER CONNECTION TESTER");
        System.out.println("========================================");
        
        MailServerTester tester = new MailServerTester();
        
        try {
            AppConfig config = AppConfig.getInstance();
            String smtpHost = config.getSmtpHost();
            int smtpPort = config.getSmtpPort();
            int imapPort = config.getImapPort();
            
            System.out.println("Configuration:");
            System.out.println("- SMTP Host: " + smtpHost);
            System.out.println("- SMTP Port: " + smtpPort);
            System.out.println("- IMAP Port: " + imapPort);
            System.out.println();
            
            // Test 1: Basic network connectivity
            System.out.println("Test 1: Network connectivity...");
            tester.testNetworkConnection(smtpHost, smtpPort);
            tester.testNetworkConnection(smtpHost, imapPort);
            
            // Test 2: SMTP greeting
            System.out.println("\nTest 2: SMTP greeting...");
            tester.testSmtpGreeting(smtpHost, smtpPort);
            
            // Test 3: JavaMail SMTP connection
            System.out.println("\nTest 3: JavaMail SMTP connection...");
            tester.testJavaMailSmtp(smtpHost, smtpPort);
            
            // Test 4: IMAP connection
            System.out.println("\nTest 4: IMAP connection...");
            tester.testImapConnection(smtpHost, imapPort);
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testNetworkConnection(String host, int port) {
        logger.info("Testing network connection to " + host + ":" + port);
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            System.out.println("✓ " + host + ":" + port + " - Network connection OK");
        } catch (Exception e) {
            System.out.println("✗ " + host + ":" + port + " - Network connection FAILED: " + e.getMessage());
        }
    }
    
    private void testSmtpGreeting(String host, int port) {
        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            
            // Read greeting
            String greeting = reader.readLine();
            System.out.println("✓ SMTP Greeting: " + greeting);
            
            // Send EHLO
            writer.println("EHLO test");
            String ehloResponse = reader.readLine();
            System.out.println("✓ EHLO Response: " + ehloResponse);
            
            // Send QUIT
            writer.println("QUIT");
            
        } catch (Exception e) {
            System.out.println("✗ SMTP Greeting FAILED: " + e.getMessage());
        }
    }
    
    private void testJavaMailSmtp(String host, int port) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(port));
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            
            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication("test@gmail.com", "123456");
                }
            });
            
            Transport transport = session.getTransport("smtp");
            transport.connect(host, "test@gmail.com", "123456");
            transport.close();
            
            System.out.println("✓ JavaMail SMTP connection OK");
            
        } catch (Exception e) {
            System.out.println("✗ JavaMail SMTP connection FAILED: " + e.getMessage());
        }
    }
    
    private void testImapConnection(String host, int port) {
        try {
            Properties props = new Properties();
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", String.valueOf(port));
            props.put("mail.imap.connectiontimeout", "5000");
            props.put("mail.imap.timeout", "5000");
            
            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");
            store.connect(host, "test@gmail.com", "123456");
            store.close();
            
            System.out.println("✓ IMAP connection OK");
            
        } catch (Exception e) {
            System.out.println("✗ IMAP connection FAILED: " + e.getMessage());
        }
    }
}
