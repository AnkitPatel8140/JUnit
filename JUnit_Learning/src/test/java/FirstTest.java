import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirstTest {

    @BeforeAll
    static void beforeAll() {
        System.out.println("beforeAll");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("afterAll");
    }

    First f;
    @BeforeEach
    void init() {
        f = new First();
        System.out.println("initializing...");
    }


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

    @AfterEach
    void destroy() {
        System.out.println("destroying...");
    }


}