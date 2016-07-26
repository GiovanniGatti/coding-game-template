package player.game;

import static org.mockito.Mockito.when;

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

import player.MockedAI;
import player.Player.AI;
import player.ai.builder.AIBuilder;
import player.ai.builder.AIInput;
import player.engine.GameEngine;
import player.engine.MockedGE;
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
            GameEngine match1 = MockedGE.anyWithPlayerScore(15);
            GameEngine match2 = MockedGE.anyWithPlayerScore(16);
            GameEngine match3 = MockedGE.anyWithPlayerScore(17);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(it::next);

            Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getAveragePlayerScore()).isEqualTo(16);
        }

        @Test
        @DisplayName("the right average opponent score")
        void tmp2() {
            GameEngine match1 = MockedGE.anyWithPlayerInput(0, 1, 2, 3);

            System.out.println(match1.playerInput());
            System.out.println(match1.playerInput());
            System.out.println(match1.playerInput());
            System.out.println(match1.playerInput());
            System.out.println(match1.playerInput());
            System.out.println(match1.playerInput());
        }

    }

    private static AIInput anyAIInput() {
        return AIBuilder.newBuilder()
                .withCtor((input) -> MockedAI.any());
    }
}