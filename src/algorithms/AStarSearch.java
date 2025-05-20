package algorithms;

import java.util.*;

import model.Board;
import model.Move;
import heuristics.Heuristic;

public class AStarSearch extends SearchAlgorithm {
    private final Heuristic heuristic;
    
    public AStarSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    // Node yang merepresentasikan satu state dalam pencarian
    private static class Node {
        Board board;
        List<Move> path;
        int cost; // Biaya dari root ke node ini (g(n))
        int heuristic; // Perkiraan biaya ke goal (h(n))
        int total;
        
        Node(Board board, List<Move> path, int cost, int heuristic) {
            this.board = board;
            this.path = path;
            this.cost = cost;
            this.heuristic = heuristic;
            this.total = cost + heuristic;
        }
    }

    @Override
    public List<Move> solve(Board initialBoard) {
        startTime = System.currentTimeMillis(); // Inisialisasi waktu pencarian
        nodesExplored = 0; // Inisialisasi jumlah node yang dieksplorasi
        
        // PriorityQueue berdasarkan total cost f(n) = g(n) + h(n)
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.total));
        // Menyimpan cost minimum untuk setiap state
        Map<String, Integer> costMap = new HashMap<>();
        
        // Menambahkan node awal
        queue.add(new Node(initialBoard, new ArrayList<>(), 0, heuristic.calculate(initialBoard)));
        costMap.put(initialBoard.getStateString(), 0);
        
        while (!queue.isEmpty()) {
            Node current = queue.poll(); // Mengambil node dengan f(n) terendah
            nodesExplored++;
            
            // Kasus goal state tercapai
            if (current.board.isSolved()) {
                printStats(true);
                return current.path;
            }
            
            // Skip jika kita sudah menemukan jalur yang lebih murah ke state ini
            if (current.cost > costMap.getOrDefault(current.board.getStateString(), Integer.MAX_VALUE)) {
                continue;
            }
            
            // Eksplorasi semua langkah legal dari state saat ini
            for (Move move : current.board.getPossibleMoves()) {
                Board newBoard = current.board.movePiece(move.getPiece(), move.getDirection(), move.getSteps());
                int newCost = current.cost + move.getSteps();
                String state = newBoard.getStateString();
                
                // Jika ditemukan jalur lebih murah ke state ini, simpan dan tambahkan ke queue
                if (newCost < costMap.getOrDefault(state, Integer.MAX_VALUE)) {
                    List<Move> newPath = new ArrayList<>(current.path);
                    newPath.add(move);
                    
                    queue.add(new Node(newBoard, newPath, newCost, heuristic.calculate(newBoard)));
                    costMap.put(state, newCost); // Memperbarui cost minimum ke state ini
                }
            }
        }
        
        // Kasus tidak ada solusi
        printStats(false);
        return null;
    }
}