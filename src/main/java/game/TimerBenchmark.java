package game;

import game.Player.Timer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class TimerBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void single_lap__base() throws InterruptedException {
        Timer timer = Timer.start(100);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void single_lap() throws InterruptedException {
        Timer timer = Timer.start(100);
        timer.lap();
        timer.finished();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_shortest_sleep() throws InterruptedException {
        Timer timer = Timer.start(100);
        for (int i = 0; i < 10_000; i++) {
            busyWaitMicros(10); // 10 us
            timer.lap();
            timer.finished();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_shortest_sleep__base() throws InterruptedException {
        for (int i = 0; i < 10_000; i++) {
            busyWaitMicros(10); // 10 us
        }
    }


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_short_sleep() throws InterruptedException {
        Timer timer = Timer.start(100);
        for (int i = 0; i < 1_000; i++) {
            busyWaitMicros(100); // 100 us
            timer.lap();
            timer.finished();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_short_sleep__base() throws InterruptedException {
        for (int i = 0; i < 1_000; i++) {
            busyWaitMicros(100); // 100 us
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_long_sleep() throws InterruptedException {
        Timer timer = Timer.start(100);
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1); // 1ms
            timer.lap();
            timer.finished();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_long_sleep__base() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            Thread.sleep(1); // 1ms
        }
    }

    public static void busyWaitMicros(long micros) {
        long waitUntil = System.nanoTime() + (micros * 1_000);
        while (waitUntil > System.nanoTime()) {
            ;
        }
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TimerBenchmark.class.getSimpleName())
                .warmupIterations(5)
                .measurementIterations(20)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
