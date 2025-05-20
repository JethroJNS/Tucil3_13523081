package heuristics;

import model.Board;
import model.Piece;

import java.util.HashSet;
import java.util.Set;

// Heuristik berdasarkan jumlah dan mobilitas piece yang menghalangi primary piece
public class BlockingHeuristic implements Heuristic {
    @Override
    public int calculate(Board board) {
        Piece main = board.getPrimaryPiece();
        Set<Character> blockers = new HashSet<>();

        if (main.isHorizontal()) {
            int row = main.getRow();
            int col = main.getCol() + main.getLength();

            // Menelusuri ke kanan sampai exit
            while (col < board.getCols()) {
                char cell = board.getGrid()[row][col];
                // Menambahkan jika ada piece yang menghalangi kecuali empty (.), goal (K), atau dirinya sendiri
                if (cell != '.' && cell != 'K' && cell != main.getId()) {
                    blockers.add(cell);
                }
                col++;
            }
        } else {
            int col = main.getCol();
            int row = main.getRow() + main.getLength();

            // Menelusuri ke bawah sampai exit
            while (row < board.getRows()) {
                char cell = board.getGrid()[row][col];
                if (cell != '.' && cell != 'K' && cell != main.getId()) {
                    blockers.add(cell);
                }
                row++;
            }
        }

        int penalty = 0;
        for (char id : blockers) {
            Piece p = board.getPieceById(id);
            if (p != null) {
                // Memberi penalti lebih jika piece yang memblokir tidak bisa bergerak
                boolean immobile = isImmobile(board, p);
                penalty += immobile ? 3 : 1;
            }
        }

        return penalty;
    }

    // Mengecek apakah sebuah piece dapat bergerak dari posisinya sekarang
    private boolean isImmobile(Board board, Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();
        int len = piece.getLength();
        char[][] grid = board.getGrid();

        if (piece.isHorizontal()) {
            return !(col > 0 && grid[row][col - 1] == '.' ||
                     col + len < board.getCols() && grid[row][col + len] == '.');
        } else {
            return !(row > 0 && grid[row - 1][col] == '.' ||
                     row + len < board.getRows() && grid[row + len][col] == '.');
        }
    }

    @Override
    public String getName() {
        return "Blocking Heuristic";
    }
}