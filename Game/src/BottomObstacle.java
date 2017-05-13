/**
 * Created by mateusz on 12.05.17.
 */
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class BottomObstacle {
    private Image bottomObstacle;
    private int locationX = 0, locationY = 0;

    public BottomObstacle(int initialWidth, int initialHeight) {
        bottomObstacle = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/bottom_obstacle.png"));
        scaleBottomObstacle(initialWidth, initialHeight);
    }

    public void scaleBottomObstacle(int width, int height) {
        bottomObstacle = bottomObstacle.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    public Image getObstacleImage() {
        return bottomObstacle;
    }

    public int getWidth() {
        return bottomObstacle.getWidth(null);
    }

    public int getHeight() {
        return bottomObstacle.getHeight(null);
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
        return (new Rectangle(locationX, locationY, bottomObstacle.getWidth(null), bottomObstacle.getHeight(null)));
    }

    public BufferedImage getBI() {
        BufferedImage bi = new BufferedImage(bottomObstacle.getWidth(null), bottomObstacle.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.drawImage(bottomObstacle, 0, 0, null);
        g.dispose();
        return bi;
    }
}
