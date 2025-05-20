package handler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import model.Board;
import model.Piece;

public class InputHandler {
    // Constants for validation
    private static final int MIN_PIECE_SIZE = 2;
    private static final char PRIMARY_PIECE = 'P';
    private static final char EMPTY_CELL = '.';

    // Kelas exception untuk kesalahan validasi puzzle
    public static class PuzzleValidationException extends Exception {
        public PuzzleValidationException(String message) {
            super(message);
        }
    }

    // Method utama untuk membaca input file
    public Board readInput(String filePath) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        
        // Membaca dimensi papan (baris dan kolom)
        String[] dimensions = reader.readLine().trim().split(" ");
        if (dimensions.length != 2) {
            throw new PuzzleValidationException("First line must contain exactly two numbers (rows and columns)");
        }

        int rows, cols;
        try {
            rows = Integer.parseInt(dimensions[0]);
            cols = Integer.parseInt(dimensions[1]);
            if (rows <= 0 || cols <= 0) {
                throw new PuzzleValidationException("Rows and columns must be positive numbers");
            }
        } catch (NumberFormatException e) {
            throw new PuzzleValidationException("Rows and columns must be valid numbers");
        }

        // Membaca jumlah piece
        String pieceCountLine = reader.readLine();
        if (pieceCountLine == null) {
            throw new PuzzleValidationException("Missing piece count line");
        }

        int numPieces;
        try {
            numPieces = Integer.parseInt(pieceCountLine.trim());
            if (numPieces < 0) {
                throw new PuzzleValidationException("Piece count must be a non-negative number");
            }
        } catch (NumberFormatException e) {
            throw new PuzzleValidationException("Piece count must be a valid number");
        }

        // Membaca grid
        char[][] grid = new char[rows][cols];
        List<Piece> pieces = new ArrayList<>();
        int exitRow = -1, exitCol = -1;

        for (int i = 0; i < rows; i++) {
            String line = reader.readLine();
            if (line == null) {
                throw new PuzzleValidationException("Not enough rows in the input file");
            }
            line = line.trim();

            // Pemeriksaan untuk 'K' di kanan
            if (line.length() > cols && line.charAt(cols) == 'K') {
                exitRow = i;
                exitCol = cols;
                line = line.substring(0, cols);
            }

            // Validasi panjang baris
            if (line.length() != cols) {
                throw new PuzzleValidationException(
                    String.format("Row %d has %d characters, expected %d", i+1, line.length(), cols));
            }

            for (int j = 0; j < cols; j++) {
                grid[i][j] = line.charAt(j);
                if (grid[i][j] == 'K') {
                    exitRow = i;
                    exitCol = j;
                }
            }
        }
        reader.close();

        // Tidak ditemukan pintu keluar
        if (exitRow == -1 || exitCol == -1) {
            exitRow = rows / 2;
            exitCol = cols;
        }

        validatePuzzle(grid, numPieces);

        // Identifikasi piece
        boolean[][] visited = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != EMPTY_CELL && grid[i][j] != 'K' && !visited[i][j]) {
                    char id = grid[i][j];
                    boolean isPrimary = id == PRIMARY_PIECE;
                    int size = 1;
                    String orientation;

                    if (j + 1 < cols && grid[i][j + 1] == id) {
                        orientation = "horizontal";
                        while (j + size < cols && grid[i][j + size] == id) {
                            size++;
                        }
                    } else {
                        orientation = "vertical";
                        while (i + size < rows && grid[i + size][j] == id) {
                            size++;
                        }
                    }

                    pieces.add(new Piece(id, i, j, size, orientation, isPrimary));

                    for (int k = 0; k < size; k++) {
                        if (orientation.equals("horizontal")) {
                            visited[i][j + k] = true;
                        } else {
                            visited[i + k][j] = true;
                        }
                    }
                }
            }
        }

        return new Board(rows, cols, grid, pieces, exitRow, exitCol);
    }

    // Validasi konfigurasi puzzle
    private void validatePuzzle(char[][] grid, int expectedPieceCount) throws PuzzleValidationException {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            throw new PuzzleValidationException("Invalid grid: Grid cannot be null or empty");
        }

        Map<Character, List<int[]>> piecePositions = new HashMap<>();
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if (cell != EMPTY_CELL && cell != 'K') {
                    piecePositions.computeIfAbsent(cell, k -> new ArrayList<>())
                                 .add(new int[]{i, j});
                }
            }
        }

        // Validasi keberadaan potongan utama
        if (!piecePositions.containsKey(PRIMARY_PIECE)) {
            throw new PuzzleValidationException("Invalid configuration: Primary piece 'P' is missing");
        }

        // Validasi ukuran minimum setiap potongan
        for (Map.Entry<Character, List<int[]>> entry : piecePositions.entrySet()) {
            if (entry.getValue().size() < MIN_PIECE_SIZE) {
                throw new PuzzleValidationException(
                    String.format("Invalid piece '%c': Each piece must be at least %d cells",
                    entry.getKey(), MIN_PIECE_SIZE));
            }
        }

        // Validasi jumlah potongan
        int actualPieceCount = piecePositions.size() - 1;
        if (actualPieceCount != expectedPieceCount) {
            throw new PuzzleValidationException(
                String.format("Invalid piece count: Expected %d pieces (excluding primary piece), found %d",
                expectedPieceCount, actualPieceCount));
        }

        // Validasi apakah setiap potongan saling terhubung
        for (Map.Entry<Character, List<int[]>> entry : piecePositions.entrySet()) {
            if (!isPieceConnected(entry.getValue())) {
                throw new PuzzleValidationException(
                    String.format("Invalid piece '%c': All cells must be connected", entry.getKey()));
            }
        }

        // Validasi apakah ada potongan yang saling tumpang tindih
        if (hasOverlappingPieces(piecePositions)) {
            throw new PuzzleValidationException("Invalid configuration: Pieces cannot overlap");
        }
    }

    // Mengecek apakah semua sel dalam satu potongan saling terhubung
    private boolean isPieceConnected(List<int[]> positions) {
        if (positions.size() < 2) return true;

        Set<String> visited = new HashSet<>();
        Queue<int[]> queue = new LinkedList<>();
        queue.add(positions.get(0));
        visited.add(positions.get(0)[0] + "," + positions.get(0)[1]);

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();

            for (int[] dir : directions) {
                int newRow = current[0] + dir[0];
                int newCol = current[1] + dir[1];
                String key = newRow + "," + newCol;

                if (!visited.contains(key)) {
                    for (int[] pos : positions) {
                        if (pos[0] == newRow && pos[1] == newCol) {
                            queue.add(pos);
                            visited.add(key);
                            break;
                        }
                    }
                }
            }
        }

        return visited.size() == positions.size();
    }

    // Mengecek apakah ada dua potongan yang menempati sel yang sama
    private boolean hasOverlappingPieces(Map<Character, List<int[]>> piecePositions) {
        Set<String> allPositions = new HashSet<>();
        for (List<int[]> positions : piecePositions.values()) {
            for (int[] pos : positions) {
                String key = pos[0] + "," + pos[1];
                if (!allPositions.add(key)) {
                    return true;
                }
            }
        }
        return false;
    }
}