package player.match;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import player.Player.AI;
import player.Player.Action;
import player.engine.GameEngine;
import player.engine.Winner;

/**
 *
 * Represents a single match between any two IAs
 *
 * @param <G> the type of the game engine input
 */
public final class Match<G> implements Callable<Match.MatchResult> {

    private final AI player;
    private final AI opponent;
    private final GameEngine gameEngine;

    //TODO: idea will probably work, but how to deal with so many constructors? Should I create a AI builder?
    public Match(
            BiFunction<Map<String, Object>, IntSupplier, AI> playerCtor,
            Map<String, Object> playerConf,
            BiFunction<Map<String, Object>, IntSupplier, AI> opponentCtor,
            Map<String, Object> opponentConf,
            Function<G, GameEngine> gameEngineCtor,
            G gameEngineConf) {

        this.gameEngine = gameEngineCtor.apply(gameEngineConf);
        this.player = playerCtor.apply(playerConf, gameEngine::playerInput);
        this.opponent = opponentCtor.apply(opponentConf, gameEngine::opponentInput);
    }

    public Match(
            Function<IntSupplier, AI> playerCtor,
            Function<IntSupplier, AI> opponentCtor,
            Supplier<GameEngine> gameEngineCtor) {

        this.gameEngine = gameEngineCtor.get();
        this.player = playerCtor.apply(gameEngine::playerInput);
        this.opponent = opponentCtor.apply(gameEngine::opponentInput);
    }

    public Match(
            BiFunction<Map<String, Object>, IntSupplier, AI> playerCtor,
            Map<String, Object> playerConf,
            BiFunction<Map<String, Object>, IntSupplier, AI> opponentCtor,
            Map<String, Object> opponentConf,
            Supplier<GameEngine> gameEngineCtor) {

        this.gameEngine = gameEngineCtor.get();
        this.player = playerCtor.apply(playerConf, gameEngine::playerInput);
        this.opponent = opponentCtor.apply(opponentConf, gameEngine::opponentInput);
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
