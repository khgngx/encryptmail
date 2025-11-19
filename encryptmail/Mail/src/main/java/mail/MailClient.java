package mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * MailClient demo local (GreenMail)
 */
public class MailClient {
    private static final Logger logger = Logger.getLogger(MailClient.class.getName());
    private static GreenMail greenMail;

    public static void startDemoServer() {
        if (greenMail != null) return;

        ServerSetup smtp = new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP);
        ServerSetup imap = new ServerSetup(3143, null, ServerSetup.PROTOCOL_IMAP);
        greenMail = new GreenMail(new ServerSetup[]{smtp, imap});
        greenMail.start();

        greenMail.setUser("user1@localhost", "user1", "password1");
        greenMail.setUser("user2@localhost", "user2", "password2");

        logger.info("Demo Mail Server started (user1/password1, user2/password2)");
    }

    public static void sendMail(String from, String to, String subject, String body) throws Exception {
        startDemoServer();

        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "3025");
        props.put("mail.smtp.auth", "false");

        Session session = Session.getInstance(props);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject);
        msg.setText(body);

        Transport.send(msg);
        logger.info(() -> "Mail sent: " + from + " -> " + to + " [" + subject + "]");
    }

    public static List<Message> fetchInbox(String user, String password) throws Exception {
        startDemoServer();

        Properties props = new Properties();
        props.put("mail.imap.host", "localhost");
        props.put("mail.imap.port", "3143");
        props.put("mail.imap.ssl.enable", "false");

        Session session = Session.getInstance(props);
        
        // Use try-with-resources for proper resource management
        try (Store store = session.getStore("imap")) {
            store.connect("localhost", user, password);
            
            try (Folder inbox = store.getFolder("INBOX")) {
                inbox.open(Folder.READ_ONLY);
                Message[] messages = inbox.getMessages();

                // Use Collections.addAll instead of manual array copy
                List<Message> result = new ArrayList<>();
                java.util.Collections.addAll(result, messages);
                
                return result;
            }
        }
    }

    /**
     * Fetches and processes new emails, adding them to the mail history
     */
    public static void fetchAndProcessNewEmails(String userEmail, String password) {
        try {
            List<Message> messages = fetchInbox(userEmail, password);
            
            for (Message message : messages) {
                String messageId = "inbox_" + System.currentTimeMillis() + "_" + message.getMessageNumber();
                String from = message.getFrom()[0].toString();
                String subject = message.getSubject();
                String body = message.getContent().toString();
                
                // Process the message for encryption and signatures
                SecureMailClient.ProcessedMessage processed = SecureMailClient.processReceivedMessage(body);
                
                // Create mail record
                util.MailHistoryManager.MailRecord mailRecord = new util.MailHistoryManager.MailRecord(
                    messageId,
                    from,
                    userEmail,
                    subject,
                    processed.getDisplayContent(),
                    message.getSentDate() != null ? message.getSentDate() : new java.util.Date(),
                    "inbox",
                    processed.isEncrypted,
                    processed.isSigned && processed.signatureVerified,
                    false, // unread by default
                    false  // not important by default
                );
                
                // Add to inbox
                util.MailHistoryManager.addMailRecord("inbox", mailRecord);
                
                logger.info(() -> "Processed new email: " + subject + " from " + from);
            }
            
        } catch (Exception e) {
            logger.severe(() -> "Failed to fetch and process emails: " + e.getMessage());
        }
    }
}
