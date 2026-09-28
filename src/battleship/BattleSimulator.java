// Simulates full games (Probability vs Random ) and measures performance based on number of shots required to sink all ships.

package battleship;

public class BattleSimulator {

    static final int GAMES = 5000; // number of simulated games 

    public static void main(String[] args) {

        BattleSimulator sim = new BattleSimulator();

        System.out.println("=== BATTLESHIP TESTER ===\n");

        sim.run(new Probability(), "My Algorithm ");
        
        sim.run(new RandomAI(),"Random ");
    }

    
    public void run(AIPlayer ai, String name) {// runs a batch of games for a given Simulation and prints stats

        GameStats stats = new GameStats();

        for (int i =0; i <GAMES; i++) {
        	
            stats.recordGame(play(ai));
        }

        System.out.println(name);
        
        System.out.println("Games: " + GAMES);
        System.out.println("Avg Shots: " + stats.getAverageShots());
        System.out.println("-------------------------------------\n");
    }

    public int play(AIPlayer ai) { // simulates one full battleship game until all ships are sunk

        GameState state =new GameState();

        int shots = 0;

        while (!state.remainingShips.isEmpty( )) {

            Shot shot = ai.getNextShot(state);
            
            shots++;

            applyShot(state,  shot);
        }

        return shots;
    }

 // applies a shot to the game state, checks if hit or miss, updates board state, updates ship damage, removes ship if sunk
    public void applyShot(GameState state, Shot shot) { 

        int r =shot.row;
        
        int c = shot.col;

        Cell cell = state.board.getCell(r,c );

        if (!cell.isUnknown() ) {// prevent re processing the same cell
        	return;  
        }

        Ship ship =state.board.getShipAt(r, c);

        if (ship != null) {

            cell.setHit();
            
            ship.hit(r,c);

            if (ship.isSunk()) {  // remove ship once destroyed
            	
                state.remainingShips.remove(ship);
            }

        } else {
        	
            cell.setMiss();
        }
    }
}