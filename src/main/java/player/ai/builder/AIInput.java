package player.ai.builder;

import java.util.function.IntSupplier;

public interface AIInput {
    AIBuild withInputSupplier(IntSupplier stream);
}
