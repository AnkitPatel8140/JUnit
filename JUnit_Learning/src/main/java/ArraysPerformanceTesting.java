import java.util.Arrays;

public class ArraysPerformanceTesting {

    public void arraysPerformanceTest(int[] arr)
    {
        for(int i = 0; i < 10000; i++) {
            Arrays.sort(arr);
        }
    }
}
