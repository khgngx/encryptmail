package ui.modern.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import core.model.Email;
import ui.theme.ThemeManager;

/**
 * Modern email content viewer with clean header and formatted content
 */
public class ModernEmailViewer extends JPanel {
    
    private JPanel headerPanel;
    private JPanel securityPanel;
    private JTextArea contentArea;
    private JScrollPane scrollPane;
    private Email currentEmail;
    
    private static final DateTimeFormatter FULL_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");
    
    public ModernEmailViewer() {
        initializeComponents();
        layoutComponents();
        applyTheme();
        showEmptyState();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Header panel for email metadata
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));
        
        // Security status panel
        securityPanel = new JPanel();
        securityPanel.setLayout(new BoxLayout(securityPanel, BoxLayout.Y_AXIS));
        securityPanel.setBorder(BorderFactory.createEmptyBorder(0, 24, 16, 24));
        securityPanel.setVisible(false);
        
        // Content area
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(ThemeManager.Fonts.BODY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));
        
        // Scroll pane for content
        scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }
    
    private void layoutComponents() {
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(securityPanel, BorderLayout.CENTER);
        
        // Combined panel for header + security + content
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(mainPanel, BorderLayout.NORTH);
        combinedPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(combinedPanel, BorderLayout.CENTER);
    }
    
    public void displayEmail(Email email) {
        this.currentEmail = email;
        
        if (email == null) {
            showEmptyState();
            return;
        }
        
        updateHeader(email);
        updateSecurityStatus(email);
        updateContent(email);
        
        revalidate();
        repaint();
    }
    
    private void updateHeader(Email email) {
        headerPanel.removeAll();
        
        // Subject
        JLabel subjectLabel = new JLabel(email.getSubject());
        subjectLabel.setFont(ThemeManager.Fonts.HEADING);
        subjectLabel.setForeground(ThemeManager.Colors.getFgColor());
        
        headerPanel.add(subjectLabel);
        headerPanel.add(Box.createVerticalStrut(16));
        
        // From/To section
        JPanel fromToPanel = createFromToSection(email);
        headerPanel.add(fromToPanel);
        
        headerPanel.add(Box.createVerticalStrut(12));
        
        // Date
        String dateText = email.getCreatedAt() != null ? 
            email.getCreatedAt().format(FULL_DATE_FORMATTER) : "Date unknown";
        JLabel dateLabel = new JLabel(dateText);
        dateLabel.setFont(ThemeManager.Fonts.SMALL);
        dateLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        headerPanel.add(dateLabel);
    }
    
    private JPanel createFromToSection(Email email) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        // From
        JPanel fromPanel = new JPanel(new BorderLayout());
        fromPanel.setOpaque(false);
        
        JLabel fromLabelTitle = new JLabel("From:");
        fromLabelTitle.setFont(ThemeManager.Fonts.BODY_MEDIUM);
        fromLabelTitle.setForeground(ThemeManager.Colors.getSecondaryColor());
        fromLabelTitle.setPreferredSize(new Dimension(50, 0));
        
        JLabel fromValue = new JLabel(formatEmailAddress(email.getFromAddr()));
        fromValue.setFont(ThemeManager.Fonts.BODY);
        fromValue.setForeground(ThemeManager.Colors.getFgColor());
        
        fromPanel.add(fromLabelTitle, BorderLayout.WEST);
        fromPanel.add(fromValue, BorderLayout.CENTER);
        
        // To
        JPanel toPanel = new JPanel(new BorderLayout());
        toPanel.setOpaque(false);
        
        JLabel toLabelTitle = new JLabel("To:");
        toLabelTitle.setFont(ThemeManager.Fonts.BODY_MEDIUM);
        toLabelTitle.setForeground(ThemeManager.Colors.getSecondaryColor());
        toLabelTitle.setPreferredSize(new Dimension(50, 0));
        
        JLabel toValue = new JLabel(formatEmailAddress(email.getToAddr()));
        toValue.setFont(ThemeManager.Fonts.BODY);
        toValue.setForeground(ThemeManager.Colors.getFgColor());
        
        toPanel.add(toLabelTitle, BorderLayout.WEST);
        toPanel.add(toValue, BorderLayout.CENTER);
        
        panel.add(fromPanel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(toPanel);
        
        return panel;
    }
    
    private void updateSecurityStatus(Email email) {
        securityPanel.removeAll();
        
        boolean hasSecurityInfo = email.isEncrypted() || email.isSigned();
        securityPanel.setVisible(hasSecurityInfo);
        
        if (!hasSecurityInfo) {
            return;
        }
        
        // Security status card
        JPanel securityCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background with subtle border
                g2.setColor(ThemeManager.Colors.getContentBgColor());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(ThemeManager.Colors.getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        securityCard.setLayout(new BoxLayout(securityCard, BoxLayout.Y_AXIS));
        securityCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        securityCard.setOpaque(false);
        
        // Security header
        JLabel securityHeader = new JLabel("🔐 Security Status");
        securityHeader.setFont(ThemeManager.Fonts.BODY_MEDIUM);
        securityHeader.setForeground(ThemeManager.Colors.getFgColor());
        
        securityCard.add(securityHeader);
        securityCard.add(Box.createVerticalStrut(12));
        
        // Encryption status
        if (email.isEncrypted()) {
            JPanel encryptionPanel = createSecurityItem("🔒", "Encrypted", 
                "This message was encrypted end-to-end", ThemeManager.Colors.getSuccessColor());
            securityCard.add(encryptionPanel);
        }
        
        // Signature status
        if (email.isSigned()) {
            String icon = email.isSignatureOk() ? "✅" : "⚠️";
            String status = email.isSignatureOk() ? "Signature Verified" : "Signature Invalid";
            String description = email.isSignatureOk() ? 
                "Digital signature is valid and verified" : 
                "Digital signature could not be verified";
            Color statusColor = email.isSignatureOk() ? 
                ThemeManager.Colors.getSuccessColor() : 
                ThemeManager.Colors.getWarningColor();
                
            JPanel signaturePanel = createSecurityItem(icon, status, description, statusColor);
            securityCard.add(signaturePanel);
        }
        
        securityPanel.add(securityCard);
    }
    
    private JPanel createSecurityItem(String icon, String title, String description, Color statusColor) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        
        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(ThemeManager.Fonts.BODY);
        
        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.Fonts.BODY_MEDIUM);
        titleLabel.setForeground(statusColor);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(ThemeManager.Fonts.SMALL);
        descLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        textPanel.add(titleLabel);
        textPanel.add(descLabel);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateContent(Email email) {
        String content = email.getBody();
        if (content == null || content.trim().isEmpty()) {
            content = "(No content)";
        }
        
        contentArea.setText(content);
        contentArea.setCaretPosition(0);
    }
    
    private void showEmptyState() {
        headerPanel.removeAll();
        securityPanel.setVisible(false);
        
        // Empty state in header
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setOpaque(false);
        emptyPanel.setPreferredSize(new Dimension(0, 200));
        
        JLabel emptyLabel = new JLabel("📧 Select a message to read", SwingConstants.CENTER);
        emptyLabel.setFont(ThemeManager.Fonts.SUBHEADING);
        emptyLabel.setForeground(ThemeManager.Colors.getSecondaryColor());
        
        emptyPanel.add(emptyLabel, BorderLayout.CENTER);
        headerPanel.add(emptyPanel);
        
        contentArea.setText("");
        
        revalidate();
        repaint();
    }
    
    private String formatEmailAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "Unknown";
        }
        
        // If it's already formatted as "Name <email>", return as is
        if (address.contains("<") && address.contains(">")) {
            return address;
        }
        
        // Otherwise just return the email
        return address;
    }
    
    public void applyTheme() {
        setBackground(ThemeManager.Colors.getContentBgColor());
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, ThemeManager.Colors.getBorderColor()));
        
        headerPanel.setBackground(ThemeManager.Colors.getContentBgColor());
        securityPanel.setBackground(ThemeManager.Colors.getContentBgColor());
        
        contentArea.setBackground(ThemeManager.Colors.getContentBgColor());
        contentArea.setForeground(ThemeManager.Colors.getFgColor());
        
        scrollPane.setBackground(ThemeManager.Colors.getContentBgColor());
        scrollPane.getViewport().setBackground(ThemeManager.Colors.getContentBgColor());
        
        // Refresh current email display
        if (currentEmail != null) {
            displayEmail(currentEmail);
        }
        
        repaint();
    }
}
