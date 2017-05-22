/**
 * Created by mateusz on 12.05.17.
 */

import javax.swing.*;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;


public class GameScreen extends JPanel {
    private int screenWidth, screenHeight;
    private boolean isMenu = true;
    private int score = 0;
    private String message = "Helicopter Game";
    private Font font = new Font("Calibri", Font.BOLD, 56);
    private int messageWidth = 0, scoreWidth = 0;
    private BottomObstacle botObstacle1, botObstacle2;
    private TopObstacle topObstacle1, topObstacle2;
    private Helicopter helicopter;

    public GameScreen(int screenWidth, int screenHeight, boolean isMenu) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.isMenu = isMenu;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, screenWidth, screenHeight*7/8);
        g.setColor(new Color(159, 25, 25));
        g.fillRect(0, screenHeight*7/8, screenWidth, screenHeight/8);
        g.setColor(Color.BLACK);
        g.drawLine(0, screenHeight*7/8, screenWidth, screenHeight*7/8);

        if (botObstacle1 != null && botObstacle2 != null && topObstacle1 != null && topObstacle2 != null) {
            g.drawImage(botObstacle1.getImage(), botObstacle1.getX(), botObstacle1.getY(), null);
            g.drawImage(botObstacle2.getImage(), botObstacle2.getX(), botObstacle2.getY(), null);
            g.drawImage(topObstacle1.getImage(), topObstacle1.getX(), topObstacle1.getY(), null);
            g.drawImage(topObstacle2.getImage(), topObstacle2.getX(), topObstacle2.getY(), null);
        }

        if (!isMenu && helicopter != null) {
            g.drawImage(helicopter.getImage(), helicopter.getX(), helicopter.getY(), null);
        }

        g.setFont(font);
        FontMetrics metric = g.getFontMetrics(font);
        messageWidth = metric.stringWidth(message);
        scoreWidth = metric.stringWidth(String.format("%d", score));

        g.setColor(Color.WHITE);
        g.drawString(message, screenWidth/2-messageWidth/2, screenHeight/4);

        if (!isMenu) {
            g.drawString(String.format("%d", score), screenWidth / 2 - scoreWidth / 2, 50);
        }
    }

    public void setBottomObstacle(BottomObstacle bottomObstacle1, BottomObstacle bottomObstacle2) {
        this.botObstacle1 = bottomObstacle1;
        this.botObstacle2 = bottomObstacle2;
    }

    public void setTopObstacle(TopObstacle topObstacle1, TopObstacle topObstacle2) {
        this.topObstacle1 = topObstacle1;
        this.topObstacle2 = topObstacle2;
    }

    public void setHelicopter(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    public void scoreUp() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
