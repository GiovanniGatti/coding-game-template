package player.contest;

import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import player.MockedAI;
import player.ai.builder.AIInput;
import player.contest.Contest.Classification;
import player.contest.Contest.ContestResult;
import player.engine.MockedGE;
import player.engine.Winner;
import player.engine.builder.GEBuild;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @DisplayName("returns the classification of a battle between multiple ais")
    void returnsClassificationBetweenMultipleAIs() throws Exception {
        AIInput firstAI = (t) -> () -> MockedAI.anyConf(ImmutableMap.of("id", "first"));
        AIInput secondAI = (t) -> () -> MockedAI.anyConf(ImmutableMap.of("id", "second"));
        AIInput thirdAI = (t) -> () -> MockedAI.anyConf(ImmutableMap.of("id", "third"));
        List<AIInput> ais = Arrays.asList(firstAI, secondAI, thirdAI);

        List<GEBuild> gameEngines = Collections.singletonList(() -> MockedGE.anyWithWinner(Winner.PLAYER));

        Contest contest = new Contest(
                ais,
                gameEngines,
                gameExecutorService,
                matchExecutorService);

        ContestResult contestResult = contest.call();

        List<Classification> classifications = contestResult.getClassifications();

        assertThat(classifications).hasSize(3);

        Classification first = classifications.get(0);
        Classification second = classifications.get(1);
        Classification third = classifications.get(2);

        assertThat(first.getAi().getConf()).containsEntry("id", "first");
        assertThat(first.getVictoryCount()).isEqualTo(2);

        assertThat(second.getAi().getConf()).containsEntry("id", "second");
        assertThat(second.getVictoryCount()).isEqualTo(1);

        assertThat(third.getAi().getConf()).containsEntry("id", "third");
        assertThat(third.getVictoryCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("returns the classification of a battle between multiple ais on multiple game engines")
    void returnsClassificationBetweenMultipleAIsOnMultipleGameEngines() throws Exception {
        AIInput firstAI = (t) -> () -> MockedAI.anyConf(ImmutableMap.of("id", "first"));
        AIInput secondAI = (t) -> () -> MockedAI.anyConf(ImmutableMap.of("id", "second"));
        List<AIInput> ais = Arrays.asList(firstAI, secondAI);

        List<GEBuild> gameEngines = Arrays.asList(
                () -> MockedGE.anyWithWinner(Winner.PLAYER),
                () -> MockedGE.anyWithWinner(Winner.OPPONENT));

        Contest contest = new Contest(
                ais,
                gameEngines,
                gameExecutorService,
                matchExecutorService);

        ContestResult contestResult = contest.call();

        List<Classification> classifications = contestResult.getClassifications();

        assertThat(classifications).hasSize(2);

        Classification first = classifications.get(0);
        Classification second = classifications.get(1);

        assertThat(first.getAi().getConf()).containsEntry("id", "first");
        assertThat(first.getVictoryCount()).isEqualTo(1);

        assertThat(second.getAi().getConf()).containsEntry("id", "second");
        assertThat(second.getVictoryCount()).isEqualTo(1);
    }

}