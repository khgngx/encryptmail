package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Left sidebar panel with navigation buttons
 */
public class SidebarPanel extends JPanel {
    
    private JButton btnInbox;
    private JButton btnCompose;
    private JButton btnSent;
    private JButton btnDrafts;
    private JButton btnTrash;
    private JButton btnKeys;
    private JButton btnSettings;
    
    private NavigationListener navigationListener;

    public interface NavigationListener {
        void onInboxSelected();
        void onComposeSelected();
        void onSentSelected();
        void onDraftsSelected();
        void onTrashSelected();
        void onKeysSelected();
        void onSettingsSelected();
    }

    public SidebarPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Create navigation buttons
        btnInbox = createNavButton("📥 Inbox");
        btnCompose = createNavButton("✏️ Compose");
        btnSent = createNavButton("📤 Sent");
        btnDrafts = createNavButton("📝 Drafts");
        btnTrash = createNavButton("🗑️ Trash");
        btnKeys = createNavButton("🔑 Keys");
        btnSettings = createNavButton("⚙️ Settings");
        
        // Set initial selection
        btnInbox.setSelected(true);
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 40));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(20, 10, 20, 10));
        setPreferredSize(new Dimension(200, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Mail Client");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Navigation panel
        JPanel navPanel = new JPanel(new GridBagLayout());
        navPanel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;
        
        gbc.gridy = 0;
        navPanel.add(btnInbox, gbc);
        gbc.gridy = 1;
        navPanel.add(btnCompose, gbc);
        gbc.gridy = 2;
        navPanel.add(btnSent, gbc);
        gbc.gridy = 3;
        navPanel.add(btnDrafts, gbc);
        gbc.gridy = 4;
        navPanel.add(btnTrash, gbc);
        
        // Add separator
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 5, 0);
        JLabel separator = new JLabel("Security");
        separator.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        separator.setForeground(new Color(108, 117, 125));
        navPanel.add(separator, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 5, 0);
        navPanel.add(btnKeys, gbc);
        gbc.gridy = 7;
        navPanel.add(btnSettings, gbc);
        
        add(titleLabel, BorderLayout.NORTH);
        add(navPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        btnInbox.addActionListener(e -> {
            selectButton(btnInbox);
            if (navigationListener != null) {
                navigationListener.onInboxSelected();
            }
        });
        
        btnCompose.addActionListener(e -> {
            selectButton(btnCompose);
            if (navigationListener != null) {
                navigationListener.onComposeSelected();
            }
        });
        
        btnSent.addActionListener(e -> {
            selectButton(btnSent);
            if (navigationListener != null) {
                navigationListener.onSentSelected();
            }
        });
        
        btnDrafts.addActionListener(e -> {
            selectButton(btnDrafts);
            if (navigationListener != null) {
                navigationListener.onDraftsSelected();
            }
        });
        
        btnTrash.addActionListener(e -> {
            selectButton(btnTrash);
            if (navigationListener != null) {
                navigationListener.onTrashSelected();
            }
        });
        
        btnKeys.addActionListener(e -> {
            selectButton(btnKeys);
            if (navigationListener != null) {
                navigationListener.onKeysSelected();
            }
        });
        
        btnSettings.addActionListener(e -> {
            selectButton(btnSettings);
            if (navigationListener != null) {
                navigationListener.onSettingsSelected();
            }
        });
    }
    
    private void selectButton(JButton selectedButton) {
        // Reset all buttons
        btnInbox.setSelected(false);
        btnCompose.setSelected(false);
        btnSent.setSelected(false);
        btnDrafts.setSelected(false);
        btnTrash.setSelected(false);
        btnKeys.setSelected(false);
        btnSettings.setSelected(false);
        
        // Select the clicked button
        selectedButton.setSelected(true);
        
        // Update button colors
        updateButtonColors();
    }
    
    private void updateButtonColors() {
        JButton[] buttons = {btnInbox, btnCompose, btnSent, btnDrafts, btnTrash, btnKeys, btnSettings};
        
        for (JButton button : buttons) {
            if (button.isSelected()) {
                // Use custom primary color if available
                util.UICustomizationManager.applyButtonColor(button, "primary");
            } else {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }
        }
    }

    public void setNavigationListener(NavigationListener listener) {
        this.navigationListener = listener;
    }
}
