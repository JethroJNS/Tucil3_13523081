import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import algorithms.AStarSearch;
import algorithms.GreedyBestFirstSearch;
import algorithms.SearchAlgorithm;
import algorithms.UniformCostSearch;
import model.Board;
import model.Move;
import model.Piece;
import handler.InputHandler;
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

        // Load board using InputHandler
        Board board;
        try {
            InputHandler inputHandler = new InputHandler();
            board = inputHandler.readInput(filePath);
        } catch (Exception e) {
            System.out.println("Failed to load board: " + e.getMessage());
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
                String exitDirection = p.getOrientation().equals("horizontal") ? "right" : "down";
                Move finalMove = new Move(p, exitDirection, 1);
                
                writer.printf("\nMove %d: %s\n", solution.size() + 1, finalMove);
                
                char[][] grid = new char[currentBoard.getRows()][currentBoard.getCols()];
                for (int i = 0; i < currentBoard.getRows(); i++) {
                    System.arraycopy(currentBoard.getGrid()[i], 0, grid[i], 0, currentBoard.getCols());
                }
                
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
                    currentBoard.getExitCol()
                );
                
                writeBoardToFile(writer, exitedBoard, null);
            }
        } catch (IOException e) {
            System.err.println("Error saving solution to file: " + e.getMessage());
        }
    }
    
    // Menulis kondisi board ke file
    private static void writeBoardToFile(PrintWriter writer, Board board, Piece movingPiece) {
        char[][] grid = board.getGrid();
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

    // Menampilkan solusi ke layar
    private static void printSolution(Board initialBoard, List<Move> solution) {
        Board currentBoard = initialBoard;
        System.out.println("Initial board:");
        printBoard(currentBoard, null);

        for (int i = 0; i < solution.size(); i++) {
            Move move = solution.get(i);
            System.out.printf("\nMove %d: %s\n", i + 1, move);

            // Mencari piece di board saat ini
            Piece pieceToMove = null;
            for (Piece p : currentBoard.getPieces()) {
                if (p.getId() == move.getPiece().getId()) {
                    pieceToMove = p;
                    break;
                }
            }

            if (pieceToMove == null) {
                throw new IllegalStateException("Piece " + move.getPiece().getId() + " not found in board");
            }

            currentBoard = currentBoard.movePiece(pieceToMove, move.getDirection(), move.getSteps());
            printBoard(currentBoard, pieceToMove);
        }

        // Menampilkan langkah keluar terakhir jika puzzle sudah terselesaikan
        if (currentBoard.isSolved()) {
            Piece p = currentBoard.getPrimaryPiece();
            String exitDirection = p.getOrientation().equals("horizontal") ? "right" : "down";
            Move finalMove = new Move(p, exitDirection, p.getLength());
            
            System.out.printf("\nMove %d: %s\n", solution.size() + 1, finalMove);
            
            char[][] grid = new char[currentBoard.getRows()][currentBoard.getCols()];
            for (int i = 0; i < currentBoard.getRows(); i++) {
                System.arraycopy(currentBoard.getGrid()[i], 0, grid[i], 0, currentBoard.getCols());
            }
            
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
                currentBoard.getExitCol()
            );
            
            printBoard(exitedBoard, null);
        }
    }

    // Menampilkan kondisi board
    private static void printBoard(Board board, Piece movingPiece) {
        final String RESET = "\u001B[0m";
        final String RED = "\u001B[31m";
        final String YELLOW = "\u001B[33m";
        final String BLUE = "\u001B[34m";
        
        char[][] grid = board.getGrid();
        int rows = board.getRows();
        int cols = board.getCols();
        Piece primaryPiece = board.getPrimaryPiece();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                
                if (cell == primaryPiece.getId()) {
                    // Primary piece dalam warna biru
                    System.out.print(BLUE + cell + RESET);
                } else if (movingPiece != null && cell == movingPiece.getId()) {
                    // Moving piece dalam warna kuning
                    System.out.print(YELLOW + cell + RESET);
                } else if (cell == '.') {
                    System.out.print(cell);
                } else if (cell != '.') {
                    System.out.print(cell);
                } else {
                    System.out.print(cell);
                }
            }

            // Pintu keluar dalam warna merah
            if (board.getExitCol() == cols && i == board.getExitRow()) {
                System.out.print(RED + 'K' + RESET);
            } else if (board.getExitCol() == cols) {
                System.out.print(' ');
            }
            
            System.out.println();
        }
    }
}