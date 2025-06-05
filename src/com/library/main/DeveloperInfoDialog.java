package com.library.main;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class DeveloperInfoDialog extends JDialog {

    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(66, 133, 244);

    public DeveloperInfoDialog(Frame parent) {
        super(parent, "Development Team", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(960, 735);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add main profile card
        addMainProfileCard(mainPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Add role cards in a grid
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(BACKGROUND_COLOR);

        // Add role cards
        addRoleCard(gridPanel, "Sponsor", "DANIEL",
                "N/a");
        addRoleCard(gridPanel, "Sponsor", "ROE",
                "N/a");
        addRoleCard(gridPanel, "Sponsor", "LOUISE",
                "N/a");
        addRoleCard(gridPanel, "Sponsor", "JESSA",
                "N/a");
        addRoleCard(gridPanel, "Sponsor", "RAY",
                "N/a");

        mainPanel.add(gridPanel);

        // Close button
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(closeButton);

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        setContentPane(scrollPane);
    }

    private void addMainProfileCard(JPanel panel) {
        JPanel cardPanel = new JPanel(new BorderLayout(30, 0));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(15, CARD_COLOR),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // Profile picture with fixed size
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setMinimumSize(new Dimension(150, 150));
        imageLabel.setMaximumSize(new Dimension(150, 150));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Use local image
        try {
            // Load image using absolute path during development
            String imagePath = System.getProperty("user.dir")
                    + "/src/main/resources/image/image1.png";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = ImageIO.read(imageFile);
                Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                ImageIcon roundedIcon = new ImageIcon(createRoundedImage(scaledImage));
                imageLabel.setIcon(roundedIcon);
            } else {
                System.out.println("Image not found at: " + imagePath);
                throw new Exception("Image not found");
            }
        } catch (Exception e) {
            // Fallback to initials
            imageLabel.setText("LG");
            imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
            imageLabel.setForeground(ACCENT_COLOR);
            imageLabel.setBackground(new Color(240, 240, 240));
            imageLabel.setOpaque(true);
        }

        // Wrap image in panel for proper alignment
        JPanel imageWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imageWrapper.setBackground(CARD_COLOR);
        imageWrapper.add(imageLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(CARD_COLOR);
        infoPanel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Add horizontal padding

        // Update name and center it
        JLabel nameLabel = new JLabel("Geo");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nameLabel.setForeground(new Color(33, 33, 33));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center the role label
        JLabel roleLabel = new JLabel("LEADER/DEVELOPER");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        roleLabel.setForeground(ACCENT_COLOR);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center the description
        JTextArea descArea = new JTextArea("");
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setEditable(false);
        descArea.setBackground(CARD_COLOR);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center the social links panel
        JPanel socialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        socialPanel.setBackground(CARD_COLOR);
        socialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addSocialLink(socialPanel, "Portfolio", "https://geodevelopment.xyz/");
        addSocialLink(socialPanel, "GitHub", "https://github.com/GEO211/libsys-group3-");

        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(roleLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(descArea);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(socialPanel);

        cardPanel.add(imageWrapper, BorderLayout.WEST);
        cardPanel.add(infoPanel, BorderLayout.CENTER);

        panel.add(cardPanel);
    }

    private void addRoleCard(JPanel panel, String role, String name, String description) {
        JPanel cardPanel = new JPanel(new BorderLayout(15, 0));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10, CARD_COLOR),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Role icon with fixed size
        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setMinimumSize(new Dimension(50, 50));
        iconLabel.setMaximumSize(new Dimension(50, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        try {
            String baseImage = switch (role) {
                case "UX/UI DESIGN" ->
                    "image2.png";
                case "SQL DESIGN" ->
                    "image3.png";
                case "PAPERS/RESEARCH" ->
                    "image4.png";
                case "IDEA DESIGN" ->
                    "image5.png";
                default ->
                    "image1.png";
            };

            String imagePath = System.getProperty("user.dir")
                    + "/src/main/resources/image/" + baseImage;
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = ImageIO.read(imageFile);
                Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                ImageIcon roundedIcon = new ImageIcon(createRoundedImage(scaledImage));
                iconLabel.setIcon(roundedIcon);
            }
        } catch (Exception e) {
            iconLabel.setText(role.substring(0, 1));
            iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            iconLabel.setForeground(ACCENT_COLOR);
            iconLabel.setBackground(new Color(240, 240, 240));
            iconLabel.setOpaque(true);
        }

        // Wrap icon in panel for proper alignment
        JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrapper.setBackground(CARD_COLOR);
        iconWrapper.add(iconLabel);

        cardPanel.add(iconWrapper, BorderLayout.WEST);

        // Add role information
        JPanel roleInfoPanel = new JPanel();
        roleInfoPanel.setLayout(new BoxLayout(roleInfoPanel, BoxLayout.Y_AXIS));
        roleInfoPanel.setBackground(CARD_COLOR);
        roleInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Name label first
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(33, 33, 33));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role label second
        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        roleLabel.setForeground(ACCENT_COLOR);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setEditable(false);
        descArea.setBackground(CARD_COLOR);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        roleInfoPanel.add(nameLabel);
        roleInfoPanel.add(Box.createVerticalStrut(5));  // Add spacing between name and role
        roleInfoPanel.add(roleLabel);
        roleInfoPanel.add(Box.createVerticalStrut(10));  // Add spacing between role and description
        roleInfoPanel.add(descArea);

        cardPanel.add(roleInfoPanel, BorderLayout.CENTER);

        panel.add(cardPanel);
    }

    private void addSocialLink(JPanel panel, String platform, String url) {
        JLabel link = new JLabel(platform);
        link.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        link.setForeground(ACCENT_COLOR);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel.add(link);
    }

    private void styleButton(JButton button) {
        // Define colors
        Color buttonColor = new Color(0, 123, 255);  // Bootstrap primary blue
        Color hoverColor = new Color(0, 105, 217);   // Darker blue for hover

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setBackground(buttonColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(hoverColor, 1),
                BorderFactory.createEmptyBorder(8, 30, 8, 30)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> dispose());

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonColor);
            }
        });

        // Make sure the button is opaque
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
    }

    private Image createRoundedImage(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();

        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, width, height, 20, 20));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return outputImage;
    }

    // Custom rounded border
    private static class RoundedBorder extends EmptyBorder {

        private final int radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            super(0, 0, 0, 0);
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
    }
}
