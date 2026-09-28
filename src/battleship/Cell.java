// Represents a single square on the battleship board, each cell tracks its current known state

package battleship;

public class Cell {

    public enum State { 
    	UNKNOWN, HIT, MISS
    }

    public State state;

    public Cell() {
    	
        state = State.UNKNOWN;
    }

    public State getState() { // returns current state of this cell
        return state;
    }

    public void setHit() { // marks this cell as a successful hit
    	
        state =State.HIT;
    }

    public void setMiss() { // marks this cell as a miss
    	
        state =State.MISS;
    }

    public boolean isUnknown() { // true if cell has not been fired on yet
    	
        return state ==State.UNKNOWN;
    }

    public boolean isHit() { // true if this cell contains a hit
        return state == State.HIT;
    }

    public boolean isMiss() { // true if this cell was fired on and missed
    	
        return state == State.MISS;
    }
}