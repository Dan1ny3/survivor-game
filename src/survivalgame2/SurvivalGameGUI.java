package survivalgame2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Survivor Game - a small tile-based survival game built with Java Swing.
 *
 * Goal: collect all the cereals on the map without running out of health.
 * Enemies are on the map too: stepping on one costs health.
 *
 * Controls: W/A/S/D or arrow keys to move, R to restart.
 */
public class SurvivalGameGUI extends JPanel {

    // ----- Game settings -----
    private static final int TILE_SIZE = 50;
    private static final int GRID_SIZE = 10;
    private static final int STATUS_BAR_HEIGHT = 50;
    private static final int MAX_HEALTH = 100;
    private static final int NUM_ENEMIES = 12;
    private static final int NUM_CEREALS = 5;
    private static final int CEREAL_HEALTH_BOOST = 10;
    private static final int ENEMY_MIN_DAMAGE = 5;
    private static final int ENEMY_MAX_DAMAGE = 14;

    // ----- What can be on a tile -----
    private enum Tile { EMPTY, ENEMY, CEREAL }

    private enum GameState { PLAYING, WON, LOST }

    private final Tile[][] map = new Tile[GRID_SIZE][GRID_SIZE];
    private final Random random = new Random();

    private final Image playerImage = loadImage("player.jpg");
    private final Image enemyImage = loadImage("enemy.jpg");
    private final Image grassImage = loadImage("grass.jpg");
    private final Image cerealImage = loadImage("cereale.jpg");

    private int playerRow;
    private int playerCol;
    private int playerHealth;
    private int cerealsLeft;
    private GameState state;
    private String message;

    public SurvivalGameGUI() {
        setPreferredSize(new Dimension(GRID_SIZE * TILE_SIZE,
                GRID_SIZE * TILE_SIZE + STATUS_BAR_HEIGHT));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });
        startNewGame();
    }

    // ----- Setup -----

    private void startNewGame() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                map[row][col] = Tile.EMPTY;
            }
        }

        playerRow = GRID_SIZE / 2;
        playerCol = GRID_SIZE / 2;
        playerHealth = MAX_HEALTH;
        cerealsLeft = NUM_CEREALS;
        state = GameState.PLAYING;
        message = "Collect all cereals and avoid enemies!";

        placeRandomly(Tile.ENEMY, NUM_ENEMIES);
        placeRandomly(Tile.CEREAL, NUM_CEREALS);
        repaint();
    }

    /** Places 'count' tiles of the given type on random free cells (never on the player). */
    private void placeRandomly(Tile type, int count) {
        for (int i = 0; i < count; i++) {
            int row;
            int col;
            do {
                row = random.nextInt(GRID_SIZE);
                col = random.nextInt(GRID_SIZE);
            } while (map[row][col] != Tile.EMPTY || (row == playerRow && col == playerCol));
            map[row][col] = type;
        }
    }

    // ----- Game logic -----

    private void handleKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> movePlayer(-1, 0);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movePlayer(1, 0);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movePlayer(0, -1);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movePlayer(0, 1);
            case KeyEvent.VK_R -> startNewGame();
            default -> { }
        }
    }

    private void movePlayer(int dRow, int dCol) {
        if (state != GameState.PLAYING) {
            return;
        }

        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;
        boolean insideMap = newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE;
        if (!insideMap) {
            return;
        }

        playerRow = newRow;
        playerCol = newCol;
        interactWith(map[newRow][newCol]);
        map[newRow][newCol] = Tile.EMPTY; // enemies and cereals disappear once used
        repaint();
    }

    private void interactWith(Tile tile) {
        switch (tile) {
            case ENEMY -> {
                int damage = ENEMY_MIN_DAMAGE + random.nextInt(ENEMY_MAX_DAMAGE - ENEMY_MIN_DAMAGE + 1);
                playerHealth -= damage;
                message = "An enemy hit you! -" + damage + " health";
                if (playerHealth <= 0) {
                    playerHealth = 0;
                    state = GameState.LOST;
                    message = "You died! Press R to restart.";
                }
            }
            case CEREAL -> {
                playerHealth = Math.min(playerHealth + CEREAL_HEALTH_BOOST, MAX_HEALTH);
                cerealsLeft--;
                message = "You found cereal! +" + CEREAL_HEALTH_BOOST + " health";
                if (cerealsLeft == 0) {
                    state = GameState.WON;
                    message = "You collected everything - you win! Press R to play again.";
                }
            }
            case EMPTY -> { }
        }
    }

    // ----- Rendering -----

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;
                drawTile(g, grassImage, new Color(80, 160, 80), x, y);
                switch (map[row][col]) {
                    case ENEMY -> drawTile(g, enemyImage, Color.RED, x, y);
                    case CEREAL -> drawTile(g, cerealImage, Color.ORANGE, x, y);
                    case EMPTY -> { }
                }
            }
        }
        drawTile(g, playerImage, Color.BLUE, playerCol * TILE_SIZE, playerRow * TILE_SIZE);

        drawStatusBar(g);
    }

    private void drawTile(Graphics g, Image image, Color fallback, int x, int y) {
        if (image != null) {
            g.drawImage(image, x, y, TILE_SIZE, TILE_SIZE, null);
        } else {
            g.setColor(fallback); // used if an image file is missing
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        }
    }

    private void drawStatusBar(Graphics g) {
        int barTop = GRID_SIZE * TILE_SIZE;
        g.setColor(Color.WHITE);
        g.fillRect(0, barTop, GRID_SIZE * TILE_SIZE, STATUS_BAR_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawString("Health: " + playerHealth + "   Cereals left: " + cerealsLeft, 10, barTop + 20);
        g.drawString(message, 10, barTop + 40);
    }

    /** Loads an image that sits next to this class (same package). Returns null if not found. */
    private static Image loadImage(String fileName) {
        java.net.URL url = SurvivalGameGUI.class.getResource(fileName);
        return url == null ? null : new ImageIcon(url).getImage();
    }

    // ----- Entry point -----

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Survivor Game");
            frame.add(new SurvivalGameGUI());
            frame.pack();
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
