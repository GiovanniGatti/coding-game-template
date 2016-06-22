package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.MoreObjects;

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

        GameResult gameResult = new GameResult();
        for (Future<MatchResult> future : futures) {
            MatchResult matchResult = future.get();
            gameResult.addMatchResult(matchResult);
        }

        return gameResult;
    }

    static final class GameResult {
        private List<MatchResult> matchResults;

        private GameResult() {
            this.matchResults = new ArrayList<>();
        }

        private void addMatchResult(MatchResult result) {
            matchResults.add(result);
        }

        double getAveragePlayerScore() {
            double totalPlayerScore =
                    matchResults.stream()
                            .mapToDouble(MatchResult::getPlayerScore)
                            .sum();

            return totalPlayerScore / matchResults.size();
        }

        double getAverageOpponentScore() {
            double totalOpponentScore =
                    matchResults.stream()
                            .mapToDouble(MatchResult::getOpponentScore)
                            .sum();

            return totalOpponentScore / matchResults.size();
        }

        double getAverageNumberOfRounds() {
            double totalNumberOfRounds =
                    matchResults.stream()
                            .mapToDouble(MatchResult::getRounds)
                            .sum();

            return totalNumberOfRounds / matchResults.size();
        }

        double getPlayerWinRate() {
            double numberOfPlayerVictories =
                    matchResults.stream()
                            .map(MatchResult::getWinner)
                            .filter(Winner.PLAYER::equals)
                            .count();

            return numberOfPlayerVictories / matchResults.size();
        }

        long getNumberOfMatches() {
            return matchResults.size();
        }

        Winner getWinner() {
            long playerVictoriesCount = matchResults.stream()
                    .map(MatchResult::getWinner)
                    .filter(Winner.PLAYER::equals)
                    .count();

            long opponentVictoriesCount = matchResults.stream()
                    .map(MatchResult::getWinner)
                    .filter(Winner.OPPONENT::equals)
                    .count();

            return playerVictoriesCount > opponentVictoriesCount ? Winner.PLAYER : Winner.OPPONENT;
        }

        List<MatchResult> getMatchResults() {
            return Collections.unmodifiableList(matchResults);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("averagePlayerScore", getAveragePlayerScore())
                    .add("averageOpponentScore", getAverageOpponentScore())
                    .add("averageNumberOfRounds", getAverageNumberOfRounds())
                    .add("playerWinRate", getPlayerWinRate())
                    .add("numberOfMatches", getNumberOfMatches())
                    .add("winner", getWinner())
                    .add("matchResults", matchResults)
                    .toString();
        }
    }
}
