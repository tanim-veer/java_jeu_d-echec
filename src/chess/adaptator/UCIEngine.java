package chess.adaptator;

import chess.businessLayer.Move;
import chess.businessLayer.board.Board;
import chess.useCases.BotStrategy;
import chess.useCases.IBotStrategy;
import chess.useCases.RandomStrategy; // pour le mode aléatoire
import java.util.Scanner;

public class UCIEngine {

    // Change "RandomStrategy()" ou "BotStrategy()"
    private IBotStrategy bot = new RandomStrategy();

    private Board board;

    public static void main(String[] args) {
        new UCIEngine().run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        board = new Board();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            handleCommand(input);
        }
    }

    // "position fen <FEN> moves e1e2 e8d8 ..." : l'interface renvoie toute la partie à chaque tour,
    // il faut donc rejouer les coups après la position de départ.
    private void loadPosition(String input) {
        int movesIndex = input.indexOf(" moves ");
        String base = (movesIndex >= 0) ? input.substring(0, movesIndex) : input;

        board = new Board();
        int fenIndex = base.indexOf(" fen ");
        if (fenIndex >= 0) {
            board.loadFen(base.substring(fenIndex + 5));
        }
        if (movesIndex >= 0) {
            for (String uci : input.substring(movesIndex + 7).trim().split("\\s+")) {
                board.performMove(Move.fromUCI(uci));
            }
        }
    }

    private void handleCommand(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0];

        switch (command) {
            case "uci":
                System.out.println("id name MonSuperBot");
                System.out.println("id author LeGroupe");
                System.out.println("uciok");
                break;

            case "isready":
                System.out.println("readyok");
                break;

            case "position":
                loadPosition(input);
                break;

            case "go":
                String bestMove = bot.findBestMove(board);
                if (bestMove != null) {
                    System.out.println("bestmove " + bestMove);
                }
                break;

            case "quit":
                System.exit(0);
                break;

            default:
                break;
        }
    }
}