package player.contest;

import java.util.Map;
import java.util.function.IntSupplier;

import player.Player;
import player.Player.AI;
import player.engine.GameEngine;

/**
 * Play a contest
 */
public class ContestRunner {

    public static void main(String args[]) throws Exception {
        // List<Contest.AIConf> ais = generateAIs();

        GameEngine gameEngine = null;

        int numberOfMatches = 100;

        // Contest.ContestResult contestResult = Contest.run(ais, gameEngine, numberOfMatches);

        // System.out.println(contestResult);
    }

    /**
     * Read configuration from file or generate it randomly and then build the AI objects
     */
    // private static List<Contest.AIConf> generateAIs() {
    // List<Contest.AIConf> ais = new ArrayList<>();
    // ais.add(new Contest.AIConf(WeirdAI::new, Collections.emptyMap()));
    // return null;
    // }

    private static class WeirdAI extends AI {

        public WeirdAI(Map<String, Object> conf, IntSupplier inputSupplier) {
            super(conf, inputSupplier);
        }

        @Override
        public Player.Action[] play() {
            return new Player.Action[0];
        }
    }
}
