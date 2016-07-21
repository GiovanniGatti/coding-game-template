package player.contest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;

import com.google.common.base.MoreObjects;

import player.Player;
import player.Player.AI;
import player.game.Game;
import player.game.Game.GameResult;
import player.engine.GameEngine;
import player.engine.Winner;

/**
 * Play any number of AIs against each other and then check its performances
 */
final class Contest {

    private Contest() {
    }

    static ContestResult run(List<AIConf> ais, GameEngine gameEngine, int numberOfMatches)
            throws InterruptedException, ExecutionException {

        ExecutorService service = Executors.newFixedThreadPool(numberOfMatches);

        List<Callable<GameResult>> games = new ArrayList<>();
        for (int i = 0; i < ais.size() - 1; i++) {
            AIConf player = ais.get(i);
            for (int j = i + 1; j < ais.size(); j++) {
                AIConf opponent = ais.get(j);
//                games.add(new Game(player.aiCtor, player.conf, opponent.aiCtor, opponent.conf, gameEngine, numberOfMatches));
            }
        }

        List<Future<GameResult>> futures = service.invokeAll(games);

        int[] scores = new int[ais.size()];
//        int offset = 0;
//        for (int i = 0; i < ais.size() - 1; i++) {
//            AI player = ais.get(i);
//
//            int k = 0;
//
//            for (int j = i + 1; j < ais.size(); j++) {
//                AI opponent = ais.get(j);
//                Future<GameResult> future = futures.get(offset + k);
//
//                GameResult result = future.get();
//
//                if (result.getWinner().equals(Winner.PLAYER)) {
//                    scores[i]++;
//                } else {
//                    scores[j]++;
//                }
//
//                k++;
//            }
//
//            offset += ais.size() - (i + 1);
//        }

        service.shutdown();

        return new ContestResult(null, scores);
    }

    static class ContestResult {

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

    public static class AIConf{
        private final BiFunction<Map<String, Object>, IntSupplier, AI> aiCtor;
        private final Map<String, Object> conf;

        public AIConf(BiFunction<Map<String, Object>, IntSupplier, AI> aiCtor, Map<String, Object> conf){
            this.aiCtor = aiCtor;
            this.conf = conf;
        }
    }

    static class Classification implements Comparable<Classification> {

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
