// selects shots completely at random from the remaining unknown cells on the board

package battleship;

import java.util.Random;

public class RandomAI implements AIPlayer {
	
    Random rand = new Random();

    
    public Shot getNextShot(GameState state ) {
    	
        while (true ) {
        	
            int r =rand.nextInt(10);
            
            int c = rand.nextInt(10);
            
            if (state.board.getCell(r, c).isUnknown( )) {
                return new Shot(r,c);
            }
        }
    }
}