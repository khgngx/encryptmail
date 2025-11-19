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
import javax.swing.border.EmptyBorder;

/**
 * Top bar panel with app name, compose button, and connection status
 */
public class TopBarPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(TopBarPanel.class.getName());
    
    private JLabel lblAppName;
    private JButton btnCompose;
    private JButton btnRefresh;
    private JLabel lblConnectionStatus;
    private JLabel lblUserInfo;

    public TopBarPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        lblAppName = new JLabel("Secure Mail Client");
        lblAppName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        lblAppName.setForeground(new Color(33, 37, 41));
        
        btnCompose = new JButton("✏️ Compose");
        btnCompose.setPreferredSize(new Dimension(120, 35));
        btnCompose.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnCompose.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnCompose.setFocusPainted(false);
        
        // Apply custom compose button color
        util.UICustomizationManager.applyButtonColor(btnCompose, "compose");
        
        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBackground(new Color(108, 117, 125));
        btnRefresh.setForeground(Color.WHITE);
        
        lblConnectionStatus = new JLabel("🟢 Connected");
        lblConnectionStatus.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblConnectionStatus.setForeground(new Color(40, 167, 69));
        
        lblUserInfo = new JLabel("user@localhost");
        lblUserInfo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblUserInfo.setForeground(new Color(108, 117, 125));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)),
            new EmptyBorder(15, 20, 15, 20)
        ));
        setPreferredSize(new Dimension(0, 60));
        
        // Left side - App name
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(lblAppName, BorderLayout.WEST);
        
        // Right side - Buttons and status
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        
        gbc.gridx = 0;
        rightPanel.add(btnCompose, gbc);
        
        gbc.gridx = 1;
        rightPanel.add(btnRefresh, gbc);
        
        gbc.gridx = 2;
        rightPanel.add(lblConnectionStatus, gbc);
        
        gbc.gridx = 3;
        rightPanel.add(lblUserInfo, gbc);
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void setupEventHandlers() {
        btnCompose.addActionListener(e -> {
            logger.info("Compose button clicked");
            // This will be handled by the main application
        });
        
        btnRefresh.addActionListener(e -> {
            logger.info("Refresh button clicked");
            // This will be handled by the main application
        });
    }
    
    public void setConnectionStatus(boolean connected) {
        if (connected) {
            lblConnectionStatus.setText("🟢 Connected");
            lblConnectionStatus.setForeground(new Color(40, 167, 69));
        } else {
            lblConnectionStatus.setText("🔴 Disconnected");
            lblConnectionStatus.setForeground(new Color(220, 53, 69));
        }
    }
    
    public void setUserInfo(String email) {
        lblUserInfo.setText(email);
    }
    
    public void updateUserDisplay() {
        String currentEmail = MainController.getEmail();
        if (currentEmail != null && !currentEmail.isEmpty()) {
            setUserInfo(currentEmail);
        } else {
            setUserInfo("Not logged in");
        }
    }
    
    public JButton getComposeButton() {
        return btnCompose;
    }
    
    public JButton getRefreshButton() {
        return btnRefresh;
    }
}

