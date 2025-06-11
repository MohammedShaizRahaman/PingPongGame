import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PingPongGame extends JPanel implements ActionListener, KeyListener {
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 600;
    private static final int PADDLE_WIDTH = 20;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;

    private Timer timer;
    private Ball ball;
    private Paddle player1;
    private Paddle player2;
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean gameRunning = true;
    private Random random;

    public PingPongGame() {
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        random = new Random();

        // Create paddles
        player1 = new Paddle(10, GAME_HEIGHT/2 - PADDLE_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        player2 = new Paddle(GAME_WIDTH - PADDLE_WIDTH - 10, GAME_HEIGHT/2 - PADDLE_HEIGHT/2, PADDLE_WIDTH, PADDLE_HEIGHT, 2);

        // Create ball
        newBall();

        // Start game timer
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    public void newBall() {
        int ballX = GAME_WIDTH/2 - BALL_SIZE/2;
        int ballY = random.nextInt(GAME_HEIGHT - BALL_SIZE);

        // Random direction for ball
        int xDirection = random.nextBoolean() ? 1 : -1;
        int yDirection = random.nextBoolean() ? 1 : -1;

        ball = new Ball(ballX, ballY, BALL_SIZE, BALL_SIZE, xDirection, yDirection);
    }

    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw center line
        g.setColor(Color.WHITE);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10}, 0));
        g.drawLine(GAME_WIDTH/2, 0, GAME_WIDTH/2, GAME_HEIGHT);

        // Draw paddles
        g.setColor(Color.WHITE);
        g.fillRect(player1.x, player1.y, player1.width, player1.height);
        g.fillRect(player2.x, player2.y, player2.width, player2.height);

        // Draw ball
        g.setColor(Color.WHITE);
        g.fillOval(ball.x, ball.y, ball.width, ball.height);

        // Draw scores
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());

        String score1 = String.valueOf(player1Score);
        String score2 = String.valueOf(player2Score);

        g.drawString(score1, GAME_WIDTH/4 - metrics.stringWidth(score1)/2, 50);
        g.drawString(score2, 3*GAME_WIDTH/4 - metrics.stringWidth(score2)/2, 50);

        // Draw game over message
        if (!gameRunning) {
            g.setFont(new Font("Arial", Font.BOLD, 60));
            metrics = getFontMetrics(g.getFont());
            String gameOverText = "Game Over!";
            String winnerText = (player1Score >= 5) ? "Player 1 Wins!" : "Player 2 Wins!";
            String restartText = "Press SPACE to restart";

            g.setColor(Color.RED);
            g.drawString(gameOverText, GAME_WIDTH/2 - metrics.stringWidth(gameOverText)/2, GAME_HEIGHT/2 - 60);
            g.drawString(winnerText, GAME_WIDTH/2 - metrics.stringWidth(winnerText)/2, GAME_HEIGHT/2);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            metrics = getFontMetrics(g.getFont());
            g.setColor(Color.WHITE);
            g.drawString(restartText, GAME_WIDTH/2 - metrics.stringWidth(restartText)/2, GAME_HEIGHT/2 + 40);
        }

        // Draw instructions
        if (gameRunning) {
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(Color.GRAY);
            g.drawString("Player 1: W/S keys", 10, GAME_HEIGHT - 40);
            g.drawString("Player 2: UP/DOWN arrows", 10, GAME_HEIGHT - 20);
            g.drawString("First to 5 points wins!", GAME_WIDTH - 150, GAME_HEIGHT - 20);
        }
    }

    public void move() {
        if (gameRunning) {
            // Move paddles
            player1.move();
            player2.move();

            // Move ball
            ball.move();
        }
    }

    public void checkCollision() {
        // Ball collision with top and bottom walls
        if (ball.y <= 0 || ball.y >= GAME_HEIGHT - BALL_SIZE) {
            ball.yDirection = -ball.yDirection;
        }

        // Ball collision with paddles
        if (ball.intersects(player1)) {
            ball.xDirection = Math.abs(ball.xDirection); // Make sure ball goes right
            ball.x = player1.x + PADDLE_WIDTH; // Prevent sticking

            // Add some angle based on where ball hits paddle
            int paddleCenter = player1.y + PADDLE_HEIGHT/2;
            int ballCenter = ball.y + BALL_SIZE/2;
            int difference = ballCenter - paddleCenter;
            ball.yDirection = difference / 10; // Adjust angle
        }

        if (ball.intersects(player2)) {
            ball.xDirection = -Math.abs(ball.xDirection); // Make sure ball goes left
            ball.x = player2.x - BALL_SIZE; // Prevent sticking

            // Add some angle based on where ball hits paddle
            int paddleCenter = player2.y + PADDLE_HEIGHT/2;
            int ballCenter = ball.y + BALL_SIZE/2;
            int difference = ballCenter - paddleCenter;
            ball.yDirection = difference / 10; // Adjust angle
        }

        // Prevent paddles from going out of bounds
        if (player1.y <= 0) player1.y = 0;
        if (player1.y >= GAME_HEIGHT - PADDLE_HEIGHT) player1.y = GAME_HEIGHT - PADDLE_HEIGHT;
        if (player2.y <= 0) player2.y = 0;
        if (player2.y >= GAME_HEIGHT - PADDLE_HEIGHT) player2.y = GAME_HEIGHT - PADDLE_HEIGHT;
    }

    public void checkScore() {
        // Player 2 scores (ball goes off left side)
        if (ball.x < 0) {
            player2Score++;
            newBall();
        }

        // Player 1 scores (ball goes off right side)
        if (ball.x > GAME_WIDTH) {
            player1Score++;
            newBall();
        }

        // Check for game over
        if (player1Score >= 5 || player2Score >= 5) {
            gameRunning = false;
        }
    }

    public void restart() {
        player1Score = 0;
        player2Score = 0;
        gameRunning = true;
        newBall();

        // Reset paddle positions
        player1.y = GAME_HEIGHT/2 - PADDLE_HEIGHT/2;
        player2.y = GAME_HEIGHT/2 - PADDLE_HEIGHT/2;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            move();
            checkCollision();
            checkScore();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Player 1 controls (W/S)
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player1.setYDirection(-5);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            player1.setYDirection(5);
        }

        // Player 2 controls (UP/DOWN arrows)
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            player2.setYDirection(-5);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            player2.setYDirection(5);
        }

        // Restart game
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameRunning) {
            restart();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Stop paddle movement when key is released
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
            player1.setYDirection(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
            player2.setYDirection(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Ball class
    public class Ball extends Rectangle {
        int xDirection;
        int yDirection;
        int speed = 5;

        Ball(int x, int y, int width, int height, int xDirection, int yDirection) {
            super(x, y, width, height);
            this.xDirection = xDirection;
            this.yDirection = yDirection;
        }

        public void move() {
            x += xDirection * speed;
            y += yDirection * speed;
        }
    }

    // Paddle class
    public class Paddle extends Rectangle {
        int id;
        int yDirection;
        int speed = 5;

        Paddle(int x, int y, int width, int height, int id) {
            super(x, y, width, height);
            this.id = id;
        }

        public void move() {
            y += yDirection * speed;
        }

        public void setYDirection(int yDirection) {
            this.yDirection = yDirection;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ping Pong Game");
        PingPongGame game = new PingPongGame();

        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}