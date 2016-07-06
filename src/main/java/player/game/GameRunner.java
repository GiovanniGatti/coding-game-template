package player.game;

import player.Player;

/**
 * Plays multiple matches between to AIs
 */
public class GameRunner {

    public static void main(String args[]) throws Exception {
        Player.AI player = null;
        Player.AI opponent = null;

        GameEngine gameEngine = null;

        int numberOfMatches = 100;

        Game game = new Game(player, opponent, gameEngine, numberOfMatches);

        Game.GameResult gameResult = game.call();

        System.out.println(gameResult);
    }
}
