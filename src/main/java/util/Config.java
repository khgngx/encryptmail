package util;

/**
 * Config: cấu hình mặc định cho Gmail.
 * Nếu muốn đổi sang server khác (VD: Outlook, Yahoo),
 * chỉ cần sửa các hằng số dưới đây.
 */
public class Config {
    // Gmail SMTP (gửi mail)
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final int SMTP_PORT = 587; // STARTTLS

    // Gmail IMAP (nhận mail)
    public static final String IMAP_HOST = "imap.gmail.com";
    public static final int IMAP_PORT = 993; // SSL

    // Nếu sau này muốn hỗ trợ nhiều server, bạn có thể thêm cấu hình ở đây
}
