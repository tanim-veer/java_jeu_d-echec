package chess.businessLayer;

import chess.businessLayer.board.Position;

public record Move(Position start, Position end) {

    public static Move fromUCI(String uci) {
        return new Move(fromAlg(uci.substring(0, 2)), fromAlg(uci.substring(2, 4)));
    }

    private static Position fromAlg(String s) {
        return new Position(s.charAt(0) - 'a', s.charAt(1) - '1');
    }

    public String toUCI() {
        return toAlg(start) + toAlg(end);
    }

    private String toAlg(Position p) {
        char col = (char) ('a' + p.x());
        char row = (char) ('1' + p.y());
        return "" + col + row;
    }
}