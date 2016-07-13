package player.ai.util;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import com.google.common.collect.ImmutableMap;

import player.Player;

public final class AIBuilder implements AICtor, AIConfig, AIInput, AIBuild {

    private AIBuilder() {
        // Builder class
    }

    private BiFunction<Map<String, Object>, IntSupplier, Player.AI> configurableCtor;
    private Map<String, Object> conf;

    private Function<IntSupplier, Player.AI> simpleCtor;

    private IntSupplier inputSupplier;

    public static AICtor newBuilder() {
        return new AIBuilder();
    }

    @Override
    public AIConfig withCtor(BiFunction<Map<String, Object>, IntSupplier, Player.AI> ctor) {
        this.configurableCtor = ctor;
        return this;
    }

    @Override
    public AIInput withConf(Map<String, Object> conf) {
        this.conf = ImmutableMap.copyOf(conf);
        return this;
    }

    @Override
    public AIInput withCtor(Function<IntSupplier, Player.AI> ctor) {
        this.simpleCtor = ctor;
        return this;
    }

    @Override
    public AIBuild withInputSupplier(IntSupplier inputSupplier) {
        this.inputSupplier = inputSupplier;
        return this;
    }

    @Override
    public Player.AI build() {
        if (simpleCtor != null) {
            return simpleCtor.apply(inputSupplier);
        }

        return configurableCtor.apply(conf, inputSupplier);
    }
}
