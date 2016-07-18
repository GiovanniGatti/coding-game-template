package player.ai.builder;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import player.Player;

public interface AICtor {
    AIConfig withCtor(BiFunction<Map<String, Object>, IntSupplier, Player.AI> playerCtor);

    AIInput withCtor(Function<IntSupplier, Player.AI> playerCtor);
}
