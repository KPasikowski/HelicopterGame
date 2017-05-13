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
    private static final int GAME_START_DELAY = 300;
    private static final int HELICOPTER_X_STARTING_LOCATION = SCREEN_WIDTH/7;
    private static final int HELICOPTER_ASCEND_SPEED = 10, HELICOPTER_DESCEND_SPEED = HELICOPTER_ASCEND_SPEED /2;
    private static final int HELICOPTER_JUMP_HEIGHT = OBSTACLE_GAP - HELICOPTER_HEIGHT - HELICOPTER_ASCEND_SPEED *2;

    private boolean mainLoop = true;
    private boolean isGameRunning = false;
    private boolean isHelicopterAscending = false;
    private boolean spacePressed = false;
    private boolean released = true;
    private int helicopterYBeforeJump = SCREEN_HEIGHT/2 - HELICOPTER_HEIGHT;
    private Object gameReady = new Object();

    private JFrame f;
    private JButton startGameButton;
    private JPanel topPanel;

    private static Main game;
    private static GameScreen gameScreen;

    public Main() {
        f = new JFrame("Helicopter Game");
    }

    public static void main(String[] args) {
        game = new Main();
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
        f.setMinimumSize(new Dimension(SCREEN_WIDTH*1/4, SCREEN_HEIGHT*1/4));
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setIconImage(icon);
        f.addKeyListener(this);
    }

    private JPanel createContentPane() {
        topPanel = new JPanel();
        topPanel.setBackground(Color.BLACK);

        LayoutManager overlay = new OverlayLayout(topPanel);
        topPanel.setLayout(overlay);

        startGameButton = new JButton("Start game");
        startGameButton.setBackground(Color.BLUE);
        startGameButton.setForeground(Color.WHITE);
        startGameButton.setFocusable(false);
        startGameButton.setFont(new Font("Calibri", Font.BOLD, 42));
        startGameButton.setAlignmentX(0.5f);
        startGameButton.setAlignmentY(0.5f);
        startGameButton.addActionListener(this);
        topPanel.add(startGameButton);

        gameScreen = new GameScreen(SCREEN_WIDTH, SCREEN_HEIGHT, true);
        topPanel.add(gameScreen);

        return topPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startGameButton) {
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
        BottomObstacle botObstacle1 = new BottomObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        BottomObstacle botObstacle2 = new BottomObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        TopObstacle topObstacle1 = new TopObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        TopObstacle topObstacle2 = new TopObstacle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        Helicopter helicopter = new Helicopter(HELICOPTER_WIDTH, HELICOPTER_HEIGHT);

        int obstacleX1 = SCREEN_WIDTH + GAME_START_DELAY, obstacleX2 = (int) (3.0/2.0*SCREEN_WIDTH+OBSTACLE_WIDTH/2.0)+ GAME_START_DELAY;
        int obstacleY1 = bottomObstacleHeight(), obstacleY2 = bottomObstacleHeight();
        int helicopterX = HELICOPTER_X_STARTING_LOCATION, helicopterY = helicopterYBeforeJump;

        long startTime = System.currentTimeMillis();

        while(mainLoop) {
            if ((System.currentTimeMillis() - startTime) > UPDATE_INTERVAL) {
                if (obstacleX1 < -OBSTACLE_WIDTH) {
                    obstacleX1 = SCREEN_WIDTH;
                    obstacleY1 = bottomObstacleHeight();
                }
                else if (obstacleX2 < -OBSTACLE_WIDTH) {
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
                topObstacle1.setY(obstacleY1 -OBSTACLE_GAP-OBSTACLE_HEIGHT);
                topObstacle2.setX(obstacleX2);
                topObstacle2.setY(obstacleY2 -OBSTACLE_GAP-OBSTACLE_HEIGHT);

                if (!isMenu && spacePressed) {
                    helicopterYBeforeJump = helicopterY;
                    spacePressed = false;
                }

                if (!isMenu && isHelicopterAscending) {
                    if (helicopterYBeforeJump - helicopterY - HELICOPTER_ASCEND_SPEED < HELICOPTER_JUMP_HEIGHT) {
                        if (helicopterY - HELICOPTER_ASCEND_SPEED > 0) {
                            helicopterY -= HELICOPTER_ASCEND_SPEED;
                        }
                        else {
                            helicopterY = 0;
                            helicopterYBeforeJump = helicopterY;
                            isHelicopterAscending = false;
                        }
                    }
                    else {
                        helicopterYBeforeJump = helicopterY;
                        isHelicopterAscending = false;
                    }
                }
                else if(!isMenu) {
                    helicopterY += HELICOPTER_DESCEND_SPEED;
                    helicopterYBeforeJump = helicopterY;
                }

                if (!isMenu) {
                    helicopter.setX(helicopterX);
                    helicopter.setY(helicopterY);
                    gameScreen.setHelicopter(helicopter);
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
        while (height <= OBSTACLE_GAP+50 || height >= SCREEN_HEIGHT-OBSTACLE_GAP) {
            height = (int)(Math.random()*((double)SCREEN_HEIGHT));
        }
        return height;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && isGameRunning && released){
            if(isHelicopterAscending) {
                spacePressed = true;
            }
            isHelicopterAscending = true;
            released = false;
        }
        else if(e.getKeyCode() == KeyEvent.VK_R && !isGameRunning) {
            helicopterYBeforeJump = SCREEN_HEIGHT/2 - HELICOPTER_HEIGHT;
            isHelicopterAscending = false;
            actionPerformed(new ActionEvent(startGameButton, -1, ""));
        }
        else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            released = true;
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    private void closeMenu() {
        Thread t = new Thread(() -> {
                topPanel.remove(startGameButton);
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

        if (helicopter.getY() + HELICOPTER_HEIGHT > SCREEN_HEIGHT*7/8) {
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
    
}