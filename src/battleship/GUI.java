// Swing based interface for manually playing and testing 
// Displays a 10x10 grid representing the board
// Drives Algorithm
// Lets the user manually confirm AI shot results, hit, miss, or sunk
// Visually updates board state (colors for hits, misses, and sunk ships)
// Tracks individual game and multiple game statistics


package battleship;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GUI extends JFrame {

    GameState state = new GameState();
    
    Probability ai =new Probability();

    JButton[][] buttons =new JButton[10][10];
    
    JLabel info = new JLabel("Click to start" );
    JButton playAgain = new JButton("Play Again???");

    Shot lastShot;

    
    int hits =0; // current game stats
    int misses =0;
    int shotCount = 0;

    
    int totalGames =0; // multiple game stats
    int totalHits =  0;
    int totalMisses =0;
    int totalShots =0;

    public GUI() {

        setTitle("Battleship Algorithm");
        
        setSize(700, 700);
        
        setLayout(new BorderLayout());

        JPanel grid =new JPanel(new GridLayout(11, 11));
        

        grid.add(new JLabel("")); // top left empty corner

        for (int i =1; i <=10;i++) { // column labels 1-10
        	
            grid.add(new JLabel(String.valueOf(i), SwingConstants.CENTER));
            
        }

        for (int r =0; r <10; r++) { // // board grid with row labels A-J

            grid.add(new JLabel (String.valueOf( (char) ('A' + r)), SwingConstants.CENTER) );

            for (int c =0; c <10; c++) {

                JButton b = new JButton();
                
                buttons[r][c] =  b;

                int rr =r;
                int cc =c;

                b.addActionListener(e -> handleClick(rr,cc) );  // each button represents a coordinate the AI is targeting

                grid.add(b);
            }
        }

        JPanel bottom = new JPanel(new BorderLayout() );
        
        bottom.add(info,  BorderLayout.CENTER);
        bottom.add(playAgain, BorderLayout.EAST);

        add(grid, BorderLayout.CENTER);
        
        add(bottom, BorderLayout.SOUTH );

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setVisible(true);

        playAgain.addActionListener(e ->  resetGame());

        nextMove();
    }

    
    public void nextMove() { // requests the next move from the AI and displays it
    	
        lastShot = ai.getNextShot(state);
        
        info.setText("Next Shot to take: " + format(lastShot) + " | Ships left: " + getRemainingShips());
    }

    
    public void handleClick(int r, int c) { // handles user confirmation of whether the AI's shot was a hit, miss, or sunk

        if (lastShot == null) {
        	return;
        }
        
        if (r != lastShot.row ||c != lastShot.col) {
        	return;
        }

        Object[] opts = {"Miss","Hit","Sunk"};

        int res = JOptionPane.showOptionDialog( this, "Result?","Shot", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
        		opts, opts[0] );

        shotCount++;

        if (res == 0) {
        	
            markMiss(r, c);
            misses++;
        } 
        else if (res == 1) {
        	
            markHit(r, c);
            hits++;
        } 
        
        else if (res == 2) {
        	
            markSunk(r, c);
            hits++;
        }

        if (state.remainingShips.isEmpty()) {
        	
            gameOver();
            return;
        }

        nextMove();
    }

    
    public void markMiss(int r,int c) { // marks a miss visually and in the game state
        state.board.getCell(r, c ).setMiss();
        
        buttons[r][c].setBackground(Color.BLUE );
    }

    public void markHit(int r, int c) { // marks a hit visually and in the game state
        state.board.getCell(r,c).setHit();
        
        buttons[r][c].setBackground(Color.RED);
    }

    public void markSunk(int r, int c) { // marks hit cell, identifies full ship cluster, removes ship from remaining list , marks surrounding cells as misses

        state.board.getCell(r, c).setHit();
        
        buttons[r][c].setBackground(Color.BLACK);

        List<int[]> cluster = getCluster(r,c);
        
        int size = cluster.size();

        
        Iterator<Ship> it =  state.remainingShips.iterator(); // remove the sunk ship matched by size

        while (it.hasNext() ) {
        	
            Ship s = it.next();
            
            if (s.getLength() ==size) {
            	
                it.remove();
                break;
            }
        }

        
        for (int[] cell : cluster ) { // mark surrounding area as misses

            for (int dr =  -1; dr <=1; dr++) {
            	
                for (int dc = -1; dc<=1; dc++ ) {

                    int nr = cell[0] + dr;
                    
                    int nc= cell[1] + dc;

                    if (nr  >= 0 && nr <10 && nc >= 0  && nc <10) {

                        Cell c2 = state.board.getCell(nr,nc);

                        if (c2.isUnknown()) {
                        	
                            c2.setMiss();
                            buttons[nr][nc ].setBackground(Color.BLUE );
                        }
                    }
              }
          }
        }
    }

    
    public List<int[]> getCluster(int r,int c) { // finds all connected hit cells, used to reconstruct a sunk ship shape

        boolean[][] vis = new boolean[10][10];
        
        List<int[]> cl =  new ArrayList<>();

        dfs(r, c,vis, cl );

        return cl;
    }

    public void dfs(int r, int c, boolean[][] vis, List<int[]> cl) { // depth first search used to collect connected hit cells

        if (r < 0) return; // boundary check
        if (r>=10) return;
        
        if (c <0) return;
        if (c >=10) return;

        if (vis[r][c] ) return;
        
        if (!state.board.getCell(r, c).isHit()) {
        	return;
        }

        vis[r][c] = true;

        int[] cell = new int[2];
        
        cell[0] =r;
        
        cell[1] = c;

        cl.add(cell);

        dfs(r + 1,c, vis,cl);
        dfs(r - 1, c,vis,cl);
        
        dfs(r,c +1, vis,cl);
        dfs(r, c -1,vis,  cl);
    }

    
    public double currentHitPercent() { // statistics

        int total = hits + misses;

        if (total == 0) {
        	return 0;
        }

        return (hits* 100.0) / total;
    }

    public double overallHitPercent() {

        int total = totalHits + totalMisses;

        if (total == 0) {
        	return 0;
        }

        
        return (totalHits *100.0) / total;
    }

    public double avgShots() {

        if (totalGames ==0) {
        	return 0;
        }

        return (double) (totalShots / totalGames);
    }

    
    public void gameOver() { // called when all ships are sunk

        totalGames++;
        totalHits +=hits;
        
        totalMisses +=misses;
        totalShots += shotCount;

        String msg =  "GAME COMPLETE! |:D\n\n" +

                "CURRENT GAME\n" +
                
                "Shots: " + shotCount + "\n" +
                "Hits: " + hits +"\n" +
                "Misses: "+ misses+ "\n" +
                "Hit %: "+ String.format("%.2f", currentHitPercent()) + "%\n\n" +

                "SESSION STATS\n"+
                
                "Games: " +totalGames +"\n" +
                "Shots: "+ totalShots +"\n" +
                "Hits: " + totalHits + "\n" +
                "Misses: " +totalMisses +"\n" +
                
                "Hit %: "+ String.format("%.2f", overallHitPercent()) +"%\n" +
                "Avg Shots/Game: " + String.format("%.2f", avgShots()) + "\n";

        JOptionPane.showMessageDialog(this,  msg);
 
        info.setText("Game Over!");
    }

    
    public void resetGame() { // resets the game state and UI for a new match

        state = new GameState();
        
        ai = new Probability();
        lastShot =null;

        hits = 0;
        misses = 0;
        shotCount =0;

        for (int r =0; r <10; r++) {
            for (int c =0; c <10; c++) {
            	
                buttons[r][c].setBackground(null);
            }
        }

        info.setText("New game started!");
        
        nextMove();
    }

    
    public String getRemainingShips() {

        if (state.remainingShips.isEmpty()) {
        	return "None";
        }

        StringBuilder sb = new StringBuilder();

        for (Ship s :state.remainingShips) {
        	
            sb.append(s.getLength()).append(" " );
        }

        return sb.toString();
    }

    public String format(Shot s) {
    	
        return "(" +(char) ('A' + s.row) + "," +(s.col + 1) + ")";
    }
}