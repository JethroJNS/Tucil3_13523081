package algorithms;

import java.util.*;

import model.Board;
import model.Move;
import heuristics.Heuristic;

public class GreedyBestFirstSearch extends SearchAlgorithm {
    private final Heuristic heuristic;
    
    public GreedyBestFirstSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    // Node untuk menyimpan state papan, jalur langkah, dan nilai heuristik
    private static class Node {
        Board board;
        List<Move> path;
        int heuristic;
        
        Node(Board board, List<Move> path, int heuristic) {
            this.board = board;
            this.path = path;
            this.heuristic = heuristic; // Perkiraan biaya ke goal (h(n))
        }
    }

    @Override
    public List<Move> solve(Board initialBoard) {
        startTime = System.currentTimeMillis(); // Inisialisasi waktu pencarian
        nodesExplored = 0; // Inisialisasi jumlah node yang dieksplorasi
        
        // PriorityQueue berdasarkan nilai heuristik terkecil
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.heuristic));
        // Set untuk melacak state yang sudah dikunjungi
        Set<String> visited = new HashSet<>();
        
        // Memasukkan node awal dengan nilai heuristik awal
        queue.add(new Node(initialBoard, new ArrayList<>(), heuristic.calculate(initialBoard)));
        
        while (!queue.isEmpty()) {
            Node current = queue.poll(); // Mengambil node dengan heuristik terendah
            nodesExplored++;
            
            // Kasus goal state tercapai
            if (current.board.isSolved()) {
                printStats(true);
                return current.path;
            }
            
            // Skip state yang pernah dikunjungi
            if (!visited.add(current.board.getStateString())) {
                continue;
            }
            
            // Iterasi semua gerakan legal dari state saat ini
            for (Move move : current.board.getPossibleMoves()) {
                Board newBoard = current.board.movePiece(move.getPiece(), move.getDirection(), move.getSteps());
                // Salin jalur lama dan tambahkan langkah baru ke jalur
                List<Move> newPath = new ArrayList<>(current.path);
                newPath.add(move);
                
                // Menambahkan node baru ke antrian
                queue.add(new Node(newBoard, newPath, heuristic.calculate(newBoard)));
            }
        }
        
        // Kasus tidak ada solusi
        printStats(false);
        return null;
    }
}