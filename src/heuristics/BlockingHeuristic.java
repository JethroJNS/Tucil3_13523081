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

        // Menghitung jumlah blocker tanpa penalti tambahan
        return blockers.size();
    }

    @Override
    public String getName() {
        return "Blocking Heuristic";
    }
}