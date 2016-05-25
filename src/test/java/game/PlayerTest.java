package game;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import game.Player.ITask;
import game.Player.Task;
import game.Player.TimedTask;
import game.Player.Timer;

@RunWith(HierarchicalContextRunner.class)
public class PlayerTest implements WithAssertions {

    public class TimedTaskTest {

        @Test
        public void tmp() {
            Task task = new Task(
                    new ITask() {
                        String value;

                        @Override
                        public void run() {
                            try {
                                value = "35";
                                Thread.sleep(100);
                                value = "43";
                            } catch (InterruptedException e) {
                            }
                        }

                        @Override
                        public String output() {
                            return value;
                        }
                    });

            TimedTask timedTask = new TimedTask(90L);

            long start = System.currentTimeMillis();
            String exec = timedTask.exec(task);
            long end = System.currentTimeMillis();

            System.out.println(end - start);

            assertThat(exec).isEqualTo("35");
        }

        @Test
        public void tmp_2() {
            Task task = new Task(
                    new ITask() {
                        String value;

                        @Override
                        public void run() {
                            try {
                                value = "35";
                                Thread.sleep(100);
                                value = "43";
                            } catch (InterruptedException e) {
                            }
                        }

                        @Override
                        public String output() {
                            return value;
                        }
                    });

            TimedTask timedTask = new TimedTask(110L);

            long start = System.currentTimeMillis();
            String exec = timedTask.exec(task);
            long end = System.currentTimeMillis();

            System.out.println(end - start);

            assertThat(exec).isEqualTo("43");
        }
    }

    @Test
    public void tmp_3() throws InterruptedException {
        Timer start = Timer.start(500);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(50L);
            start.lap();
            start.print();
        }
    }
}
