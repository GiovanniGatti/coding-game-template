package player.match;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import player.Player;

/**
 * Created by tmi on 11/07/16.
 */
public final class AIBuilder implements Start, ConfigurableAI, End {

    private AIBuilder() {
        // Builder class
    }

    private BiFunction<Map<String, Object>, IntSupplier, Player.AI> playerCtor;
    private Map<String, Object> playerConf;

    private Function<IntSupplier, Player.AI> playerCtor2;

    public static Start newBuilder() {
        return new AIBuilder();
    }

    @Override
    public ConfigurableAI withCtor(BiFunction<Map<String, Object>, IntSupplier, Player.AI> playerCtor) {

        return this;
    }

    @Override
    public End withCtor(Function<IntSupplier, Player.AI> playerCtor) {

        return this;
    }

    @Override
    public AIBuilder withConf(Map<String, Object> conf) {

        return this;
    }

    @Override
    public Player.AI build() {
        return null;
    }
}
