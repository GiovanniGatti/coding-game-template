package player.engine.builder;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import player.engine.GameEngine;

public interface GECtor<P, T> {

    GEDoubleParam<P, T> withCtor(BiFunction<P, T, GameEngine> ctor);

    GESingleParam<P> withCtor(Function<P, GameEngine> ctor);

    GEBuild withCtor(Supplier<GameEngine> ctor);
}
