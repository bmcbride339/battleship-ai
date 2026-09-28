# Battleship AI

A Java Battleship assistant built for an algorithms course. The application combines a Swing interface with a hunt-and-target strategy to recommend shots, track feedback, and compare its performance with random guessing.

## Features

- A 10-by-10 board with labeled coordinates and color-coded shot results.
- Manual Miss, Hit, and Sunk feedback for recommended shots.
- A weighted ship-placement heatmap and checkerboard search pattern.
- Depth-first search to group connected hits and target adjacent cells.
- Per-game and multi-game statistics.
- A simulator that runs 5,000 games per strategy and reports average shots.

## Requirements

Use JDK 21 to match the included Eclipse project configuration. The graphical interface needs a desktop environment. No third-party libraries are required.

## Run from a terminal

From the project folder:

```sh
javac -d bin src/battleship/*.java
java -cp bin battleship.MAIN
```

Run the automated simulation:

```sh
java -cp bin battleship.BattleSimulator
```

The simulator compares the probability strategy with a random strategy. It generates new random boards for each game, so results vary between runs. Change `GAMES` in `BattleSimulator.java` and recompile to adjust the number of games.

## Run in Eclipse

1. Import the folder using **File > Import > General > Existing Projects into Workspace**.
2. Select a JDK 21 installation for the project.
3. Run `src/battleship/MAIN.java` as a Java application.
4. To run simulations instead, launch `BattleSimulator.java`.

## Use the assistant

1. Set up a separate Battleship board with ships of lengths 5, 4, 3, 3, and 2.
2. Read the recommended coordinate at the bottom of the window.
3. Take that shot on the separate board, then click the recommended cell in the application.
4. Select **Miss**, **Hit**, or **Sunk** to record the result.
5. Continue until all ships are sunk. Use **Play Again???** to reset the game.

## How the strategy works

In target mode, the algorithm uses depth-first search to find connected hit clusters. It probes neighboring cells for a single hit and extends the ends of larger clusters. When no useful target is found, it scores possible placements of the remaining ship lengths, selects an unknown cell on a checkerboard pattern, and falls back to another unknown cell if necessary.

## Main classes

| Class | Purpose |
| --- | --- |
| `MAIN` | Launches the desktop interface |
| `GUI` | Displays the board and collects shot feedback |
| `Probability` | Implements the hunt-and-target strategy |
| `RandomAI` | Provides a random-shot baseline |
| `BattleSimulator` | Runs batches of simulated games |
| `Board`, `Cell`, `Ship` | Model the board, cells, and ships |
| `GameState`, `GameStats`, `Shot` | Track game state, statistics, and coordinates |
| `AIPlayer` | Defines the strategy interface |

## Current limitations

The manual interface marks cells surrounding a sunk ship as misses, so it assumes ships do not touch, including diagonally. The simulator prevents overlapping ships but permits touching ships, so its placement rules differ from the manual interface. Connected-hit grouping and sunk-cluster detection use heuristics and may be imperfect for touching ships.

Simulation averages describe this implementation and are not a claim of an optimal Battleship strategy.

## Author

Braden McBride
