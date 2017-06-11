/**
 * Created by mateusz on 12.05.17.
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Main implements ActionListener, KeyListener {
    private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final int OBSTACLE_GAP = SCREEN_HEIGHT/4;
    private static final int OBSTACLE_WIDTH = SCREEN_WIDTH/8, OBSTACLE_HEIGHT = 4*OBSTACLE_WIDTH;
    private static final int HELICOPTER_WIDTH = 120, HELICOPTER_HEIGHT = 75;
    private static final int UPDATE_INTERVAL = 25;
    private static final int MOVEMENT_SPEED = 5;
    private static final int GAME_START_DELAY = 200;
    private static final int HELICOPTER_X_STARTING_LOCATION = SCREEN_WIDTH/7;
    private static final int HELICOPTER_Y_STARTING_LOCATION = SCREEN_HEIGHT/2 - HELICOPTER_HEIGHT;
    private static final int HELICOPTER_ASCEND_SPEED = 10, HELICOPTER_DESCEND_SPEED = HELICOPTER_ASCEND_SPEED /2;

    private boolean mainLoop = true;
    private boolean isGameRunning = false;
    private boolean spacePressed = false;
    private boolean isRegulatorOn = false;
    private Object gameReady = new Object();

    private JFrame f;
    private JButton startGameButton;
    private JButton startRegulatorButton;
    private JPanel topPanel;
    private JPanel buttonPanel;

    private static Main game;
    private static GameScreen gameScreen;

    public Main() {
        f = new JFrame("Helicopter Game");
    }

    public static void main(String[] args) {
        game = new Main();
        game.startGame();
    }

    private void startGame() {
        javax.swing.SwingUtilities.invokeLater(() -> {
                game.buildFrame();
                Thread t = new Thread(() -> game.beginGame(true));
                t.start();
            }
        );
    }

    private void buildFrame() {
        Image icon = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("resources/helicopter.png"));
        f.setContentPane(createContentPane());
        f.setResizable(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setAlwaysOnTop(false);
        f.setVisible(true);
        f.setMinimumSize(new Dimension(SCREEN_WIDTH/4, SCREEN_HEIGHT/4));
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setIconImage(icon);
        f.addKeyListener(this);
    }

    private JPanel createContentPane() {
        topPanel = new JPanel();
        topPanel.setBackground(Color.BLACK);

        LayoutManager overlay = new OverlayLayout(topPanel);
        topPanel.setLayout(overlay);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        startGameButton = new JButton("Start game");
        startGameButton.setBackground(Color.RED);
        startGameButton.setForeground(Color.BLACK);
        startGameButton.setFocusable(false);
        startGameButton.setFont(new Font("Calibri", Font.BOLD, 42));
        startGameButton.setAlignmentX(0.5f);
        startGameButton.setAlignmentY(0.5f);
        startGameButton.addActionListener(this);

        startRegulatorButton = new JButton("Start fuzzy regulator");
        startRegulatorButton.setBackground(Color.RED);
        startRegulatorButton.setForeground(Color.BLACK);
        startRegulatorButton.setFocusable(false);
        startRegulatorButton.setFont(new Font("Calibri", Font.BOLD, 42));
        startRegulatorButton.setAlignmentX(0.5f);
        startRegulatorButton.setAlignmentY(2.0f);
        startRegulatorButton.addActionListener(this);

        buttonPanel.add(startGameButton);
        buttonPanel.add(startRegulatorButton);

        topPanel.add(buttonPanel);

        gameScreen = new GameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true);
        topPanel.add(gameScreen);

        return topPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startGameButton) {
            mainLoop = false;
            closeMenu();
        }
        else if (e.getSource() == startRegulatorButton) {
            isRegulatorOn = true;
            mainLoop = false;
            closeMenu();
        }
        else if (e.getSource() == gameReady) {
            Thread t = new Thread(() -> {
                    mainLoop = true;
                    isGameRunning = true;
                    game.beginGame(false);
            });
            t.start();
        }
    }

    private void beginGame(boolean isMenu) {
        BottomObstacle botObstacle1 = new BottomObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, "resources/bottom_obstacle.png");
        BottomObstacle botObstacle2 = new BottomObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, "resources/bottom_obstacle.png");
        TopObstacle topObstacle1 = new TopObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, "resources/top_obstacle.png");
        TopObstacle topObstacle2 = new TopObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, "resources/top_obstacle.png");
        Helicopter helicopter = new Helicopter(HELICOPTER_WIDTH, HELICOPTER_HEIGHT, "resources/helicopter.png");

        int obstacleX1 = SCREEN_WIDTH + GAME_START_DELAY, obstacleX2 = (int) (3.0/2.0*SCREEN_WIDTH+OBSTACLE_WIDTH/2.0)+ GAME_START_DELAY;
        int obstacleY1 = bottomObstacleHeight(), obstacleY2 = bottomObstacleHeight();
        int helicopterY = HELICOPTER_Y_STARTING_LOCATION;

        long startTime = System.currentTimeMillis();

        while(mainLoop) {
            if ((System.currentTimeMillis() - startTime) > UPDATE_INTERVAL) {
                if (obstacleX1 < -OBSTACLE_WIDTH) {
                    obstacleX1 = obstacleX2;
                    obstacleY1 = obstacleY2;
                    obstacleX2 = SCREEN_WIDTH;
                    obstacleY2 = bottomObstacleHeight();

                }

                obstacleX1 -= MOVEMENT_SPEED;
                obstacleX2 -= MOVEMENT_SPEED;

                botObstacle1.setX(obstacleX1);
                botObstacle1.setY(obstacleY1);
                botObstacle2.setX(obstacleX2);
                botObstacle2.setY(obstacleY2);
                topObstacle1.setX(obstacleX1);
                topObstacle1.setY(obstacleY1 - OBSTACLE_GAP - OBSTACLE_HEIGHT);
                topObstacle2.setX(obstacleX2);
                topObstacle2.setY(obstacleY2 - OBSTACLE_GAP - OBSTACLE_HEIGHT);

                if (!isMenu) {
                    // check whether helicopter should ascend due to button being pressed
                    if (spacePressed) {
                            helicopter.accelerate(HELICOPTER_ASCEND_SPEED);
                        helicopterY = moveHelicopter(helicopterY);
                    }
                    // check whether helicopter should ascend due to its acceleration
                    else if (helicopter.getAcceleration() > 0) {
                        helicopterY = moveHelicopter(helicopterY);
                        helicopter.accelerate(-HELICOPTER_ASCEND_SPEED);
                    }
                    else {
                        helicopterY += HELICOPTER_DESCEND_SPEED;
                    }
                    if (helicopterY == 0) {
                        helicopter.setAcceleration(0);
                    }
                }

                if (!isMenu) {
                    helicopter.setX(HELICOPTER_X_STARTING_LOCATION);
                    helicopter.setY(helicopterY);
                    gameScreen.setHelicopter(helicopter);

                    if (isRegulatorOn) {
                        calculateFuzzy(helicopter, botObstacle1, botObstacle2);
                    }
                }

                gameScreen.setBottomObstacle(botObstacle1, botObstacle2);
                gameScreen.setTopObstacle(topObstacle1, topObstacle2);

                if (!isMenu && helicopter.getWidth() != -1 && topObstacle1.getWidth() != -1 && topObstacle2.getWidth() != -1 && botObstacle1.getWidth() != -1 && botObstacle2.getWidth() != -1) {
                    checkCollision(botObstacle1, botObstacle2, topObstacle1, topObstacle2, helicopter);
                    updateScore(botObstacle1, botObstacle2, helicopter);
                }

                topPanel.revalidate();
                topPanel.repaint();

                startTime = System.currentTimeMillis();
            }
        }
    }

    private int bottomObstacleHeight() {
        int height = 0;
        while (height <= OBSTACLE_GAP+10 || height >= SCREEN_HEIGHT-OBSTACLE_GAP/2) {
            height = (int)(Math.random()*((double)SCREEN_HEIGHT));
        }
        return height;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && isGameRunning && !spacePressed){
            spacePressed = true;
        }
        else if(e.getKeyCode() == KeyEvent.VK_R && !isGameRunning) {
            restartGame();
        }
        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            spacePressed = false;
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    private void closeMenu() {
        Thread t = new Thread(() -> {
                topPanel.remove(buttonPanel);
                topPanel.remove(gameScreen);
                topPanel.revalidate();
                topPanel.repaint();

                JPanel blackBlink = new JPanel();
                int visibility = 5;
                blackBlink.setBackground(new Color(0, 0, 0, visibility));
                topPanel.add(blackBlink);
                topPanel.add(gameScreen);
                topPanel.revalidate();
                topPanel.repaint();

                long currentTime = System.currentTimeMillis();

                while (blackBlink.getBackground().getAlpha() != 255) {
                    if ((System.currentTimeMillis() - currentTime) > UPDATE_INTERVAL/2) {
                        visibility += 10;

                        blackBlink.setBackground(new Color(0, 0, 0, visibility));

                        topPanel.revalidate();
                        topPanel.repaint();
                        currentTime = System.currentTimeMillis();
                    }
                }

                topPanel.removeAll();
                topPanel.add(blackBlink);
                gameScreen = new GameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, false);
                gameScreen.setMessage("");
                topPanel.add(gameScreen);

                visibility = 250;
                while (blackBlink.getBackground().getAlpha() != 0) {
                    if ((System.currentTimeMillis() - currentTime) > UPDATE_INTERVAL /2) {
                        visibility -= 10;

                        blackBlink.setBackground(new Color(0, 0, 0, visibility));

                        topPanel.revalidate();
                        topPanel.repaint();
                        currentTime = System.currentTimeMillis();
                    }
                }
                actionPerformed(new ActionEvent(gameReady, -1, "Game is ready"));
            });
        t.start();
    }

    private void updateScore(BottomObstacle botObstacle1, BottomObstacle botObstacle2, Helicopter helicopter) {
        if (botObstacle1.getX() + OBSTACLE_WIDTH < helicopter.getX() && botObstacle1.getX() + OBSTACLE_WIDTH > helicopter.getX() - MOVEMENT_SPEED) {
            gameScreen.scoreUp();
        }
        else if (botObstacle2.getX() + OBSTACLE_WIDTH < helicopter.getX() && botObstacle2.getX() + OBSTACLE_WIDTH > helicopter.getX() - MOVEMENT_SPEED) {
            gameScreen.scoreUp();
        }
    }

    private void checkCollision(BottomObstacle bottomObstacle1, BottomObstacle bottomObstacle2, TopObstacle topObstacle1, TopObstacle topObstacle2, Helicopter helicopter) {
        checkCollisionAccurately(helicopter.getRectangle(), bottomObstacle1.getRectangle(), helicopter.getBI(), bottomObstacle1.getBI());
        checkCollisionAccurately(helicopter.getRectangle(), bottomObstacle2.getRectangle(), helicopter.getBI(), bottomObstacle2.getBI());
        checkCollisionAccurately(helicopter.getRectangle(), topObstacle1.getRectangle(), helicopter.getBI(), topObstacle1.getBI());
        checkCollisionAccurately(helicopter.getRectangle(), topObstacle2.getRectangle(), helicopter.getBI(), topObstacle2.getBI());

        if (helicopter.getY() + HELICOPTER_HEIGHT > (double)15/16*SCREEN_HEIGHT) {
            gameScreen.setMessage("Game Over");
            mainLoop = false;
            isGameRunning = false;
        }
    }

    private void checkCollisionAccurately(Rectangle helicopterRectangle, Rectangle obstacleRectangle, BufferedImage helicopterImage, BufferedImage obstacleImage) {
        if (helicopterRectangle.intersects(obstacleRectangle)) {
            Rectangle r = helicopterRectangle.intersection(obstacleRectangle);

            int helicopterIntersectionStartX = (int)(r.getMinX() - helicopterRectangle.getMinX());
            int helicopterIntersectionStartY = (int)(r.getMinY() - helicopterRectangle.getMinY());
            int hitDepthX = (int)(helicopterRectangle.getMinX() - obstacleRectangle.getMinX());
            int hitDepthY = (int)(helicopterRectangle.getMinY() - obstacleRectangle.getMinY());

            for (int i = helicopterIntersectionStartX; i < r.getWidth()+helicopterIntersectionStartX; i++) { //
                for (int j = helicopterIntersectionStartY; j < r.getHeight()+helicopterIntersectionStartY; j++) {
                    if ((helicopterImage.getRGB(i, j) & 0xFF000000) != 0x00 && (obstacleImage.getRGB(i + hitDepthX, j + hitDepthY) & 0xFF000000) != 0x00) {
                        gameScreen.setMessage("Game Over");
                        mainLoop = false;
                        isGameRunning = false;
                        break;
                    }
                }
            }
        }
    }

    private int moveHelicopter(int helicopterY) {
        // situation when jump doesn't result in helicopter touching the ceiling
        if (helicopterY - HELICOPTER_ASCEND_SPEED > 0) {
            helicopterY -= HELICOPTER_ASCEND_SPEED;
        }
        // situation when helicopter touches the ceiling
        else {
            helicopterY = 0;
        }
        return helicopterY;
    }

    private void restartGame() {
        isRegulatorOn = false;
        f.getContentPane().removeAll();
        f.getContentPane().invalidate();
        f.getContentPane().revalidate();
        f.getContentPane().repaint();
        startGame();
        Thread.currentThread().interrupt();
    }

    private void calculateFuzzy(Helicopter helicopter, BottomObstacle botObstacle1, BottomObstacle botObstacle2) {
        int heightDiff = 0;
        int distance = 0;
        if (HELICOPTER_X_STARTING_LOCATION > botObstacle1.getX() + OBSTACLE_WIDTH) {
            heightDiff = -helicopter.getY() + botObstacle2.getY() - HELICOPTER_HEIGHT/2;
            distance = botObstacle2.getX() - HELICOPTER_X_STARTING_LOCATION;
        }
        else {
            heightDiff = -helicopter.getY() + botObstacle1.getY() - HELICOPTER_HEIGHT/2;
            distance = botObstacle1.getX() - HELICOPTER_X_STARTING_LOCATION;
        }

        // height
        boolean below;
        if (heightDiff < 0.5*OBSTACLE_GAP) {
            below = true;
        }
        else {
            below = false;
        }

        // distance
        boolean near;
        if (distance < SCREEN_WIDTH/3) {
            near = true;
        }
        else {
            near = false;
        }

        // above middle of screen
        boolean low;
        if (helicopter.getY() + HELICOPTER_HEIGHT > SCREEN_HEIGHT/2) {
            low = true;
        }
        else {
            low = false;
        }

        // speed
        boolean fast;
        if (helicopter.getAcceleration() > 100) {
            fast = true;
        }
        else {
            fast = false;
        }

        // calculate control
        if (fast) {
            spacePressed = false;
        }
        else if (!near && !low) {
            spacePressed = false;
        }
        else if (!near && low) {
            spacePressed = true;
        }
        else if (near && below) {
            spacePressed = true;
        }
        else if (near && !below) {
            spacePressed = false;
        }
    }
}
