// FlappyBird.java
import src.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    int lastScoreMilestone = 0;
    boolean reverseGravityActive = false;
    boolean reverseGravityTriggered = false;
    long reverseGravityStartTime;
    final int reverse_gravity_duration = 10000;
    final int reverse_gravity_score = 100;
    final int reverse_jump_force = 10;
    boolean eagleEffectActive = false;
    long eagleEffectStartTime;
    final int EAGLE_EFFECT_DURATION = 5000; // 5 seconds
    final int WEAKENED_JUMP_FORCE = -4; // Much weaker jump (was -10)


    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image coinImg;
    Image eagleImg;
    Image bombImg;

    // Bird variables
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    // Pipe variables
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // game logic
    Bird bird;
    double velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    ArrayList<Coin> coins = new ArrayList<>();
    ArrayList<Eagle> eagles = new ArrayList<>();
    ArrayList<Bomb> bombs = new ArrayList<>();
    int normalJumpForce = -10;
    int currentJumpForce = normalJumpForce;
    boolean bombCollision = false;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    double hs = readHighScore();
    int coinsCollected = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load images (paths might need adjustment)
        try {
            backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
            birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
            topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
            bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
            coinImg = new ImageIcon(getClass().getResource("./coin.png")).getImage();
            eagleImg = new ImageIcon(getClass().getResource("./eagle.png")).getImage();
            bombImg = new ImageIcon(getClass().getResource("./bomb.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
        }

        bird = new Bird(birdX, birdY, birdWidth, birdHeight, birdImg);
        pipes = new ArrayList<Pipe>();

        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/3;

        Pipe topPipe = new Pipe(pipeX, randomPipeY, pipeWidth, pipeHeight, topPipeImg);
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(pipeX, randomPipeY + pipeHeight + openingSpace, pipeWidth, pipeHeight, bottomPipeImg);
        pipes.add(bottomPipe);

        if (random.nextDouble() < 0.4) {
            int coinX = pipeX + pipeWidth + 20; // a bit after the pipes
            int coinY = bottomPipe.y - 100 + random.nextInt(80); // between pipes
            int coinSize = 24;
            coins.add(new Coin(coinX, coinY, coinSize, coinSize, coinImg));
        }
        if (random.nextDouble() < 0.2) { // 20% chance
            int eagleX = boardWidth;
            int eagleY = random.nextInt(boardHeight - 100);
            int eagleWidth = 50;
            int eagleHeight = 50;
            eagles.add(new Eagle(eagleX, eagleY, eagleWidth, eagleHeight, eagleImg));
        }
        if (random.nextDouble() < 0.15) { // 15% chance
            int bombX = boardWidth;
            int bombY = random.nextInt(boardHeight - 100);
            int bombWidth = 30;
            int bombHeight = 30;
            bombs.add(new Bomb(bombX, bombY, bombWidth, bombHeight, bombImg));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        for (Coin coin : coins) {
            if (!coin.collected) {
                double scale = 1 + 0.1 * Math.sin(System.currentTimeMillis() / 200.0);
                int drawWidth = (int)(coin.width * scale);
                int drawHeight = (int)(coin.height * scale);
                int drawX = coin.x + (coin.width - drawWidth) / 2;
                int drawY = coin.y + (coin.height - drawHeight) / 2;

                g.drawImage(coin.img, drawX, drawY, drawWidth, drawHeight, null);
            }
        }
        for (Eagle eagle : eagles) {
            g.drawImage(eagle.img, eagle.x, eagle.y, eagle.width, eagle.height, null);
        }

        // Draw bombs
        for (Bomb bomb : bombs) {

            g.drawImage(bomb.img, bomb.x, bomb.y, bomb.width, bomb.height, null);
        }

        // Score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Pixelify Sans", Font.PLAIN, 32));

        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 70, 240);
            g.setColor(Color.WHITE);
            if(score>hs){
                g.drawString("HighScore: " + (int) score, 70, 280);
            } else {
                g.drawString("HighScore: " + (int) hs, 70, 280);
            }
            if (coinsCollected >= 1) {
                g.setColor(Color.RED);
                g.setFont(new Font("Pixelify Sans", Font.BOLD, 24));
                g.drawString("Press SPACE to revive (10 coins)", 40, 320);
            }
        } else {
            g.drawString("Score:" + (int) score, 10, 35);
            g.setColor(Color.YELLOW);
            g.drawString("Coins: " + coinsCollected, 10, 65);

            // Display reverse gravity timer if active
            if (reverseGravityActive) {
                long timeLeft = reverse_gravity_duration - (System.currentTimeMillis() - reverseGravityStartTime);
                int secondsLeft = (int) (timeLeft / 1000) + 1;
                g.setColor(Color.RED);
                g.drawString("Reverse: " + secondsLeft + "s", 200, 35);
            }
        }
    }

    private void updatePipeTimerDelay() {
        int baseSpeed = -4;
        int baseDelay = 1800;
        int newDelay = (int) (baseDelay * baseSpeed / velocityX);
        newDelay = Math.max(newDelay, 500);
        placePipeTimer.setDelay(newDelay);
    }

    public void move() {
        // Handle reverse gravity activation
        if (!reverseGravityTriggered && (int) score >= reverse_gravity_score) {
            activateReverseGravity();
        }
        // Handle reverse gravity timeout
        if (reverseGravityActive &&
                System.currentTimeMillis() - reverseGravityStartTime >= reverse_gravity_duration) {
            deactivateReverseGravity();
        }

        // Apply gravity
        velocityY += gravity;
        bird.y += velocityY;

        // Apply boundaries (different for reverse gravity)
        if (reverseGravityActive) {
            bird.y = Math.min(bird.y, boardHeight - bird.height);
        } else {
            bird.y = Math.max(bird.y, 0);
        }

        // Move pipes and check collisions
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        // Increase difficulty every 2 points
        if ((int) score % 2 == 0 && score > 0 && (int) score != lastScoreMilestone) {
            velocityX = Math.max(velocityX - 1, -12);
            updatePipeTimerDelay();
            lastScoreMilestone = (int) score;
        }

        // Ground collision (different for reverse gravity)
        if ((!reverseGravityActive && bird.y > boardHeight) ||
                (reverseGravityActive && bird.y <= 0)) {
            gameOver = true;
        }

        for (int i = 0; i < coins.size(); i++) {
            Coin coin = coins.get(i);
            coin.x += velocityX;

            if (!coin.collected && collision(bird, coin)) {
                coin.collected = true;
                score += 1;
                coinsCollected++;
            }
        }
        for (int i = 0; i < eagles.size(); i++) {
            Eagle eagle = eagles.get(i);
            eagle.updatePosition(velocityX); // Use the new method with sin wave movement

            // Remove eagles that go off-screen
            if (eagle.x + eagle.width < 0) {
                eagles.remove(i);
                i--;
                continue;
            }

            if (collision(bird, eagle)) {
                eagleEffectActive = true;
                eagleEffectStartTime = System.currentTimeMillis();

                System.out.println("Eagle hit! Jump weakened for 5 seconds.");
                eagles.remove(i);
                i--;
            }
        }
        for (int i = 0; i < bombs.size(); i++) {
            Bomb bomb = bombs.get(i);
            bomb.updatePosition(velocityX); // Use the new method with sin wave movement

            // Remove bombs that go off-screen
            if (bomb.x + bomb.width < 0) {
                bombs.remove(i);
                i--;
                continue;
            }

            // Check collision
            if (collision(bird, bomb)) {
                bombCollision = true;
                gameOver = true;
                bombs.remove(i);
                i--;
                break;
            }
        }
    }

    private void activateReverseGravity() {
        reverseGravityActive = true;
        reverseGravityTriggered = true;
        reverseGravityStartTime = System.currentTimeMillis();
        gravity = -1; // Reverse gravity direction
    }

    private void deactivateReverseGravity() {
        reverseGravityActive = false;
        gravity = 1; // Restore normal gravity
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    boolean collision(Bird a, Coin b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    boolean collision(Bird a, Eagle b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    boolean collision(Bird a, Bomb b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }
    private double readHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader("highScore.txt"))) {
            String line = br.readLine();
            if (line != null && !line.isEmpty()) {
                return Double.parseDouble(line);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Couldn't read high score: " + e.getMessage());
        }
        return 0.0; // default if no file or invalid content
    }

    private void saveHighscore() {
        if (this.score > this.hs) {
            try (PrintWriter pw = new PrintWriter(new FileWriter("highScore.txt"))) {
                pw.println((int)this.score); // save as integer if you don't want decimals
                this.hs = this.score; // update in-memory high score too
            } catch (IOException e) {
                System.err.println("Error saving high score: " + e.getMessage());
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                if (bombCollision && coinsCollected >= 10) {
                    // Revival option for bomb collision
                    coinsCollected -= 10; // Deduct coins for revive
                    bombCollision = false;
                    gameOver = false;

                    // Reset bird position
                    bird.y = boardHeight / 2;
                    velocityY = 0;

                    // Restart timers
                    gameLoop.start();
                    placePipeTimer.start();
                } else {
                    // Normal restart when game is over
                    saveHighscore();
                    resetGame();
                }
            } else {
                if (reverseGravityActive) {
                    velocityY = reverse_jump_force;
                } else {
                    velocityY = currentJumpForce;
                }
            }
        }
    }

    private void resetGame() {
        coins.clear();
        eagles.clear();
        bombs.clear();

        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        velocityX = -4;
        lastScoreMilestone = 0;
        currentJumpForce = normalJumpForce;

        // Reset reverse gravity state
        reverseGravityActive = false;
        reverseGravityTriggered = false;
        gravity = 1;
        bombCollision = false;

        updatePipeTimerDelay();
        gameLoop.start();
        placePipeTimer.start();
    }

    public static void main(String[] args) {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
