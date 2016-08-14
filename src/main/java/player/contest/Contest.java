package player.contest;

import com.google.common.base.MoreObjects;
import player.Player.AI;
import player.ai.builder.AIInput;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.game.Game;
import player.game.Game.GameResult;
import player.match.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Play any number of AIs against each other and then check its performances
 */
public final class Contest implements Callable<Contest.ContestResult> {

    private static final int DEFAULT_NUMBER_OF_MATCHES = 5;

    private final List<AIInput> ais;
    private final List<GEBuild> gameEngines;
    private final ExecutorService gameExecutorService;
    private final ExecutorService matchExecutorService;
    private final int numberOfMatches;

    public Contest(
            List<AIInput> ais,
            List<GEBuild> gameEngines,
            ExecutorService gameExecutorService,
            ExecutorService matchExecutorService) {

        this(ais, gameEngines, gameExecutorService, matchExecutorService, DEFAULT_NUMBER_OF_MATCHES);
    }

    public Contest(
            List<AIInput> ais,
            List<GEBuild> gameEngines,
            ExecutorService gameExecutorService,
            ExecutorService matchExecutorService,
            int numberOfMatches) {

        this.ais = ais;
        this.gameEngines = gameEngines;
        this.gameExecutorService = gameExecutorService;
        this.matchExecutorService = matchExecutorService;
        this.numberOfMatches = numberOfMatches;
    }

    @Override
    public ContestResult call() throws InterruptedException, ExecutionException {

        List<Callable<GameResult>> games = new ArrayList<>();
        for (int i = 0; i < ais.size() - 1; i++) {
            AIInput player = ais.get(i);
            for (int j = i + 1; j < ais.size(); j++) {
                AIInput opponent = ais.get(j);
                for (GEBuild gameEngine : gameEngines) {
                    games.add(
                            new Game(
                                    player,
                                    opponent,
                                    gameEngine,
                                    matchExecutorService,
                                    numberOfMatches));
                }
            }
        }

        List<Future<GameResult>> futures = gameExecutorService.invokeAll(games);

        Score[] scores = new Score[ais.size()];

        int offset = 0;

        for (int i = 0; i < ais.size() - 1; i++) {
            int k = 0;

            for (int j = i + 1; j < ais.size(); j++) {
                Future<GameResult> future = futures.get(offset + k);
                GameResult result = future.get();

                AI player = result.getMatchResults().stream()
                        .map(Match.MatchResult::getPlayer)
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("Expected at least one player, but none found"));

                if (scores[i] == null) {
                    scores[i] = new Score(player);
                }

                AI opponent = result.getMatchResults().stream()
                        .map(Match.MatchResult::getOpponent)
                        .findAny()
                        .orElseThrow(() -> new IllegalStateException("Expected at least one opponent, but none found"));

                if (scores[j] == null) {
                    scores[j] = new Score(opponent);
                }

                if (result.getWinner().equals(Winner.PLAYER)) {
                    scores[i].incrementVictoryCount();
                } else {
                    scores[j].incrementVictoryCount();
                }

                k++;
            }

            offset += ais.size() - (i + 1);
        }

        return new ContestResult(scores);
    }

    public static class ContestResult {

        private final List<Classification> classifications;

        ContestResult(Score[] scores) {
            this.classifications = new ArrayList<>();
            for (int i = 0; i < scores.length; i++) {
                Score score = scores[i];
                this.classifications.add(new Classification(score.getAi(), score.getVictoryCount(), scores.length - 1));
            }

            Collections.sort(classifications);
        }

        List<Classification> getClassifications() {
            return Collections.unmodifiableList(classifications);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < classifications.size(); i++) {
                stringBuilder.append(i + 1).append("- ").append(classifications.get(i).toString()).append('\n');
            }
            return stringBuilder.toString();
        }
    }

    public static class Classification implements Comparable<Classification> {

        static final Comparator<Classification> SCORE_COMPARATOR =
                Comparator.comparing(Classification::getScore).reversed();

        private final AI ai;
        private final int score;
        private final double winRate;

        Classification(AI ai, int score, int numberOfMatches) {
            this.ai = ai;
            this.score = score;
            this.winRate = ((double) score) / numberOfMatches;
        }

        int getScore() {
            return score;
        }

        AI getAi() {
            return ai;
        }

        double getWinRate() {
            return winRate;
        }

        @Override
        public int compareTo(Classification o) {
            return SCORE_COMPARATOR.compare(this, o);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("ai", ai.getClass().getSimpleName())
                    .add("conf", ai.getConf())
                    .add("winRate", winRate)
                    .add("victoryCount", score)
                    .toString();
        }
    }

    private static class Score {
        private int victoryCount;
        private final AI ai;

        Score(AI ai) {
            this.ai = ai;
            this.victoryCount = 0;
        }

        void incrementVictoryCount() {
            victoryCount++;
        }

        int getVictoryCount() {
            return victoryCount;
        }

        AI getAi() {
            return ai;
        }
    }
}
