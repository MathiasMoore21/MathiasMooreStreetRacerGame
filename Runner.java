import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.awt.Image;
import java.util.ArrayList;

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
   
   // Animation frames
   private ArrayList<Image> leftFrames;
   private ArrayList<Image> rightFrames;
   private Image currentImage;
   
   // Animation timing
   private int currentFrame;
   private int animationDelay;
   private int animationCounter;
   private boolean moving;
   private int currentDirection;
   
   // Bounce effect when moving
   private float bounceY = 0f;
   private boolean bounceUp = true;
   private int moveCounter = 0;

   public Runner (JPanel p, int xPos, int yPos) {
      panel = p;
      dimension = panel.getSize();

      backgroundColour = panel.getBackground();
      x = xPos;
      y = yPos;

      dx = 20;    
      dy = 20;    

      width = 50;
      height = 50;

      leftFrames = new ArrayList<>();
      rightFrames = new ArrayList<>();
      
      loadAnimations();
      
      currentFrame = 0;
      animationDelay = 3;
      animationCounter = 0;
      moving = false;
      currentDirection = 2;
      
      if (rightFrames.size() > 0) {
          currentImage = rightFrames.get(0);
      }
   }
   
   private void loadAnimations() {
       leftFrames.add(ImageManager.loadImage("runnerleft1.png"));
       leftFrames.add(ImageManager.loadImage("runnerleft2.png"));
       leftFrames.add(ImageManager.loadImage("runnerleft3.png"));
       
       rightFrames.add(ImageManager.loadImage("runnerright1.png"));
       rightFrames.add(ImageManager.loadImage("runnerright2.png"));
       rightFrames.add(ImageManager.loadImage("runnerright3.png"));
   }

   public void draw(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      
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
      
      updateAnimation();
      
      int shadowWidth = width - 10;
      int shadowHeight = 5;
      g2.setColor(new Color(0, 0, 0, 100));
      g2.fillOval(x + 5, y + height - 5 + (int)(bounceY/2), shadowWidth, shadowHeight);
      
      g2.drawImage(currentImage, x, y + (int)bounceY, width, height, null);
   }
   
   private void updateAnimation() {
      animationCounter++;
      if (animationCounter >= animationDelay) {
         animationCounter = 0;
         currentFrame++;
         
         ArrayList<Image> currentAnimation;
         
         if (moving) {
            if (currentDirection == 1) {
               currentAnimation = leftFrames;
            } else {
               currentAnimation = rightFrames;
            }
         } else {
            if (currentDirection == 2 && rightFrames.size() > 0) {
                currentImage = rightFrames.get(0);
            } else if (leftFrames.size() > 0) {
                currentImage = leftFrames.get(0);
            }
            return;
         }
         
         if (currentFrame >= currentAnimation.size()) {
            currentFrame = 0;
         }
         
         currentImage = currentAnimation.get(currentFrame);
      }
   }

   public void move (int direction) {
      if (!panel.isVisible()) return;
      
      dimension = panel.getSize();
      
      moving = true;
      moveCounter = 10;
      bounceY = 0;
      bounceUp = true;
      currentDirection = direction;

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

   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double(x, y, width, height);
   }
   
   public int getX() { return x; }
   public int getY() { return y; }
}