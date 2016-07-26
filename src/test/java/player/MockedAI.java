package player;

import static org.mockito.Mockito.when;

import org.mockito.Mockito;

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
            AI ai = Mockito.mock(AI.class);

            when(ai.play()).thenReturn(actions);

            return ai;
        }
    }
}
