package heuristics;

import model.Board;

public interface Heuristic {
    int calculate(Board board);
    String getName();
}