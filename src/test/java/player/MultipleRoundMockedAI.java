package player;

import static player.Player.AI;
import static player.Player.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import player.MockedAI.Builder;

public class MultipleRoundMockedAI extends AI {

    private final Iterator<AI> rounds;

    public MultipleRoundMockedAI(Builder... rounds) {
        super(Collections.emptyMap(), () -> 0);

        List<AI> r = new ArrayList<>();
        for (Builder round : rounds) {
            r.add(round.build());
        }
        this.rounds = r.iterator();
    }

    @Override
    public Action[] play() {
        return rounds.next().play();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
