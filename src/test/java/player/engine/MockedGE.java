package player.engine;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

public final class MockedGE {

    private MockedGE() {
        // Utility class
    }

    public static Builder newBuilder() {
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

    public static GameEngine anyWithNumberOfRounds(int rounds) {
        return newBuilder()
                .withNumberOfRounds(rounds)
                .build();
    }

    public static GameEngine anyWithPlayerInput(int... playerInput) {
        return newBuilder()
                .withPlayerInput(playerInput)
                .build();
    }

    public static GameEngine anyWithOpponentInput(int... opponentInput) {
        return newBuilder()
                .withOpponentInput(opponentInput)
                .build();
    }

    public static final class Builder {

        private Random random;
        private Winner winner;
        private int playerScore;
        private int opponentScore;
        private int numberOfRounds;

        private List<Integer> playerInput;
        private List<Integer> opponentInput;

        private Builder() {
            this.random = new Random();

            this.winner = random.nextBoolean() ? Winner.PLAYER : Winner.OPPONENT;
            this.playerScore = random.nextInt(100);
            this.opponentScore = random.nextInt(100);
            this.numberOfRounds = random.nextInt(100);

            int inputSize = random.nextInt(4) + 1;
            List<Integer> playerInput = new ArrayList<>();
            List<Integer> opponentInput = new ArrayList<>();
            for (int i = 0; i < inputSize; i++) {
                playerInput.add(random.nextInt(100));
                opponentInput.add(random.nextInt(100));
            }

            this.playerInput = playerInput;
            this.opponentInput = opponentInput;
        }

        public Builder withWinner(Winner winner) {
            this.winner = winner;
            return this;
        }

        public Builder withPlayerScore(int playerScore) {
            this.playerScore = playerScore;
            return this;
        }

        public Builder withOpponentScore(int opponentScore) {
            this.opponentScore = opponentScore;
            return this;
        }

        public Builder withNumberOfRounds(int numberOfRounds) {
            this.numberOfRounds = numberOfRounds;
            return this;
        }

        public Builder withPlayerInput(int[] playerInput) {
            List<Integer> input = new ArrayList<>();
            for (int i : playerInput) {
                input.add(i);
            }
            this.playerInput = input;
            return this;
        }

        public Builder withOpponentInput(int... opponentInput) {
            List<Integer> input = new ArrayList<>();
            for (int i : opponentInput) {
                input.add(i);
            }
            this.opponentInput = input;
            return this;
        }

        public GameEngine build() {
            GameEngine gameEngine = Mockito.mock(GameEngine.class);

            when(gameEngine.getWinner()).thenReturn(winner);
            when(gameEngine.getPlayerScore()).thenReturn(playerScore);
            when(gameEngine.getOpponentScore()).thenReturn(opponentScore);
            when(gameEngine.getNumberOfRounds()).thenReturn(numberOfRounds);

            Iterator<Integer> it = playerInput.iterator();
            OngoingStubbing<Integer> playerStubbing = when(gameEngine.playerInput()).thenReturn(it.next());
            it.forEachRemaining(playerStubbing::thenReturn);

            it = opponentInput.iterator();
            OngoingStubbing<Integer> opponentStubbing = when(gameEngine.opponentInput()).thenReturn(it.next());
            it.forEachRemaining(opponentStubbing::thenReturn);

            return gameEngine;
        }
    }
}
