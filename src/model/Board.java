package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int rows, cols;
    private char[][] grid;
    private List<Piece> pieces;
    private Piece primaryPiece;
    private int exitRow, exitCol;

    public Board(int rows, int cols, char[][] grid, List<Piece> pieces, int exitRow, int exitCol) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
        this.pieces = pieces;
        this.exitRow = exitRow;
        this.exitCol = exitCol;
        for (Piece p : pieces) {
            if (p.isPrimary()) {
                this.primaryPiece = p;
                break;
            }
        }
        if (this.primaryPiece == null) {
            throw new IllegalStateException("No primary piece (P) found in the board");
        }
    }

    public Board(Board other) {
        this.rows = other.rows;
        this.cols = other.cols;
        this.grid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, cols);
        }
        this.pieces = new ArrayList<>();
        for (Piece p : other.pieces) {
            Piece newPiece = new Piece(p.getId(), p.getRow(), p.getCol(), p.getLength(), p.getOrientation(),
                    p.isPrimary());
            this.pieces.add(newPiece);
            if (p.isPrimary()) {
                this.primaryPiece = newPiece;
            }
        }
        this.exitRow = other.exitRow;
        this.exitCol = other.exitCol;
    }

    // Memeriksa apakah move valid
    public boolean isValidMove(Piece piece, String direction, int steps) {
        int newRow = piece.getRow();
        int newCol = piece.getCol();

        // Memeriksa posisi setelah move
        if (piece.getOrientation().equals("horizontal")) {
            if (direction.equals("left")) {
                newCol -= steps;
            } else if (direction.equals("right")) {
                newCol += steps;
            } else {
                return false;
            }
        } else {
            if (direction.equals("up")) {
                newRow -= steps;
            } else if (direction.equals("down")) {
                newRow += steps;
            } else {
                return false;
            }
        }

        if (newRow < 0 || newCol < 0) {
            return false;
        }

        if (piece.getOrientation().equals("horizontal")) {
            if (newCol + piece.getLength() > cols) {
                return false;
            }
        } else {
            if (newRow + piece.getLength() > rows) {
                return false;
            }
        }

        char[][] tempGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tempGrid[i][j] = grid[i][j] == 'K' ? 'K' : '.';
            }
        }

        for (Piece p : pieces) {
            if (p != piece) {
                for (int[] cell : p.getOccupiedCells()) {
                    tempGrid[cell[0]][cell[1]] = p.getId();
                }
            }
        }

        for (int i = 0; i < piece.getLength(); i++) {
            int r = piece.getOrientation().equals("horizontal") ? newRow : newRow + i;
            int c = piece.getOrientation().equals("horizontal") ? newCol + i : newCol;
            if (tempGrid[r][c] != '.' && tempGrid[r][c] != 'K') {
                return false;
            }
        }

        return true;
    }

    // Menggerakkan piece
    public Board movePiece(Piece piece, String direction, int steps) {
        Board newBoard = new Board(this);
        
        Piece movedPiece = null;
        for (Piece p : newBoard.pieces) {
            if (p.equals(piece)) {
                movedPiece = p;
                break;
            }
        }
        
        if (movedPiece == null) {
            throw new IllegalArgumentException("Piece not found in the board");
        }

        int newRow = movedPiece.getRow();
        int newCol = movedPiece.getCol();
        
        if (direction.equals("left")) {
            newCol -= steps;
        } else if (direction.equals("right")) {
            newCol += steps;
        } else if (direction.equals("up")) {
            newRow -= steps;
        } else if (direction.equals("down")) {
            newRow += steps;
        }

        // Mengupdate grid
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newBoard.grid[i][j] = newBoard.grid[i][j] == 'K' ? 'K' : '.';
            }
        }

        // Membuat piece baru dengan posisi yang telah diupdate
        Piece updatedPiece = new Piece(
            movedPiece.getId(),
            newRow,
            newCol,
            movedPiece.getLength(),
            movedPiece.getOrientation(),
            movedPiece.isPrimary()
        );

        int pieceIndex = newBoard.pieces.indexOf(movedPiece);
        newBoard.pieces.set(pieceIndex, updatedPiece);
        
        if (updatedPiece.isPrimary()) {
            newBoard.primaryPiece = updatedPiece;
        }

        for (Piece p : newBoard.pieces) {
            for (int[] cell : p.getOccupiedCells()) {
                newBoard.grid[cell[0]][cell[1]] = p.getId();
            }
        }

        return newBoard;
    }

    // Memeriksa apakah target state sudah tercapai
    public boolean isSolved() {
        if (primaryPiece.getOrientation().equals("horizontal")) {
            int rightEnd = primaryPiece.getCol() + primaryPiece.getLength() - 1;
            return primaryPiece.getRow() == exitRow && rightEnd == cols - 1;
        }
        else {
            int bottomEnd = primaryPiece.getRow() + primaryPiece.getLength() - 1;
            return primaryPiece.getCol() == exitCol && bottomEnd == rows - 1;
        }
    }

    public String getState() {
        StringBuilder sb = new StringBuilder();
        for (Piece p : pieces) {
            sb.append(p.getId()).append(p.getRow()).append(p.getCol());
        }
        return sb.toString();
    }

    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (Object[] moveObj : getPossibleMovesArray()) {
            Piece piece = (Piece) moveObj[0];
            String direction = (String) moveObj[1];
            int steps = (Integer) moveObj[2];
            moves.add(new Move(piece, direction, steps));
        }
        return moves;
    }

    private List<Object[]> getPossibleMovesArray() {
        List<Object[]> moves = new ArrayList<>();
        for (Piece piece : pieces) {
            String[] directions = piece.getOrientation().equals("horizontal") 
                ? new String[]{"left", "right"} 
                : new String[]{"up", "down"};
            for (String dir : directions) {
                int maxSteps = piece.getOrientation().equals("horizontal")
                    ? (dir.equals("left") ? piece.getCol() : cols - piece.getCol() - piece.getLength())
                    : (dir.equals("up") ? piece.getRow() : rows - piece.getRow() - piece.getLength());
                for (int steps = 1; steps <= maxSteps; steps++) {
                    if (isValidMove(piece, dir, steps)) {
                        moves.add(new Object[]{piece, dir, steps});
                    } else {
                        break;
                    }
                }
            }
        }
        return moves;
    }

    // Getters
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char[][] getGrid() {
        return grid;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public int getExitRow() {
        return exitRow;
    }

    public int getExitCol() {
        return exitCol;
    }

    public Piece getPrimaryPiece() {
        return primaryPiece;
    }

    public String getStateString() {
        return getState();
    }

}