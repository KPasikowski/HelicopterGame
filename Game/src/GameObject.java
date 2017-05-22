/**
 * Created by mateusz on 22.05.17.
 */

import java.awt.*;
import java.awt.image.BufferedImage;


public class GameObject {
    private Image image;
    private String imageLocation;
    private int locationX = 0, locationY = 0;
    public GameObject(int initialWidth, int initialHeight, String imageLocation) {
        this.imageLocation = imageLocation;
        image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(imageLocation));
        scale(initialWidth, initialHeight);
    }

    public void scale(int width, int height) {
        image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public Image getImage() {
        return image;
    }

    public int getWidth() {
        try {
            return image.getWidth(null);
        }
        catch(Exception e) {
            return -1;
        }
    }

    public int getHeight() {
        try {
            return image.getHeight(null);
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
        return (new Rectangle(locationX, locationY, image.getWidth(null), image.getHeight(null)));
    }

    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bi;
    }
}
