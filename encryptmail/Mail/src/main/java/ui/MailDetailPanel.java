package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Mail detail panel showing From, To, Date, Subject, Body, and attachments
 */
public class MailDetailPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(MailDetailPanel.class.getName());
    
    private JLabel lblFrom;
    private JLabel lblTo;
    private JLabel lblDate;
    private JLabel lblSubject;
    private JTextArea txtBody;
    private JScrollPane bodyScrollPane;
    private JPanel attachmentPanel;
    private JPanel securityPanel;
    private JLabel lblEncrypted;
    private JLabel lblSigned;

    public MailDetailPanel() {
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // Header labels
        lblFrom = new JLabel("From: ");
        lblFrom.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        lblTo = new JLabel("To: ");
        lblTo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        lblDate = new JLabel("Date: ");
        lblDate.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        lblSubject = new JLabel("Subject: ");
        lblSubject.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        lblSubject.setForeground(new Color(33, 37, 41));
        
        // Body text area
        txtBody = new JTextArea();
        txtBody.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        txtBody.setLineWrap(true);
        txtBody.setWrapStyleWord(true);
        txtBody.setEditable(false);
        txtBody.setBackground(Color.WHITE);
        
        bodyScrollPane = new JScrollPane(txtBody);
        bodyScrollPane.setPreferredSize(new Dimension(400, 300));
        bodyScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Security panel
        securityPanel = new JPanel(new BorderLayout());
        securityPanel.setBackground(new Color(248, 249, 250));
        securityPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        lblEncrypted = new JLabel("🔒 End-to-End Encrypted");
        lblEncrypted.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        lblEncrypted.setForeground(new Color(40, 167, 69));
        lblEncrypted.setVisible(false);
        
        lblSigned = new JLabel("✓ Digitally Signed");
        lblSigned.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        lblSigned.setForeground(new Color(0, 123, 255));
        lblSigned.setVisible(false);
        
        securityPanel.add(lblEncrypted, BorderLayout.WEST);
        securityPanel.add(lblSigned, BorderLayout.EAST);
        
        // Attachment panel
        attachmentPanel = new JPanel();
        attachmentPanel.setLayout(new GridBagLayout());
        attachmentPanel.setBackground(Color.WHITE);
        attachmentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(lblFrom, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        headerPanel.add(lblTo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        headerPanel.add(lblDate, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        headerPanel.add(lblSubject, gbc);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(securityPanel, BorderLayout.CENTER);
        add(bodyScrollPane, BorderLayout.CENTER);
        add(attachmentPanel, BorderLayout.SOUTH);
    }

    public void loadMail(String mailId) {
        logger.info(() -> "Loading mail: " + mailId);
        
        // Simulate loading mail data
        lblFrom.setText("From: sender@example.com");
        lblTo.setText("To: recipient@example.com");
        lblDate.setText("Date: " + java.time.LocalDateTime.now().toString());
        lblSubject.setText("Subject: Sample Email Subject");
        
        txtBody.setText("""
            This is the body of the email. It contains the main content that the sender wanted to communicate.
            
            In a real implementation, this would be loaded from the actual email message.
            
            The email may contain formatting, links, and other rich content.""");
        
        // Show security indicators (simulate some encrypted/signed emails)
        boolean isEncrypted = Math.random() > 0.6;
        boolean isSigned = Math.random() > 0.7;
        
        lblEncrypted.setVisible(isEncrypted);
        lblSigned.setVisible(isSigned);
        
        // Add some sample attachments
        loadAttachments();
        
        // Refresh the display
        revalidate();
        repaint();
    }
    
    private void loadAttachments() {
        attachmentPanel.removeAll();
        
        // Add some sample attachments
        addAttachment("document.pdf", "2.3 MB");
        addAttachment("image.jpg", "1.1 MB");
        addAttachment("spreadsheet.xlsx", "856 KB");
        
        attachmentPanel.revalidate();
        attachmentPanel.repaint();
    }
    
    private void addAttachment(String filename, String size) {
        JPanel attachmentItem = new JPanel(new BorderLayout());
        attachmentItem.setBackground(new Color(248, 249, 250));
        attachmentItem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        JLabel nameLabel = new JLabel("📎 " + filename);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        JLabel sizeLabel = new JLabel(size);
        sizeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        sizeLabel.setForeground(new Color(108, 117, 125));
        
        JButton downloadBtn = new JButton("Download");
        downloadBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        downloadBtn.setPreferredSize(new Dimension(80, 25));
        downloadBtn.setBackground(new Color(0, 123, 255));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        downloadBtn.setFocusPainted(false);
        
        downloadBtn.addActionListener(e -> {
            logger.info(() -> "Downloading attachment: " + filename);
            // In a real implementation, this would trigger the download
        });
        
        attachmentItem.add(nameLabel, BorderLayout.WEST);
        attachmentItem.add(sizeLabel, BorderLayout.CENTER);
        attachmentItem.add(downloadBtn, BorderLayout.EAST);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = attachmentPanel.getComponentCount();
        
        attachmentPanel.add(attachmentItem, gbc);
    }
}
