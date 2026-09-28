// Holds the full current state of a single battleship game
// Contains the board ship positions and cell states
// Tracks which ships are still alive

package battleship;

import java.util.*;

public class GameState {

    public Board board;
    
    public List<Ship> remainingShips;

    public GameState() {

        board = new Board();
        
        remainingShips =new ArrayList<>(board.getShips()); // initializes remaining ships
    }
}