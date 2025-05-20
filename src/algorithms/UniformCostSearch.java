package algorithms;

import java.util.*;

import model.Board;
import model.Move;

public class UniformCostSearch extends SearchAlgorithm {
    // Node untuk menyimpan state papan saat ini, jalur gerakan menuju state tersebut, dan total cost dari root
    private static class Node {
        Board board;
        List<Move> path;
        int cost;
        
        Node(Board board, List<Move> path, int cost) {
            this.board = board;
            this.path = path;
            this.cost = cost; // Biaya dari root ke node ini (g(n))
        }
    }

    @Override
    public List<Move> solve(Board initialBoard) {
        startTime = System.currentTimeMillis(); // Inisialisasi waktu pencarian
        nodesExplored = 0; // Inisialisasi jumlah node yang dieksplorasi
        
        // PriorityQueue berdasarkan cost terkecil (min-heap), sesuai prinsip UCS
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));
        // Set untuk melacak state yang sudah dikunjungi
        Set<String> visited = new HashSet<>();
        
        // Menambahkan node awal ke dalam antrian
        queue.add(new Node(initialBoard, new ArrayList<>(), 0));
        
        while (!queue.isEmpty()) {
            Node current = queue.poll(); // Mengambil node dengan cost terkecil
            nodesExplored++;
            
            // Kondisi goal state tercapai
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
                queue.add(new Node(newBoard, newPath, current.cost + move.getSteps()));
            }
        }
        
        // Kasus tidak ada solusi
        printStats(false);
        return null;
    }
}