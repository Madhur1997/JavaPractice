package LLD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

enum DrawReason {
    StaleMate,
    DrawByChoice,
    NotEnoughPieces,
    FiftyMoveRule,
    ThreeFoldRepetition
}

enum GameState {
    ACTIVE,
    WHITEWINNER,
    BLACKWINNER,
    DRAW
}

class MoveResult {
    public boolean validMove;
    public GameState gameState;
    public Optional<DrawReason> drawReason;
}

// this is the top level class the interacts with pieces to generate and apply moves.
// Board class is used as a utility by other classes to provide information regarding the state of the board.
public class ChessGame {
    private final Board board = new Board();
    private Player[] player = new Player[2];
    private Color nextTurn = Color.White;
    private GameState gameState = GameState.ACTIVE;

    public List<Move> validMoves(Position position) {

        if(!validateTurn(position)) {
            return new ArrayList<>();
        }

        Piece piece = board.pieceAt(position);
        // It only generates moves that are valid in the sense of piece. The overall turn/check level checks happen here
        List<Move> moves = piece.generateMoves(board);

        Iterator<Move> it = moves.iterator();

        while (it.hasNext()) {
            Move move = it.next();

            if (!isLegalMove(move)) {
                it.remove();
            }
        }

        return moves;
    }

    private boolean isLegalMove(Move move) {
        move.apply(board);
        if(isKingInCheck(board, move.getMovedPiece().getColor())) {
            move.undo(board);
            return false;
        }

        return true;
    }

    // Generate moves for all pieces and see if king can be captured.
    private boolean isKingInCheck(Board board, Color color) {
        return true;
    }


    private boolean validateTurn(Position position) {
        Piece piece = board.pieceAt(position);
        return piece != null && piece.getColor() == nextTurn;
    }

    public MoveResult makeMove(Move move) {

        // Check after ma
        return new MoveResult();
    }
}

// This will have the intelligence to check if the move is a valid one and returns the Move Object
abstract class Piece {
    private Color color;
    private Position position;

    public abstract List<Move> generateMoves(Board board);
    public void move(Move move) {
        this.position = move.getFinalPosition();
    }

    protected Color getColor() {
        return color;
    }

    protected Position getPosition() {
        return position;
    }
}

// For each piece, it maintains a direction and distance, it can move and based on it, the moves are generated
class Pawn extends Piece {
    int normalDistance[][] = {{1, 0}};
    private boolean isMoved;

    @Override
    public List<Move> generateMoves(Board board) {


        // create the type of move
        return new ArrayList<>();
    }
}

class Rook extends Piece {
    private boolean isMoved;

    @Override
    public List<Move> generateMoves(Board board) {
        return new ArrayList<>();
    }
}

class King extends Piece {
    private boolean isMoved;

    @Override
    public List<Move> generateMoves(Board board) {
        return new ArrayList<>();
    }
}

class Knight extends Piece {

    @Override
    public List<Move> generateMoves(Board board) {
        return new ArrayList<>();
    }
}

// this is mostly a data class which stores the pieces and also can cache the pieces, the position of white/black kings to serve
// as helper methods.
class Board {
    private final Piece[][] pieceMatrix = new Piece[8][8];

    public Board() {
    }

    public void setPiece(Piece piece, Position position) {
        this.pieceMatrix[position.row][position.col] = piece;
    }

    public @Nullable Piece pieceAt(Position position) {
        return pieceMatrix[position.row][position.col];
    }
}

enum Color {
    White,
    Black
}

class Position {
    public int row;
    public int col;

    public Position(final int row, final int col) {
        this.row = row;
        this.col = col;
    }
}

// Each special type of move stores its characteristics, for ex
// 1. En passant move
// 2. Castling move stores the Rook position.
// 3. Promotion piece stores the promoted piece.
abstract class Move{
    private final Position initialPosition;
    private final Position finalPosition;

    private final Piece movedPiece;
    private final @Nullable Piece capturedPiece;

    public Move(Position position, Position finalPosition, Piece movedPiece, Piece capturedPiece) {
        this.initialPosition = position;
        this.finalPosition = finalPosition;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
    }

    // Apply the move on board, and also call piece.move to mutate the state of pieces.
    public abstract void apply(Board board);

    // Undo the move on board and also call piece.move to mutate the state of pieces.
    public abstract void undo(Board board);

    public Position getInitialPosition() {
        return initialPosition;
    }

    public Position getFinalPosition() {
        return finalPosition;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }
}

class NormalMove extends Move {
    public NormalMove(Position initialPosition, Position finalPosition, Piece movedPiece, Piece capturedPiece) {
        super(initialPosition, finalPosition, movedPiece, capturedPiece);
    }
    @Override
    public void apply(Board board) {

    }
    @Override
    public void undo(Board board) {}
}

class Player {
    private final String name;
    private final Color color;

    public Player(final String name, final Color color) {
        this.name = name;
        this.color = color;
    }
}

interface Validator {

    public abstract boolean validate(Board board, Color color);
}

class DrawValidator implements Validator {
    @Override
    public boolean validate(Board board, Color color) {
        return true;
    }
}

class CheckValidator implements Validator {
    @Override
    public boolean validate(Board board, Color color) {
        return true;
    }
}

class CheckmateValidator implements Validator {
    @Override
    public boolean validate(Board board, Color color) {
        return false;
    }
}
