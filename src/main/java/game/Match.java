package game;

import game.Player.AI;
import game.Player.Action;
import game.Player.State;

/**
 * Represents a single match between any two IAs
 */
final class Match implements Runnable {

    private final AI player;
    private final AI opponent;
    private final StateSupplier playerStateSupplier;
    private final StateSupplier opponentStateSupplier;

    private MatchResult result;

    Match(
            AI player,
            AI opponent,
            StateSupplier playerStateSupplier,
            StateSupplier opponentStateSupplier) {

        this.player = player;
        this.opponent = opponent;
        this.playerStateSupplier = playerStateSupplier;
        this.opponentStateSupplier = opponentStateSupplier;
        this.result = null;
    }

    @Override
    public void run() {

        if (result != null) {
            throw new IllegalStateException("Game should not be played twice");
        }

        State playerCurrentState = playerStateSupplier.first();
        State opponentCurrentState = opponentStateSupplier.first();
        int rounds = 0;

        do {
            Action playerAction = player.play(playerCurrentState.clone());
            Action opponentAction = opponent.play(opponentCurrentState.clone());

            playerCurrentState.perform(playerAction);
            opponentCurrentState.perform(opponentAction);

            playerCurrentState = playerStateSupplier.next(playerCurrentState);
            opponentCurrentState = opponentStateSupplier.next(opponentCurrentState);

            rounds++;
        } while (isNotFinished(playerCurrentState, opponentCurrentState, rounds));

        result =
                new MatchResult(
                        playerCurrentState.getPlayerScore(),
                        opponentCurrentState.getOpponentScore(),
                        rounds,
                        getWinner(playerCurrentState, opponentCurrentState, rounds));
    }

    /**
     * 
     * @param playerState
     * @param opponentState
     * @param rounds total number of played rounds
     * @return
     */
    static boolean isNotFinished(State playerState, State opponentState, int rounds) {
        // TODO: implement game ending conditions
        return true;
    }

    /**
     *
     * @param playerState
     * @param opponentState
     * @param rounds total number of played rounds
     * @return
     */
    static Winner getWinner(State playerState, State opponentState, int rounds) {
        // TODO: implement game ending conditions
        return Winner.OPPONENT;
    }

    public MatchResult getResult() {
        if (result == null) {
            throw new IllegalStateException("Game has not been played yet");
        }

        return result;
    }

    static final class MatchResult {

        private final int playerScore;
        private final int opponentScore;
        private final int rounds;
        private final Winner winner;

        private MatchResult(
                int playerScore,
                int opponentScore,
                int rounds,
                Winner winner) {

            this.playerScore = playerScore;
            this.opponentScore = opponentScore;
            this.rounds = rounds;
            this.winner = winner;
        }

        public int getPlayerScore() {
            return playerScore;
        }

        public int getOpponentScore() {
            return opponentScore;
        }

        public int getRounds() {
            return rounds;
        }

        public Winner getWinner() {
            return winner;
        }
    }
}
