import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel {
   
   // Game entities
   private CopyOnWriteArrayList<Barricade> barricades;  
   private CopyOnWriteArrayList<PowerUp> powerUps;
   private Runner run;
   private boolean gameOverOverlay;
   
   // Game state
   private GameWindow window;
   private int points;
   private int timeLeft;
   private boolean gameActive;
   private int baseSpeed;
   private int currentSpeed;
   
   // Power-up timers and states
   private boolean shieldActive = false;
   private float shieldTimer = 0f;
   private boolean slowmoActive = false;
   private float slowmoTimer = 0f;
   private int multiplier = 1;
   private float multiplierTimer = 0f;
   
   // Background scrolling
   private Image backgroundImage;
   private int backgroundY = 0;
   private int scrollSpeed = 2;
   
   // Game loop and spawning
   private Timer gameLoopTimer;
   private int powerUpSpawnCounter = 0;
   private static final int POWER_UP_SPAWN_RATE = 300;

   public GamePanel(GameWindow w) {
       window = w;
       points = 0;
       timeLeft = 30;
       gameActive = false;
       gameOverOverlay = false;
       
       barricades = new CopyOnWriteArrayList<>();
       powerUps = new CopyOnWriteArrayList<>();
       
       // Load background image
       try {
           backgroundImage = new ImageIcon("road.png.png").getImage();
           System.out.println("Background image loaded successfully");
       } catch (Exception e) {
           System.out.println("Could not load background image: " + e);
           backgroundImage = null;
       }
       
       setFocusable(true);

       // Main game loop timer (approx 30 FPS)
       gameLoopTimer = new Timer(33, e -> {
           if (gameActive) {
               updateGame();
               checkCollisions();
               spawnPowerUps();
           }
           repaint();
       });
   }
   
   // Update game world
   private void updateGame() {
       // Scroll background
       backgroundY += scrollSpeed;
       if (getHeight() > 0 && backgroundY >= getHeight()) {
           backgroundY = 0;
       }
       
       // Update power-up timers
       updatePowerUpTimers(0.033f);
       
       // Update and remove inactive barricades
       for (Barricade b : barricades) {
           b.update();
           if (!b.isActive()) {
               barricades.remove(b);
           }
       }
       
       // Update and remove inactive power-ups
       for (PowerUp p : powerUps) {
           p.update();
           if (!p.isActive()) {
               powerUps.remove(p);
           }
       }
   }
   
   // Check for collisions between runner and barricades
   private void checkCollisions() {
       if (run == null) return;
       
       for (Barricade b : barricades) {
           if (b.checkCollision()) {
               if (consumeShield()) {
                   b.deactivate();
                   addPoint();
               } else {
                   handleCollision();
               }
               break;
           }
       }
   }

   // Spawn power-ups at random intervals
   private void spawnPowerUps() {
       powerUpSpawnCounter++;
       if (powerUpSpawnCounter >= POWER_UP_SPAWN_RATE) {
           int randomX = 50 + (int)(Math.random() * 300);
           PowerUp p = new PowerUp(this, randomX, run);
           powerUps.add(p);
           powerUpSpawnCounter = 0;
       }
   }
   
   // Create initial game objects
   public void createGameEntities() {
       run = new Runner(this, 150, 350);
       barricades.clear(); 
       powerUps.clear();
       
       addBarricadeWithSpacing(50, 20);
       addBarricadeWithSpacing(200, 10);
       addBarricadeWithSpacing(300, 15);
   }
   
   // Add a barricade and ensure it doesn't overlap with existing ones
   private void addBarricadeWithSpacing(int xPos, int yPos) {
       Barricade b = new Barricade(this, xPos, yPos, run);
       b.adjustPosition(new ArrayList<>(barricades));
       barricades.add(b);
   }
   
   // Start the game with specified time and difficulty
   public void startGame(int time, String difficulty) {
       // Set base speed based on difficulty
       switch(difficulty) {
           case "Easy": baseSpeed = 3; break;
           case "Medium": baseSpeed = 5; break;
           case "Hard": baseSpeed = 8; break;
           case "Insane": baseSpeed = 12; break;
           default: baseSpeed = 5;
       }
       
       // Reset game state
       points = 0;
       gameActive = true;
       multiplier = 1;
       shieldActive = false;
       slowmoActive = false;
       backgroundY = 0;
       powerUpSpawnCounter = 0;
       currentSpeed = baseSpeed;
       
       timeLeft = time > 0 ? time : -1;
       
       window.updatePoints(points);
       window.updateTimer(timeLeft);
       
       createGameEntities();
       
       // Set initial speed for all barricades
       for (Barricade b : barricades) {
           b.setSpeed(baseSpeed);
       }
       
       gameLoopTimer.start();
       
       // Start countdown timer if time limit is set
       if (time > 0) {
           Timer countdownTimer = new Timer(1000, new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   if (gameActive) {
                       timeLeft--;
                       window.updateTimer(timeLeft);
                        
                       if (timeLeft <= 0) {
                           gameActive = false;
                           gameLoopTimer.stop();
                           ((Timer)e.getSource()).stop();
                           window.gameOver(points, true);
                       }
                   }
               }
           });
           countdownTimer.start();
       }
       
       SoundManager.getInstance().playClip("background", true);
       repaint();
   }
   
   // Stop the game
   public void stopGame() {
       gameActive = false;
       gameLoopTimer.stop();
       SoundManager.getInstance().stopClip("background");
   }
   
   // Update power-up timers and deactivate when expired
   private void updatePowerUpTimers(float delta) {
       if (shieldActive) {
           shieldTimer -= delta;
           if (shieldTimer <= 0f) {
               shieldActive = false;
               shieldTimer = 0f;
           }
       }

       if (slowmoActive) {
           slowmoTimer -= delta;
           if (slowmoTimer <= 0f) {
               slowmoActive = false;
               slowmoTimer = 0f;
               for (Barricade b : barricades) {
                   b.setSpeed(currentSpeed);
               }
           }
       }

       if (multiplier > 1) {
           multiplierTimer -= delta;
           if (multiplierTimer <= 0f) {
               multiplier = 1;
               multiplierTimer = 0f;
           }
       }
   }
   
   // Apply power-up effects
   public void activatePowerUp(int type) {
       switch(type) {
           case PowerUp.SHIELD:
               shieldActive = true;
               shieldTimer += 5f;
               break;
           case PowerUp.SLOW:
               slowmoActive = true;
               slowmoTimer += 5f;
               int slowed = Math.max(1, currentSpeed / 2);
               for (Barricade b : barricades) {
                   b.setSpeed(slowed);
               }
               break;
           case PowerUp.EXTRA_TIME:
               if (timeLeft < 0) {
                   addPoint();
                   addPoint();
                   addPoint();
               } else {
                   timeLeft += 10;
                   window.updateTimer(timeLeft);
               }
               break;
           case PowerUp.MULTIPLY:
               multiplier = 2;
               multiplierTimer += 10f;
               break;
       }
   }

   // Move the runner left or right
   public void updateGameEntities(int direction) {
       if (run == null || !gameActive) return;
       run.move(direction);
   }
   
   // Draw all game graphics
   @Override
   protected void paintComponent(Graphics g) {
       super.paintComponent(g);
       Graphics2D g2 = (Graphics2D) g;
       
       // Draw scrolling background
       if (backgroundImage != null) {
           g2.drawImage(backgroundImage, 0, backgroundY, getWidth(), getHeight(), null);
           g2.drawImage(backgroundImage, 0, backgroundY - getHeight(), getWidth(), getHeight(), null);
       } else {
           g2.setColor(new Color(30, 30, 30));
           g2.fillRect(0, 0, getWidth(), getHeight());
       }
       
       drawPowerUpStatus(g2);
       
       // Draw game objects
       for (Barricade b : barricades) {
           b.draw(g2);
       }
      
       for (PowerUp p : powerUps) {
           p.draw(g2);
       }
       
       // Draw runner with shield effect if active
       if (run != null) {
           if (shieldActive) {
               g2.setColor(new Color(0, 255, 255, 80));
               g2.setStroke(new BasicStroke(3));
               g2.drawOval(run.getX() - 8, run.getY() - 8, 66, 66);
           }
           run.draw(g2);
       }

       // Draw game over overlay if needed
       if (gameOverOverlay) {
           g2.setColor(new Color(0, 0, 0, 150));
           g2.fillRect(0, 0, getWidth(), getHeight());
       }
   }
   
   // Draw active power-up status text
   private void drawPowerUpStatus(Graphics2D g2) {
       g2.setFont(new Font("Arial", Font.BOLD, 14));
       
       int yPos = 30;
       
       if (shieldActive) {
           g2.setColor(Color.CYAN);
           g2.drawString("🛡️ SHIELD: " + (int)Math.ceil(shieldTimer) + "s", 15, yPos);
           yPos += 25;
       }
       
       if (slowmoActive) {
           g2.setColor(new Color(100, 150, 255));
           g2.drawString("⏱️ SLOWMO: " + (int)Math.ceil(slowmoTimer) + "s", 15, yPos);
           yPos += 25;
       }
       
       if (multiplier > 1) {
           g2.setColor(Color.YELLOW);
           g2.drawString("✖️ " + multiplier + "x (" + (int)Math.ceil(multiplierTimer) + "s)", 15, yPos);
       }
   }
   
   // Add points and adjust difficulty
   public void addPoint() {
       if (gameActive) {
           points += multiplier;
           window.updatePoints(points);
           
           // Increase speed based on points
           currentSpeed = baseSpeed + (points / 15);
           if (!slowmoActive) {
               for (Barricade b : barricades) {
                   b.setSpeed(currentSpeed);
               }
           }
           
           // Add new barricade every 30 points
           if (points % 30 == 0 && points > 0) {
               addNewBarricade();
           }
       }
   }
   
   // Add a new barricade at the top of the screen
   private void addNewBarricade() {
       int randomX = 50 + (int)(Math.random() * 300);
       Barricade newB = new Barricade(this, randomX, 5, run);
       newB.adjustPosition(new ArrayList<>(barricades));
       newB.setSpeed(currentSpeed);
       barricades.add(newB);
   }
   
   // Handle collision with barricade
   public void handleCollision() {
       if (gameActive) {
           gameActive = false;
           gameOverOverlay = true;
           repaint();
           gameLoopTimer.stop();
           SoundManager.getInstance().playClip("hit", false);
           window.gameOver(points, false);
       }
   }

   // Consume shield if active
   public boolean consumeShield() {
       if (shieldActive) {
           shieldActive = false;
           shieldTimer = 0f;
           return true;
       }
       return false;
   }

   // Getters
   public boolean isGameActive() { return gameActive; }
   public Runner getRunner() { return run; }
   public CopyOnWriteArrayList<Barricade> getBarricades() { return barricades; }
}