// Any simulator that plays the game must implement getNextShot(),
// which receives the current GameState and returns the next  coordinate to fire at.

package battleship;

public interface AIPlayer {
	
    Shot getNextShot(GameState state );
}