package player.match;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import player.Player.AI;
import player.Player.Action;
import player.ai.util.AIBuilder;
import player.ai.util.AIInput;
import player.engine.GameEngine;
import player.engine.Winner;

public class MatchTest implements WithAssertions {

    @Test
    @DisplayName("ha!")
    public void start_up_game_engine() throws Exception {
        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);

        AIInput player = AIBuilder.newBuilder()
                .withCtor(MockedAI::new)
                .withConf(new HashMap<>());

        AIInput opponent = AIBuilder.newBuilder()
                .withCtor(MockedAI::new)
                .withConf(new HashMap<>());

        Match match =
                new Match(player, opponent, MockedGE::new);

        match.call();

        verify(gameEngine, atLeastOnce()).start();
    }

    @Test
    public void play_ai_actions() throws Exception {
        Action playerAction = Mockito.mock(Action.class);
        Action opponentAction = Mockito.mock(Action.class);

        AI player = Mockito.mock(AI.class);
        when(player.play()).thenReturn(new Action[] { playerAction });

        AI opponent = Mockito.mock(AI.class);
        when(opponent.play()).thenReturn(new Action[] { opponentAction });

        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);

        // Match match = new Match(player, opponent, gameEngine);

        // match.call();

        ArgumentCaptor<Action[]> playerActions = ArgumentCaptor.forClass(Action[].class);
        ArgumentCaptor<Action[]> opponentActions = ArgumentCaptor.forClass(Action[].class);
        verify(gameEngine).run(playerActions.capture(), opponentActions.capture());

        assertThat(playerAction).isEqualTo(playerActions.getValue()[0]);
        assertThat(opponentAction).isEqualTo(opponentActions.getValue()[0]);
    }

    @Test
    public void test_multiple_turns() throws Exception {

    }

    @Test
    public void test_match_result() throws Exception {

    }

    @Test
    public void test_multithreading() throws Exception {

    }

    public static class MockedAI extends AI {

        public MockedAI(Map<String, Object> conf, IntSupplier inputSupplier) {
            super(inputSupplier);
        }

        @Override
        public Action[] play() {
            Action action = new Action();
            return new Action[] { action };
        }
    }

    private static class MockedGE implements GameEngine {

        public MockedGE() {

        }

        @Override
        public void start() {

        }

        @Override
        public void run(Action[] playerActions, Action[] opponentActions) {

        }

        @Override
        public Winner getWinner() {
            return null;
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
            return 0;
        }
    }

}