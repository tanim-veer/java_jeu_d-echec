package chess.useCases;

import chess.businessLayer.Color;
import chess.businessLayer.Move;
import chess.businessLayer.board.Board;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

// Tests sans dépendance externe : lancer ./test.sh
public class MoveGeneratorTest {

    private static int reussis = 0;
    private static int echoues = 0;

    public static void main(String[] args) {
        tourSeuleDepuisUnCoin();
        tourBloqueeParSonRoi();
        tourCaptureUnePieceAdverse();
        roiNePeutPasSeMettreEnEchec();
        roiNePeutPasPrendreUneTourDefendue();
        roiEnEchecDoitSortirDeLEchec();
        tourCloueeResteSurLaColonne();
        traitLuDepuisLeFen();
        coupUCIJoueEtChangeLeTrait();
        conversionUCIAllerRetour();
        strategieJoueLeCampQuiALeTrait();

        System.out.printf("%n%d/%d tests reussis%n", reussis, reussis + echoues);
        System.exit(echoues == 0 ? 0 : 1);
    }

    private static Board plateau(String fen) {
        Board b = new Board();
        b.loadFen(fen);
        return b;
    }

    private static Set<String> coups(Board b, Color c) {
        return new MoveGenerator().generateLegalMoves(b, c).stream()
                .map(Move::toUCI).collect(Collectors.toCollection(TreeSet::new));
    }

    private static Set<String> coupsDepuis(Board b, Color c, String caseDepart) {
        return coups(b, c).stream().filter(m -> m.startsWith(caseDepart))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static void verifier(String nom, Object obtenu, Object attendu) {
        if (obtenu.equals(attendu)) {
            reussis++;
            System.out.println("OK    " + nom);
        } else {
            echoues++;
            System.out.println("ECHEC " + nom + "\n      obtenu  : " + obtenu + "\n      attendu : " + attendu);
        }
    }

    private static void tourSeuleDepuisUnCoin() {
        Board b = plateau("7K/8/8/7k/8/8/8/R7 w - - 0 1");
        verifier("la tour en a1 a 14 coups (colonne + rangee libres)",
                coupsDepuis(b, Color.WHITE, "a1").size(), 14);
    }

    private static void tourBloqueeParSonRoi() {
        Board b = plateau("8/8/8/7k/K7/8/8/R7 w - - 0 1");
        verifier("la tour ne traverse pas son propre roi",
                coupsDepuis(b, Color.WHITE, "a1"),
                Set.of("a1a2", "a1a3", "a1b1", "a1c1", "a1d1", "a1e1", "a1f1", "a1g1", "a1h1"));
    }

    private static void tourCaptureUnePieceAdverse() {
        Board b = plateau("7K/8/8/r6k/8/8/8/R7 w - - 0 1");
        Set<String> tour = coupsDepuis(b, Color.WHITE, "a1");
        verifier("la tour peut prendre en a5 mais pas aller au-dela",
                tour.contains("a1a5") && !tour.contains("a1a6"), true);
    }

    private static void roiNePeutPasSeMettreEnEchec() {
        Board b = plateau("4k3/R7/8/8/8/8/8/7K b - - 0 1");
        verifier("le roi noir ne descend pas sur la 7e rangee controlee par la tour",
                coups(b, Color.BLACK), Set.of("e8d8", "e8f8"));
    }

    private static void roiNePeutPasPrendreUneTourDefendue() {
        Board b = plateau("4k3/4R3/4K3/8/8/8/8/8 b - - 0 1");
        verifier("le roi noir ne prend pas une tour defendue par le roi blanc",
                coups(b, Color.BLACK), Set.of("e8d8", "e8f8"));
    }

    private static void roiEnEchecDoitSortirDeLEchec() {
        Board b = plateau("R3k3/8/8/8/8/8/8/7K b - - 0 1");
        verifier("en echec sur la 8e rangee, le roi noir doit la quitter",
                coups(b, Color.BLACK), Set.of("e8d7", "e8e7", "e8f7"));
    }

    private static void tourCloueeResteSurLaColonne() {
        Board b = plateau("4r2k/8/8/8/8/8/4R3/4K3 w - - 0 1");
        verifier("la tour clouee devant son roi ne quitte pas la colonne e",
                coupsDepuis(b, Color.WHITE, "e2"),
                Set.of("e2e3", "e2e4", "e2e5", "e2e6", "e2e7", "e2e8"));
    }

    private static void traitLuDepuisLeFen() {
        verifier("le trait est lu depuis le FEN",
                plateau("4k3/8/8/8/8/8/8/R3K3 b - - 0 1").getSideToMove(), Color.BLACK);
    }

    private static void coupUCIJoueEtChangeLeTrait() {
        Board b = plateau("4k3/8/8/8/8/8/8/R3K3 w - - 0 1");
        b.performMove(Move.fromUCI("a1a5"));
        verifier("apres a1a5, la tour est en a5 et c'est aux noirs",
                b.toString().contains("5   R ") && b.getSideToMove() == Color.BLACK, true);
    }

    private static void conversionUCIAllerRetour() {
        verifier("fromUCI puis toUCI redonne le meme coup", Move.fromUCI("h1a8").toUCI(), "h1a8");
    }

    private static void strategieJoueLeCampQuiALeTrait() {
        Board b = plateau("4k3/8/8/8/8/8/8/R3K3 b - - 0 1");
        List<String> coupsNoirs = List.copyOf(coups(b, Color.BLACK));
        verifier("la strategie joue un coup du roi noir quand c'est aux noirs",
                coupsNoirs.contains(new BotStrategy().findBestMove(b)), true);
    }
}
