// AI for picking the battleship location
// It uses a hybrid strategy made of 3 modes
//
// 1. Target Mode 
//   - If a hit is found, it groups connected hits into clusters
//   - It tries to finish and sink the ship by expanding from that cluster
//
// 2. Hunt Mode
//    - If no active hits exist, it generates a probability heatmap
//   - This estimates where ships are most likely to exist
//
// 3. Fallback Mode
//    - If everything fails it just picks the first unknown cell

package battleship;

import java.util.*;

public class Probability implements AIPlayer {

    public Shot getNextShot(GameState state) {

        List< List<int[]> > clusters = getHitClusters(state); // Step 1: look for existing hit clusters

        for (List<int[]> cluster :clusters) { // try to finish any partially discovered ship first

            boolean sunk =isSunkCluster(state, cluster);

            if (!sunk ) { // only target clusters that are NOT already sunk
                Shot s= targetCluster(state, cluster);

                
                if (s !=  null) { // if valid target it found, use it immediately
                    return s;
                }
            }
        }

        int[][] heat = generateHeatMap(state); // Step 2: no active targets so switch to probability search
        
        Shot best =bestShot(heat, state.board);

        if (best != null) {
        	
            return best;
        }

        for (int r =0; r<10; r++) { // Step 3: worst case look for next empty cell
        	
            for (int c =0; c <10; c++) {

                if (state.board.getCell(r, c).isUnknown()) {
                	
                    return new Shot(r,  c);
                }
            }
        }

        return null; // if board is fully explored error
    }

    // finds all connected groups of HIT cells, each cluster represents part of a ship, uses DFS to group adjacent hits
    public List< List<int[]> > getHitClusters(GameState state) {

        boolean[][] vis = new boolean[10][10]; // prevents revisting 
        
        List<List<int[]>> clusters =new ArrayList<>();

        for (int r =0; r <10; r++) {
        	
            for (int c= 0; c< 10;c++) {

                if (!vis[r][c]  && state.board.getCell(r, c).isHit() ) {  // only start DFS from unvisited HIT cells

                    List<int[]> cl = new ArrayList<>();
                    
                    dfs(state, r, c,vis,cl);
                    
                    clusters.add(cl);
                }
            }
        }
        return clusters;
    }
    // DFS search to collect connected HIT cells, builds a single ship fragment cluster
    public void dfs(GameState s,int r,int c, boolean[][] vis,List<int[]> cl ) {

        if (r < 0 ||r >= 10 || c< 0 || c>= 10) { // boundary check
        	return;   
        }
        
        if (vis[r][c]) { // already visited the node
        	return;
        }
        
        if (!s.board.getCell(r,c).isHit() ) { // stop if not part of a hit cluster
        	return; 
        }

        vis[r][c] = true;
        
        cl.add(new int[]{r,c});

        dfs(s,(r + 1), c,vis, cl);  // explore all 4 directions, no diagonals
        dfs(s,(r - 1), c,vis, cl);
        
        dfs(s,r, (c + 1),vis, cl);
        dfs(s,r, (c - 1),vis, cl);
    }

// determines whether a cluster is already a sunk ship, if any remaining ship is >= cluster size, it might still exist
//otherwise assume the cluster is already resolved
    public boolean isSunkCluster(GameState state, List<int[] > cluster) {

        int size =cluster.size();

        for (Ship s :state.remainingShips) {
        	
            if (s.getLength() >=  size) {
                return false;
            }
        }

        return true;
    }

    // when there is already a hit cell, this function tries to expand around them to finish the ship
    public Shot targetCluster(GameState state, List<int[]> cl) {

        if (cl.size() ==1 ) { // only one hit found (unknown direction)

            int r = cl.get(0)[0];
            
            int c =cl.get(0)[1];

            int[][] d = { // check all 4 directions around the hit
                {1, 0},
                {-1, 0},
                {0, 1},
                
                {0, -1}
            };

            for (int i =0; i <4; i++) {

                int nr = r +d[i][0];
                
                int nc =c + d[i][1];

                if ( in(nr,nc)) {
                	

                    if (state.board.getCell(nr,nc).isUnknown() ) {
                    	
                        return new Shot(nr, nc);
                    }
                }
            }
        }

        boolean horiz = isHorizontal(cl); // // determine orientation, horizontal or vertical

        cl.sort((a,b) -> { // sort cluster to find ends of the ship
        	
            if (horiz) {
                return a[1] -b[1]; // sort by column
                
            }
            
            else {
                return a[0] -  b[0]; // sort by row
            }
        });

        int[] f =cl.get(0);  // front of ship
        
        int[] l = cl.get(cl.size() - 1); // end of ship

        if (horiz) {  // try extending horizontally

            if (in(f[0], f[1] - 1) && state.board.getCell(f[0], f[1] - 1).isUnknown()) {
            	
                return new Shot(f[0], f[1] -1);
            }

            if (in(l[0], l[1] + 1) && state.board.getCell(l[0], l[1] + 1).isUnknown()) {
            	
                return new Shot(l[0], l[1] + 1);
            }

        } 
        else { // try extending vertically

            if (in(f[0] - 1, f[1]) && state.board.getCell(f[0] - 1, f[1]).isUnknown()) {
            	
                return new Shot(f[0] - 1, f[1]);
            }

            if (in(l[0] + 1, l[1]) &&  state.board.getCell(l[0] + 1, l[1]).isUnknown()) {
            	
                return new Shot(l[0] + 1, l[1]);
            }
        }

        return null;
    }

    public boolean isHorizontal(List<int[]>  cl) { // if first two points share same row it is horizontal

        if (cl.size() <2) {
        	
            return true;
        }

        if (cl.get(0)[0 ] ==cl.get(1)[0]) {
        	
            return true;
        }

        return false;
    }

    public boolean in(int r, int c) { // bounds checker for grid 

        if (r <0 || r >=10) {
        	return false;
        }
        
        if (c< 0 || c >=10) {
        	
        	return false;
        }

        return true;
    }

    // builds a heatmap representing how likely each cell is to contain part of a remaining ship
    public int[][] generateHeatMap(GameState state) {

        int[][] heat = new int[10][10];

        for (Ship s :state.remainingShips) {
        	
            add(state, heat, s.getLength());
        }

        return heat;
    }

    public void add(GameState s, int[][] h,int len) { //adds probability contributions for a given ship length

        for (int r = 0; r < 10; r++) {  // horizontal placements
        	
            for (int c =0; c <=10 - len; c++) {

                if ( valid(s, r, c, len, true)) {

                    for (int i =0; i <len; i++) {
                    	
                        h[r][c + i]  += len * len; // longer ships increase the score more
                    }
                }
            }
        }

        for (int r =0; r <=10 - len; r++) {  // vertical placements
        	
            for (int c =0; c <10; c++) {

                if (valid(s, r, c, len, false) ) {

                    for (int i =0; i <len; i++) {
                    	
                        h[r + i][c]  += len *  len;
                    }
                }
            }
        }
    }

    public boolean valid(GameState s, int r,int c,int len,boolean h) { //checks if a ship placement is valid

        for (int i =0; i <  len; i++) {

            int rr;
            
            int cc;

            if (h) {
            	
                rr =r;
                cc = (c + i);
                
            } 
            else {
                rr =(r + i);
                cc = c;
            }

            Cell cell = s.board.getCell(rr, cc);

            if (cell.isMiss()) {
            	
            	return false;
            }
        }

        return true;
    }

    public Shot bestShot(int[][] heat, Board b) { // selects best shot from heatmap, (i + j ) % 2 == 0 reduces wasted shots

        int max = -1;
        
        Shot best = null;

        for (int i =0; i <10; i++) {
        	
            for (int j =0; j <10; j++) {

                if (!b.getCell(i, j).isUnknown()) { // if sell has been fired on skip it
                	continue;
                }
                
                if ((i + j) % 2 != 0) { // evaluate half the board
                	continue;
                }

                if (heat[i][j] >  max) {
                	
                    max =heat[i][j];
                    
                    best = new Shot(i, j);
                }
            }
        }

        return best;
    }
}