import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FirstTest {

    First f = new First();

    @Test
    void add() {
        assertEquals( f.add(2, 3), 5);
    }

    @Test
    void add2() {
        assertEquals( f.add(2, 3), 6, "Test case failed for part 2");
        System.out.println("Test file ran");
    }

    @Test
    void add_Supplier() {
//        the difference between the supplier/lambda and normal string is that the supplier will
//        only be evaluated if test case failed and in normal string it will be evaluated regardless of
//        the result
        assertEquals( f.add(2, 3), 6, () -> "Test case failed for supplier");
    }


}