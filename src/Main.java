import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import algorithms.AStarSearch;
import algorithms.GreedyBestFirstSearch;
import algorithms.SearchAlgorithm;
import algorithms.UniformCostSearch;
import model.Board;
import model.Move;
import model.Piece;
import heuristics.BlockingHeuristic;
import heuristics.MobilityHeuristic;
import heuristics.Heuristic;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=============================================================");
        System.out.println("  _____  _    _  _____ _    _   _    _  ____  _    _ _____  ");
        System.out.println(" |  __ \\| |  | |/ ____| |  | | | |  | |/ __ \\| |  | |  __ \\ ");
        System.out.println(" | |__) | |  | | (___ | |__| | | |__| | |  | | |  | | |__) | ");
        System.out.println(" |  _  /| |  | |\\___ \\|  __  | |  __  | |  | | |  | |  _  / ");
        System.out.println(" | | \\ \\| |__| |____) | |  | | | |  | | |__| | |__| | | \\ \\ ");
        System.out.println(" |_|__\\_\\\\____/|_____/|_| _|_|_|_| _|_|\\____/ \\____/|_|  \\_\\ ");
        System.out.println("  / ____|/ __ \\| |\\ \\    / /  ____|  __ \\                   ");
        System.out.println(" | (___ | |  | | | \\ \\  / /| |__  | |__) |                  ");
        System.out.println("  \\___ \\| |  | | |  \\ \\/ / |  __| |  _  /                   ");
        System.out.println("  ____) | |__| | |___\\  /  | |____| | \\ \\                   ");
        System.out.println(" |_____/ \\____/|______\\/   |______|_|  \\_\\                 ");
        System.out.println("=============================================================");

        // Input nama file puzzle
        System.out.print("Enter puzzle file name: ");
        String fileName = scanner.nextLine();
        String filePath = "test/input/" + fileName;
        File file = new File(filePath);

        // Cek keberadaan file
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Error: File '" + filePath + "' not found.");
            return;
        }

        // Load board
        Board board = loadBoard(filePath);
        if (board == null) {
            System.out.println("Failed to load board. The file may be corrupted or incorrectly formatted.");
            return;
        }
        
        // Pilihan algoritma
        System.out.println("\nSelect algorithm:");
        System.out.println("1. Uniform Cost Search");
        System.out.println("2. Greedy Best First Search");
        System.out.println("3. A* Search");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        
        SearchAlgorithm algorithm = null;
        Heuristic heuristic = null;
        
        // Pilihan heuristic
        if (choice == 2 || choice == 3) {
            System.out.println("\nSelect heuristic:");
            System.out.println("1. Mobility");
            System.out.println("2. Blocking Pieces");
            System.out.print("Choice: ");
            int hChoice = scanner.nextInt();
            
            heuristic = hChoice == 1 ? new MobilityHeuristic() : new BlockingHeuristic();
        }
        
        switch (choice) {
            case 1:
                algorithm = new UniformCostSearch();
                break;
            case 2:
                algorithm = new GreedyBestFirstSearch(heuristic);
                break;
            case 3:
                algorithm = new AStarSearch(heuristic);
                break;
            default:
                System.out.println("Invalid choice");
                return;
        }
        
        System.out.println("\nSolving puzzle...");
        // Menjalankan algoritma
        List<Move> solution = algorithm.solve(board);
        
        if (solution != null) {
            System.out.println("\nSolution found in " + solution.size() + " moves:");
            printSolution(board, solution);
            
            // Menyimpan hasil solusi ke file
            scanner.nextLine();
            String outputFileName;
            while (true) {
                System.out.print("\nEnter output file name (must end with .txt): ");
                outputFileName = scanner.nextLine().trim();
                if (outputFileName.toLowerCase().endsWith(".txt")) {
                    break;
                } else {
                    System.out.println("Invalid file name. The output file must have a '.txt' extension.");
                }
            }
            String outputDir = "test/output";
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            String outputPath = outputDir + File.separator + outputFileName;
            saveSolutionToFile(outputPath, board, solution);
            System.out.println("\nSolution saved to: " + outputPath);
        } else {
            System.out.println("\nNo solution found");
        }
    }
    
    // Menyimpan solusi ke file .txt
    private static void saveSolutionToFile(String filePath, Board initialBoard, List<Move> solution) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            Board currentBoard = initialBoard;
            writer.println("Initial board:");
            writeBoardToFile(writer, currentBoard, null);

            for (int i = 0; i < solution.size(); i++) {
                Move move = solution.get(i);
                writer.printf("\nMove %d: %s\n", i + 1, move);

                currentBoard = currentBoard.movePiece(move.getPiece(), move.getDirection(), move.getSteps());
                writeBoardToFile(writer, currentBoard, move.getPiece());
            }

            // Move terakhir
            if (currentBoard.isSolved()) {
                Piece p = currentBoard.getPrimaryPiece();
                Move finalMove = new Move(p, currentBoard.getExitDirection(), p.getLength());
                
                writer.printf("\nMove %d: %s\n", solution.size() + 1, finalMove);
                
                char[][] grid = currentBoard.getGridCopy();
                for (int k = 0; k < p.getLength(); k++) {
                    if (p.getOrientation().equals("horizontal")) {
                        grid[p.getRow()][p.getCol() + k] = '.';
                    } else {
                        grid[p.getRow() + k][p.getCol()] = '.';
                    }
                }
                
                Board exitedBoard = new Board(
                    currentBoard.getRows(),
                    currentBoard.getCols(),
                    grid,
                    currentBoard.getPieces(),
                    currentBoard.getExitRow(),
                    currentBoard.getExitCol(),
                    currentBoard.getExitDirection()
                );
                
                writeBoardToFile(writer, exitedBoard, null);
            }
        } catch (IOException e) {
            System.err.println("Error saving solution to file: " + e.getMessage());
        }
    }
    
    // Menulis kondisi board ke file
    private static void writeBoardToFile(PrintWriter writer, Board board, Piece movingPiece) {
        char[][] grid = board.getGridCopy();
        int rows = board.getRows();
        int cols = board.getCols();
        
        if (board.getExitCol() == cols) {
            for (int i = 0; i < rows; i++) {
                String line = new String(grid[i]);
                if (i == board.getExitRow()) {
                    writer.println(line + "K");
                } else {
                    writer.println(line + " ");
                }
            }
        } else {
            for (int i = 0; i < rows; i++) {
                writer.println(new String(grid[i]));
            }
        }
    }

    // Load konfigurasi board dari file input
    public static Board loadBoard(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Membaca ukuran board (baris dan kolom)
            String[] dims = br.readLine().split(" ");
            if (dims.length != 2) {
                System.out.println("Error: First line must contain exactly two numbers (rows and columns)");
                return null;
            }

            int rows, cols;
            try {
                rows = Integer.parseInt(dims[0]);
                cols = Integer.parseInt(dims[1]);
                if (rows <= 0 || cols <= 0) {
                    System.out.println("Error: Rows and columns must be positive numbers");
                    return null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Rows and columns must be valid numbers");
                return null;
            }

            // Membaca jumlah piece
            String pieceCountLine = br.readLine();
            if (pieceCountLine == null) {
                System.out.println("Error: Missing piece count line");
                return null;
            }

            int pieceCount;
            try {
                pieceCount = Integer.parseInt(pieceCountLine);
                if (pieceCount <= 0) {
                    System.out.println("Error: Piece count must be a positive number");
                    return null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Piece count must be a valid number");
                return null;
            }

            // Membaca grid
            char[][] grid = new char[rows][cols];
            List<Piece> pieces = new ArrayList<>();
            int exitRow = -1, exitCol = -1;
            String exitDirection = "right";

            for (int i = 0; i < rows; i++) {
                String line = br.readLine();
                if (line == null) {
                    System.out.println("Error: Not enough rows in the input file");
                    return null;
                }

                // Mengecek posisi pintu keluar 'K'
                if (line.length() > cols && line.charAt(cols) == 'K') {
                    exitRow = i;
                    exitCol = cols;
                    exitDirection = "right";
                    line = line.substring(0, cols); // Menghapus 'K' dari grid
                }

                for (int j = 0; j < cols; j++) {
                    char c = j < line.length() ? line.charAt(j) : '.';

                    if (c != '.' && c != 'K' && (c < 'A' || c > 'Z')) {
                        System.out.println("Error: Invalid character '" + c + "' in the grid. Only A-Z, '.', and 'K' allowed.");
                        return null;
                    }

                    grid[i][j] = c;

                    // Mencatat posisi pintu keluar jika 'K' berada di dalam grid
                    if (c == 'K') {
                        exitRow = i;
                        exitCol = j;
                        if (i == 0) exitDirection = "top";
                        else if (i == rows - 1) exitDirection = "bottom";
                        else if (j == 0) exitDirection = "left";
                        else if (j == cols - 1) exitDirection = "right";
                    }
                }
            }

            // Jika tidak ditemukan pintu keluar, maka pintu keluar default berada di tengah sisi kanan
            if (exitRow == -1) {
                exitRow = rows / 2;
                exitCol = cols;
                exitDirection = "right";
            }

            // Mengidentifikasi potongan (kecuali '.' dan 'K')
            boolean[][] visited = new boolean[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c != '.' && c != 'K' && !visited[i][j]) {
                        char id = c;
                        boolean isPrimary = false;

                        // Menentukan exit direction dari primary piece
                        if (exitDirection.equals("right") && i == exitRow && j + 1 < cols && grid[i][j + 1] == id) {
                            isPrimary = true;
                        } else if (exitDirection.equals("left") && i == exitRow && j > 0 && grid[i][j - 1] == id) {
                            isPrimary = true;
                        } else if (exitDirection.equals("bottom") && j == exitCol && i + 1 < rows && grid[i + 1][j] == id) {
                            isPrimary = true;
                        } else if (exitDirection.equals("top") && j == exitCol && i > 0 && grid[i - 1][j] == id) {
                            isPrimary = true;
                        }

                        int size = 1;
                        String orientation;

                        // Mengecek orientasi (horizontal atau vertikal)
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

                        // Menandai sel yang sudah dikunjungi
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

            // Kasus jumlah potongan dalam file berbeda dengan jumlah potongan yang ditemukan
            int nonPrimaryCount = 0;
            for (Piece p : pieces) {
                if (!p.isPrimary()) {
                    nonPrimaryCount++;
                }
            }

            if (pieceCount != nonPrimaryCount) {
                System.out.println("Warning: Piece count (" + pieceCount + ") does not match actual non-primary pieces found (" + nonPrimaryCount + ").");
            }

            return new Board(rows, cols, grid, pieces, exitRow, exitCol, exitDirection);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    private static void printSolution(Board initialBoard, List<Move> solution) {
        Board currentBoard = initialBoard;
        System.out.println("Initial board:");
        currentBoard.printBoard(null);

        for (int i = 0; i < solution.size(); i++) {
            Move move = solution.get(i);
            System.out.printf("\nMove %d: %s\n", i + 1, move);

            currentBoard = currentBoard.movePiece(move.getPiece(), move.getDirection(), move.getSteps());
            currentBoard.printBoard(move.getPiece());  // Pass the moving piece
        }

        // Langkah akhir saat primary piece keluar
        if (currentBoard.isSolved()) {
            Piece p = currentBoard.getPrimaryPiece();
            Move finalMove = new Move(p, currentBoard.getExitDirection(), p.getLength());
            
            System.out.printf("\nMove %d: %s\n", solution.size() + 1, finalMove);
            
            char[][] grid = currentBoard.getGridCopy();
            for (int k = 0; k < p.getLength(); k++) {
                if (p.getOrientation().equals("horizontal")) {
                    grid[p.getRow()][p.getCol() + k] = '.';
                } else {
                    grid[p.getRow() + k][p.getCol()] = '.';
                }
            }
            
            Board exitedBoard = new Board(
                currentBoard.getRows(),
                currentBoard.getCols(),
                grid,
                currentBoard.getPieces(),
                currentBoard.getExitRow(),
                currentBoard.getExitCol(),
                currentBoard.getExitDirection()
            );
            
            exitedBoard.printBoard(null);
        }
    }

}