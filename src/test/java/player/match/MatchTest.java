package player.match;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import player.MockedAI;
import player.Player.Action;
import player.ai.builder.AIBuilder;
import player.ai.builder.AIInput;
import player.engine.GameEngine;
import player.engine.MockedGE;
import player.engine.MultipleRoundMockedGE;
import player.engine.Winner;
import player.engine.builder.GEBuild;
import player.engine.builder.GEBuilder;
import player.match.Match.MatchResult;

@DisplayName("A match")
class MatchTest implements WithAssertions {

    @Test
    @DisplayName("starts up only once the provided game engine")
    void startUpGameEngine() {
        MockedGE.Builder start = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round1 = MockedGE.newBuilder().withWinner(Winner.PLAYER);

        MultipleRoundMockedGE gameEngine = new MultipleRoundMockedGE(start, round1);

        GEBuild gameEngineSupplier = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Match match = new Match(anyAIInput(), anyAIInput(), gameEngineSupplier);

        match.call();

        assertThat(gameEngine.getStartCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("is played until someone wins")
    void playUntilWinner() {
        MockedGE.Builder start = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round1 = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round2 = MockedGE.newBuilder().withWinner(Winner.ON_GOING);
        MockedGE.Builder round3 = MockedGE.newBuilder().withWinner(Winner.PLAYER);

        MultipleRoundMockedGE gameEngine = new MultipleRoundMockedGE(start, round1, round2, round3);

        GEBuild gameEngineSupplier = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Match match = new Match(anyAIInput(), anyAIInput(), gameEngineSupplier);

        match.call();

        assertThat(gameEngine.getRunCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("consists of providing AI input and them playing their output actions")
    void playAIActions() {

        Action playerAction = Mockito.mock(Action.class);
        AIInput playerAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> MockedAI.anyWithActions(playerAction));

        Action opponentAction = Mockito.mock(Action.class);
        AIInput opponentAIInput = AIBuilder.newBuilder()
                .withCtor((input) -> MockedAI.anyWithActions(opponentAction));

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);
        GEBuild gameEngineBuild = GEBuilder.newBuilder()
                .withCtor(() -> gameEngine);

        Match match = new Match(playerAIInput, opponentAIInput, gameEngineBuild);

        match.call();

        ArgumentCaptor<Action[]> playerActions = ArgumentCaptor.forClass(Action[].class);
        ArgumentCaptor<Action[]> opponentActions = ArgumentCaptor.forClass(Action[].class);
        verify(gameEngine).run(playerActions.capture(), opponentActions.capture());

        assertThat(playerAction).isEqualTo(playerActions.getValue()[0]);
        assertThat(opponentAction).isEqualTo(opponentActions.getValue()[0]);
    }

    @Nested
    @DisplayName("returns")
    class MatchResults {

        @Test
        @DisplayName("the right winner")
        void rightWinner() {
            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(() -> MockedGE.anyWithWinner(Winner.OPPONENT));

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getWinner()).isEqualTo(Winner.OPPONENT);
        }

        @Test
        @DisplayName("the right player score")
        void playerScore() {
            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(() -> MockedGE.anyWithPlayerScore(17));

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getPlayerScore()).isEqualTo(17);
        }

        @Test
        @DisplayName("the right opponent score")
        void opponentScore() {
            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(() -> MockedGE.anyWithOpponentScore(17));

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getOpponentScore()).isEqualTo(17);
        }

        @Test
        @DisplayName("the right number of rounds")
        void numberOfRounds() {
            GEBuild gameEngineBuild = GEBuilder.newBuilder()
                    .withCtor(() -> MockedGE.anyWithNumberOfRounds(3));

            Match match = new Match(anyAIInput(), anyAIInput(), gameEngineBuild);

            MatchResult matchResult = match.call();

            assertThat(matchResult.getRounds()).isEqualTo(3);
        }
    }

    private static AIInput anyAIInput() {
        return AIBuilder.newBuilder()
                .withCtor((input) -> MockedAI.any());
    }
}