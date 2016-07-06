package game;

import java.util.concurrent.Callable;

import game.Player.AI;
import game.Player.Action;

/**
 * Represents a single match between any two IAs
 */
final class Match implements Callable<Match.MatchResult> {

    private final AI player;
    private final AI opponent;
    private final GameEngine gameEngine;

    Match(
            AI player,
            AI opponent,
            GameEngine gameEngine) {

        this.player = player;
        this.opponent = opponent;
        this.gameEngine = gameEngine;
    }

    @Override
    public MatchResult call() throws Exception {
        gameEngine.start();

        player.reset();
        opponent.reset();

        do {
            Action[] playerActions = player.play();
            Action[] opponentActions = opponent.play();

            gameEngine.run(playerActions, opponentActions);

        } while (gameEngine.getWinner() != Winner.ON_GOING);

        return new MatchResult(
                gameEngine.getPlayerScore(),
                gameEngine.getOpponentScore(),
                gameEngine.getNumberOfRounds(),
                gameEngine.getWinner());
    }

    static final class MatchResult {

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

        int getPlayerScore() {
            return playerScore;
        }

        int getOpponentScore() {
            return opponentScore;
        }

        int getRounds() {
            return rounds;
        }

        Winner getWinner() {
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
