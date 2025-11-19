package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * Mail list panel showing sender, subject, and preview text
 */
public class MailListPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(MailListPanel.class.getName());
    
    private JList<MailItem> mailList;
    private DefaultListModel<MailItem> listModel;
    private JScrollPane scrollPane;
    private JLabel lblFolderTitle;
    
    private MailSelectionListener selectionListener;

    public interface MailSelectionListener {
        void onMailSelected(String mailId);
    }

    public static class MailItem {
        private final String id;
        private final String sender;
        private final String subject;
        private final String preview;
        private final String date;
        private boolean isRead;
        private final boolean isEncrypted;
        private final boolean isSigned;
        private final boolean isImportant;

        public MailItem(String id, String sender, String subject, String preview, String date, 
                       boolean isRead, boolean isEncrypted, boolean isSigned, boolean isImportant) {
            this.id = id;
            this.sender = sender;
            this.subject = subject;
            this.preview = preview;
            this.date = date;
            this.isRead = isRead;
            this.isEncrypted = isEncrypted;
            this.isSigned = isSigned;
            this.isImportant = isImportant;
        }

        // Getters
        public String getId() { return id; }
        public String getSender() { return sender; }
        public String getSubject() { return subject; }
        public String getPreview() { return preview; }
        public String getDate() { return date; }
        public boolean isRead() { return isRead; }
        public boolean isEncrypted() { return isEncrypted; }
        public boolean isSigned() { return isSigned; }
        public boolean isImportant() { return isImportant; }
        
        public void setRead(boolean read) { this.isRead = read; }
    }

    public MailListPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        listModel = new DefaultListModel<>();
        mailList = new JList<>(listModel);
        mailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mailList.setCellRenderer(new MailItemRenderer());
        
        scrollPane = new JScrollPane(mailList);
        scrollPane.setPreferredSize(new Dimension(400, 0));
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        lblFolderTitle = new JLabel("Inbox");
        lblFolderTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        lblFolderTitle.setBorder(new EmptyBorder(15, 15, 10, 15));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(lblFolderTitle, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        mailList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                MailItem selectedItem = mailList.getSelectedValue();
                if (selectedItem != null && selectionListener != null) {
                    selectionListener.onMailSelected(selectedItem.getId());
                    // Mark as read when selected
                    selectedItem.setRead(true);
                    mailList.repaint();
                }
            }
        });
    }

    public void loadInbox() {
        lblFolderTitle.setText("Inbox");
        loadMails("inbox");
    }
    
    public void loadSent() {
        lblFolderTitle.setText("Sent");
        loadMails("sent");
    }
    
    public void loadDrafts() {
        lblFolderTitle.setText("Drafts");
        loadMails("drafts");
    }
    
    public void loadTrash() {
        lblFolderTitle.setText("Trash");
        loadMails("trash");
    }

    private void loadMails(String folder) {
        logger.info(() -> "Loading mails from folder: " + folder);
        
        listModel.clear();
        
        // Load from mail history manager instead of mock data
        List<util.MailHistoryManager.MailRecord> records = util.MailHistoryManager.getFolderMails(folder);
        
        if (records.isEmpty()) {
            logger.info(() -> "No mails found in " + folder + " folder");
            return;
        }
        
        for (util.MailHistoryManager.MailRecord record : records) {
            String preview = record.getBody().length() > 50 ? 
                record.getBody().substring(0, 50) + "..." : record.getBody();
            
            MailItem item = new MailItem(
                record.getId(),
                record.getFrom(),
                record.getSubject(),
                preview,
                record.getTimestamp().toString(),
                record.isRead(),
                record.isEncrypted(),
                record.isSigned(),
                record.isImportant()
            );
            
            listModel.addElement(item);
        }
        
        logger.info(() -> "Loaded " + records.size() + " mails from " + folder);
    }

    public void setMailSelectionListener(MailSelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Custom cell renderer for mail items
     */
    private static class MailItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            
            if (!(value instanceof MailItem)) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            
            MailItem item = (MailItem) value;
            
            // Create a custom panel for the mail item
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(8, 12, 8, 12));
            
            if (isSelected) {
                panel.setBackground(new Color(0, 123, 255, 20));
            } else if (!item.isRead()) {
                panel.setBackground(new Color(248, 249, 250));
            } else {
                panel.setBackground(Color.WHITE);
            }
            
            // Sender and date
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            
            JLabel senderLabel = new JLabel(item.getSender());
            senderLabel.setFont(new Font(Font.SANS_SERIF, item.isRead() ? Font.PLAIN : Font.BOLD, 12));
            senderLabel.setForeground(isSelected ? new Color(0, 123, 255) : Color.BLACK);
            
            JLabel dateLabel = new JLabel(item.getDate());
            dateLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            dateLabel.setForeground(new Color(108, 117, 125));
            
            topPanel.add(senderLabel, BorderLayout.WEST);
            topPanel.add(dateLabel, BorderLayout.EAST);
            
            // Subject
            JLabel subjectLabel = new JLabel(item.getSubject());
            subjectLabel.setFont(new Font(Font.SANS_SERIF, item.isRead() ? Font.PLAIN : Font.BOLD, 13));
            subjectLabel.setForeground(isSelected ? new Color(0, 123, 255) : Color.BLACK);
            
            // Preview
            JLabel previewLabel = new JLabel(item.getPreview());
            previewLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            previewLabel.setForeground(new Color(108, 117, 125));
            
            // Security badges
            JPanel badgePanel = new JPanel(new BorderLayout());
            badgePanel.setOpaque(false);
            
            if (item.isEncrypted()) {
                JLabel encryptedBadge = new JLabel("🔒 Encrypted");
                encryptedBadge.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                encryptedBadge.setForeground(new Color(40, 167, 69));
                badgePanel.add(encryptedBadge, BorderLayout.WEST);
            }
            
            if (item.isSigned()) {
                JLabel signedBadge = new JLabel("✓ Verified");
                signedBadge.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
                signedBadge.setForeground(new Color(0, 123, 255));
                badgePanel.add(signedBadge, BorderLayout.EAST);
            }
            
            // Assemble the panel
            panel.add(topPanel, BorderLayout.NORTH);
            panel.add(subjectLabel, BorderLayout.CENTER);
            panel.add(previewLabel, BorderLayout.SOUTH);
            panel.add(badgePanel, BorderLayout.EAST);
            
            return panel;
        }
    }
}
