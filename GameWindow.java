import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JFrame
        implements ActionListener,
        KeyListener,
        MouseListener
{

    private JLabel pointsL;
    private JLabel timerL;


    private JTextField pointsTF;
    private JTextField timerTF;

    private JButton show;
    private JButton exitB;

    private Container c;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private MenuPanel menuPanel;
    private TutorialPanel tutorialPanel;
    private CardLayout cardLayout;

    private JPanel infoPanel;
    private String currentDifficulty = "Medium";

    // Constructor - sets up the main game window
    public GameWindow() {

        setTitle("Street Racer");
        setSize(500, 550);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        gamePanel = new GamePanel(this);
        gamePanel.setPreferredSize(new Dimension(400, 400));
        gamePanel.createGameEntities();

        menuPanel = new MenuPanel(this);
        tutorialPanel = new TutorialPanel();

        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(tutorialPanel, "TUTORIAL");

        // Info panel at top showing points and time
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 6));
        infoPanel.setBackground(Color.white);


        pointsL = new JLabel("Points:");
        timerL = new JLabel("Time:");


        pointsTF = new JTextField(5);
        timerTF = new JTextField(5);


        pointsTF.setEditable(false);
        timerTF.setEditable(false);


        pointsTF.setBackground(Color.WHITE);
        timerTF.setBackground(Color.WHITE);

        pointsTF.setText("0");
        timerTF.setText("30");



        infoPanel.add(pointsL);
        infoPanel.add(pointsTF);
        infoPanel.add(timerL);
        infoPanel.add(timerTF);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        show = new JButton("RESTART");
        exitB = new JButton("Exit");

        show.addActionListener(this);
        exitB.addActionListener(this);

        buttonPanel.add(show);
        buttonPanel.add(exitB);

        // Main container
        c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(infoPanel, BorderLayout.NORTH);
        c.add(mainPanel, BorderLayout.CENTER);
        c.add(buttonPanel, BorderLayout.SOUTH);

        gamePanel.addMouseListener(this);
        gamePanel.addKeyListener(this);
        gamePanel.setFocusable(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


        // Start on menu - HIDE the info panel
        infoPanel.setVisible(false);
        cardLayout.show(mainPanel, "MENU");
    }

    // Starts game with selected time and difficulty
    public void startGame(int time, String difficulty) {
        currentDifficulty = difficulty; // STORE IT
        gamePanel.startGame(time, difficulty);

        // SHOW the info panel when game starts
        infoPanel.setVisible(true);

        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    // Returns to main menu
    public void returnToMenu() {
        gamePanel.stopGame();

        // HIDE the info panel when returning to menu
        infoPanel.setVisible(false);

        cardLayout.show(mainPanel, "MENU");
    }

    // Shows tutorial screen
    public void showTutorial() {
        // HIDE the info panel when showing tutorial
        infoPanel.setVisible(false);

        cardLayout.show(mainPanel, "TUTORIAL");
    }

    // Returns current difficulty setting
    public String getCurrentDifficulty() {
        return currentDifficulty;
    }

    // Handles button clicks
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if(command.equals("RESTART")) {
            returnToMenu();
        }

        if (command.equals("Exit"))
            System.exit(0);
    }

    // Handles keyboard input for runner movement
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (gamePanel.isGameActive()) {
            if (keyCode == KeyEvent.VK_RIGHT) {
                gamePanel.updateGameEntities(2);
            }
            if (keyCode == KeyEvent.VK_LEFT) {
                gamePanel.updateGameEntities(1);
            }
            if (keyCode == KeyEvent.VK_UP) {
                gamePanel.updateGameEntities(4);
            }
            if (keyCode == KeyEvent.VK_DOWN) {
                gamePanel.updateGameEntities(3);
            }
        }
    }

    public void keyReleased(KeyEvent e) { }
    public void keyTyped(KeyEvent e) { }

    // Updates points display
    public void updatePoints(int points) {
        pointsTF.setText(String.valueOf(points));
    }

    // Updates timer display
    public void updateTimer(int timeLeft) {
        if (timeLeft >= 0) {
            timerTF.setText(String.valueOf(timeLeft));
        } else {
            timerTF.setText("∞");
        }
    }

    // Handles game over - shows message and checks high scores
    public void gameOver(int points, boolean won) {
        // Check for high score
        if (HighScoreManager.getInstance().isHighScore(points, currentDifficulty)) {
            String playerName = JOptionPane.showInputDialog(this,
                    "NEW HIGH SCORE for " + currentDifficulty + "!\nYou scored " + points + " points!\nEnter your name:",
                    "🏆 HIGH SCORE! 🏆",
                    JOptionPane.QUESTION_MESSAGE);

            if (playerName != null && !playerName.trim().isEmpty()) {
                HighScoreManager.getInstance().addScore(playerName, points, currentDifficulty);
            } else if (playerName != null) {
                HighScoreManager.getInstance().addScore("Anonymous", points, currentDifficulty);
            }
        }

        String message = won ?
                "Congratulations! You survived!\nYou scored " + points + " points!" :
                "GAME OVER! You crashed!\nYou scored " + points + " points!";

        JOptionPane.showMessageDialog(this, message);
        returnToMenu();
    }

    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
}