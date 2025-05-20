package model;

public class Move {
    private Piece piece;
    private String direction;
    private int steps;

    public Move(Piece piece, String direction, int steps) {
        this.piece = piece;
        this.direction = direction;
        this.steps = steps;
    }

    // Getters
    public Piece getPiece() { return piece; }
    public String getDirection() { return direction; }
    public int getSteps() { return steps; }

    @Override
    public String toString() {
        return piece.getId() + " " + direction + " " + steps;
    }
}