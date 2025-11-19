package mail;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Mock mail client for demonstration purposes without external dependencies
 */
public class MockMailClient {
    private static final Logger logger = Logger.getLogger(MockMailClient.class.getName());
    
    private static boolean serverStarted = false;

    public static void startDemoServer() {
        if (serverStarted) return;
        
        logger.info("Mock Mail Server started (user1/password1, user2/password2)");
        serverStarted = true;
    }

    public static void sendMail(String from, String to, String subject, String body) throws Exception {
        startDemoServer();
        
        logger.info(() -> "Mock mail sent: " + from + " -> " + to + " [" + subject + "]");
        
        // Simulate some processing time
        Thread.sleep(100);
    }

    public static List<MockMessage> fetchInbox(String user, String password) throws Exception {
        startDemoServer();
        
        List<MockMessage> messages = new ArrayList<>();
        
        // Add some sample messages
        messages.add(new MockMessage("sender1@example.com", "Important Meeting", "Let's discuss the project timeline...", "2024-01-15"));
        messages.add(new MockMessage("sender2@example.com", "Project Update", "Here's the latest status on our project...", "2024-01-14"));
        messages.add(new MockMessage("sender3@example.com", "Security Alert", "Please review the attached security report...", "2024-01-13"));
        messages.add(new MockMessage("sender4@example.com", "Weekly Report", "This week's progress summary...", "2024-01-12"));
        messages.add(new MockMessage("sender5@example.com", "Team Meeting", "Don't forget about tomorrow's team meeting...", "2024-01-11"));
        
        return messages;
    }
    
    public static class MockMessage {
        private final String from;
        private final String subject;
        private final String body;
        private final String date;
        
        public MockMessage(String from, String subject, String body, String date) {
            this.from = from;
            this.subject = subject;
            this.body = body;
            this.date = date;
        }
        
        public String getFrom() { return from; }
        public String getSubject() { return subject; }
        public String getBody() { return body; }
        public String getDate() { return date; }
    }
}

