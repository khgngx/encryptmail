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
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ui.theme.ThemeManager;

/**
 * Modern sidebar navigation component with active states and icons
 */
public class ModernSidebar extends JPanel {
    
    public interface SidebarListener {
        void onFolderSelected(String folder);
        void onComposeClicked();
    }
    
    private SidebarListener listener;
    private Map<String, NavigationItem> navigationItems;
    private String activeFolder = "inbox";
    private JButton composeButton;
    
    public ModernSidebar(SidebarListener listener) {
        this.listener = listener;
        this.navigationItems = new HashMap<>();
        
        initializeComponents();
        layoutComponents();
        applyTheme();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 0));
        
        // Compose button
        composeButton = createComposeButton();
    }
    
    private void layoutComponents() {
        // Header with compose button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.add(composeButton, BorderLayout.CENTER);
        
        // Navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 20, 16));
        
        // Add navigation items
        addNavigationItem(navPanel, "inbox", "📥", "Inbox", 0);
        addNavigationItem(navPanel, "sent", "📤", "Sent", 0);
        addNavigationItem(navPanel, "drafts", "📝", "Drafts", 0);
        addNavigationItem(navPanel, "trash", "🗑️", "Trash", 0);
        
        // Add spacing
        navPanel.add(Box.createVerticalGlue());
        
        add(headerPanel, BorderLayout.NORTH);
        add(navPanel, BorderLayout.CENTER);
    }
    
    private JButton createComposeButton() {
        JButton button = new JButton("Compose") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isPressed()) {
                    g2.setColor(ThemeManager.Colors.getAccentHoverColor());
                } else if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.Colors.getAccentColor().brighter());
                } else {
                    g2.setColor(ThemeManager.Colors.getAccentColor());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(ThemeManager.Fonts.BUTTON);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(0, 44));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            if (listener != null) {
                listener.onComposeClicked();
            }
        });
        
        return button;
    }
    
    private void addNavigationItem(JPanel parent, String folder, String icon, String label, int count) {
        NavigationItem item = new NavigationItem(folder, icon, label, count);
        navigationItems.put(folder, item);
        parent.add(item);
        parent.add(Box.createVerticalStrut(4)); // Small spacing between items
    }
    
    public void updateFolderCount(String folder, int count) {
        NavigationItem item = navigationItems.get(folder);
        if (item != null) {
            item.setCount(count);
        }
    }
    
    public void setActiveFolder(String folder) {
        // Update previous active item
        if (navigationItems.containsKey(activeFolder)) {
            navigationItems.get(activeFolder).setActive(false);
        }
        
        // Update new active item
        this.activeFolder = folder;
        if (navigationItems.containsKey(folder)) {
            navigationItems.get(folder).setActive(true);
        }
        
        repaint();
    }
    
    public void applyTheme() {
        setBackground(ThemeManager.Colors.getContentBgColor());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.Colors.getBorderColor()));
        
        // Update all navigation items
        for (NavigationItem item : navigationItems.values()) {
            item.applyTheme();
        }
        
        repaint();
    }
    
    /**
     * Custom navigation item component
     */
    private class NavigationItem extends JPanel {
        private String folder;
        private String icon;
        private String label;
        private int count;
        private boolean isActive;
        private boolean isHovered;
        
        public NavigationItem(String folder, String icon, String label, int count) {
            this.folder = folder;
            this.icon = icon;
            this.label = label;
            this.count = count;
            this.isActive = folder.equals(activeFolder);
            
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(0, 40));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            setupContent();
            setupMouseHandlers();
            applyTheme();
        }
        
        private void setupContent() {
            setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            
            // Left side: label only (no icon)
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setOpaque(false);
            
            JLabel textLabel = new JLabel(label);
            textLabel.setFont(ThemeManager.Fonts.BODY_MEDIUM);
            
            leftPanel.add(textLabel, BorderLayout.CENTER);
            
            // Right side: count badge (if > 0)
            JPanel rightPanel = new JPanel();
            rightPanel.setOpaque(false);
            
            if (count > 0) {
                JLabel countLabel = new JLabel(String.valueOf(count));
                countLabel.setFont(ThemeManager.Fonts.CAPTION);
                countLabel.setHorizontalAlignment(SwingConstants.CENTER);
                countLabel.setPreferredSize(new Dimension(24, 18));
                countLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                rightPanel.add(countLabel);
            }
            
            add(leftPanel, BorderLayout.CENTER);
            add(rightPanel, BorderLayout.EAST);
        }
        
        private void setupMouseHandlers() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (listener != null && !isActive) {
                        listener.onFolderSelected(folder);
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
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
        
        public void setActive(boolean active) {
            this.isActive = active;
            applyTheme();
        }
        
        public void setCount(int count) {
            this.count = count;
            removeAll();
            setupContent();
            applyTheme();
            revalidate();
            repaint();
        }
        
        public void applyTheme() {
            if (isActive) {
                setBackground(ThemeManager.Colors.getAccentColor());
                setForeground(Color.WHITE);
                // Update child components
                updateChildColors(this, Color.WHITE, ThemeManager.Colors.getAccentColor());
            } else if (isHovered) {
                setBackground(ThemeManager.Colors.getHoverColor());
                setForeground(ThemeManager.Colors.getFgColor());
                updateChildColors(this, ThemeManager.Colors.getFgColor(), ThemeManager.Colors.getHoverColor());
            } else {
                setBackground(ThemeManager.Colors.getContentBgColor());
                setForeground(ThemeManager.Colors.getFgColor());
                updateChildColors(this, ThemeManager.Colors.getFgColor(), ThemeManager.Colors.getContentBgColor());
            }
            
            repaint();
        }
        
        private void updateChildColors(Component parent, Color fg, Color bg) {
            if (parent instanceof JPanel) {
                JPanel panel = (JPanel) parent;
                panel.setBackground(bg);
                for (Component child : panel.getComponents()) {
                    updateChildColors(child, fg, bg);
                }
            } else if (parent instanceof JLabel) {
                JLabel label = (JLabel) parent;
                label.setForeground(fg);
                label.setBackground(bg);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint rounded background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
