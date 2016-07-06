package game;

import game.Match.MatchResult;
import game.Player.AI;

/**
 * Plays a single match between to AIs
 */
public class MatchRunner {

    public static void main(String args[]) throws Exception {

        AI player = null;
        AI opponent = null;

        GameEngine gameEngine = null;

        Match match = new Match(player, opponent, gameEngine);

        MatchResult matchResult = match.call();

        System.out.println(matchResult);
    }

}
