package player.game;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import player.Player.AI;
import player.ai.builder.AIBuilder;
import player.ai.builder.AIInput;
import player.engine.GameEngine;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.engine.builder.GEBuilder;
import player.game.Game.GameResult;

@DisplayName("A game")
class GameTest implements WithAssertions {

    private ExecutorService service;

    @BeforeEach
    void init() {
        service = Executors.newFixedThreadPool(3);
    }

    @Test
    @DisplayName("is a run of multiple matches")
    void play_matches() throws Exception {
        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> Mockito.mock(AI.class));

        AIInput opponentIInput = AIBuilder.newBuilder()
                .withCtor((input) -> Mockito.mock(AI.class));

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);
        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Game game = new Game(playerAIInput, opponentIInput, gameEngineBuild, service, 4);

        GameResult result = game.call();

        assertThat(result.getMatchResults()).hasSize(4);
    }

    @Test
    @DisplayName("two games should independently run in parallel")
    void two_games_should_run_in_parallel() throws Exception {
        // TODO: what????
    }

    @Nested
    @DisplayName("that finished, returns a result with")
    class Statisticts {

        @Test
        @DisplayName("the right average player score")
        void average_player_score() throws Exception {
            Iterator<GameEngine> gameEngines = generateMultipleGE(
                    Arrays.asList(Winner.PLAYER, Winner.PLAYER, Winner.PLAYER),
                    Arrays.asList(2, 3, 4),
                    Arrays.asList(0, 0, 0),
                    Arrays.asList(0, 0, 0));

            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(gameEngines::next);

            AIInput playerAIInput = AIBuilder.newBuilder()
                    .withCtor((input) -> Mockito.mock(AI.class));

            AIInput opponentIInput = AIBuilder.newBuilder()
                    .withCtor((input) -> Mockito.mock(AI.class));

            Game game = new Game(playerAIInput, opponentIInput, gameEngineBuild, service, 3);

            GameResult result = game.call();

            assertThat(result.getAveragePlayerScore()).isEqualTo(3);
        }

        @Test
        @DisplayName("the right average opponent score")
        void tmp2() {

        }

    }

    // TODO: create test utility to create mocked Game engines
    private static Iterator<GameEngine> generateMultipleGE(
            List<Winner> winner,
            List<Integer> playerScore,
            List<Integer> opponentScore,
            List<Integer> numberOfRounds) {

        List<GameEngine> gameEngines = new ArrayList<>();

        for (int i = 0; i < winner.size(); i++) {
            gameEngines.add(gameEngineProvider(winner.get(i), playerScore.get(i), opponentScore.get(i),
                    numberOfRounds.get(i)));
        }

        return gameEngines.iterator();
    }

    private static GameEngine gameEngineProvider(
            Winner winner,
            int playerScore,
            int opponentScore,
            int numberOfRounds) {

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(winner);
        when(gameEngine.getPlayerScore()).thenReturn(playerScore);
        when(gameEngine.getOpponentScore()).thenReturn(opponentScore);
        when(gameEngine.getNumberOfRounds()).thenReturn(numberOfRounds);
        return gameEngine;
    }
}