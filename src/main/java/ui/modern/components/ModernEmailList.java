package ui.modern.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import core.model.Email;
import ui.theme.ThemeManager;

/**
 * Modern email list component with clean design and selection states
 */
public class ModernEmailList extends JPanel {
    
    public interface EmailListListener {
        void onEmailSelected(Email email);
    }
    
    private EmailListListener listener;
    private JPanel emailContainer;
    private JScrollPane scrollPane;
    private Email selectedEmail;
    private EmailItem selectedItem;
    
    public ModernEmailList(EmailListListener listener) {
        this.listener = listener;
        initializeComponents();
        layoutComponents();
        applyTheme();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Container for email items
        emailContainer = new JPanel();
        emailContainer.setLayout(new BoxLayout(emailContainer, BoxLayout.Y_AXIS));
        
        // Scroll pane
        scrollPane = new JScrollPane(emailContainer);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    private void layoutComponents() {
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setEmails(List<Email> emails) {
        emailContainer.removeAll();
        selectedEmail = null;
        selectedItem = null;
        
        if (emails.isEmpty()) {
            // Empty state
            JPanel emptyPanel = createEmptyState();
            emailContainer.add(emptyPanel);
        } else {
            for (Email email : emails) {
                EmailItem item = new EmailItem(email);
                emailContainer.add(item);
                emailContainer.add(Box.createVerticalStrut(1)); // Separator
            }
        }
        
        revalidate();
        repaint();
    }
    
    private JPanel createEmptyState() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
        
        JLabel emptyLabel = new JLabel("📭 No messages", SwingConstants.CENTER);
        emptyLabel.setFont(ThemeManager.Fonts.SUBHEADING);
        emptyLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        JLabel subLabel = new JLabel("Messages will appear here when you receive them", SwingConstants.CENTER);
        subLabel.setFont(ThemeManager.Fonts.SMALL);
        subLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(emptyLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(subLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }
    
    public void applyTheme() {
        setBackground(ThemeManager.Colors.getContentBgColor());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, ThemeManager.Colors.getBorderColor()));
        emailContainer.setBackground(ThemeManager.Colors.getContentBgColor());
        scrollPane.setBackground(ThemeManager.Colors.getContentBgColor());
        scrollPane.getViewport().setBackground(ThemeManager.Colors.getContentBgColor());
        
        // Update all email items
        for (Component comp : emailContainer.getComponents()) {
            if (comp instanceof EmailItem) {
                ((EmailItem) comp).applyTheme();
            }
        }
        
        repaint();
    }
    
    /**
     * Get currently selected email
     * @return selected email or null if none selected
     */
    public Email getSelectedEmail() {
        return selectedEmail;
    }
    
    /**
     * Individual email item component
     */
    private class EmailItem extends JPanel {
        private Email email;
        private boolean isSelected;
        private boolean isHovered;
        
        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d");
        
        public EmailItem(Email email) {
            this.email = email;
            this.isSelected = false;
            
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 80));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            setupContent();
            setupMouseHandlers();
            applyTheme();
        }
        
        private void setupContent() {
            setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
            
            // Main content panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);
            
            // Top row: sender + time + security indicators
            JPanel topRow = new JPanel(new BorderLayout());
            topRow.setOpaque(false);
            
            // Sender name
            String senderName = extractSenderName(email.getFromAddr());
            JLabel senderLabel = new JLabel(senderName);
            senderLabel.setFont(email.isRead() ? ThemeManager.Fonts.BODY : ThemeManager.Fonts.BODY_MEDIUM);
            
            // Right side: time + indicators
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
            rightPanel.setOpaque(false);
            
            // Security indicators
            if (email.isEncrypted()) {
                JLabel encryptedLabel = new JLabel("🔒");
                encryptedLabel.setFont(ThemeManager.Fonts.SMALL);
                rightPanel.add(encryptedLabel);
                rightPanel.add(Box.createHorizontalStrut(4));
            }
            
            if (email.isSigned()) {
                String signIcon = email.isSignatureOk() ? "✅" : "⚠️";
                JLabel signedLabel = new JLabel(signIcon);
                signedLabel.setFont(ThemeManager.Fonts.SMALL);
                rightPanel.add(signedLabel);
                rightPanel.add(Box.createHorizontalStrut(4));
            }
            
            // Time
            String timeText = email.getCreatedAt() != null ? 
                email.getCreatedAt().format(TIME_FORMATTER) : "Unknown";
            JLabel timeLabel = new JLabel(timeText);
            timeLabel.setFont(ThemeManager.Fonts.CAPTION);
            timeLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
            rightPanel.add(timeLabel);
            
            topRow.add(senderLabel, BorderLayout.WEST);
            topRow.add(rightPanel, BorderLayout.EAST);
            
            // Bottom row: subject + unread indicator
            JPanel bottomRow = new JPanel(new BorderLayout());
            bottomRow.setOpaque(false);
            
            // Subject with truncation
            String subject = email.getSubject();
            if (subject.length() > 60) {
                subject = subject.substring(0, 57) + "...";
            }
            
            JLabel subjectLabel = new JLabel(subject);
            subjectLabel.setFont(email.isRead() ? ThemeManager.Fonts.SMALL : ThemeManager.Fonts.BODY);
            
            // Unread indicator
            if (!email.isRead()) {
                JLabel unreadDot = new JLabel("●");
                unreadDot.setFont(ThemeManager.Fonts.CAPTION);
                unreadDot.setForeground(ThemeManager.Colors.getAccentColor());
                bottomRow.add(unreadDot, BorderLayout.EAST);
            }
            
            bottomRow.add(subjectLabel, BorderLayout.CENTER);
            
            // Combine rows
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            textPanel.add(topRow);
            textPanel.add(Box.createVerticalStrut(6));
            textPanel.add(bottomRow);
            
            contentPanel.add(textPanel, BorderLayout.CENTER);
            add(contentPanel, BorderLayout.CENTER);
        }
        
        private String extractSenderName(String fromAddr) {
            if (fromAddr == null) return "Unknown";
            
            // Extract name from "Name <email>" format
            if (fromAddr.contains("<")) {
                String name = fromAddr.substring(0, fromAddr.indexOf("<")).trim();
                if (!name.isEmpty()) {
                    return name;
                }
            }
            
            // Extract name from email address
            if (fromAddr.contains("@")) {
                return fromAddr.substring(0, fromAddr.indexOf("@"));
            }
            
            return fromAddr;
        }
        
        private void setupMouseHandlers() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectItem();
                    if (listener != null) {
                        listener.onEmailSelected(email);
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isSelected) {
                        isHovered = true;
                        applyTheme();
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    applyTheme();
                }
            });
        }
        
        private void selectItem() {
            // Deselect previous item
            if (selectedItem != null && selectedItem != this) {
                selectedItem.setSelected(false);
            }
            
            // Select this item
            setSelected(true);
            selectedEmail = email;
            selectedItem = this;
        }
        
        public void setSelected(boolean selected) {
            this.isSelected = selected;
            applyTheme();
        }
        
        public void applyTheme() {
            if (isSelected) {
                setBackground(ThemeManager.Colors.getSelectedColor());
            } else if (isHovered) {
                setBackground(ThemeManager.Colors.getHoverColor());
            } else {
                setBackground(ThemeManager.Colors.getContentBgColor());
            }
            
            // Update child component colors
            updateChildColors(this);
            repaint();
        }
        
        private void updateChildColors(Component parent) {
            Color textColor = isSelected ? 
                ThemeManager.Colors.getAccentColor() : 
                ThemeManager.Colors.getFgColor();
                
            if (parent instanceof JPanel) {
                JPanel panel = (JPanel) parent;
                panel.setBackground(getBackground());
                for (Component child : panel.getComponents()) {
                    updateChildColors(child);
                }
            } else if (parent instanceof JLabel) {
                JLabel label = (JLabel) parent;
                // Don't override secondary colors for time/metadata
                if (!label.getForeground().equals(ThemeManager.Colors.getSecondaryColor()) &&
                    !label.getForeground().equals(ThemeManager.Colors.getAccentColor())) {
                    label.setForeground(textColor);
                }
                label.setBackground(getBackground());
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint background
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            // Paint left border for selected item
            if (isSelected) {
                g2.setColor(ThemeManager.Colors.getAccentColor());
                g2.fillRect(0, 0, 3, getHeight());
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
