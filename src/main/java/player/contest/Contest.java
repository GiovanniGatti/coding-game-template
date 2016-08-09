package player.contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;

import player.Player.AI;
import player.ai.builder.AIInput;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.game.Game;
import player.game.Game.GameResult;
import player.match.Match;

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

        List<Score> scores = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            scores.add(new Score());
        }

        int offset = 0;

        for (int i = 0; i < ais.size() - 1; i++) {
            int k = 0;

            for (int j = i + 1; j < ais.size(); j++) {
                Future<GameResult> future = futures.get(offset + k);
                GameResult result = future.get();

                List<AI> opponents = result.getMatchResults().stream()
                        .map(Match.MatchResult::getOpponent)
                        .collect(Collectors.toList());

                List<AI> players = result.getMatchResults().stream()
                        .map(Match.MatchResult::getPlayer)
                        .collect(Collectors.toList());

                scores.get(i).addAIs(players);
                scores.get(j).addAIs(opponents);

                if (result.getWinner().equals(Winner.PLAYER)) {
                    scores.get(i).incrementScore();
                } else {
                    scores.get(j).incrementScore();
                }

                k++;
            }

            offset += ais.size() - (i + 1);
        }

        return new ContestResult(scores);
    }

    public static class ContestResult {

        private final List<Classification> classifications;

        ContestResult(List<Score> scores) {
            this.classifications = new ArrayList<>();
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                this.classifications.add(new Classification(score.getAis(), score.getScore(), scores.size() - 1));
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

        private final List<AI> ais;
        private final int score;
        private final double winRate;

        Classification(List<AI> ais, int score, int numberOfMatches) {
            this.ais = ais;
            this.score = score;
            this.winRate = ((double) score) / numberOfMatches;
        }

        int getScore() {
            return score;
        }

        List<AI> getAi() {
            return ais;
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
                    // all ais are the same, so any of them is sufficient
                    .add("ai", ais.get(0).getClass().getSimpleName())
                    .add("conf", ais.get(0).getConf())
                    .add("winRate", winRate)
                    .add("score", score)
                    .toString();
        }
    }

    private static class Score {
        private int score;
        private List<AI> ais;

        Score() {
            this.score = 0;
            this.ais = new ArrayList<>();
        }

        void incrementScore() {
            score++;
        }

        int getScore() {
            return score;
        }

        void addAIs(List<AI> ais) {
            this.ais.addAll(ais);
        }

        void addAI(AI ai) {
            ais.add(ai);
        }

        List<AI> getAis() {
            return Collections.unmodifiableList(ais);
        }
    }
}
