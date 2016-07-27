package player.game;

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

import player.MockedAI;
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
        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(MockedGE::any);

        Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, 4);

        GameResult result = game.call();

        assertThat(result.getMatchResults()).hasSize(4);
    }

    @Test
    @DisplayName("winner is the player that won the most number of matches")
    void player_win_rate() throws Exception {
        GameEngine match1 = MockedGE.anyWithWinner(Winner.PLAYER);
        GameEngine match2 = MockedGE.anyWithWinner(Winner.PLAYER);
        GameEngine match3 = MockedGE.anyWithWinner(Winner.OPPONENT);
        GameEngine match4 = MockedGE.anyWithWinner(Winner.OPPONENT);
        GameEngine match5 = MockedGE.anyWithWinner(Winner.OPPONENT);

        List<GameEngine> matches = Arrays.asList(match1, match2, match3, match4, match5);
        Iterator<GameEngine> it = matches.iterator();

        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(it::next);

        Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, matches.size());

        GameResult result = game.call();

        assertThat(result.getWinner()).isEqualTo(Winner.OPPONENT);
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
        void average_opponent_score() throws Exception {
            GameEngine match1 = MockedGE.anyWithOpponentScore(15);
            GameEngine match2 = MockedGE.anyWithOpponentScore(16);
            GameEngine match3 = MockedGE.anyWithOpponentScore(17);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(it::next);

            Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getAverageOpponentScore()).isEqualTo(16);
        }

        @Test
        @DisplayName("the right average number of rounds")
        void average_number_of_rounds() throws Exception {
            GameEngine match1 = MockedGE.anyWithNumberOfRounds(15);
            GameEngine match2 = MockedGE.anyWithNumberOfRounds(16);
            GameEngine match3 = MockedGE.anyWithNumberOfRounds(17);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(it::next);

            Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getAverageNumberOfRounds()).isEqualTo(16);
        }

        @Test
        @DisplayName("the right player win rate")
        void player_win_rate() throws Exception {
            GameEngine match1 = MockedGE.anyWithWinner(Winner.PLAYER);
            GameEngine match2 = MockedGE.anyWithWinner(Winner.PLAYER);
            GameEngine match3 = MockedGE.anyWithWinner(Winner.OPPONENT);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(it::next);

            Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getPlayerWinRate()).isEqualTo(2.0 / 3.0);
        }

        @Test
        @DisplayName("the right number of matches")
        void number_of_matches() throws Exception {
            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(MockedGE::any);

            Game game = new Game(anyAIInput(), anyAIInput(), gameEngineBuild, service, 5);

            GameResult result = game.call();

            assertThat(result.getNumberOfMatches()).isEqualTo(5L);
        }
    }

    private static AIInput anyAIInput() {
        return AIBuilder.newBuilder()
                .withCtor((input) -> MockedAI.any());
    }
}