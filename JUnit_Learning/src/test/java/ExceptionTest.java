import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionTest {
    ExceptionTestingExample ex = new ExceptionTestingExample();

    @Test
    public void exceptionTest() {
        int[] testArray = null;
        assertThrows(Exception.class, () -> ex.exceptionTestingExample(testArray));
    }
}
