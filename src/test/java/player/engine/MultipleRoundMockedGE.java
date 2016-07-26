package player.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import player.Player;
import player.engine.MockedGE.Builder;

public class MultipleRoundMockedGE implements GameEngine {

    private final Iterator<GameEngine> rounds;
    private GameEngine currentState;

    public MultipleRoundMockedGE(Builder... rounds) {
        List<GameEngine> r = new ArrayList<>();

        for (Builder round : rounds) {
            r.add(round.build());
        }

        this.rounds = r.iterator();
        this.currentState = this.rounds.next();
    }

    @Override
    public void start() {
        // ILB
    }

    @Override
    public void run(Player.Action[] playerActions, Player.Action[] opponentActions) {
        currentState = rounds.next();
    }

    @Override
    public Winner getWinner() {
        return currentState.getWinner();
    }

    @Override
    public int playerInput() {
        return currentState.playerInput();
    }

    @Override
    public int opponentInput() {
        return currentState.opponentInput();
    }

    @Override
    public int getPlayerScore() {
        return currentState.getPlayerScore();
    }

    @Override
    public int getOpponentScore() {
        return currentState.getOpponentScore();
    }

    @Override
    public int getNumberOfRounds() {
        return currentState.getNumberOfRounds();
    }
}
