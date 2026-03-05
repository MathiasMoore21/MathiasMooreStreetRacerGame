import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;

public class PowerUp {
    private JPanel panel;
    private int x;
    private int y;
    private int width;
    private int height;
    private int dy;
    private boolean active;
    private int type;
    private Color color;
    private String symbol;
    private Runner run;
    
    private float angle = 0f;
    private float scale = 1f;
    private boolean scaleUp = true;

    // Constructor - sets up power-up with random type
    public PowerUp(JPanel p, int xPos, Runner runner) {
        this.panel = p;
        this.run = runner;
        this.x = xPos;
        this.y = 10;
        this.active = true;
        
        width = 20;
        height = 20;
        dy = 4;
        
        type = new Random().nextInt(4);
        
        switch(type) {
            case 0: color = Color.CYAN; symbol = "🛡️"; break;
            case 1: color = Color.BLUE; symbol = "⏱️"; break;
            case 2: color = Color.GREEN; symbol = "⏰"; break;
            case 3: color = Color.YELLOW; symbol = "✖️"; break;
        }
    }
    
    // Called every frame - moves power-up and checks collection
    public void update() {
        if (!active) return;
        
        y += dy;
        
        Rectangle2D.Double powerRect = new Rectangle2D.Double(x, y, width, height);
        Rectangle2D.Double runRect = run.getBoundingRectangle();
        
        // Check if too close to barricades and reposition if needed
        if (panel instanceof GamePanel) {
            for (Barricade b : ((GamePanel)panel).getBarricades()) {
                if (powerRect.intersects(b.getBoundingRectangle())) {
                    x = 50 + (int)(Math.random() * 300);
                    y = 10;
                    return;
                }
            }
        }
        
        // Check if runner collected it
        if (powerRect.intersects(runRect)) {
            if (panel instanceof GamePanel) {
                ((GamePanel)panel).activatePowerUp(type);
            }
            active = false;
        }
        
        // Remove if it fell off bottom of screen
        if (y > panel.getHeight()) {
            active = false;
        }
    }
    
    // Draws spinning power-up on screen
    public void draw(Graphics2D g2) {
        if (!active) return;
        
        angle += 0.1f;
        if (scaleUp) {
            scale += 0.02f;
            if (scale > 1.2f) scaleUp = false;
        } else {
            scale -= 0.02f;
            if (scale < 0.8f) scaleUp = true;
        }

        AffineTransform old = g2.getTransform();
        
        int centerX = x + width/2;
        int centerY = y + height/2;
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        g2.scale(scale, scale);
        g2.translate(-centerX, -centerY);

        g2.setColor(color);
        g2.fillOval(x, y, width, height);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y, width, height);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(symbol, x + 3, y + 15);

        g2.setTransform(old);
    }
    
    // Returns whether power-up is still on screen
    public boolean isActive() { return active; }
}