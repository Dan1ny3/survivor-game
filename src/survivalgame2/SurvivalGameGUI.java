package survivalgame2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

// Clasa principală pentru jocul grafic
public class SurvivalGameGUI extends JPanel implements KeyListener {
    private final int tileSize = 50; // Dimensiunea fiecărei celule din hartă
    private final int gridSize = 10; // Dimensiunea hărții (10x10)
    private final char[][] map = new char[gridSize][gridSize];

    private int playerX = gridSize / 2; // Poziția inițială a jucătorului
    private int playerY = gridSize / 2;

    private final Image playerImage;
    private final Image enemyImage;
    private final Image grassImage;
    private Image cerealeImage;


    private boolean running = true;
    private int playerHealth = 100;

    private void increaseHealth(int healthBoost) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void decreaseHealth(int damage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

abstract class MapObject {
    protected String name;

    public MapObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Metodă abstractă pentru interacțiunea cu playerul
    public abstract void interact(SurvivalGameGUI game);
}

class Cereal extends MapObject {
    private final int healthBoost;

    public Cereal(int healthBoost) {
        super("Cereal");
        this.healthBoost = healthBoost;
    }

    @Override
    public void interact(SurvivalGameGUI game) {
        JOptionPane.showMessageDialog(game, "Ai găsit cereale! +" + healthBoost + " Sănătate!");
        game.increaseHealth(healthBoost);
    }
}

class Enemy extends MapObject {
    public Enemy() {
        super("Enemy");
    }

    @Override
    public void interact(SurvivalGameGUI game) {
        JOptionPane.showMessageDialog(game, "Ai întâlnit un inamic!");
        Random random = new Random();
        int damage = random.nextInt(10) + 5; // Pierzi sănătate între 5 și 15
        game.decreaseHealth(damage);
    }
}

    public SurvivalGameGUI() {
        
        
        // Încarcă imaginile
        playerImage = new ImageIcon("player.jpg").getImage();
        enemyImage = new ImageIcon("enemy.jpg").getImage();
        grassImage = new ImageIcon("grass.jpg").getImage();
        cerealeImage = new ImageIcon("cereale.jpg").getImage();
        
        // Inițializează harta
        initializeMap();

        // Setează dimensiunea ferestrei și ascultătorul de taste
        setPreferredSize(new Dimension(gridSize * tileSize, gridSize * tileSize));
        addKeyListener(this);
        setFocusable(true);
    }

    private void initializeMap() {
    for (int i = 0; i < gridSize; i++) {
        for (int j = 0; j < gridSize; j++) {
            map[i][j] = '0'; // Loc liber
        }
    }
    map[playerX][playerY] = 'P'; // Poziționează jucătorul inițial

    // Generează inamici pe hartă
    Random random = new Random();
    int numEnemies = 12; // Număr de inamici
    for (int i = 0; i < numEnemies; i++) {
        int enemyX, enemyY;
        do {
            enemyX = random.nextInt(gridSize);
            enemyY = random.nextInt(gridSize);
        } while (map[enemyX][enemyY] != '0'); // Evită suprapunerea
        map[enemyX][enemyY] = 'E'; // Plasează un inamic
    }

    // Generează pizza pe hartă
    int numCereale = 5; // Număr de cereale
    for (int i = 0; i < numCereale; i++) {
        int cerealeX, cerealeY;
        do {
            cerealeX = random.nextInt(gridSize);
            cerealeY = random.nextInt(gridSize);
        } while (map[cerealeX][cerealeY] != '0'); // Evită suprapunerea
        map[cerealeX][cerealeY] = 'Z'; // Plasează cerealele
    }
}



    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Desenează harta
    for (int i = 0; i < gridSize; i++) {
        for (int j = 0; j < gridSize; j++) {
            g.drawImage(grassImage, j * tileSize, i * tileSize, tileSize, tileSize, null); // Fundal
            if (map[i][j] == 'P') {
                g.drawImage(playerImage, j * tileSize, i * tileSize, tileSize, tileSize, null); // Player
            } else if (map[i][j] == 'E') {
                g.drawImage(enemyImage, j * tileSize, i * tileSize, tileSize, tileSize, null); // Inamic
            } else if (map[i][j] == 'Z') {
                g.drawImage(cerealeImage, j * tileSize, i * tileSize, tileSize, tileSize, null); // Pizza
            }
        }
    }

    // Desenează statistici
    g.setColor(Color.WHITE);
    g.fillRect(0, gridSize * tileSize, gridSize * tileSize, 50);
    g.setColor(Color.BLACK);
    g.drawString("Sănătate: " + playerHealth, 10, gridSize * tileSize + 25);
}



    public void movePlayer(int dx, int dy) {
    if (!running) return;

    int newX = playerX + dx;
    int newY = playerY + dy;

    // Verifică limitele hărții
    if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
        char target = map[newX][newY];

        if (target == 'E') {
            // Luptă cu inamicul
            JOptionPane.showMessageDialog(this, "Ai întâlnit un inamic!");
            Random random = new Random();
            playerHealth -= random.nextInt(10) + 5; // Pierzi sănătate
            if (playerHealth <= 0) {
                running = false;
                JOptionPane.showMessageDialog(this, "Ai murit! Jocul s-a terminat.");
                return;
            }

            // Elimină inamicul
            map[newX][newY] = '0';
            } else if (target == 'Z') {
            // Colectează cerealele
            JOptionPane.showMessageDialog(this, "Ai găsit o pizza! +10 Sănătate!");
            playerHealth = Math.min(playerHealth + 10, 100); // Crește sănătatea, maxim 100
            map[newX][newY] = '0';
        }

        // Mută jucătorul
        map[playerX][playerY] = '0'; // Șterge poziția anterioară
        playerX = newX;
        playerY = newY;
        map[playerX][playerY] = 'P'; // Actualizează poziția jucătorului
        repaint();
    }
}


    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> movePlayer(-1, 0); // Sus
            case KeyEvent.VK_A -> movePlayer(0, -1); // Stânga
            case KeyEvent.VK_S -> movePlayer(1, 0);  // Jos
            case KeyEvent.VK_D -> movePlayer(0, 1);  // Dreapta
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Survival Game");
        SurvivalGameGUI gamePanel = new SurvivalGameGUI();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
