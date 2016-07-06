package player.match;

import player.engine.GameEngine;
import player.Player.AI;

/**
 * Plays a single match between to AIs
 */
public class MatchRunner {

    public static void main(String args[]) throws Exception {

        AI player = null;
        AI opponent = null;

        GameEngine gameEngine = null;

        Match match = new Match(player, opponent, gameEngine);

        Match.MatchResult matchResult = match.call();

        System.out.println(matchResult);
    }

}
