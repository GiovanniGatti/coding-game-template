package player.contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.base.MoreObjects;

import player.Player.AI;
import player.ai.builder.AIInput;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.game.Game;
import player.game.Game.GameResult;

/**
 * Play any number of AIs against each other and then check its performances
 */
public final class Contest implements Callable<Contest.ContestResult> {

    private static final int DEFAULT_NUMBER_OF_MATCHES = 5;

    private final List<AIInput> ais;
    private final GEBuild gameEngine;
    private final ExecutorService gameExecutorService;
    private final ExecutorService matchExecutorService;
    private final int numberOfMatches;

    public Contest(
            List<AIInput> ais,
            GEBuild gameEngine,
            ExecutorService gameExecutorService,
            ExecutorService matchExecutorService) {

        this(ais, gameEngine, gameExecutorService, matchExecutorService, DEFAULT_NUMBER_OF_MATCHES);
    }

    public Contest(
            List<AIInput> ais,
            // TODO: make it a list
            GEBuild gameEngine,
            ExecutorService gameExecutorService,
            ExecutorService matchExecutorService,
            int numberOfMatches) {

        this.ais = ais;
        this.gameEngine = gameEngine;
        this.gameExecutorService = gameExecutorService;
        this.matchExecutorService = matchExecutorService;
        this.numberOfMatches = numberOfMatches;
    }

    @Override
    public ContestResult call() throws InterruptedException, ExecutionException {
        // TODO: wrapper class for indexing
        List<Callable<GameResult>> games = new ArrayList<>();
        for (int i = 0; i < ais.size() - 1; i++) {
            AIInput player = ais.get(i);
            for (int j = i + 1; j < ais.size(); j++) {
                AIInput opponent = ais.get(j);
                games.add(
                        new Game(
                                player,
                                opponent,
                                gameEngine,
                                matchExecutorService,
                                numberOfMatches));
            }
        }

        List<Future<GameResult>> futures = gameExecutorService.invokeAll(games);

        int[] scores = new int[ais.size()];
        int offset = 0;
        for (int i = 0; i < ais.size() - 1; i++) {
            int k = 0;

            for (int j = i + 1; j < ais.size(); j++) {
                Future<GameResult> future = futures.get(offset + k);
                GameResult result = future.get();

                AI opponent = result.getMatchResults().get(0).getOpponent();
                AI player = result.getMatchResults().get(0).getPlayer();

                if (result.getWinner().equals(Winner.PLAYER)) {
                    scores[i]++;
                } else {
                    scores[j]++;
                }

                k++;
            }

            offset += ais.size() - (i + 1);
        }

        return new ContestResult(null, scores);
    }

    public static class ContestResult {

        private final List<Classification> classifications;

        ContestResult(List<AI> ais, int[] scores) {
            this.classifications = new ArrayList<>();
            for (int i = 0; i < ais.size(); i++) {
                this.classifications.add(new Classification(ais.get(i), scores[i], ais.size() - 1));
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

        static final Comparator<Classification> SCORE_COMPARATOR = Comparator.comparing(Classification::getScore);

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
                    .add("score", score)
                    .toString();
        }
    }
}
