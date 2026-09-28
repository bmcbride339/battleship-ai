// represents a single attack on the board
// stores the row and column of a move 

package battleship;

public class Shot {
	
    public int row;
    public int col;

    public Shot(int r,int c) {
        row =r;
        col = c;
    }
}
