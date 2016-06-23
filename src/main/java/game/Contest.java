package game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import game.Game.GameResult;
import game.Player.AI;

final class Contest {

    private Contest() {
    }

    public static ContestResult run(List<AI> ais, StateSupplier stateSupplier, int numberOfMatches)
            throws InterruptedException, ExecutionException {

        ExecutorService service = Executors.newFixedThreadPool(numberOfMatches);

        List<Callable<GameResult>> games = new ArrayList<>();
        for (int i = 0; i < ais.size() - 1; i++) {
            AI player = ais.get(i);
            for (int j = i + 1; j < ais.size(); j++) {
                AI opponent = ais.get(j);
                games.add(new Game(player, opponent, stateSupplier, numberOfMatches));
            }
        }

        List<Future<GameResult>> futures = service.invokeAll(games);

        int[] scores = new int[ais.size()];
        int offset = 0;
        for (int i = 0; i < ais.size() - 1; i++) {
            AI player = ais.get(i);

            int k = 0;

            for (int j = i + 1; j < ais.size(); j++) {
                AI opponent = ais.get(j);
                Future<GameResult> future = futures.get(offset + k);

                GameResult result = future.get();

                if(result.getWinner().equals(Winner.PLAYER)){
                    scores[i]++;
                }else{
                    scores[j]++;
                }

                //TODO: keep going... One must map the scores to each AI and output as the ContestResult
                //printing the AI config + win rate + type + other usefull information

                k++;
            }

            offset += ais.size() - (i + 1);
        }

        service.shutdown();

        return null;
    }

    static class ContestResult {

        ContestResult(List<AI> ais){

        }

        List<AI> getClassification(){
            return null;
        }

    }
}
