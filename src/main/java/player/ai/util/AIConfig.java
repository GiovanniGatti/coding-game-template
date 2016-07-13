package player.ai.util;

import java.util.Map;

public interface AIConfig {
    AIInput withConf(Map<String, Object> conf);
}
