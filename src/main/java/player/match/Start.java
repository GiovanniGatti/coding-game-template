package player.match;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import player.Player;

/**
 * Created by tmi on 11/07/16.
 */
public interface Start {
    ConfigurableAI withCtor(BiFunction<Map<String, Object>, IntSupplier, Player.AI> playerCtor);

    End withCtor(Function<IntSupplier, Player.AI> playerCtor);
}
