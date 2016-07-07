package player.match;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import player.Player.AI;
import player.Player.Action;
import player.engine.GameEngine;
import player.engine.Winner;

public class MatchTest implements WithAssertions {

    @Test
    public void start_up_game_engine() throws Exception {
        AI player = Mockito.mock(AI.class);
        AI opponent = Mockito.mock(AI.class);
        GameEngine gameEngine = Mockito.mock(GameEngine.class);
        when(gameEngine.getWinner()).thenReturn(Winner.PLAYER);

        Match match = new Match(player, opponent, gameEngine);

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

        Match match = new Match(player, opponent, gameEngine);

        match.call();

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
}