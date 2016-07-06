package player.util;

// Change it to sample standard deviation instead of population standard deviation
/*
 * See more https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Online_algorithm
 * http://www.alcula.com/calculators/statistics/variance/
 * https://en.wikipedia.org/wiki/68%E2%80%9395%E2%80%9399.7_rule
 */
public final class Timer {

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

    public static Timer start(long expectedMillis) {
        return new Timer(expectedMillis);
    }

    public static Timer start(long expectedMillis, double security) {
        return new Timer(expectedMillis, security);
    }

    public boolean finished() {
        if (strict) {
            double deviation = Math.sqrt(variance);
            return System.nanoTime() + (mean + security * deviation) > endTime;
        }
        return System.nanoTime() + mean > endTime;
    }

    public void lap() {
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

    public void print() {
        System.out.println("lap=" + laps + ", elapsed=" + elapsed + "," + "mean=" + (long) mean + ", sigma^2="
                + (long) variance + ", sigma=" + Math.sqrt(variance));
    }
}