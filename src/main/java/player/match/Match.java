package player.match;

import java.util.concurrent.Callable;

import player.Player.AI;
import player.Player.Action;
import player.ai.builder.AIInput;
import player.engine.GameEngine;
import player.engine.Winner;
import player.engine.builder.GEBuild;

/**
 *
 * Represents a single match between any two IAs
 *
 */
public final class Match implements Callable<Match.MatchResult> {

    private final AI player;
    private final AI opponent;
    private final GameEngine gameEngine;

    public Match(
            AIInput player,
            AIInput opponent,
            GEBuild gameEngine) {

        this.gameEngine = gameEngine.build();
        this.player = player.withInputSupplier(this.gameEngine::playerInput).build();
        this.opponent = opponent.withInputSupplier(this.gameEngine::opponentInput).build();
    }

    @Override
    public MatchResult call() throws Exception {
        gameEngine.start();

        do {
            Action[] playerActions = player.play();
            Action[] opponentActions = opponent.play();

            gameEngine.run(playerActions, opponentActions);

        } while (gameEngine.getWinner() == Winner.ON_GOING);

        return new MatchResult(
                gameEngine.getPlayerScore(),
                gameEngine.getOpponentScore(),
                gameEngine.getNumberOfRounds(),
                gameEngine.getWinner());
    }

    public static final class MatchResult {

        private final int playerScore;
        private final int opponentScore;
        private final int rounds;
        private final Winner winner;

        private MatchResult(
                int playerScore,
                int opponentScore,
                int rounds,
                Winner winner) {

            this.playerScore = playerScore;
            this.opponentScore = opponentScore;
            this.rounds = rounds;
            this.winner = winner;
        }

        public int getPlayerScore() {
            return playerScore;
        }

        public int getOpponentScore() {
            return opponentScore;
        }

        public int getRounds() {
            return rounds;
        }

        public Winner getWinner() {
            return winner;
        }

        @Override
        public String toString() {
            return com.google.common.base.MoreObjects.toStringHelper(this)
                    .add("playerScore", playerScore)
                    .add("opponentScore", opponentScore)
                    .add("rounds", rounds)
                    .add("winner", winner)
                    .toString();
        }
    }
}
