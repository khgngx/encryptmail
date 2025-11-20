package ui.modern;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import core.ServiceRegistry;
import core.model.Email;
import core.service.MailHistoryService;
import core.service.MailService;
import ui.modern.components.ModernEmailList;
import ui.modern.components.ModernEmailViewer;
import ui.modern.components.ModernSidebar;
import ui.theme.ThemeManager;

import jakarta.mail.Message;
import java.time.ZoneId;

/**
 * Modern main application with SaaS-like design
 */
public class ModernMainApplication extends JFrame {
    private static final Logger logger = Logger.getLogger(ModernMainApplication.class.getName());
    
    private ServiceRegistry serviceRegistry;
    private MailService mailService;
    private MailHistoryService mailHistoryService;
    private ThemeManager themeManager;
    private String currentUser;
    private String currentPassword;
    private Long currentAccountId;
    
    // Modern UI Components
    private ModernSidebar sidebar;
    private ModernEmailList emailList;
    private ModernEmailViewer emailViewer;
    private JLabel statusLabel;
    private JToggleButton themeToggleButton;
    
    // Current state
    private String currentFolder = "inbox";
    
    public ModernMainApplication(String userEmail, String password) {
        this.currentUser = userEmail;
        this.currentPassword = password;
        this.serviceRegistry = ServiceRegistry.getInstance();
        this.mailService = serviceRegistry.getMailService();
        this.themeManager = ThemeManager.getInstance();
        
        // Initialize mail history service if available
        try {
            if (!serviceRegistry.getConfig().isDemoMode()) {
                this.mailHistoryService = serviceRegistry.getMailHistoryService();
                // Find account ID
                var account = serviceRegistry.getAccountRepository().findByEmail(userEmail);
                if (account.isPresent()) {
                    this.currentAccountId = account.get().getId();
                }
            }
        } catch (Exception e) {
            logger.info("Mail history service not available: " + e.getMessage());
        }
        
        initializeComponents();
        layoutComponents();
        setupEventHandlers();
        applyTheme();
        loadFolders();
        loadEmails(currentFolder);
        
        setTitle("Secure Mail Client - " + userEmail);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
        
        syncInbox();
    }
    
    private void initializeComponents() {
        // Initialize modern components
        sidebar = new ModernSidebar(new ModernSidebar.SidebarListener() {
            @Override
            public void onFolderSelected(String folder) {
                currentFolder = folder;
                sidebar.setActiveFolder(folder);
                if ("inbox".equals(folder)) {
                    // For Inbox, always re-sync from server to ensure latest mails are shown
                    syncInbox();
                } else {
                    loadEmails(folder);
                }
            }
            
            @Override
            public void onComposeClicked() {
                openComposeWindow();
            }
        });
        
        emailList = new ModernEmailList(new ModernEmailList.EmailListListener() {
            @Override
            public void onEmailSelected(Email email) {
                emailViewer.displayEmail(email);
                markEmailAsRead(email);
            }
        });
        
        emailViewer = new ModernEmailViewer();
        
        // Theme toggle button
        themeToggleButton = new JToggleButton(themeManager.isDarkMode() ? "☀" : "🌙");
        themeToggleButton.setSelected(themeManager.isDarkMode());
        themeToggleButton.setBorderPainted(false);
        themeToggleButton.setContentAreaFilled(false);
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setPreferredSize(new Dimension(40, 32));
        
        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(ThemeManager.Fonts.SMALL);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Header panel with modern styling
        JPanel headerPanel = createHeaderPanel();
        
        // Main content area with 3-panel layout
        JPanel mainContentPanel = createMainContentPanel();
        
        // Status bar
        JPanel statusPanel = createStatusPanel();
        
        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Modern header background
                g2.setColor(ThemeManager.Colors.getContentBgColor());
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle bottom border
                g2.setColor(ThemeManager.Colors.getBorderColor());
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        headerPanel.setOpaque(false);
        
        // Title
        JLabel titleLabel = new JLabel("Secure Mail Client");
        titleLabel.setFont(ThemeManager.Fonts.HEADING);
        titleLabel.setForeground(ThemeManager.Colors.getFgColor());
        
        // User info
        JLabel userLabel = new JLabel(currentUser);
        userLabel.setFont(ThemeManager.Fonts.SMALL);
        userLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        // Left panel with title and user
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(userLabel, BorderLayout.SOUTH);
        
        // Right panel with theme toggle and actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        // Reset Inbox button - re-sync emails from server and reload inbox
        javax.swing.JButton resetInboxButton = new javax.swing.JButton("Reset Inbox");
        resetInboxButton.setFont(ThemeManager.Fonts.BUTTON);
        resetInboxButton.setFocusPainted(false);
        resetInboxButton.addActionListener(e -> syncInbox());

        rightPanel.add(resetInboxButton);
        rightPanel.add(themeToggleButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.Colors.getBgColor());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        
        // Create split panes for 3-panel layout
        JSplitPane emailContentSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        emailContentSplit.setLeftComponent(emailList);
        emailContentSplit.setRightComponent(emailViewer);
        emailContentSplit.setDividerLocation(450);
        emailContentSplit.setBorder(null);
        emailContentSplit.setOpaque(false);
        emailContentSplit.setDividerSize(2);
        
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(sidebar);
        mainSplit.setRightComponent(emailContentSplit);
        mainSplit.setDividerLocation(280);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);
        mainSplit.setDividerSize(2);
        
        mainPanel.add(mainSplit, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Status bar background
                g2.setColor(ThemeManager.Colors.getContentBgColor());
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle top border
                g2.setColor(ThemeManager.Colors.getBorderColor());
                g2.fillRect(0, 0, getWidth(), 1);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        return statusPanel;
    }
    
    private void setupEventHandlers() {
        // Theme toggle
        themeToggleButton.addActionListener(e -> {
            themeManager.toggleTheme();
            themeToggleButton.setText(themeManager.isDarkMode() ? "☀" : "🌙");
            themeToggleButton.setSelected(themeManager.isDarkMode());
            applyTheme();
        });
    }
    
    private void loadFolders() {
        if (mailHistoryService != null && currentAccountId != null) {
            try {
                Map<String, Integer> folderStats = mailHistoryService.getFolderStats(currentAccountId);
                
                // Update sidebar with folder counts
                sidebar.updateFolderCount("inbox", folderStats.getOrDefault("inbox", 0));
                sidebar.updateFolderCount("sent", folderStats.getOrDefault("sent", 0));
                sidebar.updateFolderCount("drafts", folderStats.getOrDefault("drafts", 0));
                sidebar.updateFolderCount("trash", folderStats.getOrDefault("trash", 0));
                
            } catch (Exception e) {
                logger.warning("Failed to load folder stats: " + e.getMessage());
            }
        }
        
        // Set initial active folder
        sidebar.setActiveFolder(currentFolder);
    }
    
    private void loadEmails(String folder) {
        setStatus("Loading emails...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                List<Email> emails;
                
                if (mailHistoryService != null && currentAccountId != null) {
                    // Load from database
                    emails = mailHistoryService.getEmailsByFolder(currentAccountId, folder);
                    setStatus("Loaded " + emails.size() + " emails from " + folder);
                } else {
                    // Demo mode - create sample emails
                    emails = createSampleEmails(folder);
                    setStatus("Loaded sample emails for " + folder + " (demo mode)");
                }
                
                emailList.setEmails(emails);
                
            } catch (Exception e) {
                logger.warning("Failed to load emails: " + e.getMessage());
                setStatus("Failed to load emails: " + e.getMessage());
            }
        });
    }
    
    private List<Email> createSampleEmails(String folder) {
        java.util.ArrayList<Email> emails = new java.util.ArrayList<>();
        
        switch (folder) {
            case "inbox":
                emails.add(createSampleEmail(1L, folder, 
                    "demo@example.com", "Welcome to Secure Mail!", 
                    "Welcome to our secure mail client! This message demonstrates basic functionality.", 
                    false, false, true));
                emails.add(createSampleEmail(2L, folder,
                    "security@company.com", "🔒 Encrypted Message",
                    "This is an encrypted message. Only you can read this content!",
                    true, false, false));
                emails.add(createSampleEmail(3L, folder,
                    "trusted@partner.com", "✍️ Signed Document", 
                    "This message has been digitally signed to verify authenticity.",
                    false, true, true));
                break;
            case "sent":
                emails.add(createSampleEmail(4L, folder,
                    currentUser, "Re: Project Update",
                    "Thanks for the update. The project is progressing well.",
                    false, false, true));
                break;
            case "drafts":
                emails.add(createSampleEmail(5L, folder,
                    currentUser, "Draft: Meeting Notes",
                    "Draft of meeting notes from yesterday's discussion...",
                    false, false, true));
                break;
            case "trash":
                emails.add(createSampleEmail(6L, folder,
                    "spam@nowhere.com", "Deleted: Spam Message",
                    "This message was moved to trash.",
                    false, false, true));
                break;
        }
        
        return emails;
    }
    
    private Email createSampleEmail(Long id, String folder, String from, String subject, String body, 
                                   boolean encrypted, boolean signed, boolean read) {
        Email email = new Email(currentAccountId != null ? currentAccountId : 1L, folder, from, currentUser, subject, body);
        email.setId(id);
        email.setEncrypted(encrypted);
        email.setSigned(signed);
        email.setSignatureOk(signed); // Assume signatures are valid for demo
        email.setRead(read);
        email.setCreatedAt(java.time.LocalDateTime.now().minusHours(id)); // Stagger times
        return email;
    }
    
    private void markEmailAsRead(Email email) {
        if (!email.isRead() && mailHistoryService != null) {
            try {
                mailHistoryService.markAsRead(email.getId());
                email.setRead(true);
                // Refresh folder counts
                loadFolders();
            } catch (Exception e) {
                logger.warning("Failed to mark email as read: " + e.getMessage());
            }
        }
    }
    
    private void openComposeWindow() {
        SwingUtilities.invokeLater(() -> {
            ModernComposeWindow composeWindow = new ModernComposeWindow(this, currentUser);
            composeWindow.setVisible(true);
        });
    }
    
    public void refreshEmails() {
        loadFolders();
        loadEmails(currentFolder);
    }
    
    private void applyTheme() {
        // Apply theme to main frame
        getContentPane().setBackground(ThemeManager.Colors.getBgColor());
        
        // Apply theme to components
        sidebar.applyTheme();
        emailList.applyTheme();
        emailViewer.applyTheme();
        
        statusLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        repaint();
    }
    
    private void setStatus(String message) {
        statusLabel.setText(message);
        logger.info(message);
    }
    
    /**
     * Test mail service connection
     * @return true if connection is available
     */
    public boolean testMailConnection() {
        if (mailService != null) {
            return mailService.testConnection(currentUser, "");
        }
        return false;
    }
    
    private void syncInbox() {
        if (mailService == null || mailHistoryService == null || currentAccountId == null) return;
        
        new Thread(() -> {
            try {
                // Load existing inbox emails to avoid duplicates
                java.util.Set<String> existingIds = new java.util.HashSet<>();
                for (Email existing : mailHistoryService.getEmailsByFolder(currentAccountId, "inbox")) {
                    if (existing.getServerMessageId() != null) {
                        existingIds.add(existing.getServerMessageId());
                    }
                }
                
                // Ưu tiên dùng mật khẩu người dùng vừa đăng nhập thành công
                String userPassword = currentPassword != null ? currentPassword : "";

                // Nếu vì lý do nào đó mật khẩu hiện tại rỗng, thử fallback sang plainPassword trong DB
                if (userPassword.isEmpty()) {
                    try {
                        var accountOpt = serviceRegistry.getAccountRepository().findByEmail(currentUser);
                        if (accountOpt.isPresent() && accountOpt.get().getPlainPassword() != null) {
                            userPassword = accountOpt.get().getPlainPassword();
                        }
                    } catch (Exception ex) {
                        logger.warning("Failed to load account for inbox sync: " + ex.getMessage());
                    }
                }

                List<Message> messages = mailService.fetchInbox(currentUser, userPassword);
                for (Message msg : messages) {
                    try {
                        String[] messageIdHeaders = msg.getHeader("Message-ID");
                        String messageId;
                        if (messageIdHeaders != null && messageIdHeaders.length > 0 && messageIdHeaders[0] != null) {
                            messageId = messageIdHeaders[0];
                        } else {
                            // Fallback ID ổn định dựa trên from + subject + sentDate
                            String from = msg.getFrom() != null && msg.getFrom().length > 0 ? msg.getFrom()[0].toString() : "";
                            String subject = msg.getSubject() != null ? msg.getSubject() : "";
                            java.util.Date sent = msg.getSentDate();
                            long sentTime = sent != null ? sent.getTime() : 0L;
                            messageId = from + "|" + subject + "|" + sentTime;
                        }
                        
                        // Skip if already exists
                        if (existingIds.contains(messageId)) {
                            continue;
                        }
                        
                        Email email = new Email(currentAccountId, "inbox", 
                                            msg.getFrom()[0].toString(), 
                                            currentUser,
                                            msg.getSubject(),
                                            msg.getContent().toString());
                        email.setServerMessageId(messageId);
                        email.setCreatedAt(msg.getSentDate().toInstant()
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDateTime());
                        mailHistoryService.saveEmail(email);
                        existingIds.add(messageId);
                    } catch (Exception e) {
                        logger.warning("Error processing message: " + e.getMessage());
                    }
                }
                SwingUtilities.invokeLater(() -> loadEmails("inbox"));
            } catch (Exception e) {
                logger.severe("Failed to sync inbox: " + e.getMessage());
            }
        }).start();
    }
}
