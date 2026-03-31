import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.*;

public class PowerUp {
    
    // Power-up type constants 
    public static final int SHIELD   = 0;
    public static final int SLOW     = 1;
    public static final int EXTRA_TIME = 2;
    public static final int MULTIPLY = 3;

    // Position and movement
    private JPanel panel;
    private int x;
    private int y;
    private int width;
    private int height;
    private int dy;
    private boolean active;
    private int type;
    private Runner run;
    
    // Animation effects
    private Image powerImage;
    private float angle = 0f;
    private float scale = 1f;
    private boolean scaleUp = true;

    public PowerUp(JPanel p, int xPos, Runner runner) {
        this.panel = p;
        this.run = runner;
        this.x = xPos;
        this.y = 10;
        this.active = true;
        
        width = 40;
        height = 40;
        dy = 4;
        
        // Randomly select power-up type
        type = new Random().nextInt(4);
        loadImage();
    }
    
    // Load the appropriate image based on power-up type
    private void loadImage() {
        switch(type) {
            case SHIELD:     powerImage = ImageManager.loadImage("powershield.png"); break;
            case SLOW:       powerImage = ImageManager.loadImage("powerslow.png");   break;
            case EXTRA_TIME: powerImage = ImageManager.loadImage("powerpoints.png");   break;
            case MULTIPLY:   powerImage = ImageManager.loadImage("powermultiply.png"); break;
        }
    }
    
    // Update power-up position and check collisions
    public void update() {
        if (!active) return;
        
        y += dy;
        
        Rectangle2D.Double powerRect = new Rectangle2D.Double(x, y, width, height);
        Rectangle2D.Double runRect = run.getBoundingRectangle();
        
        // Check collision with barricades - reposition if colliding
        if (panel instanceof GamePanel) {
            for (Barricade b : ((GamePanel)panel).getBarricades()) {
                if (powerRect.intersects(b.getBoundingRectangle())) {
                    x = 50 + (int)(Math.random() * 300);
                    y = 10;
                    return;
                }
            }
        }
        
        // Check collision with runner
        if (powerRect.intersects(runRect)) {
            if (panel instanceof GamePanel) {
                ((GamePanel)panel).activatePowerUp(type);
            }
            active = false;
        }
        
        // Remove if off screen
        if (y > panel.getHeight()) {
            active = false;
        }
    }
    
    // Draw the power-up with rotating and scaling animation
    public void draw(Graphics2D g2) {
        if (!active) return;
        
        // Update rotation angle
        angle += 0.05f;
        
        // Update scale (pulsing effect)
        if (scaleUp) {
            scale += 0.02f;
            if (scale > 1.15f) scaleUp = false;
        } else {
            scale -= 0.02f;
            if (scale < 0.85f) scaleUp = true;
        }

        // Apply transformations
        AffineTransform old = g2.getTransform();
        
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        g2.scale(scale, scale);
        g2.translate(-centerX, -centerY);

        // Draw the image
        if (powerImage != null) {
            g2.drawImage(powerImage, x, y, width, height, null);
        }

        // Restore original transform
        g2.setTransform(old);
    }
    
    // Getters
    public boolean isActive() { return active; }
    public int getType() { return type; }
}