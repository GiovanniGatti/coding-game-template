package player.match;

import java.util.Map;

/**
 * Created by tmi on 11/07/16.
 */
public interface ConfigurableAI {
    AIBuilder withConf(Map<String, Object> conf);
}
