package game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import game.Match.MatchResult;
import game.Player.AI;

/**
 * Plays multiple matches between to AIs. It is useful when IAs or State supplier are not deterministic,
 * otherwise, a single match is enough
 */
class Game implements Callable<Game.GameResult> {

    private final AI player;
    private final AI opponent;
    private final StateSupplier stateSupplier;
    private final int numberOfMatches;

    Game(AI player, AI opponent, StateSupplier stateSupplier, int numberOfMatches) {
        this.player = player;
        this.opponent = opponent;
        this.stateSupplier = stateSupplier;
        this.numberOfMatches = numberOfMatches;
    }

    @Override
    public GameResult call() throws Exception {
        ExecutorService service = Executors.newFixedThreadPool(numberOfMatches);

        List<Callable<MatchResult>> matches = new ArrayList<>();
        for (int i = 0; i < numberOfMatches; i++) {
            matches.add(new Match(player, opponent, stateSupplier));
        }

        List<Future<MatchResult>> futures = service.invokeAll(matches);

        for (Future<MatchResult> future : futures) {
            MatchResult matchResult = future.get();

        }

        return null;
    }

    //TODO: keep implementing game scoring 
    static final class GameResult {
        private double avaregePlayerScore;
        private double avaregeOpponentScore;
        private double avaregeNumberOfRounds;
        private double playerWinRate;
        private Winner winner;

        private GameResult() {
            this.avaregePlayerScore = 0.0;
            this.avaregeOpponentScore = 0.0;
            this.avaregeNumberOfRounds = 0.0;
            this.playerWinRate = 0.0;
        }

        public double getAvaregeOpponentScore() {
            return avaregeOpponentScore;
        }

        public void setAvaregeOpponentScore(double avaregeOpponentScore) {
            this.avaregeOpponentScore = avaregeOpponentScore;
        }

        public double getAvaregeNumberOfRounds() {
            return avaregeNumberOfRounds;
        }

        public void setAvaregeNumberOfRounds(double avaregeNumberOfRounds) {
            this.avaregeNumberOfRounds = avaregeNumberOfRounds;
        }

        public double getPlayerWinRate() {
            return playerWinRate;
        }

        public void setPlayerWinRate(double playerWinRate) {
            this.playerWinRate = playerWinRate;
        }

        public double getAvaregePlayerScore() {
            return avaregePlayerScore;
        }

        public void setWinner(Winner winner) {
            this.winner = winner;
        }

        public Winner getWinner() {
            return winner;
        }
    }
}
