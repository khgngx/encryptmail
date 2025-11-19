package infra.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import config.AppConfig;
import core.service.MailService;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * SMTP/IMAP implementation of MailService using real mail servers
 */
public class SmtpImapMailService implements MailService {
    private static final Logger logger = Logger.getLogger(SmtpImapMailService.class.getName());
    private final AppConfig config;
    
    public SmtpImapMailService() {
        this.config = AppConfig.getInstance();
    }
    
    @Override
    public void sendMail(String from, String password, String to, String subject, String body) throws Exception {
        Properties props = new Properties();
        
        // Configure SMTP properties based on app mode
        String smtpHost = config.getSmtpHost();
        int smtpPort = config.getSmtpPort();
        
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        
        // Configure authentication and security based on mode
        if (config.isGuiRemoteMode()) {
            // For remote server, use TLS/SSL
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            if (smtpPort == 465) {
                props.put("mail.smtp.ssl.enable", "true");
            }
        } else if (config.isCliLocalMode()) {
            // For local server, minimal security
            props.put("mail.smtp.auth", "false");
        } else {
            // Demo mode - no auth
            props.put("mail.smtp.auth", "false");
        }
        
        Session session;
        if (props.getProperty("mail.smtp.auth", "false").equals("true")) {
            session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(from, password);
                }
            });
        } else {
            session = Session.getInstance(props);
        }
        
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        
        Transport.send(message);
        logger.info(() -> "Mail sent: " + from + " -> " + to + " [" + subject + "]");
    }
    
    @Override
    public List<Message> fetchInbox(String email, String password) throws Exception {
        Properties props = new Properties();
        
        // Configure IMAP properties based on app mode
        String imapHost = config.getImapHost();
        int imapPort = config.getImapPort();
        
        props.put("mail.imap.host", imapHost);
        props.put("mail.imap.port", String.valueOf(imapPort));
        
        // Configure security based on mode
        if (config.isGuiRemoteMode()) {
            props.put("mail.imap.ssl.enable", "true");
        } else {
            props.put("mail.imap.ssl.enable", "false");
        }
        
        Session session = Session.getInstance(props);
        
        try (Store store = session.getStore("imap")) {
            if (config.isGuiRemoteMode() || config.isCliLocalMode()) {
                // Use authentication for real servers
                store.connect(imapHost, email, password);
            } else {
                // Demo mode - connect without auth
                store.connect(imapHost, email, password);
            }
            
            try (Folder inbox = store.getFolder("INBOX")) {
                inbox.open(Folder.READ_ONLY);
                Message[] messages = inbox.getMessages();
                
                List<Message> result = new ArrayList<>();
                java.util.Collections.addAll(result, messages);
                
                logger.info(() -> "Fetched " + result.size() + " messages from " + email);
                return result;
            }
        }
    }
    
    @Override
    public boolean testConnection(String email, String password) {
        try {
            // Test SMTP connection
            Properties smtpProps = new Properties();
            smtpProps.put("mail.smtp.host", config.getSmtpHost());
            smtpProps.put("mail.smtp.port", String.valueOf(config.getSmtpPort()));
            smtpProps.put("mail.smtp.auth", config.isGuiRemoteMode() ? "true" : "false");
            
            Session smtpSession = Session.getInstance(smtpProps);
            Transport transport = smtpSession.getTransport("smtp");
            
            if (config.isGuiRemoteMode()) {
                transport.connect(config.getSmtpHost(), email, password);
            } else {
                transport.connect(config.getSmtpHost(), null, null);
            }
            transport.close();
            
            // Test IMAP connection
            Properties imapProps = new Properties();
            imapProps.put("mail.imap.host", config.getImapHost());
            imapProps.put("mail.imap.port", String.valueOf(config.getImapPort()));
            
            Session imapSession = Session.getInstance(imapProps);
            Store store = imapSession.getStore("imap");
            
            if (config.isGuiRemoteMode() || config.isCliLocalMode()) {
                store.connect(config.getImapHost(), email, password);
            } else {
                store.connect(config.getImapHost(), email, password);
            }
            store.close();
            
            logger.info("Mail server connection test successful for: " + email);
            return true;
            
        } catch (Exception e) {
            logger.warning(() -> "Mail server connection test failed for " + email + ": " + e.getMessage());
            return false;
        }
    }
}
