import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;


public class Runner {

   private JPanel panel;
   private volatile int x;
   private volatile int y;
   private int width;
   private int height;

   private int dx;
   private int dy;

   private Color backgroundColour;
   private Dimension dimension;
   private Image runnerimage;
   
   // Animation variables
   private int frame = 0;
   private boolean moving = false;
   private int moveCounter = 0;
   private float bounceY = 0f;
   private boolean bounceUp = true;

   // Constructor - sets up runner at starting position
   public Runner (JPanel p, int xPos, int yPos) {
      panel = p;
      dimension = panel.getSize();

      backgroundColour = panel.getBackground ();
      x = xPos;
      y = yPos;

      dx = 20;    
      dy = 20;    

      width = 50;
      height = 50;

      runnerimage = ImageManager.loadImage("Runner.png");
   }

   // Draws runner with bounce animation when moving
   public void draw(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      
      // Update bounce if moving
      if (moving) {
          if (bounceUp) {
              bounceY += 0.3f;
              if (bounceY > 3) bounceUp = false;
          } else {
              bounceY -= 0.3f;
              if (bounceY < -3) bounceUp = true;
          }
          moveCounter--;
          if (moveCounter <= 0) {
              moving = false;
              bounceY = 0;
          }
      }
      
      // Draw shadow (size varies with bounce)
      int shadowWidth = width - 10;
      int shadowHeight = 5;
      g2.setColor(new Color(0, 0, 0, 100));
      g2.fillOval(x + 5, y + height - 5 + (int)(bounceY/2), shadowWidth, shadowHeight);
      
      // Draw runner with bounce offset
      g2.drawImage(runnerimage, x, y + (int)bounceY, width, height, null);
   }

   // Moves runner in specified direction (1=left,2=right,3=down,4=up)
   public void move (int direction) {
      if (!panel.isVisible ()) return;
      
      dimension = panel.getSize();
      
      moving = true;
      moveCounter = 10;
      bounceY = 0;
      bounceUp = true;

      if (direction == 1) {    
          x = x - dx;
          if (x < 0) x = 0;
      }
      else if (direction == 2) {      
          x = x + dx;
          if (x + width > dimension.width)
              x = dimension.width - width;
      }
      else if(direction == 3) {  
          y = y + dy;
          if (y + height > dimension.height)
              y = dimension.height - height;
      }
      else if(direction == 4) {    
          y = y - dy;
          if(y < 0) y = 0;
      }
   }

   // Returns rectangle for collision detection
   public Rectangle2D.Double getBoundingRectangle() {
      // Use actual y position without bounce for collision
      return new Rectangle2D.Double(x, y, width, height);
   }
   
   public int getX() { return x; }
   public int getY() { return y; }
}