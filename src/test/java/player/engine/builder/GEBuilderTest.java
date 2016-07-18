package player.engine.builder;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import player.Player;
import player.engine.GameEngine;
import player.engine.Winner;

public class GEBuilderTest implements WithAssertions {

    @Test
    @DisplayName("Build no-op game engine with no args constructor")
    public void build_with_no_args() {
        GameEngine gameEngine = GEBuilder.<Void, Void> newBuilder()
                .withCtor((Supplier<GameEngine>) NoOpGameEngine::new)
                .build();

        assertThat(gameEngine).isExactlyInstanceOf(NoOpGameEngine.class);
        NoOpGameEngine noOpGameEngine = (NoOpGameEngine) gameEngine;
        assertThat(noOpGameEngine.getI()).isEqualTo(null);
        assertThat(noOpGameEngine.getS()).isEqualTo(null);
    }

    @Test
    @DisplayName("Build no-op game engine with one args constructor")
    public void build_with_one_arg() {
        GameEngine gameEngine = GEBuilder.<Integer, Void> newBuilder()
                .withCtor((Function<Integer, GameEngine>) NoOpGameEngine::new)
                .withParam(5)
                .build();

        assertThat(gameEngine).isExactlyInstanceOf(NoOpGameEngine.class);
    }

    @Test
    @DisplayName("Build no-op game engine with two args constructor")
    public void build_with_two_args() {
        GameEngine gameEngine = GEBuilder.<Integer, String> newBuilder()
                .withCtor((BiFunction<Integer, String, GameEngine>) NoOpGameEngine::new)
                .withParam(5, "empty")
                .build();

        assertThat(gameEngine).isExactlyInstanceOf(NoOpGameEngine.class);
    }

    public static class NoOpGameEngine implements GameEngine {

        private Integer i;
        private String s;

        NoOpGameEngine() {

        }

        NoOpGameEngine(Integer i) {
            this.i = i;
        }

        NoOpGameEngine(Integer i, String s) {
            this.i = i;
            this.s = s;
        }

        Integer getI() {
            return i;
        }

        String getS() {
            return s;
        }

        @Override
        public void start() {

        }

        @Override
        public void run(Player.Action[] playerActions, Player.Action[] opponentActions) {

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