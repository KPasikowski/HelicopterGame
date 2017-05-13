/**
 * Created by mateusz on 12.05.17.
 */
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Helicopter {
    private Image helicopterImage;
    private int locationX = 0, locationY = 0;

    public Helicopter(int initialWidth, int initialHeight) {
        helicopterImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/helicopter.png"));
        scaleHelicopter(initialWidth, initialHeight);
    }

    public void scaleHelicopter(int width, int height) {
        helicopterImage = helicopterImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public Image getHelicopterImage() {
        return helicopterImage;
    }

    public int getWidth() {
        try {
            return helicopterImage.getWidth(null);
        }
        catch(Exception e) {
            return -1;
        }
    }

    public int getHeight() {
        try {
            return helicopterImage.getHeight(null);
        }
        catch(Exception e) {
            return -1;
        }
    }

    public void setX(int x) {
        locationX = x;
    }

    public int getX() {
        return locationX;
    }

    public void setY(int y) {
        locationY = y;
    }

    public int getY() {
        return locationY;
    }

    public Rectangle getRectangle() {
        return (new Rectangle(locationX, locationY, helicopterImage.getWidth(null), helicopterImage.getHeight(null)));
    }

    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(helicopterImage.getWidth(null), helicopterImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(helicopterImage, 0, 0, null);
        g.dispose();
        return bi;
    }
}
