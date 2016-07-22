package player.engine;

import static org.mockito.Mockito.when;

import java.util.Random;

import org.mockito.Mockito;

public final class MockedGE {

    private MockedGE() {
        // Utility class
    }

    private static Builder newBuilder() {
        return new Builder();
    }

    public static GameEngine any() {
        return newBuilder().build();
    }

    public static GameEngine anyWithWinner(Winner winner) {
        return newBuilder()
                .withWinner(winner)
                .build();
    }

    public static GameEngine anyWithPlayerScore(int playerScore) {
        return newBuilder()
                .withPlayerScore(playerScore)
                .build();
    }

    public static GameEngine anyWithOpponentScore(int opponentScore) {
        return newBuilder()
                .withOpponentScore(opponentScore)
                .build();
    }

    public static GameEngine anyWithRounds(int rounds) {
        return newBuilder()
                .withNumberOfRounds(rounds)
                .build();
    }

    public static final class Builder {

        private Random random;
        private Winner winner;
        private int playerScore;
        private int opponentScore;
        private int numberOfRounds;

        private Builder() {
            this.random = new Random();

            this.winner = random.nextBoolean() ? Winner.PLAYER : Winner.OPPONENT;
            this.playerScore = random.nextInt(100);
            this.opponentScore = random.nextInt(100);
            this.numberOfRounds = random.nextInt(100);
        }

        Builder withWinner(Winner winner) {
            this.winner = winner;
            return this;
        }

        Builder withPlayerScore(int playerScore) {
            this.playerScore = playerScore;
            return this;
        }

        Builder withOpponentScore(int opponentScore) {
            this.opponentScore = opponentScore;
            return this;
        }

        Builder withNumberOfRounds(int numberOfRounds) {
            this.numberOfRounds = numberOfRounds;
            return this;
        }

        GameEngine build() {
            GameEngine gameEngine = Mockito.mock(GameEngine.class);

            when(gameEngine.getWinner()).thenReturn(winner);
            when(gameEngine.getPlayerScore()).thenReturn(playerScore);
            when(gameEngine.getOpponentScore()).thenReturn(opponentScore);
            when(gameEngine.getNumberOfRounds()).thenReturn(numberOfRounds);

            // TODO
            when(gameEngine.playerInput()).thenReturn(null);
            when(gameEngine.opponentInput()).thenReturn(null);

            return gameEngine;
        }
    }
}
