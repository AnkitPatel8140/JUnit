import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ArraysPerformanceTest {
    @Test
    public void testPerformance() {
        ArraysPerformanceTesting test = new ArraysPerformanceTesting();
        assertTimeout(Duration.ofMillis(1), ()->{test.arraysPerformanceTest(new int[] {1, 2, 3});});
    }
}
