package game;

import java.util.Collections;
import java.util.Map;

final class Player {

    public static void main(String args[]) {
        // TODO: implement me!
    }

    /**
     * TODO: what if the player IA is not stateless?
     */
    static abstract class AI {

        private final Map<String, Object> conf;

        /**
         * Builds an AI with specified configuration.<br>
         * If the AI does not need a configuration, an empty one may be provided.<br>
         * It is also recommended to create a default configuration.
         */
        AI(Map<String, Object> conf) {
            this.conf = Collections.unmodifiableMap(conf);
        }

        /**
         * Implements the IA algorithm
         * 
         * @param current the current state
         * @return the best action found
         */
        abstract Action play(State current);

        Map<String, Object> getConf() {
            return conf;
        }
    }

    /**
     * Represents the game state
     */
    static final class State implements Cloneable {

        private int playerScore;
        private int opponentScore;

        State() {
            playerScore = 0;
            opponentScore = 0;
            // TODO: implement what a game state is (all game input variables)
        }

        /**
         * Performs an action (which will mutate the game state)
         * 
         * @param action to perform
         */
        void perform(Action action) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        protected State clone() {
            throw new UnsupportedOperationException("Not implemented");
        }

        int getPlayerScore() {
            return playerScore;
        }

        int getOpponentScore() {
            return opponentScore;
        }
    }

    /**
     * Represents an action that can be taken
     */
    static final class Action {

        Action() {
            // TODO: implement what action is
        }

        String asString() {
            return "";
        }
    }

    // Change it to sample standard deviation instead of population standard deviation
    /*
     * See more https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
     * http://www.alcula.com/calculators/statistics/variance/
     * https://en.wikipedia.org/wiki/68%E2%80%9395%E2%80%9399.7_rule
     */
    static final class Timer {

        private final long endTime;
        private final long startTime;
        private final boolean strict;

        private int laps;
        private long elapsed;
        private long previous;
        private double mean;
        private double M2;
        private double variance;

        private double security;

        private Timer(long expectedMillis) {
            startTime = System.nanoTime();
            endTime = startTime + expectedMillis * 1_000_000L;
            laps = 0;
            previous = startTime;
            mean = 0L;
            M2 = 0L;
            variance = 0L;
            this.strict = false;
        }

        private Timer(long expectedMillis, double security) {
            startTime = System.nanoTime();
            endTime = startTime + expectedMillis * 1_000_000L;
            laps = 0;
            previous = startTime;
            mean = 0L;
            M2 = 0L;
            variance = 0L;
            this.strict = true;
            this.security = security;
        }

        static Timer start(long expectedMillis) {
            return new Timer(expectedMillis);
        }

        static Timer start(long expectedMillis, double security) {
            return new Timer(expectedMillis, security);
        }

        boolean finished() {
            if (strict) {
                double deviation = Math.sqrt(variance);
                return System.nanoTime() + (mean + security * deviation) > endTime;
            }
            return System.nanoTime() + mean > endTime;
        }

        void lap() {
            long current = System.nanoTime();
            elapsed = current - previous;
            previous = current;
            laps++;
            double delta = elapsed - mean;
            mean += delta / laps;
            if (strict) {
                M2 += delta * (elapsed - mean);
                if (laps > 1) {
                    variance = M2 / (laps - 1);
                }
            }
        }

        void print() {
            System.out.println("lap=" + laps + ", elapsed=" + elapsed + "," + "mean=" + (long) mean + ", sigma^2="
                    + (long) variance + ", sigma=" + Math.sqrt(variance));
        }
    }
}
