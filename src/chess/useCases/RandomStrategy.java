package chess.useCases;

import chess.businessLayer.board.Board;
import chess.businessLayer.Move;
import java.util.List;
import java.util.Random;

public class RandomStrategy implements IBotStrategy {

    private final Random random = new Random();

    @Override
    public String findBestMove(Board board) {
        MoveGenerator generator = new MoveGenerator();

        List<Move> moves = generator.generateLegalMoves(board, board.getSideToMove());

        if (moves.isEmpty()) {
            return null;
        }

        // On tire au hasard
        int index = random.nextInt(moves.size());
        Move move = moves.get(index);

        // On renvoie le format texte (UCI)
        return move.toUCI();
    }
}