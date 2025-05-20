package heuristics;

import model.Board;
import model.Piece;

// Heuristik yang mengukur jumlah piece yang tidak bisa bergerak
public class MobilityHeuristic implements Heuristic {
    @Override
    public int calculate(Board board) {
        int immobileCount = 0;

        // Menghitung jumlah piece yang tidak bisa bergerak sama sekali
        for (Piece piece : board.getPieces()) {
            if (!canMove(board, piece)) {
                immobileCount++;
            }
        }

        return immobileCount; // Semakin banyak yang tidak bisa bergerak, semakin buruk
    }

    // Mengecek apakah sebuah piece bisa bergerak ke arah mana pun
    private boolean canMove(Board board, Piece piece) {
        char[][] grid = board.getGrid();
        int row = piece.getRow();
        int col = piece.getCol();
        int len = piece.getLength();

        if (piece.isHorizontal()) {
            // Cek kiri
            if (col > 0 && grid[row][col - 1] == '.') return true;
            // Cek kanan
            if (col + len < board.getCols() && grid[row][col + len] == '.') return true;
        } else {
            // Cek atas
            if (row > 0 && grid[row - 1][col] == '.') return true;
            // Cek bawah
            if (row + len < board.getRows() && grid[row + len][col] == '.') return true;
        }

        return false; // Tidak ada ruang untuk bergerak
    }

    @Override
    public String getName() {
        return "Mobility Heuristic";
    }
}