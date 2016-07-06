package game;

import java.util.List;

import game.Contest.ContestResult;
import game.Player.AI;

/**
 * Play a contest
 */
public class ContestRunner {

    public static void main(String args[]) throws Exception {
        List<AI> ais = generateAIs();

        GameEngine gameEngine = null;

        int numberOfMatches = 100;

        ContestResult contestResult = Contest.run(ais, gameEngine, numberOfMatches);

        System.out.println(contestResult);
    }

    /**
     * Read configuration from file or generate it randomly and then build the AI objects
     */
    private static List<AI> generateAIs() {
        return null;
    }
}
