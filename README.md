# Survivor Game

A small tile-based survival game written in **Java (Swing)**.
Move around a 10x10 map, collect all the cereals and avoid the enemies.

<img width="248" height="290" alt="survivorgame" src="https://github.com/user-attachments/assets/7be63753-a650-4a6a-8d8f-f7555fa6b8a5" />

## Gameplay

- Move with **W / A / S / D** or the **arrow keys**
- Enemies are placed randomly on the map. Stepping on one costs 5-14 health
- Each cereal restores 10 health (max 100)
- **Win:** collect all 5 cereals
- **Lose:** health reaches 0
- Press **R** to restart

## Tech

- Java (JDK 17 or newer), Swing / AWT
- No external libraries

## How to run

Requires a JDK (17+) installed.

```bash
git clone https://github.com/Danny/survivor-game.git
cd survivor-game
java -cp src src/survivalgame2/SurvivalGameGUI.java
```

Alternatively, open the `src` folder in NetBeans, IntelliJ IDEA or Eclipse and run `SurvivalGameGUI`.

## Project structure

```
src/survivalgame2/
├── SurvivalGameGUI.java   # game logic, input handling and rendering
├── grass.jpg
├── player.jpg
├── enemy.jpg
└── cereale.jpg
```

## About this project

This is a small personal project I made to practice Java, GUI rendering,
keyboard input and basic game logic. It is intentionally simple.

Ideas for future improvements:
- Split the code into separate classes (game state, renderer, entities)
- Multiple levels and difficulty settings
- Sound effects and better graphics
- Unit tests for the game logic
