import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArraysTesting {
    @Test
    void testArray() {
        int[] expected = {1,2,3,4};
        int[] actual = {1,2,4,3};

        assertArrayEquals(expected, actual);
    }
}
