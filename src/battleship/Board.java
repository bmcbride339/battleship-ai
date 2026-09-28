// Represents the 10x10 Battleship grid
// - Stores all cell objects (hit or miss tracking)
// - Randomly generates and places ships for testing
// -Ensures valid ship placement
// - Provides lookup functions to check if a ship is at a coordinate

package battleship;

import java.util.*;

public class Board {

    Cell[][] grid = new Cell[10][10 ];
    
    List<Ship> ships =new ArrayList<>();
    
    Random rand = new Random();

    public Board() {

        for (int r=0; r <10; r++) { // initialize all cells as UNKNOWN at start of game
        	
            for (int c = 0; c<10; c++) {
            	
                grid[r ][c] = new Cell();
            }
        }

        placeShips();
    }

    public void placeShips() { // randomly places all ships on the board for simulations

        int[] shipSizes = {5,4,3,3,2};

        for (int size : shipSizes) {

            boolean placed =false;

            while (!placed ) {

                boolean horizontal = rand.nextBoolean();

                int r =rand.nextInt(10);
                int c = rand.nextInt(10 );

                if (canPlace(r,c,size, horizontal)) {

                    Ship s =new Ship(size);

                    for (int i =0; i<size; i++) { // assign ship cells

                    	int rr;
                    	
                    	int cc;

                    	if (horizontal ) {
                    	    rr = r;
                    	    cc = (c + i);
                    	    
                    	} 
                    	else {
                    	    rr =r + i;
                    	    cc = c;
                    	}

                        s.addCell(rr,cc);
                    }

                    ships.add(s);
                    
                    placed =true;
                }
            }
        }
    }

    public boolean canPlace(int r, int c, int size, boolean h) { // checks whether a ship can be placed at a location

        for (int i =0; i <size; i++) {

        	int rr;
        	int cc;

        	if (h ) {
        	    rr =r;
        	    cc = (c + i);
        	    
        	} 
        	else {
        	    rr = (r+i);
        	    cc = c;
        	}

            if (rr >= 10 ||cc >= 10) { // boundary check
            	return false;  
            }

            for (Ship s :ships) { // overlap check with existing ships
                if (s.occupies(rr,cc)) {
                	
                	return false;
                }
            }
        }

        return true;
    }

    public Cell getCell(int r,int c ) {
    	
        return grid[r][c];
    }

    public List<Ship> getShips() {
        return ships;
    }

    public Ship getShipAt(int r, int c ) { // returns the ship located at a coordinate, or null if empty water

        for (Ship s :ships) {
        	
            if (s.occupies(r,c)) {
            	return s;
            }
        }

        return null;
    }
}