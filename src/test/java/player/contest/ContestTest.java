package player.contest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import player.MockedAI;
import player.ai.builder.AIInput;
import player.contest.Contest.ContestResult;
import player.engine.MockedGE;
import player.engine.Winner;
import player.engine.builder.GEBuild;

@DisplayName("A contest")
class ContestTest implements WithAssertions {

    private ExecutorService gameExecutorService;
    private ExecutorService matchExecutorService;

    @BeforeEach
    void init() {
        gameExecutorService = Executors.newFixedThreadPool(2);
        matchExecutorService = Executors.newFixedThreadPool(3);
    }

    @Test
    @DisplayName("is a battle between multiple ais against each other")
    void tmp() throws Exception {

        List<AIInput> ais = Arrays.asList((t) -> MockedAI::any, (t) -> MockedAI::any, (t) -> MockedAI::any);
        List<GEBuild> gameEngines = Collections.singletonList(() -> MockedGE.anyWithWinner(Winner.PLAYER));

        Contest contest = new Contest(
                ais,
                gameEngines,
                gameExecutorService,
                matchExecutorService);

        ContestResult contestResult = contest.call();

        System.out.println(contestResult);

    }

}