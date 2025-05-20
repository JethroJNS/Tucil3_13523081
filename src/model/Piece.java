package model;

public class Piece {
    private final char id;
    private final int length;
    private int row, col;
    private final String orientation;
    private final boolean isPrimary;

    public Piece(char id, int row, int col, int length, String orientation, boolean isPrimary) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.orientation = orientation;
        this.isPrimary = isPrimary;
    }

    // Getters
    public char getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getLength() { return length; }
    public String getOrientation() { return orientation; }
    public boolean isPrimary() { return isPrimary; }

    // Memindahkan posisi Piece berdasarkan delta baris dan kolom
    public void move(int dr, int dc) {
        row += dr;
        col += dc;
    }

    // Mengkloning Piece
    public Piece clone() {
        return new Piece(id, row, col, length, orientation, isPrimary);
    }

    // Setters
    public void setRow(int row) {
        this.row = row;
    }
    public void setCol(int col) {
        this.col = col;
    }


    // Mengembalikan array koordinat cell yang ditempati Piece
    public int[][] getOccupiedCells() {
        int[][] cells = new int[length][2];
        for (int i = 0; i < length; i++) {
            cells[i][0] = orientation.equals("horizontal") ? row : row + i;
            cells[i][1] = orientation.equals("horizontal") ? col + i : col;
        }
        return cells;
    }

    public boolean isHorizontal() {
        return orientation.equals("horizontal");
    }

    public boolean isVertical() {
        return orientation.equals("vertical");
    }

    @Override
    public String toString() {
        return String.format("%c(%d,%d,%d,%s)", id, row, col, length, orientation);
    }
}