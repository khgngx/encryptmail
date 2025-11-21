package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Utility class for exporting mail information to a text file
 */
public class MailExportUtil {
    private static final Logger logger = Logger.getLogger(MailExportUtil.class.getName());
    private static final String EXPORT_DIR = "exports";
    private static final DateTimeFormatter FILE_TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter DISPLAY_TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MailExportUtil() {
    }

    public static Path exportMail(String from,
                                  String to,
                                  String subject,
                                  String body,
                                  String senderPublicKey,
                                  String recipientPublicKey,
                                  boolean encrypted,
                                  boolean signed) {
        LocalDateTime now = LocalDateTime.now();

        try {
            Path dir = Paths.get(EXPORT_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String safeFrom = sanitizeForFile(from);
            String safeTo = sanitizeForFile(to);
            String fileName = "mail_" + FILE_TS_FORMAT.format(now) + "_" + safeFrom + "_to_" + safeTo + ".txt";
            Path file = dir.resolve(fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                writer.write("Sender: " + from);
                writer.newLine();
                writer.write("Recipient: " + to);
                writer.newLine();
                writer.write("SentAt: " + DISPLAY_TS_FORMAT.format(now));
                writer.newLine();
                writer.write("Encrypted: " + (encrypted ? "YES" : "NO"));
                writer.newLine();
                writer.write("Signed: " + (signed ? "YES" : "NO"));
                writer.newLine();
                writer.write("SenderPublicKey: " + (senderPublicKey != null ? senderPublicKey : "N/A"));
                writer.newLine();
                writer.write("RecipientPublicKey: " + (recipientPublicKey != null ? recipientPublicKey : "N/A"));
                writer.newLine();
                writer.write("Subject: " + (subject != null ? subject : ""));
                writer.newLine();
                writer.write("Body:");
                writer.newLine();
                writer.write(body != null ? body : "");
                writer.newLine();
            }

            logger.info("Mail export written to: " + file.toAbsolutePath());
            return file;
        } catch (IOException e) {
            logger.severe("Failed to export mail: " + e.getMessage());
            return null;
        }
    }

    private static String sanitizeForFile(String value) {
        if (value == null || value.isEmpty()) {
            return "unknown";
        }
        String result = value.replace("@", "_at_").replace(":", "_");
        result = result.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (result.length() > 50) {
            result = result.substring(0, 50);
        }
        return result;
    }
}
