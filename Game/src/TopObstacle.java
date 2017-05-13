/**
 * Created by mateusz on 12.05.17.
 */
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class TopObstacle {
    private Image topObstacle;
    private int locationX = 0, locationY = 0;

    public TopObstacle(int initialWidth, int initialHeight) {
        topObstacle = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/top_obstacle.png"));
        scaleTopObstacle(initialWidth, initialHeight);
    }

    public void scaleTopObstacle(int width, int height) {
        topObstacle = topObstacle.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public Image getObstacleImage() {
        return topObstacle;
    }

    public int getWidth() {
        return topObstacle.getWidth(null);
    }

    public int getHeight() {
        return topObstacle.getHeight(null);
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
        return (new Rectangle(locationX, locationY, topObstacle.getWidth(null), topObstacle.getHeight(null)));
    }

    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(topObstacle.getWidth(null), topObstacle.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(topObstacle, 0, 0, null);
        g.dispose();
        return bi;
    }
}
