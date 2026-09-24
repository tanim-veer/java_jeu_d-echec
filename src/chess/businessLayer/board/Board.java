package chess.businessLayer.board;

import chess.businessLayer.Color;
import chess.businessLayer.IPiece;
import chess.businessLayer.Move;
import chess.businessLayer.piece.PieceType;
import java.util.HashMap;
import java.util.Map;
import chess.businessLayer.piece.King;
import chess.businessLayer.piece.Rook;

public class Board {

    public static final int BOARD_SIZE = 8;
    private final Map<Position, Square> tableau = new HashMap<>();
    private Color sideToMove = Color.WHITE;

    public Board() {
        clear();
    }

    public Board(Board original) {
        for (Map.Entry<Position, Square> entry : original.tableau.entrySet()) {
            Position pos = entry.getKey();
            Square originalSquare = entry.getValue();
            this.tableau.put(pos, new Square(originalSquare.getPiece()));
        }
        this.sideToMove = original.sideToMove;
    }

    public Color getSideToMove() {
        return sideToMove;
    }

    public final void clear() {
        tableau.clear();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                tableau.put(new Position(x,y), new Square(null));
            }
        }
    }

    public IPiece getPieceAt(Position p) {
        Square s = tableau.get(p);
        return (s != null) ? s.getPiece() : null;
    }

    public boolean isSquareEmpty(Position p) {
        return getPieceAt(p) == null;
    }

    public Position findKing(Color color) {
        for (Map.Entry<Position, Square> entry : tableau.entrySet()) {
            IPiece p = entry.getValue().getPiece();
            if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void performMove(Move move) {
        Square startSq = tableau.get(move.start());
        Square endSq = tableau.get(move.end());

        IPiece pieceToMove = startSq.getPiece();

        endSq.setPiece(pieceToMove);
        startSq.setPiece(null);
        sideToMove = (sideToMove == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public void putPiece(Position p, IPiece piece) {
        Square s = tableau.get(p);
        if(s != null) {
            s.setPiece(piece);
        }
    }

    public void loadFen(String fen) {
        clear();

        String[] parts = fen.trim().split("\\s+");
        String piecePlacement = parts[0];
        sideToMove = (parts.length > 1 && parts[1].equals("b")) ? Color.BLACK : Color.WHITE;

        int row = 7;
        int col = 0;

        for (int i = 0; i < piecePlacement.length(); i++) {
            char c = piecePlacement.charAt(i);

            if (c == '/') {
                row--;
                col = 0;
            } else if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
            } else {
                Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;
                IPiece piece = null;

                switch (Character.toLowerCase(c)) {
                    case 'k': piece = new King(color); break;
                    case 'r': piece = new Rook(color); break;
                }

                if (piece != null) {
                    putPiece(new Position(col, row), piece);
                }
                col++;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = BOARD_SIZE - 1; y >= 0; y--) {
            sb.append(y + 1).append("  ");
            for (int x = 0; x < BOARD_SIZE; x++) {
                IPiece piece = tableau.get(new Position(x, y)).getPiece();
                char p = (piece == null) ? '.' : switch (piece.getType()) {
                    case PieceType.KING -> (piece.getColor() == Color.WHITE ? 'K' : 'k');
                    case PieceType.BISHOP -> 'b';
                    case PieceType.KNIGHT -> 'n';
                    case PieceType.PAWN -> 'p';
                    case PieceType.QUEEN -> 'q';
                    case PieceType.ROOK -> (piece.getColor() == Color.WHITE ? 'R' : 'r');
                };
                sb.append(" ").append(p).append(" ");
            }
            sb.append("\n");
        }
        sb.append("   a  b  c  d  e  f  g  h");
        return sb.toString();
    }
}