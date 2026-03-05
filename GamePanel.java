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
   
   private CopyOnWriteArrayList<Barricade> barricades;  
   private CopyOnWriteArrayList<PowerUp> powerUps;
   private Runner run;
   
   private GameWindow window;
   private int points;
   private int timeLeft;
   private boolean gameActive;
   private int baseSpeed;
   
   // Power-up effects
   private boolean shieldActive = false;
   private float shieldTimer = 0f;
   private boolean slowmoActive = false;
   private float slowmoTimer = 0f;
   private int multiplier = 1;
   private float multiplierTimer = 0f;
   
   // Background
   private Image backgroundImage;
   private int backgroundY = 0;
   private int scrollSpeed = 2;
   
   
   private Timer gameLoopTimer;
   private int powerUpSpawnCounter = 0;
   private static final int POWER_UP_SPAWN_RATE = 300; // ~10 seconds at 33ms

   // Constructor - sets up game panel
   public GamePanel(GameWindow w) {
       window = w;
       points = 0;
       timeLeft = 30;
       gameActive = false;
       
       barricades = new CopyOnWriteArrayList<>();
       powerUps = new CopyOnWriteArrayList<>();
       
       try {
           backgroundImage = new ImageIcon("road.png.png").getImage();
           System.out.println("Background image loaded successfully");
       } catch (Exception e) {
           System.out.println("Could not load background image: " + e);
           backgroundImage = null;
       }
       
       setFocusable(true);

       // Main game loop timer - runs everything
       gameLoopTimer = new Timer(33, e -> {
           if (gameActive) {
               updateGame();
               checkCollisions();
               spawnPowerUps();
           }
           repaint();
       });
   }
   
   // Updates all game objects positions
   private void updateGame() {
       // Update background
       backgroundY += scrollSpeed;
       if (getHeight() > 0 && backgroundY >= getHeight()) {
           backgroundY = 0;
       }
       
       // Update power-up timers
       updatePowerUpTimers(0.033f); // 33ms in seconds
       
       // Update all barricades
       for (Barricade b : barricades) {
           b.update();
           if (!b.isActive()) {
               barricades.remove(b);
           }
       }
       
       // Update all power-ups
       for (PowerUp p : powerUps) {
           p.update();
           if (!p.isActive()) {
               powerUps.remove(p);
           }
       }
   }
   
   // Checks if barricades hit runner
   private void checkCollisions() {
    if (run == null) return;
    
    for (Barricade b : barricades) {
        if (b.checkCollision()) {
            // First try to consume shield
            if (consumeShield()) {
                // Shield blocked it! Remove this barricade
                b.deactivate();
                addPoint(); // Bonus point for blocking
                System.out.println("Shield blocked barricade!");
            } else {
                // No shield - game over
                handleCollision();
            }
            break; // Only handle one collision per frame
        }
    }
}
   // Randomly spawns power-ups over time
   private void spawnPowerUps() {
       powerUpSpawnCounter++;
       if (powerUpSpawnCounter >= POWER_UP_SPAWN_RATE) {
           int randomX = 50 + (int)(Math.random() * 300);
           PowerUp p = new PowerUp(this, randomX, run);
           powerUps.add(p);
           powerUpSpawnCounter = 0;
       }
   }
   
   // Creates initial barricades at start of game
   public void createGameEntities() {
       run = new Runner(this, 150, 350);
       barricades.clear(); 
       powerUps.clear();
       
       addBarricadeWithSpacing(50, 20);
       addBarricadeWithSpacing(200, 10);
       addBarricadeWithSpacing(300, 15);
   }
   
   // Helper to add barricades without overlapping
   private void addBarricadeWithSpacing(int xPos, int yPos) {
       Barricade b = new Barricade(this, xPos, yPos, run);
       b.adjustPosition(new ArrayList<>(barricades));
       barricades.add(b);
   }
   
   // Starts the game with chosen difficulty and time
   public void startGame(int time, String difficulty) {
       switch(difficulty) {
           case "Easy": baseSpeed = 3; break;
           case "Medium": baseSpeed = 5; break;
           case "Hard": baseSpeed = 8; break;
           case "Insane": baseSpeed = 12; break;
           default: baseSpeed = 5;
       }
       
       points = 0;
       gameActive = true;
       multiplier = 1;
       shieldActive = false;
       slowmoActive = false;
       backgroundY = 0;
       powerUpSpawnCounter = 0;
       
       timeLeft = time > 0 ? time : -1;
       
       window.updatePoints(points);
       window.updateTimer(timeLeft);
       
       createGameEntities();
       
       // Set initial speeds
       for (Barricade b : barricades) {
           b.setSpeed(baseSpeed);
       }
       
       // Start the game loop
       gameLoopTimer.start();
       
       // Start timer for countdown if not endless
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
   
   // Stops the game
   public void stopGame() {
       gameActive = false;
       gameLoopTimer.stop();
       SoundManager.getInstance().stopClip("background");
   }
   
   // Updates timers for all active power-ups
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
              int restored = baseSpeed + (points / 5);
              for (Barricade b : barricades) {
                  b.setSpeed(restored);
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
   
   // Called when player collects a power-up
   public void activatePowerUp(int type) {
       switch(type) {
           case 0:
               shieldActive = true;
               shieldTimer += 5f;
               break;
           case 1:
               slowmoActive = true;
               slowmoTimer += 5f;
               for (Barricade b : barricades) {
                   int slowed = Math.max(1, (baseSpeed + (points / 5)) * 3 / 4);
                   b.setSpeed(slowed);
               }
               break;
           case 2:
               if (timeLeft < 0) {
                   addPoint();
                   addPoint();
                   addPoint();
               } else {
                   timeLeft += 10;
                   window.updateTimer(timeLeft);
               }
               break;
           case 3:
               multiplier = 2;
               multiplierTimer += 10f;
               break;
       }
       System.out.println("Power-up activated! Type: " + type);
   }

   // Moves runner based on key presses
   public void updateGameEntities(int direction) {
       if (run == null || !gameActive) return;
       run.move(direction);
   }
   
   // Draws everything on screen
   @Override
   protected void paintComponent(Graphics g) {
       super.paintComponent(g);
       Graphics2D g2 = (Graphics2D) g;
       
       // Draw background
       if (backgroundImage != null) {
           g2.drawImage(backgroundImage, 0, backgroundY, getWidth(), getHeight(), null);
           g2.drawImage(backgroundImage, 0, backgroundY - getHeight(), getWidth(), getHeight(), null);
       } else {
           g2.setColor(new Color(30, 30, 30));
           g2.fillRect(0, 0, getWidth(), getHeight());
       }
       
       // Draw power-up statuses
       drawPowerUpStatus(g2);
       
       // Draw barricades
       for (Barricade b : barricades) {
           b.draw(g2);
       }
      
       // Draw power-ups
       for (PowerUp p : powerUps) {
           p.draw(g2);
       }
        
       // Draw runner
       if (run != null) {
           if (shieldActive) {
               g2.setColor(new Color(0, 255, 255, 80));
               g2.setStroke(new BasicStroke(3));
               g2.drawOval(run.getX() - 8, run.getY() - 8, 66, 66);
           }
           run.draw(g2);
       }
   }
   
   // Shows active power-ups and their timers
   private void drawPowerUpStatus(Graphics2D g2) {
       g2.setFont(new Font("Arial", Font.BOLD, 14));
       
       int yPos = 30;
       
       if (shieldActive) {
           g2.setColor(Color.CYAN);
           int displayShield = (int)Math.ceil(shieldTimer);
           g2.drawString("🛡️ SHIELD: " + displayShield + "s", 15, yPos);
           yPos += 25;
       }
       
       if (slowmoActive) {
           g2.setColor(new Color(100, 150, 255));
           int displaySlow = (int)Math.ceil(slowmoTimer);
           g2.drawString("⏱️ SLOWMO: " + displaySlow + "s", 15, yPos);
           yPos += 25;
       }
       
       if (multiplier > 1) {
           g2.setColor(Color.YELLOW);
           int displayMult = multiplier;
           int displayMultTime = (int)Math.ceil(multiplierTimer);
           g2.drawString("✖️ " + displayMult + "x (" + displayMultTime + "s)", 15, yPos);
       }
   }
   
   // Adds point and checks for speed increases/new barricades
   public void addPoint() {
       if (gameActive) {
           points += multiplier;
           window.updatePoints(points);
           
           int speed = baseSpeed + (points / 15);
           if (!slowmoActive) {
               for (Barricade b : barricades) {
                   b.setSpeed(speed);
               }
           }
           
           if (points % 30 == 0 && points > 0) {
               addNewBarricade();
           }
       }
   }
   
   // Creates a new barricade at top of screen
   private void addNewBarricade() {
       int randomX = 50 + (int)(Math.random() * 300);
       Barricade newB = new Barricade(this, randomX, 5, run);
       newB.adjustPosition(new ArrayList<>(barricades));
       newB.setSpeed(baseSpeed + (points / 5));
       barricades.add(newB);
   }
   
   // Game over - called when player hits barricade with no shield
   public void handleCollision() {
       
       if (gameActive) {
           gameActive = false;
           gameLoopTimer.stop();
           SoundManager.getInstance().playClip("hit", false);
           window.gameOver(points, false);
       }
   }
   
   // Uses shield to block a hit - returns true if shield was active
   public boolean consumeShield() {
    if (shieldActive) {
        shieldActive = false;  // Shield is GONE after blocking
        shieldTimer = 0f;      // Reset timer
        System.out.println("Shield consumed to block hit!");
        return true;
    }
    return false;
}

   public boolean isGameActive() { return gameActive; }
   public Runner getRunner() { return run; }
   public CopyOnWriteArrayList<Barricade> getBarricades() { return barricades; }


}