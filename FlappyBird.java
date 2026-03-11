import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    static final int WIDTH = 400, HEIGHT = 600;

    int birdX = 80, birdY = 250;
    double birdVelY = 0;
    final double GRAVITY = 0.7;
    final double JUMP = -8.4;
    final int BIRD_SIZE = 30;

    static final int PIPE_WIDTH = 60;
    static final int GAP = 160;
    static final int PIPE_SPEED = 4;
    ArrayList<int[]> pipes = new ArrayList<>();

    boolean gameStarted = false;
    boolean gameOver = false;
    int score = 0;
    int highScore = 0;

    Timer timer;
    Random rand = new Random();

    Color SKY       = new Color(113, 197, 232);
    Color GROUND    = new Color(222, 184, 135);
    Color PIPE      = new Color(83, 183, 47);
    Color PIPE_DARK = new Color(62, 137, 35);
    Color BIRD_CLR  = new Color(255, 220, 50);
    Color BIRD_WING = new Color(255, 165, 0);

    public FlappyBird() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(20, this);
        timer.start();
        spawnPipe();
    }

    void spawnPipe() {
        int topHeight = 80 + rand.nextInt(220);
        pipes.add(new int[]{WIDTH, topHeight});
    }

    void jump() {
        if (gameOver) {
            resetGame();
            return;
        }
        if (!gameStarted) gameStarted = true;
        birdVelY = JUMP;
    }

    void resetGame() {
        birdX = 80; birdY = 250; birdVelY = 0;
        pipes.clear(); spawnPipe();
        score = 0; gameOver = false; gameStarted = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver) {
            birdVelY += GRAVITY;
            birdY += (int) birdVelY;

            ArrayList<int[]> toRemove = new ArrayList<>();
            for (int[] pipe : pipes) {
                pipe[0] -= PIPE_SPEED;
                if (pipe[0] + PIPE_WIDTH == birdX) score++;
                if (pipe[0] + PIPE_WIDTH < 0) toRemove.add(pipe);
            }
            pipes.removeAll(toRemove);

            if (pipes.isEmpty() || pipes.get(pipes.size() - 1)[0] < WIDTH - 220) {
                spawnPipe();
            }

            if (birdY + BIRD_SIZE >= HEIGHT - 60 || birdY <= 0) {
                endGame();
            }

            for (int[] pipe : pipes) {
                int px = pipe[0], topH = pipe[1];
                int botY = topH + GAP;
                boolean inX = birdX + BIRD_SIZE - 5 > px && birdX + 5 < px + PIPE_WIDTH;
                boolean inY = birdY + 5 < topH || birdY + BIRD_SIZE - 5 > botY;
                if (inX && inY) endGame();
            }
        }
        repaint();
    }

    void endGame() {
        gameOver = true;
        if (score > highScore) highScore = score;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(SKY);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(new Color(255, 255, 255, 180));
        drawCloud(g2, 60, 80, 70);
        drawCloud(g2, 250, 120, 55);
        drawCloud(g2, 340, 60, 45);

        for (int[] pipe : pipes) {
            int px = pipe[0], topH = pipe[1];
            int botY = topH + GAP;

            g2.setColor(PIPE);
            g2.fillRect(px, 0, PIPE_WIDTH, topH - 15);
            g2.setColor(PIPE_DARK);
            g2.fillRect(px - 5, topH - 15, PIPE_WIDTH + 10, 20);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRect(px + 8, 0, 10, topH - 15);

            g2.setColor(PIPE);
            g2.fillRect(px, botY + 15, PIPE_WIDTH, HEIGHT - botY - 15 - 60);
            g2.setColor(PIPE_DARK);
            g2.fillRect(px - 5, botY, PIPE_WIDTH + 10, 20);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillRect(px + 8, botY + 20, 10, HEIGHT - botY - 60);
        }

        g2.setColor(GROUND);
        g2.fillRect(0, HEIGHT - 60, WIDTH, 60);
        g2.setColor(new Color(180, 140, 90));
        g2.fillRect(0, HEIGHT - 60, WIDTH, 8);
        g2.setColor(new Color(100, 180, 60));
        g2.fillRect(0, HEIGHT - 60, WIDTH, 10);

        int bx = birdX, by = birdY;
        g2.setColor(BIRD_WING);
        int wingOffset = (birdVelY > 0) ? 5 : -3;
        g2.fillOval(bx + 2, by + BIRD_SIZE / 2 + wingOffset, 18, 10);
        g2.setColor(BIRD_CLR);
        g2.fillOval(bx, by, BIRD_SIZE, BIRD_SIZE);
        g2.setColor(Color.WHITE);
        g2.fillOval(bx + 16, by + 6, 10, 10);
        g2.setColor(Color.BLACK);
        g2.fillOval(bx + 19, by + 8, 6, 6);
        g2.setColor(new Color(255, 140, 0));
        int[] bkX = {bx + 28, bx + 36, bx + 28};
        int[] bkY = {by + 13, by + 17, by + 21};
        g2.fillPolygon(bkX, bkY, 3);
        g2.setColor(new Color(255, 100, 100, 150));
        g2.fillOval(bx + 12, by + 16, 9, 7);

        g2.setFont(new Font("Arial Black", Font.BOLD, 36));
        g2.setColor(Color.WHITE);
        String sc = String.valueOf(score);
        int sw = g2.getFontMetrics().stringWidth(sc);
        g2.drawString(sc, WIDTH / 2 - sw / 2 + 2, 62);
        g2.setColor(new Color(80, 80, 80, 150));
        g2.drawString(sc, WIDTH / 2 - sw / 2, 60);

        if (!gameStarted && !gameOver) {
            drawPanel(g2, "FLAPPY BIRD", "Press SPACE or UP to Start", null);
        }
        if (gameOver) {
            drawPanel(g2, "GAME OVER", "Score: " + score + "   Best: " + highScore, "Press SPACE to Restart");
        }
    }

    void drawCloud(Graphics2D g2, int x, int y, int s) {
        g2.fillOval(x, y, s, s / 2);
        g2.fillOval(x + s / 4, y - s / 4, s / 2, s / 2);
        g2.fillOval(x + s / 2, y, s / 2, s / 2);
    }

    void drawPanel(Graphics2D g2, String title, String line1, String line2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(50, 180, WIDTH - 100, 200, 20, 20);

        g2.setFont(new Font("Arial Black", Font.BOLD, 28));
        g2.setColor(new Color(255, 220, 50));
        int tw = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, WIDTH / 2 - tw / 2, 225);

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        int lw = g2.getFontMetrics().stringWidth(line1);
        g2.drawString(line1, WIDTH / 2 - lw / 2, 270);

        if (line2 != null) {
            g2.setColor(new Color(180, 255, 150));
            int l2w = g2.getFontMetrics().stringWidth(line2);
            g2.drawString(line2, WIDTH / 2 - l2w / 2, 310);
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_SPACE || k == KeyEvent.VK_UP) jump();
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
