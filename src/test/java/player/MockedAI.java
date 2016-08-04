package player;

import java.util.Collections;
import java.util.Objects;

import org.mockito.Mockito;

import com.google.common.base.MoreObjects;

import player.Player.AI;
import player.Player.Action;

public final class MockedAI {

    private MockedAI() {
        // Utility class
    }

    public static AI any() {
        return newBuilder().build();
    }

    public static AI anyWithActions(Action... actions) {
        return newBuilder()
                .withActions(actions)
                .build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Action[] actions;

        private Builder() {
            this.actions = new Action[] { Mockito.mock(Action.class) };
        }

        public Builder withActions(Action... actions) {
            this.actions = actions;
            return this;
        }

        AI build() {
            return new MockedArtificialIntelligence(actions);
        }
    }

    private static class MockedArtificialIntelligence extends AI {

        private final Action[] actions;

        private MockedArtificialIntelligence(Action[] actions) {
            super(Collections.emptyMap(), () -> 0);
            this.actions = actions;
        }

        @Override
        public Action[] play() {
            return actions;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || !(o == null || getClass() != o.getClass());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("actions", actions)
                    .toString();
        }
    }
}
