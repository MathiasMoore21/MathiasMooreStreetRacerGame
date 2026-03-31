import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TutorialPanel extends JPanel {

    public TutorialPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));

        // Main content panel with scrolling
        JPanel content = new JPanel();
        content.setBackground(new Color(20, 20, 20));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("🏁 HOW TO PLAY 🏁");
        title.setForeground(Color.ORANGE);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setAlignmentX(CENTER_ALIGNMENT);
        content.add(Box.createVerticalStrut(20));
        content.add(title);
        content.add(Box.createVerticalStrut(30));

        // ========== CONTROLS SECTION ==========
        addSectionHeader(content, "🎮 CONTROLS");

        String[] controls = {
                "← LEFT ARROW: Move left",
                "→ RIGHT ARROW: Move right",
                "↑ UP ARROW: Move up",
                "↓ DOWN ARROW: Move down"
        };
        addBulletPoints(content, controls, Color.WHITE);
        content.add(Box.createVerticalStrut(20));

        // ========== BARRICADES SECTION ==========
        addSectionHeader(content, "🧱 CONE TYPES");

        // Normal Barricade
        addBarricadeExplanation(content,
                "🟠 NORMAL (Orange)",
                "Moves straight down at medium speed. The standard obstacle.",
                new Color(255, 165, 0));

        // Zigzag Barricade
        addBarricadeExplanation(content,
                "⭐ ZIGZAG (Yellow)",
                "Moves side to side while going down. Unpredictable!",
                new Color(255, 200, 0));

        // Fast Barricade
        addBarricadeExplanation(content,
                "🔴 FAST (Red)",
                "Moves straight down but MUCH faster than normal ones.",
                Color.RED);

        // Patrol Barricade
        addBarricadeExplanation(content,
                "🟢 PATROL (Green)",
                "Moves slowly and changes direction. Stays near the top.",
                new Color(0, 150, 0));

        content.add(Box.createVerticalStrut(20));

        // ========== POWER-UPS SECTION ==========
        addSectionHeader(content, "✨ POWER-UPS");

        // Shield
        addPowerUpExplanation(content,
                "🛡️ SHIELD (Cyan)",
                "Protects you from ONE collision. Lasts 5 seconds.",
                Color.CYAN);

        // Slow Motion
        addPowerUpExplanation(content,
                "⏱️ SLOW MOTION (Blue)",
                "Slows down all barricades by half. Lasts 5 seconds.",
                Color.BLUE);

        // Extra Time
        addPowerUpExplanation(content,
                "⏰ EXTRA POINTS (Green)",
                "Gives you three additional points!",
                Color.GREEN);

        // Score Multiplier
        addPowerUpExplanation(content,
                "✖️ SCORE MULTIPLIER (Yellow)",
                "Doubles all points earned for 10 seconds.",
                Color.YELLOW);

        content.add(Box.createVerticalStrut(30));

        // ========== SCORING SECTION ==========
        addSectionHeader(content, "💰 SCORING");

        String[] scoring = {
                "• +1 point for each barricade that passes safely",
                "• x2 multiplier doubles your points",
                "• Survive as long as possible!"
        };
        addBulletPoints(content, scoring, Color.WHITE);

        content.add(Box.createVerticalStrut(30));

        // BACK button
        JButton back = new JButton("BACK TO MENU");
        back.setFont(new Font("Arial", Font.BOLD, 18));
        back.setForeground(Color.WHITE);
        back.setBackground(new Color(100, 100, 200));
        back.setFocusPainted(false);
        back.setBorder(BorderFactory.createRaisedBevelBorder());
        back.setPreferredSize(new Dimension(200, 50));
        back.setAlignmentX(CENTER_ALIGNMENT);

        back.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                back.setBackground(new Color(120, 120, 255));
            }
            public void mouseExited(MouseEvent e) {
                back.setBackground(new Color(100, 100, 200));
            }
        });

        back.addActionListener(e -> {
            Container top = getTopLevelAncestor();
            if (top instanceof GameWindow) {
                ((GameWindow)top).returnToMenu();
            }
        });

        content.add(back);
        content.add(Box.createVerticalStrut(30));

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(20, 20, 20));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addSectionHeader(JPanel content, String title) {
        JLabel header = new JLabel(title);
        header.setForeground(Color.ORANGE);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setAlignmentX(CENTER_ALIGNMENT);
        content.add(header);
        content.add(Box.createVerticalStrut(10));
    }

    private void addBulletPoints(JPanel content, String[] points, Color color) {
        for (String point : points) {
            JLabel label = new JLabel(point);
            label.setForeground(color);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setAlignmentX(CENTER_ALIGNMENT);
            content.add(label);
        }
    }

    private void addBarricadeExplanation(JPanel content, String title, String description, Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(400, 100));
        panel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descLabel);

        content.add(panel);
        content.add(Box.createVerticalStrut(10));
    }

    private void addPowerUpExplanation(JPanel content, String title, String description, Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(400, 100));
        panel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(descLabel);

        content.add(panel);
        content.add(Box.createVerticalStrut(10));
    }
}