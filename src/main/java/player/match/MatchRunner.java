package player.match;

import player.ai.builder.AIBuilder;
import player.ai.builder.AIInput;
import player.engine.builder.GEBuild;
import player.engine.builder.GEBuilder;
import player.match.Match.MatchResult;

/**
 * Plays a single match between to AIs
 */
public class MatchRunner {

    public static void main(String args[]) throws Exception {

        AIInput player = AIBuilder.newBuilder()
                .withCtor((inputSupplier) -> {
                    throw new UnsupportedOperationException("Missing implementation: player AI constructor");
                });

        AIInput opponent = AIBuilder.newBuilder()
                .withCtor((inputSupplier) -> {
                    throw new UnsupportedOperationException("Missing implementation: opponent AI constructor");
                });

        GEBuild gameEngine = GEBuilder.newBuilder()
                .withCtor(() -> {
                    throw new UnsupportedOperationException("Missing implementation: game engine constructor");
                });

        Match match = new Match(player, opponent, gameEngine);

        MatchResult matchResult = match.call();

        System.out.println(matchResult);
    }
}
