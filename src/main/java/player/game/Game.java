package player.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.MoreObjects;

import player.ai.builder.AIInput;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.match.Match;
import player.match.Match.MatchResult;

/**
 * Plays multiple matches between to AIs. It is useful when IAs or State supplier are not deterministic,
 * otherwise, a single match is enough
 */
public class Game implements Callable<Game.GameResult> {

    private static final int DEFAULT_NUMBER_OF_MATCHES = 5;

    private final AIInput player;
    private final AIInput opponent;
    private final GEBuild gameEngine;
    private final int numberOfMatches;
    private final ExecutorService executorService;

    public Game(
            AIInput player,
            AIInput opponent,
            GEBuild gameEngine,
            ExecutorService executorService) {

        this(player, opponent, gameEngine, executorService, DEFAULT_NUMBER_OF_MATCHES);
    }

    public Game(
            AIInput player,
            AIInput opponent,
            GEBuild gameEngine,
            ExecutorService executorService,
            int numberOfMatches) {

        this.player = player;
        this.opponent = opponent;
        this.gameEngine = gameEngine;
        this.numberOfMatches = numberOfMatches;
        this.executorService = executorService;
    }

    @Override
    public GameResult call() throws Exception {
        List<Callable<MatchResult>> matches = new ArrayList<>();
        for (int i = 0; i < numberOfMatches; i++) {
            matches.add(new Match(player, opponent, gameEngine));
        }

        List<Future<MatchResult>> futures = executorService.invokeAll(matches);

        GameResult gameResult = new GameResult();
        for (Future<MatchResult> future : futures) {
            MatchResult matchResult = future.get();
            gameResult.addMatchResult(matchResult);
        }

        return gameResult;
    }

    public static final class GameResult {
        private List<MatchResult> matchResults;

        private GameResult() {
            this.matchResults = new ArrayList<>();
        }

        private void addMatchResult(MatchResult result) {
            matchResults.add(result);
        }

        public double getAveragePlayerScore() {
            double totalPlayerScore =
                    matchResults.stream()
                            .mapToDouble(MatchResult::getPlayerScore)
                            .sum();

            return totalPlayerScore / matchResults.size();
        }

        public double getAverageOpponentScore() {
            double totalOpponentScore =
                    matchResults.stream()
                            .mapToDouble(MatchResult::getOpponentScore)
                            .sum();

            return totalOpponentScore / matchResults.size();
        }

        public double getAverageNumberOfRounds() {
            double totalNumberOfRounds =
                    matchResults.stream()
                            .mapToDouble(MatchResult::getRounds)
                            .sum();

            return totalNumberOfRounds / matchResults.size();
        }

        public double getPlayerWinRate() {
            double numberOfPlayerVictories =
                    matchResults.stream()
                            .map(MatchResult::getWinner)
                            .filter(Winner.PLAYER::equals)
                            .count();

            return numberOfPlayerVictories / matchResults.size();
        }

        public long getNumberOfMatches() {
            return matchResults.size();
        }

        public Winner getWinner() {
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

        public List<MatchResult> getMatchResults() {
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
