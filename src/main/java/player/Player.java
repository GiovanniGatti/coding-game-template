package player;

import java.util.Collections;
import java.util.Map;
import java.util.function.IntSupplier;

public final class Player {

    public static void main(String args[]) {
        // TODO: implement me!
        AI ai = null;

    }

    public static abstract class AI {

        private final Map<String, Object> conf;
        private final IntSupplier inputSupplier;

        /**
         * Builds an AI with specified configuration.<br>
         * If the AI does not need a configuration, an empty one may be provided.<br>
         * It is also recommended to create a default configuration.
         */
        public AI(Map<String, Object> conf, IntSupplier inputSupplier) {
            this.conf = Collections.unmodifiableMap(conf);
            this.inputSupplier = inputSupplier;
        }

        /**
         * Implements the IA algorithm
         * 
         * @return the best action found
         */
        public abstract Action[] play();

        public Map<String, Object> getConf() {
            return conf;
        }

        protected int readInput() {
            return inputSupplier.getAsInt();
        }
    }

    /**
     * Represents an action that can be taken
     */
    public static class Action {

        Action() {
            // TODO: implement what action is
        }

        String asString() {
            return "";
        }
    }
}
