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
            // For hMailServer, configure properly
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            
            if (smtpPort == 587) {
                // Port 587 - STARTTLS
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "false"); // hMailServer có thể không yêu cầu TLS
            } else if (smtpPort == 465) {
                // Port 465 - SSL
                props.put("mail.smtp.ssl.enable", "true");
            } else {
                // Port 25 - Plain hoặc STARTTLS optional
                props.put("mail.smtp.starttls.enable", "false");
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

        String imapHost = config.getImapHost();
        int imapPort = config.getImapPort();

        props.put("mail.imap.host", imapHost);
        props.put("mail.imap.port", String.valueOf(imapPort));

        // Security config
        if (config.isGuiRemoteMode()) {
            props.put("mail.imap.ssl.enable", "true");
        } else {
            props.put("mail.imap.ssl.enable", "false");
        }

        Session session = Session.getInstance(props);
        Store store = session.getStore("imap");

        // Kết nối
        store.connect(imapHost, email, password);

        List<Message> result = new ArrayList<>();

        // === ĐOẠN CODE MỚI: QUÉT TẤT CẢ CÁC THƯ MỤC ===
        System.out.println("--- BẮT ĐẦU QUÉT TÌM THƯ CHO: " + email + " ---");

        // Lấy danh sách tất cả thư mục trên server
        Folder defaultFolder = store.getDefaultFolder();
        Folder[] allFolders = defaultFolder.list("*");

        for (Folder folder : allFolders) {
            try {
                // Mở từng thư mục để xem (Mở chế độ chỉ đọc)
                if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
                    folder.open(Folder.READ_ONLY);
                    int messageCount = folder.getMessageCount();
                    System.out.println("   + Thư mục [" + folder.getFullName() + "] có: " + messageCount + " lá thư.");

                    // Nếu có thư, lấy hết ra để xem
                    if (messageCount > 0) {
                        Message[] msgs = folder.getMessages();
                        // Nếu đây là INBOX thì thêm vào kết quả trả về cho giao diện
                        if (folder.getFullName().equalsIgnoreCase("INBOX")) {
                            java.util.Collections.addAll(result, msgs);
                        } else {
                            // Nếu thư nằm ở Junk hay chỗ khác, in ra Console để biết đường tìm
                            for (Message m : msgs) {
                                System.out.println("     -> TÌM THẤY THƯ LẠC ở [" + folder.getFullName() + "]: " + m.getSubject());
                            }
                        }
                    }
                    // Không đóng folder ngay vì Message object cần folder mở để đọc nội dung (Lazy load)
                    // Nhưng trong phạm vi bài này ta đóng để tránh lỗi connection limit nếu mở quá nhiều
                    // folder.close(false);
                }
            } catch (Exception ex) {
                System.out.println("   ! Không đọc được thư mục " + folder.getFullName() + ": " + ex.getMessage());
            }
        }
        System.out.println("--- KẾT THÚC QUÉT ---");

        return result;
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
