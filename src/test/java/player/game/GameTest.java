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
    void playMatches() throws Exception {

        Game game = new Game(anyAIInput(), anyAIInput(), MockedGE::any, service, 4);

        GameResult result = game.call();

        assertThat(result.getMatchResults()).hasSize(4);
    }

    @Test
    @DisplayName("winner is the player that won the most number of matches")
    void playerWinRate() throws Exception {
        GameEngine match1 = MockedGE.anyWithWinner(Winner.PLAYER);
        GameEngine match2 = MockedGE.anyWithWinner(Winner.PLAYER);
        GameEngine match3 = MockedGE.anyWithWinner(Winner.OPPONENT);
        GameEngine match4 = MockedGE.anyWithWinner(Winner.OPPONENT);
        GameEngine match5 = MockedGE.anyWithWinner(Winner.OPPONENT);

        List<GameEngine> matches = Arrays.asList(match1, match2, match3, match4, match5);
        Iterator<GameEngine> it = matches.iterator();

        Game game = new Game(anyAIInput(), anyAIInput(), it::next, service, matches.size());

        GameResult result = game.call();

        assertThat(result.getWinner()).isEqualTo(Winner.OPPONENT);
    }

    @Test
    @DisplayName("TODO")
    void check_that_ais_and_game_engines_are_always_the_same() {
        throw new UnsupportedOperationException("TODO");
    }

    @Nested
    @DisplayName("that finished, returns a result with")
    class Statisticts {

        @Test
        @DisplayName("the right average player score")
        void averagePlayerScore() throws Exception {
            GameEngine match1 = MockedGE.anyWithPlayerScore(15);
            GameEngine match2 = MockedGE.anyWithPlayerScore(16);
            GameEngine match3 = MockedGE.anyWithPlayerScore(17);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            Game game = new Game(anyAIInput(), anyAIInput(), it::next, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getAveragePlayerScore()).isEqualTo(16);
        }

        @Test
        @DisplayName("the right average opponent score")
        void averageOpponentScore() throws Exception {
            GameEngine match1 = MockedGE.anyWithOpponentScore(15);
            GameEngine match2 = MockedGE.anyWithOpponentScore(16);
            GameEngine match3 = MockedGE.anyWithOpponentScore(17);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            Game game = new Game(anyAIInput(), anyAIInput(), it::next, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getAverageOpponentScore()).isEqualTo(16);
        }

        @Test
        @DisplayName("the right average number of rounds")
        void averageNumberOfRounds() throws Exception {
            GameEngine match1 = MockedGE.anyWithNumberOfRounds(15);
            GameEngine match2 = MockedGE.anyWithNumberOfRounds(16);
            GameEngine match3 = MockedGE.anyWithNumberOfRounds(17);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            Game game = new Game(anyAIInput(), anyAIInput(), it::next, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getAverageNumberOfRounds()).isEqualTo(16);
        }

        @Test
        @DisplayName("the right player win rate")
        void playerWinRate() throws Exception {
            GameEngine match1 = MockedGE.anyWithWinner(Winner.PLAYER);
            GameEngine match2 = MockedGE.anyWithWinner(Winner.PLAYER);
            GameEngine match3 = MockedGE.anyWithWinner(Winner.OPPONENT);

            List<GameEngine> matches = Arrays.asList(match1, match2, match3);
            Iterator<GameEngine> it = matches.iterator();

            Game game = new Game(anyAIInput(), anyAIInput(), it::next, service, matches.size());

            GameResult result = game.call();

            assertThat(result.getPlayerWinRate()).isEqualTo(2.0 / 3.0);
        }

        @Test
        @DisplayName("the right number of matches")
        void numberOfMatches() throws Exception {
            Game game = new Game(anyAIInput(), anyAIInput(), MockedGE::any, service, 5);

            GameResult result = game.call();

            assertThat(result.getNumberOfMatches()).isEqualTo(5L);
        }

        @Test
        @DisplayName("a readable output")
        void readableOutput() throws Exception {
            MockedGE.Builder match = MockedGE.newBuilder()
                    .withOpponentScore(3)
                    .withPlayerScore(5)
                    .withNumberOfRounds(7)
                    .withWinner(Winner.PLAYER);

            Game game = new Game(anyAIInput(), anyAIInput(), match::build, service, 1);

            GameResult result = game.call();

            assertThat(
                    "GameResult{averagePlayerScore=5.0, "
                            +
                            "averageOpponentScore=3.0, "
                            +
                            "averageNumberOfRounds=7.0, "
                            +
                            "playerWinRate=1.0, "
                            +
                            "numberOfMatches=1, "
                            +
                            "winner=PLAYER, "
                            +
                            "matchResults=["
                            +
                            "MatchResult{player=MockedArtificialIntelligence{ai=MockedArtificialIntelligence, actions=[Mock for Action, hashCode: 1292040526]}, opponent=MockedArtificialIntelligence{ai=MockedArtificialIntelligence, actions=[Mock for Action, hashCode: 1973233403]}, gameEngine=MockedGameEngine{gameEngine=MockedGameEngine, winner=PLAYER, playerScore=5, opponentScore=3, numberOfRounds=7, playerInput=[16, 5, 95], opponentInput=[51, 78, 92], playerInputIt=java.util.ArrayList$Itr@3c73951, opponentInputIt=java.util.ArrayList$Itr@3d5c822d}, playerScore=5, opponentScore=3, rounds=7, winner=PLAYER}]}")
                    .isEqualTo(result.toString());

        }
    }

    private static AIInput anyAIInput() {
        return AIBuilder.newBuilder()
                .withCtor((input) -> MockedAI.any());
    }
}