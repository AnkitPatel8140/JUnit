import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FirstTest {

    @Test
    void add() {
        First f = new First();
        assertEquals( f.add(2, 3), 5);
    }

    @Test
    void add2() {
        System.out.println("Test file ran");
    }


}