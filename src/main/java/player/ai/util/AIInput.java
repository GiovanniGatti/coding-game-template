package player.ai.util;

import java.util.function.IntSupplier;

public interface AIInput {
    AIBuild withInputSupplier(IntSupplier stream);
}
