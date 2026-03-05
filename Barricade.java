import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Barricade {

    private double speedMultiplier = 1.0;
    private JPanel panel;
    private int x;
    private int y;
    private int startY;
    private int height;
    private int width;
    private Random random;
    private boolean active;

    private Rectangle2D.Double barr1;    

    private int dx;        
    private int dy; 
    private int movePattern;
    private int direction = 1;
    private int patrolCount = 0;

    private Runner run;
    private boolean pointAwarded;
    
    private float pulse = 0f;
    private boolean pulseUp = true;
    private int originalDx;
    private int originalDy;

    // Constructor - sets up barricade with random movement pattern
    public Barricade(JPanel p, int xPos, int yPos, Runner runner) {
        this.panel = p;
        this.run = runner;
        this.x = xPos;
        this.y = yPos;
        this.startY = yPos;
        this.active = true;
        
        random = new Random();
        movePattern = random.nextInt(4);
        
        switch(movePattern) {
            case 0: dy = 5; dx = 0; break;
            case 1: dy = 4; dx = 2; break;
            case 2: dy = 8; dx = 0; break;
            case 3: dy = 2; dx = 1; break;
        }
        
        width = 35;
        height = 50;
        pointAwarded = false;
        originalDx = dx;
        originalDy = dy;
    }
    
    // Moves barricade if it's too close to others when first created
    public void adjustPosition(List<Barricade> others) {
        int attempts = 0;
        while (isTooCloseToOthers(others) && attempts < 20) {
            this.x = 30 + random.nextInt(panel.getWidth() - width - 60);
            attempts++;
        }
    }
    
    // Checks if this barricade is too close to any other
    private boolean isTooCloseToOthers(List<Barricade> others) {
        for (Barricade b : others) {
            if (b == this) continue;
            int dist = Math.abs(this.x - b.x);
            if (dist < 50) {
                return true;
            }
        }
        return false;
    }
   
    // Main update - called every frame to move barricade
    public void update() {
        if (!active) return;
        
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // Pulse animation for barricade size
        if (pulseUp) {
            pulse += 0.05f;
            if (pulse > 1.0f) pulseUp = false;
        } else {
            pulse -= 0.05f;
            if (pulse < 0.0f) pulseUp = true;
        } 

        // Move based on pattern type
        switch(movePattern) {
            case 0: x += dx; y += dy; break;
            case 1: 
                x += dx * direction; 
                y += dy;
                if (x <= 0 || x >= panelWidth - width) direction *= -1;
                break;
            case 2: y += dy + 3; break;
            case 3:
                x += dx * direction;
                y += dy;
                patrolCount++;
                if (patrolCount > 30) {
                    direction *= -1;
                    patrolCount = 0;
                }
                if (y < 20) y = 20;
                break;
        }

        // Reset if off bottom of screen
        if (y > panelHeight) {
            resetPosition(panelWidth, panelHeight);
        }
    }
    
    // Sends barricade back to top and gives point
    private void resetPosition(int panelWidth, int panelHeight) {
        y = startY;
        if (panel instanceof GamePanel) {
            GamePanel gp = (GamePanel) panel;
            int newX;
            int attempts = 0;
            do {
                newX = 30 + random.nextInt(panelWidth - width - 60);
                attempts++;
                if (attempts > 30) break;
            } while (tooCloseToOthers(newX, gp.getBarricades()));
            x = newX;
        } else {
            x = random.nextInt(panelWidth - width);
        }

        if (!pointAwarded && panel instanceof GamePanel) {
            ((GamePanel)panel).addPoint();
            pointAwarded = true;
        }
        pointAwarded = false;
    }
    
    // Checks if new X position is too close to other barricades
    private boolean tooCloseToOthers(int newX, List<Barricade> others) {
        for (Barricade b : others) {
            if (b == this) continue;
            if (Math.abs(newX - b.x) < 50) return true;
        }
        return false;
    }
    
    // Returns true if barricade is touching runner
    public boolean checkCollision() {
        if (run == null || !active) return false;       
        Rectangle2D.Double myRect = getBoundingRectangle(); 
        Rectangle2D.Double runRect = run.getBoundingRectangle();  
        return myRect.intersects(runRect);
    }

    // Changes how fast barricade moves
    public void setSpeed(int newDy) {
        double speedRatio = (double) newDy / originalDy;
        dy = newDy;
        dx = (int) (originalDx * speedRatio);
    }
   
    // Returns whether barricade is still active
    public boolean isActive() { return active; }
    
    // Turns off barricade (removes from game)
    public void deactivate() { active = false; }
    
    // Gets rectangle used for collision detection
    public Rectangle2D.Double getBoundingRectangle() {
        return new Rectangle2D.Double (x + 5, y + 5, width - 10, height - 10);
    }
   
    // Draws barricade on screen
    public void draw(Graphics2D g2) {
        if (!active) return;
        
        Color mainColor;
        switch(movePattern) {
            case 0: mainColor = Color.ORANGE; break;
            case 1: mainColor = new Color(255, 200, 0); break;
            case 2: mainColor = Color.RED; break;
            case 3: mainColor = new Color(0, 150, 0); break;
            default: mainColor = Color.ORANGE;
        }

        int pulseOffset = (int)(pulse * 3);

        g2.setColor(mainColor);
        g2.fillRect(x, y, 30 + pulseOffset, 35);

        g2.setColor(Color.WHITE);
        g2.fillRect(x, y + 8, 30 + pulseOffset, 3);
        g2.fillRect(x, y + 20, 30 + pulseOffset, 3);
        g2.fillRect(x, y + 32, 30 + pulseOffset, 3);

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x - 3, y + 40, 33 + pulseOffset, 5);
        g2.fillRect(x - 3, y + 50, width + 1 + pulseOffset, 5);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 8));
        String label = movePattern == 0 ? "NORMAL" : 
                      movePattern == 1 ? "ZIGZAG" : 
                      movePattern == 2 ? "FAST" : "PATROL";
        g2.drawString(label, x + 2, y - 2);

        barr1 = new Rectangle2D.Double(x, y, 25 + pulseOffset, 35);
    }
}

  