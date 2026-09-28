// represents a single ship placed on the Battleship board
//- stores the ships length
// - tracks which grid cells it occupies
// - tracks which parts have been hit
// - determines when the ship is sunk

package battleship;

import java.util.*;

public class Ship {

    int length;
    List <int[] > cells = new ArrayList<>(); // stores all coordinates this ship occupies on the board
    
    Set<String > hits =  new HashSet<>(); // stores hit coordinates as "r,c" string

    public Ship(int length) {
    	
        this.length = length;
    }

    public int getLength() { //  returns ship size
    	
        return length;
    }

    public void addCell(int r, int c) { // adds a coordinate to this ships occupied cells
    	
        cells.add(new int[ ]{r, c });
    }

    public boolean occupies(int r, int c) { // checks whether this ship occupies a specific board cell
    	
        for (int[] cell :cells ) {
            if (cell[0] ==r && cell[1] ==c) {
            	
            	return true;
            }
        }
        return false;
    }

    public void hit(int r,int c ) { // records a hit on this ship at a specific coordinate
        hits.add(r + "," +c);
    }

    public boolean isSunk() { // determines if the ship is completely destroyed, if number of hits equals ship length the ship is sunk
    	
        return hits.size() >=  length;
    }
}