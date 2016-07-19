package player.match;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import player.Player.AI;
import player.Player.Action;
import player.ai.builder.AIBuilder;
import player.ai.builder.AIInput;
import player.engine.GameEngine;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.engine.builder.GEBuilder;
import player.match.Match.MatchResult;

public class MatchTest implements WithAssertions {

    @Test
    @DisplayName("Start up game engine")
    public void start_up_game_engine() throws Exception {
        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);

        GEBuild gameEngineSupplier = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        AI player = mock(AI.class);
        AIInput playerInput = AIBuilder.newBuilder()
                .withCtor((input) -> player);

        AI opponent = mock(AI.class);
        AIInput opponentInput = AIBuilder.newBuilder()
                .withCtor((input) -> opponent);

        Match match = new Match(playerInput, opponentInput, gameEngineSupplier);

        match.call();

        verify(gameEngine, times(1)).start();
    }

    @Test
    @DisplayName("Search player and opponent AI actions and play them")
    public void play_ai_actions() throws Exception {
        AI player = Mockito.mock(AI.class);
        Action playerAction = Mockito.mock(Action.class);
        when(player.play()).thenReturn(new Action[] { playerAction });

        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> player);

        AI opponent = Mockito.mock(AI.class);
        Action opponentAction = Mockito.mock(Action.class);
        when(opponent.play()).thenReturn(new Action[] { opponentAction });

        AIInput opponentIInput =
                AIBuilder.newBuilder()
                        .withCtor((input) -> opponent);

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);
        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Match match = new Match(playerAIInput, opponentIInput, gameEngineBuild);

        match.call();

        ArgumentCaptor<Action[]> playerActions = ArgumentCaptor.forClass(Action[].class);
        ArgumentCaptor<Action[]> opponentActions = ArgumentCaptor.forClass(Action[].class);
        verify(gameEngine).run(playerActions.capture(), opponentActions.capture());

        assertThat(playerAction).isEqualTo(playerActions.getValue()[0]);
        assertThat(opponentAction).isEqualTo(opponentActions.getValue()[0]);
    }

    @Test
    @DisplayName("Return the right winner")
    public void return_the_right_winner() throws Exception {
        AI player = Mockito.mock(AI.class);
        Action playerAction = Mockito.mock(Action.class);
        when(player.play()).thenReturn(new Action[] { playerAction });

        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> player);

        AI opponent = Mockito.mock(AI.class);
        Action opponentAction = Mockito.mock(Action.class);
        when(opponent.play()).thenReturn(new Action[] { opponentAction });

        AIInput opponentIInput =
                AIBuilder.newBuilder()
                        .withCtor((input) -> opponent);

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);
        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Match match = new Match(playerAIInput, opponentIInput, gameEngineBuild);

        MatchResult matchResult = match.call();

        assertThat(matchResult.getWinner()).isEqualTo(Winner.PLAYER);
    }

    @Test
    @DisplayName("Play until winner is found")
    public void play_until_winner_is_found() throws Exception {
        AI player = Mockito.mock(AI.class);
        Action playerAction = Mockito.mock(Action.class);
        when(player.play()).thenReturn(new Action[] { playerAction });

        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> player);

        AI opponent = Mockito.mock(AI.class);
        Action opponentAction = Mockito.mock(Action.class);
        when(opponent.play()).thenReturn(new Action[] { opponentAction });

        AIInput opponentIInput =
                AIBuilder.newBuilder()
                        .withCtor((input) -> opponent);

        GEBuild gameEngineBuild = GEBuilder.<Integer, Void> newBuilder()
                .withCtor(RoundTrackerGE::new)
                .withParam(2);

        Match match = new Match(playerAIInput, opponentIInput, gameEngineBuild);

        MatchResult matchResult = match.call();

        assertThat(matchResult.getRounds()).isEqualTo(2);
    }

    @Test
    @DisplayName("Assign statistics from game engine to match's result")
    public void assign_statistics_from_game_engine_to_match_result() throws Exception {
        AI player = Mockito.mock(AI.class);
        Action playerAction = Mockito.mock(Action.class);
        when(player.play()).thenReturn(new Action[] { playerAction });

        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> player);

        AI opponent = Mockito.mock(AI.class);
        Action opponentAction = Mockito.mock(Action.class);
        when(opponent.play()).thenReturn(new Action[] { opponentAction });

        AIInput opponentIInput =
                AIBuilder.newBuilder()
                        .withCtor((input) -> opponent);

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.OPPONENT);
        when(gameEngine.getPlayerScore()).thenReturn(5);
        when(gameEngine.getOpponentScore()).thenReturn(10);
        when(gameEngine.getNumberOfRounds()).thenReturn(7);

        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Match match = new Match(playerAIInput, opponentIInput, gameEngineBuild);

        MatchResult matchResult = match.call();

        assertThat(matchResult.getWinner()).isEqualTo(Winner.OPPONENT);
        assertThat(matchResult.getPlayerScore()).isEqualTo(5);
        assertThat(matchResult.getOpponentScore()).isEqualTo(10);
        assertThat(matchResult.getRounds()).isEqualTo(7);
    }

    @Test
    @DisplayName("Supports multithreading")
    public void should_support_multithreading() throws Exception {
        AI player = Mockito.mock(AI.class);
        Action playerAction = Mockito.mock(Action.class);
        when(player.play()).thenReturn(new Action[] { playerAction });

        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> player);

        AI opponent = Mockito.mock(AI.class);
        Action opponentAction = Mockito.mock(Action.class);
        when(opponent.play()).thenReturn(new Action[] { opponentAction });

        AIInput opponentIInput =
                AIBuilder.newBuilder()
                        .withCtor((input) -> opponent);

        GEBuild gameEngineBuild = GEBuilder.<Integer, Void> newBuilder()
                .withCtor(RoundTrackerGE::new)
                .withParam(10);

        Match match1 = new Match(playerAIInput, opponentIInput, gameEngineBuild);
        Match match2 = new Match(playerAIInput, opponentIInput, gameEngineBuild);

        ExecutorService service = Executors.newFixedThreadPool(2);
        List<Callable<MatchResult>> matches = Arrays.asList(match1, match2);
        List<Future<MatchResult>> futures = service.invokeAll(matches);

        assertThat(futures)
                .extracting(MatchTest::unsafeFutureExtractor)
                .extracting(MatchResult::getRounds)
                .containsOnly(10);
    }

    private static <T> T unsafeFutureExtractor(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class RoundTrackerGE implements GameEngine {
        int rounds;
        int numberOfRounds;

        RoundTrackerGE(int numberOfRounds) {
            rounds = 0;
            this.numberOfRounds = numberOfRounds;
        }

        @Override
        public void start() {

        }

        @Override
        public void run(Action[] playerActions, Action[] opponentActions) {
            rounds++;
        }

        @Override
        public Winner getWinner() {
            if (rounds < numberOfRounds) {
                return Winner.ON_GOING;
            }
            return Winner.PLAYER;
        }

        @Override
        public int playerInput() {
            return 0;
        }

        @Override
        public int opponentInput() {
            return 0;
        }

        @Override
        public int getPlayerScore() {
            return 0;
        }

        @Override
        public int getOpponentScore() {
            return 0;
        }

        @Override
        public int getNumberOfRounds() {
            return rounds;
        }
    }

}