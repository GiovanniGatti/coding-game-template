package game;

/**
 * Plays multiple matches between to AIs
 */
public class GameRunner {

    public static void main(String args[]) throws Exception {
        Player.AI player = null;
        Player.AI opponent = null;

        StateSupplier stateSupplier = null;

        int numberOfMatches = 100;

        Game game = new Game(player, opponent, stateSupplier, numberOfMatches);

        Game.GameResult gameResult = game.call();

        System.out.println(gameResult);
    }
}
