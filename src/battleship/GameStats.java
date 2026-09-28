// Tracks performance across multiple battleship games
// Used by the BattleSimulator to compute total number of games played, total shots taken, and average shots per game

package battleship;

public class GameStats {

    int totalShots = 0;
    int games = 0;

    public void recordGame(int shots) { // records the number of shots taken in a single completed game
        totalShots +=shots;
        games++;
    }

    public double getAverageShots() { // returns the average number of shots required to finish a game
    	
        if (games ==0) {
            return 0;
        }
        
        return (double) (totalShots / games);
    }
}