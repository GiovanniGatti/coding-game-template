package player.ai.builder;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static player.Player.AI;
import static player.Player.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class AIBuilderTest implements WithAssertions {

    @Test
    @DisplayName("Build no-op non-configurable AI with basic conf")
    public void build_non_configurable_ai() {
        AI ai = AIBuilder.newBuilder()
                .withCtor((Function<IntSupplier, AI>) NoOpAI::new)
                .withInputSupplier(() -> 0)
                .build();

        assertThat(ai).isExactlyInstanceOf(NoOpAI.class);
        assertThat(ai.getConf()).isEmpty();
    }

    @Test
    @DisplayName("Build no-op configurable AI with basic conf")
    public void build_configurable_ai() {
        Map<String, Object> conf = new HashMap<>();
        conf.put("key", "value");

        AI ai = AIBuilder.newBuilder()
                .withCtor((BiFunction<Map<String, Object>, IntSupplier, AI>) NoOpAI::new)
                .withConf(conf)
                .withInputSupplier(() -> 0)
                .build();

        assertThat(ai).isExactlyInstanceOf(NoOpAI.class);
        assertThat(ai.getConf()).containsEntry("key", "value");
    }

    @Test
    @DisplayName("Configuration must be immutable")
    public void configuration_must_be_immutable() {
        Map<String, Object> conf = new HashMap<>();
        conf.put("key", "value");

        AI ai = AIBuilder.newBuilder()
                .withCtor((BiFunction<Map<String, Object>, IntSupplier, AI>) NoOpAI::new)
                .withConf(conf)
                .withInputSupplier(() -> 0)
                .build();

        // Mutate configuration
        conf.put("key", "not_a_value");

        assertThat(ai.getConf()).containsKey("key");
        assertThat(ai.getConf().get("key")).isEqualTo("value");
    }

    @Test
    @DisplayName("Configuration deep copy is not performed")
    public void configuration_deep_copy_is_not_performed() {
        MutableConfParam value = new MutableConfParam("value");

        Map<String, Object> conf = new HashMap<>();
        conf.put("key", value);

        AI ai = AIBuilder.newBuilder()
                .withCtor((BiFunction<Map<String, Object>, IntSupplier, AI>) NoOpAI::new)
                .withConf(conf)
                .withInputSupplier(() -> 0)
                .build();

        // Mutate configuration
        value.setValue("not_a_value");

        assertThat(ai.getConf()).containsKey("key");
        MutableConfParam confValue = (MutableConfParam) ai.getConf().get("key");
        assertThat(confValue.getValue()).isEqualTo("not_a_value");
    }

    @Test
    @DisplayName("Input supplier should be properly assigned")
    public void input_supplier() {
        IntSupplier inputSupplier = Mockito.mock(IntSupplier.class);

        AI ai = AIBuilder.newBuilder()
                .withCtor((Function<IntSupplier, AI>) NoOpAI::new)
                .withInputSupplier(inputSupplier)
                .build();

        assertThat(ai).isExactlyInstanceOf(NoOpAI.class);
        NoOpAI noOpAI = (NoOpAI) ai;
        noOpAI.read();

        verify(inputSupplier, times(1)).getAsInt();
    }

    private static class MutableConfParam {
        private String value;

        MutableConfParam(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private static class NoOpAI extends AI {

        public NoOpAI(IntSupplier inputSupplier) {
            super(Collections.emptyMap(), inputSupplier);

        }

        public NoOpAI(Map<String, Object> conf, IntSupplier inputSupplier) {
            super(conf, inputSupplier);
        }

        @Override
        public Action[] play() {
            return new Action[0];
        }

        public int read() {
            return readInput();
        }
    }
}