package player.ai.builder;

import java.util.Map;

public interface AIConfig {
    AIInput withConf(Map<String, Object> conf);
}
