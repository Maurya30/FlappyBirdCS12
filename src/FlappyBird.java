// FlappyBird.java
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;
    int lastScoreMilestone = 0;

    // Reverse gravity mode variables
    boolean reverseGravityActive = false;
    boolean reverseGravityTriggered = false;
    long reverseGravityStartTime;
    final int REVERSE_GRAVITY_DURATION = 20000; // 20 seconds in milliseconds
    final int REVERSE_GRAVITY_THRESHOLD = 1;
    final int REVERSE_JUMP_FORCE = 10; // Jump force in reverse mode

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image coinImg;

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
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

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
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(pipeX, randomPipeY, pipeWidth, pipeHeight, topPipeImg);
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(pipeX, randomPipeY + pipeHeight + openingSpace, pipeWidth, pipeHeight, bottomPipeImg);
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background with different color if in reverse gravity mode
        if (reverseGravityActive) {
            g.setColor(new Color(135, 206, 250)); // Light blue background
            g.fillRect(0, 0, boardWidth, boardHeight);
        } else {
            g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        }

        // Bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Pixelify Sans", Font.PLAIN, 32));

        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 70, 240);
        } else {
            g.drawString("Score:" + (int) score, 10, 35);

            // Display reverse gravity timer if active
            if (reverseGravityActive) {
                long timeLeft = REVERSE_GRAVITY_DURATION - (System.currentTimeMillis() - reverseGravityStartTime);
                int secondsLeft = (int) (timeLeft / 1000) + 1;
                g.setColor(Color.RED);
                g.drawString("Reverse: " + secondsLeft + "s", 200, 35);
            }
        }
    }

    private void updatePipeTimerDelay() {
        int baseSpeed = -4;
        int baseDelay = 1500;
        int newDelay = (int) (baseDelay * baseSpeed / velocityX);
        newDelay = Math.max(newDelay, 500);
        placePipeTimer.setDelay(newDelay);
    }

    public void move() {
        // Handle reverse gravity activation
        if (!reverseGravityTriggered && (int) score >= REVERSE_GRAVITY_THRESHOLD) {
            activateReverseGravity();
        }

        // Handle reverse gravity timeout
        if (reverseGravityActive &&
                System.currentTimeMillis() - reverseGravityStartTime >= REVERSE_GRAVITY_DURATION) {
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

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }

    private void saveHighscore() {
        try {
            FileWriter fw = new FileWriter("highScore.txt");
            PrintWriter pw = new PrintWriter(fw);
            pw.println((int) score);
            pw.close();
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (reverseGravityActive) {
                // Downward jump in reverse gravity mode
                velocityY = REVERSE_JUMP_FORCE;
            } else {
                // Upward jump in normal mode
                velocityY = -10;
            }

            if (gameOver) {
                saveHighscore();
                resetGame();
            }
        }
    }

    private void resetGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        velocityX = -4;
        lastScoreMilestone = 0;

        // Reset reverse gravity state
        reverseGravityActive = false;
        reverseGravityTriggered = false;
        gravity = 1;

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