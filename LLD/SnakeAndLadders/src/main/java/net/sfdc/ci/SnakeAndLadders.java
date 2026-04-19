package net.sfdc.ci;

/*
Flow: 1-100 board, snakes at some position, ladder at some position.
Players: Color, roll dice move. both snake and ladders modeled as initial, final position
snake can't be on 100, ladder can't be on 100.

*/

import java.util.*;

enum GameStatus {
    ACTIVE,
    OVER
}

class MoveResult {
    public final Optional<Piece> winnerPiece;
    boolean moved;

    public MoveResult(Piece winnerPiece, boolean moved) {
        this.winnerPiece = Optional.of(winnerPiece);
        this.moved = true;
    }

    public MoveResult(boolean b) {
        this(null, b);
    }
}

class Board {
    private final HashMap<Position, Position> snakeAndLadders = new HashMap<>();
    private List<Piece> nextPiece = new LinkedList<Piece>();
    private Dice dice = new Dice();
    private int maxColumn, maxRow;

    Board(List<List<Position>> snakes, List<List<Position>> ladders, List<String> playerNames) {
        for(int i=0; i<snakes.size(); i++) {
            snakeAndLadders.put(snakes.get(i).get(0), snakes.get(i).get(1));
        }

        for(int i=0; i<ladders.size(); i++) {
            snakeAndLadders.put(ladders.get(i).get(0), ladders.get(i).get(1));
        }

        for(int i=0; i<playerNames.size(); i++) {
            nextPiece.addLast(new Piece(playerNames.get(i), generateColor(i)));
        }
    }

    // Generate new colors for each idx.
    private String generateColor(int idx) {
        return "RED";
    }

    public Dice getDice() {
        return dice;
    }

    public MoveResult move(int steps) {
        Piece pieceToMove = nextPiece.removeFirst();
        nextPiece.addLast(pieceToMove);

        Position newPosition = getFinalPosition(pieceToMove, steps);
        if(newPosition == Position.FINAL_POSITION) {
            return new MoveResult(pieceToMove, true);
        }

        if(newPosition == pieceToMove.getPosition()) return new MoveResult(false);
        return new MoveResult(true);
    }

    private Position getFinalPosition(Piece pieceToMove, int steps) {
        int r = pieceToMove.getPosition().getRow(), c = pieceToMove.getPosition().getCol();
        Position finalPosition;
        if(steps <= maxColumn - c) {
            c += steps;
        } else {
            if(r == maxRow) return pieceToMove.getPosition();
            r++;
            c = steps;
        }

        finalPosition = new Position(r, c);
        if(snakeAndLadders.containsKey(finalPosition)) {
            finalPosition = snakeAndLadders.get(finalPosition);
        }

        return finalPosition;
    }
}

class Position {
    public int r;
    public int c;

    public static final Position FINAL_POSITION = new Position(10, 10);

    public Position(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public int getRow() {
        return r;
    }

    public int getCol() {
        return c;
    }
}

class Piece {
    private final String color;
    private final String name;
    private Position position;

    public Piece(String name, String color) {
        this.name = name;
        this.color = color;
        this.position = new Position(0, 0);
    }

    public Position getPosition() {
        return position;
    }
}

class Dice {
    public int generateMove() {
        Random rand = new Random();
        return rand.nextInt(1, 7);
    }
}

public class SnakeAndLadders {
    private Board board;
    private GameStatus gameStatus;
    public SnakeAndLadders() {
        // Generate snakes and ladders and initialize board.
    }

    public void move() {
        if(gameStatus == GameStatus.OVER)
        int steps = board.getDice().generateMove();
        MoveResult moveResult = board.move(steps);
        if(moveResult.winnerPiece.isPresent()) {
            gameStatus = GameStatus.OVER;
        }
    }
}

