package algorithms;

import java.util.List;
import model.Board;
import model.Move;

public abstract class SearchAlgorithm {
    protected int nodesExplored;
    protected long startTime;
    
    public abstract List<Move> solve(Board initialBoard);
    
    public int getNodesExplored() {
        return nodesExplored;
    }
    
    public long getSearchTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    protected void printStats(boolean solutionFound) {
        System.out.println("\n=== Search Statistics ===");
        System.out.println("Solution found: " + solutionFound);
        System.out.println("Nodes explored: " + nodesExplored);
        System.out.printf("Time taken: %.3f seconds%n", getSearchTime() / 1000.0);
    }
}