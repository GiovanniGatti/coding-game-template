package game;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import game.Player.Timer;

public class TimerBenchmark {

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
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void multiple_laps_with_short_sleep() throws InterruptedException {
        Timer timer = Timer.start(100);
        while (!timer.finished()) {
            TimeUnit.MICROSECONDS.sleep(100);
            timer.lap();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void multiple_laps_with_log_sleep() throws InterruptedException {
        Timer timer = Timer.start(100);
        while (!timer.finished()) {
            TimeUnit.MILLISECONDS.sleep(1);
            timer.lap();
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
