import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPanel extends JPanel {
    private GameWindow window;
    private JButton startButton;
    private JButton highScoresButton;
    private JButton exitButton;
    private JComboBox<String> difficultyCombo;
    
    public MenuPanel(GameWindow w) {
        this.window = w;
        setLayout(new GridBagLayout());
        setBackground(new Color(20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Title
        JLabel title = new JLabel("STREET RACER");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.ORANGE);
        gbc.gridy = 0;
        add(title, gbc);
        
        // Subtitle
        JLabel sub = new JLabel("Dodge the Traffic Cones!");
        sub.setFont(new Font("Arial", Font.ITALIC, 18));
        sub.setForeground(Color.WHITE);
        gbc.gridy = 1;
        add(sub, gbc);
        
        // Difficulty selection
        JPanel diffPanel = new JPanel(new FlowLayout());
        diffPanel.setBackground(new Color(20, 20, 20));
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setFont(new Font("Arial", Font.BOLD, 14));
        String[] diffs = {"Easy", "Medium", "Hard", "Insane"};
        difficultyCombo = new JComboBox<>(diffs);
        difficultyCombo.setBackground(Color.WHITE);
        diffPanel.add(diffLabel);
        diffPanel.add(difficultyCombo);
        gbc.gridy = 2;
        add(diffPanel, gbc);
        
        // Buttons
        startButton = new JButton("START GAME");
        styleButton(startButton, new Color(0, 150, 0));
        gbc.gridy = 3;
        add(startButton, gbc);
        
        // Add with other buttons
        JButton tutorialButton = new JButton("HOW TO PLAY");
        styleButton(tutorialButton, new Color(100, 100, 200));
        gbc.gridy = 4; // Adjusted position
        add(tutorialButton, gbc);
        
        // Add action listener
        tutorialButton.addActionListener(e -> window.showTutorial());
                
        highScoresButton = new JButton("HIGH SCORES");
        styleButton(highScoresButton, new Color(0, 0, 150));
        gbc.gridy = 5;
        add(highScoresButton, gbc);
        
        exitButton = new JButton("EXIT");
        styleButton(exitButton, new Color(150, 0, 0));
        gbc.gridy = 6; // Move to its own row so it's not overlapped
        add(exitButton, gbc);
        
        // Add animations
        startButton.addActionListener(e -> startGame());
        highScoresButton.addActionListener(e -> showHighScores());
        exitButton.addActionListener(e -> System.exit(0));
    }
    
    private void styleButton(JButton button, Color bg) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(200, 50));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bg.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });
    }
    
    private void startGame() {
        // Always endless mode
        int time = -1;
         
        String difficulty = (String)difficultyCombo.getSelectedItem();
        window.startGame(time, difficulty);
    }

    private void showHighScores() {
        String difficulty = (String)difficultyCombo.getSelectedItem();
        String scores = HighScoreManager.getInstance().getFormattedScores(difficulty);
        JOptionPane.showMessageDialog(this,
                scores,
                "🏆 " + difficulty.toUpperCase() + " HIGH SCORES 🏆",
                JOptionPane.INFORMATION_MESSAGE);
    }
}