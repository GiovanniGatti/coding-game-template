package game;

final class Player {

    public static void main(String args[]) {
        // TODO: implement me!
    }

    /**
     * Class used for execution timing
     */
    static class TimedTask {

        private final long endTime;

        TimedTask(long millis) {
            this.endTime = System.currentTimeMillis() + millis;
        }

        String exec(Task task) {

            task.start();
            try {
                Thread.sleep(endTime - System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new IllegalStateException("Timing thread should not be interrupted", e);
            }

            System.out.println("Alive? " + task.isAlive());
            task.interrupt();
            System.out.println("Alive? " + task.isAlive());
            String output = task.output();
            System.out.println("Alive? " + task.isAlive());
            try {
                System.out.println("Alive? " + task.isAlive());
                task.join();
                System.out.println("Alive? " + task.isAlive());
            } catch (InterruptedException e) {
                throw new IllegalStateException("Thread should've been interrupted before", e);
            }
            System.out.println("Alive? " + task.isAlive());

            return output;
        }
    }

    static class Task extends Thread {

        private ITask runnable;

        Task(ITask runnable) {
            super(runnable);
            this.runnable = runnable;
        }

        String output() {
            return runnable.output();
        }
    }

    interface ITask extends Runnable {
        String output();
    }

    static class Timer {

        private final long endTime;
        private final long startTime;

        private int laps;
        private long elapsed;
        private long previous;
        private long mean;
        private long M2;
        private long standardDeviation;

        private Timer(long expectedMillis) {
            startTime = System.nanoTime();
            endTime = startTime + expectedMillis * 1000L;
            laps = 0;
            previous = startTime;
            mean = 0L;
            M2 = 0L;
            standardDeviation = 0L;
        }

        static Timer start(long expectedMillis) {
            return new Timer(expectedMillis);
        }

        boolean finished() {
            return endTime >= System.nanoTime();
        }

        void lap() {
            long current = System.nanoTime();
            elapsed = current - previous;
            previous = current;
            laps++;
            long delta = elapsed - mean;
            mean += delta / laps;
            M2 += delta * (elapsed - mean);
            standardDeviation = M2 / laps;
        }

        void print() {
            System.out.println("lap=" + laps + ", elapsed=" + elapsed + ", mean=" + mean + ", sigma^2="
                    + standardDeviation);
        }
    }
}
