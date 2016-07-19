package player.engine.builder;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import player.engine.GameEngine;

public final class GEBuilder<P, T> implements GECtor<P, T>, GEDoubleParam<P, T>, GESingleParam<P>, GEBuild {

    private BiFunction<P, T, GameEngine> doubleParamCtor;
    private Function<P, GameEngine> singleParamCtor;
    private Supplier<GameEngine> noParamCtor;

    private P paramP;
    private T paramT;

    private GEBuilder() {
        // Builder class
    }

    public static <P, T> GECtor<P, T> newBuilder() {
        return new GEBuilder<>();
    }

    @Override
    public GEDoubleParam<P, T> withCtor(BiFunction<P, T, GameEngine> ctor) {
        this.doubleParamCtor = ctor;
        return this;
    }

    @Override
    public GESingleParam<P> withCtor(Function<P, GameEngine> ctor) {
        this.singleParamCtor = ctor;
        return this;
    }

    @Override
    public GEBuild withCtor(Supplier<GameEngine> ctor) {
        this.noParamCtor = ctor;
        return this;
    }

    @Override
    public GEBuild withParam(P param) {
        this.paramP = param;
        return this;
    }

    @Override
    public GEBuild withParam(P paramP, T paramT) {
        this.paramP = paramP;
        this.paramT = paramT;
        return this;
    }

    @Override
    public GameEngine build() {

        if (noParamCtor != null) {
            return noParamCtor.get();
        }

        if (singleParamCtor != null) {
            return singleParamCtor.apply(paramP);
        }

        return doubleParamCtor.apply(paramP, paramT);
    }
}
