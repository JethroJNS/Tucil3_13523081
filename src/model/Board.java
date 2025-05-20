package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int rows, cols;
    private final char[][] grid;
    private final List<Piece> pieces;
    private Piece primaryPiece;
    private final int exitRow, exitCol;
    private final String exitDirection; // "top", "right", "bottom", "left"

    public Board(int rows, int cols, char[][] grid, List<Piece> pieces, int exitRow, int exitCol, String exitDirection) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.pieces = new ArrayList<>(pieces);
        this.exitRow = exitRow;
        this.exitCol = exitCol;
        this.exitDirection = exitDirection;
        
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, cols);
        }
        
        for (Piece p : this.pieces) {
            if (p.isPrimary()) {
                primaryPiece = p;
                break;
            }
        }
    }

    public Board(Board other) {
        this(other.rows, other.cols, other.grid, other.pieces, other.exitRow, other.exitCol, other.exitDirection);
    }

    public boolean isValidMove(Piece piece, String direction, int steps) {
        int newRow = piece.getRow();
        int newCol = piece.getCol();

        // Menghitung posisi baru berdasarkan arah dan orientasi piece
        if (piece.isHorizontal()) {
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

        // Mengecek apakah langkah berada di dalam batas board
        if (newRow < 0 || newCol < 0) {
            return false;
        }

        // Mengecek kondisi khusus untuk primaryPiece agar bisa keluar board sesuai arah keluar
        if (piece.isPrimary()) {
            if (exitDirection.equals("right") && piece.isHorizontal() && 
                direction.equals("right") && newCol + piece.getLength() == exitCol && 
                piece.getRow() == exitRow) {
                return true;
            }
            else if (exitDirection.equals("left") && piece.isHorizontal() && 
                     direction.equals("left") && newCol == exitCol && 
                     piece.getRow() == exitRow) {
                return true;
            }
            else if (exitDirection.equals("bottom") && piece.isVertical() && 
                     direction.equals("down") && newRow + piece.getLength() == exitRow && 
                     piece.getCol() == exitCol) {
                return true;
            }
            else if (exitDirection.equals("top") && piece.isVertical() && 
                     direction.equals("up") && newRow == exitRow && 
                     piece.getCol() == exitCol) {
                return true;
            }
        }

        // Mengecek apakah keluar dari batas grid
        if (piece.isHorizontal()) {
            if (newCol + piece.getLength() > cols) {
                return false;
            }
        } else {
            if (newRow + piece.getLength() > rows) {
                return false;
            }
        }

        char[][] tempGrid = createTempGrid(piece);

        // Validasi apakah posisi baru kosong atau berupa 'K' (exit)
        for (int i = 0; i < piece.getLength(); i++) {
            int r = piece.isHorizontal() ? newRow : newRow + i;
            int c = piece.isHorizontal() ? newCol + i : newCol;
            
            // Mengabaikan posisi exit sesuai arah
            if (r == exitRow && c == exitCol && exitDirection.equals("right") && c == cols - 1) {
                continue;
            }
            if (r == exitRow && c == exitCol && exitDirection.equals("left") && c == 0) {
                continue;
            }
            if (r == exitRow && c == exitCol && exitDirection.equals("bottom") && r == rows - 1) {
                continue;
            }
            if (r == exitRow && c == exitCol && exitDirection.equals("top") && r == 0) {
                continue;
            }
            
            // Jika bukan '.' atau 'K', maka terblokir
            if (r >= rows || c >= cols || r < 0 || c < 0 || (tempGrid[r][c] != '.' && tempGrid[r][c] != 'K')) {
                return false;
            }
        }

        return true;
    }

    // Membuat grid sementara tanpa piece yang akan digerakkan
    private char[][] createTempGrid(Piece excludedPiece) {
        char[][] tempGrid = new char[rows][cols];

        // Menyimpan simbol 'K'
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tempGrid[i][j] = (grid[i][j] == 'K') ? 'K' : '.';
            }
        }
        
          // Menyimpan semua piece ke grid kecuali yang sedang diuji
        for (Piece p : pieces) {
            if (p != excludedPiece) {
                for (int[] cell : p.getOccupiedCells()) {
                    if (cell[0] >= 0 && cell[0] < rows && cell[1] >= 0 && cell[1] < cols) {
                        tempGrid[cell[0]][cell[1]] = p.getId();
                    }
                }
            }
        }
        return tempGrid;
    }

    public Board movePiece(Piece piece, String direction, int steps) {
        int dRow = 0, dCol = 0;
        switch (direction) {
            case "up": dRow = -1; break;
            case "down": dRow = 1; break;
            case "left": dCol = -1; break;
            case "right": dCol = 1; break;
        }

        int newRow = piece.getRow() + dRow * steps;
        int newCol = piece.getCol() + dCol * steps;

        // Clone grid
        char[][] newGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, newGrid[i], 0, cols);
        }

        // Clone list piece
        List<Piece> newPieces = new ArrayList<>();
        for (Piece p : pieces) {
            newPieces.add(p.clone());
        }

        // Mengambil piece yang sesuai dari clone
        Piece movingPiece = null;
        for (Piece p : newPieces) {
            if (p.getId() == piece.getId()) {
                movingPiece = p;
                break;
            }
        }

        // Membersihkan posisi lama
        for (int i = 0; i < movingPiece.getLength(); i++) {
            int r = movingPiece.getRow() + (movingPiece.isVertical() ? i : 0);
            int c = movingPiece.getCol() + (movingPiece.isHorizontal() ? i : 0);
            newGrid[r][c] = '.';
        }

        // Validasi tabrakan di posisi baru
        for (int i = 0; i < movingPiece.getLength(); i++) {
            int r = newRow + (movingPiece.isVertical() ? i : 0);
            int c = newCol + (movingPiece.isHorizontal() ? i : 0);

            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                return null; 
            }

            if (newGrid[r][c] != '.' && newGrid[r][c] != 'K') {
                return null;
            }
        }

        // Update posisi piece
        movingPiece.setRow(newRow);
        movingPiece.setCol(newCol);

        // Menulis ulang posisi baru di grid
        for (int i = 0; i < movingPiece.getLength(); i++) {
            int r = newRow + (movingPiece.isVertical() ? i : 0);
            int c = newCol + (movingPiece.isHorizontal() ? i : 0);
            newGrid[r][c] = movingPiece.getId();
        }

        return new Board(rows, cols, newGrid, newPieces, exitRow, exitCol, exitDirection);
    }

    // Mengupdate grid berdasarkan posisi semua pieces
    private void updateGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = grid[i][j] == 'K' ? 'K' : '.';
            }
        }
        
        for (Piece p : pieces) {
            for (int[] cell : p.getOccupiedCells()) {
                if (cell[0] >= 0 && cell[0] < rows && cell[1] >= 0 && cell[1] < cols) {
                    grid[cell[0]][cell[1]] = p.getId();
                }
            }
        }
    }

    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        
        for (Piece piece : pieces) {
            String[] directions = piece.isHorizontal() ? 
                new String[]{"left", "right"} : new String[]{"up", "down"};
            
            for (String dir : directions) {
                int maxSteps = getMaxSteps(piece, dir);
                for (int steps = 1; steps <= maxSteps; steps++) {
                    if (isValidMove(piece, dir, steps)) {
                        moves.add(new Move(piece, dir, steps));
                    } else {
                        break;
                    }
                }
            }
        }
        
        return moves;
    }

    // Menghitung langkah maksimum berdasarkan arah dan batas grid
    private int getMaxSteps(Piece piece, String direction) {
        if (piece.isHorizontal()) {
            return direction.equals("left") ? 
                piece.getCol() : cols - piece.getCol() - piece.getLength();
        } else {
            return direction.equals("up") ? 
                piece.getRow() : rows - piece.getRow() - piece.getLength();
        }
    }

    public boolean isSolved() {
        if (primaryPiece == null) return false;
        
        // Mengecek apakah posisi primary piece sudah mencapai pintu keluar
        switch (exitDirection) {
            case "right":
                return primaryPiece.isHorizontal() && 
                    primaryPiece.getRow() == exitRow && 
                    (primaryPiece.getCol() + primaryPiece.getLength()) == exitCol;
            case "left":
                return primaryPiece.isHorizontal() && 
                    primaryPiece.getRow() == exitRow && 
                    primaryPiece.getCol() == exitCol;
            case "bottom":
                return primaryPiece.isVertical() && 
                    primaryPiece.getCol() == exitCol && 
                    (primaryPiece.getRow() + primaryPiece.getLength()) == exitRow;
            case "top":
                return primaryPiece.isVertical() && 
                    primaryPiece.getCol() == exitCol && 
                    primaryPiece.getRow() == exitRow;
            default:
                return false;
        }
    }

    public void printBoard(Piece movingPiece) {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                
                if (i == exitRow && j == exitCol) {
                    // Print 'K' dengan warna merah
                    System.out.print(RED + 'K' + RESET);
                } else if (cell == primaryPiece.getId()) {
                    // Print primary piece dengan warna biru
                    System.out.print(BLUE + cell + RESET);
                } else if (movingPiece != null && cell == movingPiece.getId()) {
                    // Print moving piece dengan warna kuning
                    System.out.print(YELLOW + cell + RESET);
                } else {
                    System.out.print(cell);
                }
            }

            if (exitCol == cols && i == exitRow && exitDirection.equals("right")) {
                System.out.print(RED + 'K' + RESET);
            }

            System.out.println();
        }
    }

    // Getters
    public char[][] getGridCopy() {
        char[][] copy = new char[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                copy[i][j] = grid[i][j];
            }
        }
        return copy;
    }

    public String getStateString() {
        StringBuilder sb = new StringBuilder();
        for (Piece p : pieces) {
            sb.append(p.getId()).append(p.getRow()).append(p.getCol());
        }
        return sb.toString();
    }

    public Piece getPieceById(char id) {
        for (Piece p : pieces) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public char[][] getGrid() { return grid; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Piece getPrimaryPiece() { return primaryPiece; }
    public List<Piece> getPieces() { return new ArrayList<>(pieces); }
    public int getExitRow() { return exitRow; }
    public int getExitCol() { return exitCol; }
    public String getExitDirection() { return exitDirection; }
}