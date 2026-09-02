# JUnit 5 --- Complete Notes

## Beginner → Advanced → Real Projects → Interview Preparation

> **Goal:** This guide is designed to take you from writing your first
> JUnit 5 test to understanding the architecture, lifecycle, extensions,
> mocking boundaries, parameterized testing, parallel execution,
> Maven/Gradle integration, Spring Boot testing patterns, and
> interview-level internals.

------------------------------------------------------------------------

# 1. What is JUnit?

**JUnit** is a Java testing framework used primarily to write and
execute automated unit and integration tests.

Testing helps answer:

-   Does this method behave correctly?
-   What happens for invalid input?
-   Did a code change break existing behavior?
-   Can the application components work together?
-   Can the same test run repeatedly and reliably?

JUnit itself is **not a mocking framework**. Libraries such as Mockito
are commonly used alongside it.

### JUnit family

  -----------------------------------------------------------------------
  Version                             Important characteristic
  ----------------------------------- -----------------------------------
  JUnit 3                             Naming conventions such as
                                      `testSomething()`

  JUnit 4                             Annotations such as `@Test`,
                                      `@Before`, `@RunWith`

  JUnit 5                             Modern architecture, Jupiter API,
                                      extensions, parameterized tests
  -----------------------------------------------------------------------

JUnit 5 is actually a platform made of multiple components.

------------------------------------------------------------------------

# 2. JUnit 5 Architecture

One of the most important interview concepts is:

> **JUnit 5 is not one monolithic library.**

It consists primarily of:

## JUnit Platform

The **JUnit Platform** is the foundation used to discover and execute
tests.

It provides:

-   Test discovery
-   Test execution
-   Engine API
-   Launcher API
-   Integration with IDEs and build tools

Think:

``` text
IDE / Maven / Gradle / CI
          |
          v
   JUnit Platform
          |
     Test Engines
          |
    JUnit Jupiter
```

## JUnit Jupiter

Jupiter is the programming and extension model introduced with JUnit 5.

It provides:

-   `@Test`
-   `@BeforeEach`
-   `@AfterEach`
-   `@BeforeAll`
-   `@AfterAll`
-   Assertions
-   Assumptions
-   Parameterized tests
-   Dynamic tests
-   Extensions

## JUnit Vintage

Vintage allows older JUnit 3 and JUnit 4 tests to run on the JUnit
Platform.

``` text
JUnit 5
├── JUnit Platform
├── JUnit Jupiter
└── JUnit Vintage
```

### Interview question

**Q: Is JUnit Jupiter the same thing as JUnit 5?**

No.

JUnit 5 is the overall ecosystem/platform. Jupiter is the JUnit 5
programming and extension model.

------------------------------------------------------------------------

# 3. Maven Setup

Typical Maven dependencies:

``` xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <junit.version>5.13.4</junit.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Use the JUnit version appropriate for your project's Java/build-tool
compatibility.

Maven's Surefire plugin normally executes tests during:

``` bash
mvn test
```

For integration-test conventions, projects may additionally use the
Failsafe plugin.

------------------------------------------------------------------------

# 4. Standard Project Structure

A typical Maven project:

``` text
project/
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/example/
    │           └── Calculator.java
    │
    └── test/
        └── java/
            └── com/example/
                └── CalculatorTest.java
```

Important rule:

``` text
Production code -> src/main/java
Test code       -> src/test/java
```

Tests should normally mirror the production package structure.

Example:

``` text
src/main/java/com/app/service/UserService.java
src/test/java/com/app/service/UserServiceTest.java
```

------------------------------------------------------------------------

# 5. Your First JUnit 5 Test

Production:

``` java
public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }
}
```

Test:

``` java
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void add_shouldReturnSum() {
        Calculator calculator = new Calculator();

        int result = calculator.add(2, 3);

        assertEquals(5, result);
    }
}
```

The fundamental structure is:

``` text
Arrange
Act
Assert
```

``` java
// Arrange
Calculator calculator = new Calculator();

// Act
int result = calculator.add(2, 3);

// Assert
assertEquals(5, result);
```

------------------------------------------------------------------------

# 6. `@Test`

`@Test` identifies a method as a test method.

``` java
@Test
void shouldAddNumbers() {
    assertEquals(10, calculator.add(4, 6));
}
```

A JUnit Jupiter test method generally:

-   Has no return value
-   Can have supported parameter injection
-   Is discovered by the Jupiter engine

------------------------------------------------------------------------

# 7. Test Naming

Prefer names that describe behavior.

Bad:

``` java
@Test
void test1() { }
```

Better:

``` java
@Test
void shouldReturnZeroWhenCartIsEmpty() { }
```

Another useful style:

``` java
@Test
void calculateDiscount_whenAmountAbove1000_shouldApply10PercentDiscount() {
}
```

Good test names are extremely valuable during CI failures.

------------------------------------------------------------------------

# 8. Assertions

Assertions verify expected behavior.

Import:

``` java
import static org.junit.jupiter.api.Assertions.*;
```

## assertEquals

``` java
assertEquals(10, calculator.add(4, 6));
```

Expected value comes first.

``` java
assertEquals(expected, actual);
```

You can also provide a message:

``` java
assertEquals(
    10,
    calculator.add(4, 6),
    "Calculator should return the sum"
);
```

Prefer lazy messages for expensive message construction:

``` java
assertEquals(
    10,
    actual,
    () -> "Unexpected value: " + actual
);
```

------------------------------------------------------------------------

# 9. Common Assertions

## assertNotEquals

``` java
assertNotEquals(10, result);
```

## assertTrue

``` java
assertTrue(user.isActive());
```

## assertFalse

``` java
assertFalse(user.isDeleted());
```

## assertNull

``` java
assertNull(result);
```

## assertNotNull

``` java
assertNotNull(user);
```

## assertSame

Checks object identity:

``` java
assertSame(expectedObject, actualObject);
```

Meaning:

``` java
expectedObject == actualObject
```

## assertNotSame

``` java
assertNotSame(first, second);
```

------------------------------------------------------------------------

# 10. `assertThrows`

Very important for real projects and interviews.

Suppose:

``` java
public void withdraw(int amount) {
    if (amount > balance) {
        throw new InsufficientFundsException();
    }
}
```

Test:

``` java
@Test
void withdraw_whenAmountExceedsBalance_shouldThrowException() {

    InsufficientFundsException exception =
        assertThrows(
            InsufficientFundsException.class,
            () -> account.withdraw(1000)
        );
}
```

You can inspect the exception:

``` java
assertEquals(
    "Insufficient balance",
    exception.getMessage()
);
```

### Why not use try/catch?

This is much cleaner:

``` java
assertThrows(
    IllegalArgumentException.class,
    () -> service.process(null)
);
```

------------------------------------------------------------------------

# 11. `assertDoesNotThrow`

``` java
assertDoesNotThrow(
    () -> service.process(validRequest)
);
```

Use it when successful execution without an exception is itself
important behavior.

------------------------------------------------------------------------

# 12. `assertAll`

Useful when several properties of an object should be validated
together.

``` java
assertAll(
    () -> assertEquals("Ankit", user.getName()),
    () -> assertEquals(25, user.getAge()),
    () -> assertTrue(user.isActive())
);
```

Without `assertAll`, the first failed assertion normally prevents later
assertions in that test method from being evaluated.

With `assertAll`, JUnit executes the supplied assertions and reports the
failures together.

------------------------------------------------------------------------

# 13. Equality vs Identity

A common interview question.

``` java
assertEquals(a, b);
```

checks logical equality, normally through `equals()`.

``` java
assertSame(a, b);
```

checks whether both references point to the exact same object.

Example:

``` java
String a = new String("hello");
String b = new String("hello");

assertEquals(a, b);  // passes
assertNotSame(a, b); // passes
```

------------------------------------------------------------------------

# 14. Lifecycle Annotations

JUnit provides lifecycle hooks.

``` text
@BeforeAll
    |
@BeforeEach
    |
@Test
    |
@AfterEach
    |
@AfterAll
```

## `@BeforeEach`

Runs before every test.

``` java
@BeforeEach
void setUp() {
    calculator = new Calculator();
}
```

If there are 5 tests, it runs 5 times.

## `@AfterEach`

Runs after every test.

``` java
@AfterEach
void tearDown() {
    calculator = null;
}
```

Useful for cleanup.

## `@BeforeAll`

Runs once before all tests in the class.

``` java
@BeforeAll
static void setupDatabase() {
}
```

By default, `@BeforeAll` and `@AfterAll` methods are static.

## `@AfterAll`

Runs once after all tests.

``` java
@AfterAll
static void shutdownDatabase() {
}
```

------------------------------------------------------------------------

# 15. Why are `@BeforeAll` and `@AfterAll` static?

JUnit normally creates a **new test class instance for each test
method**.

Therefore, instance lifecycle methods cannot naturally be shared across
test instances.

Static methods belong to the class rather than an individual test
instance.

However, JUnit supports:

``` java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
```

Then lifecycle methods can be instance methods:

``` java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTest {

    @BeforeAll
    void setup() {
    }
}
```

------------------------------------------------------------------------

# 16. Test Instance Lifecycle

JUnit Jupiter supports:

``` java
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
```

and:

``` java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
```

## PER_METHOD

Default.

A new test class instance is created for each test method.

Conceptually:

``` text
Test 1 -> new TestClass()
Test 2 -> new TestClass()
Test 3 -> new TestClass()
```

This provides strong isolation.

## PER_CLASS

One test class instance is used for all test methods.

``` text
             TestClass
             /   |   \
           Test Test Test
```

Useful when:

-   Setup is expensive
-   Non-static `@BeforeAll` is desirable
-   Shared state is intentionally required

But shared mutable state can create test-order and isolation problems.

------------------------------------------------------------------------

# 17. Test Order

By default, you should **not depend on test execution order**.

If ordering is genuinely required:

``` java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentTest {

    @Test
    @Order(1)
    void createPayment() {}

    @Test
    @Order(2)
    void confirmPayment() {}
}
```

Available orderers include mechanisms based on:

-   Method name
-   Display name
-   `@Order`
-   Random order

### Best practice

Avoid making tests dependent on each other.

A test suite should ideally be:

-   Independent
-   Repeatable
-   Isolated

------------------------------------------------------------------------

# 18. `@DisplayName`

Makes test reports more readable.

``` java
@Test
@DisplayName("Should reject negative account balance")
void negativeBalance() {
}
```

Can also be applied to classes.

------------------------------------------------------------------------

# 19. `@DisplayNameGeneration`

JUnit can automatically generate display names.

``` java
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CalculatorTest {

    @Test
    void should_return_sum_for_two_numbers() {
    }
}
```

The underscores become spaces.

------------------------------------------------------------------------

# 20. Disabled Tests

Temporarily disable:

``` java
@Disabled
@Test
void temporarilyDisabledTest() {
}
```

Or:

``` java
@Disabled("Waiting for payment service fix")
```

Avoid leaving disabled tests permanently. A permanently disabled test
often represents technical debt.

------------------------------------------------------------------------

# 21. Assumptions

Assertions mean:

> This behavior must be true.

Assumptions mean:

> Run this test only if a condition is true.

Example:

``` java
assumeTrue(System.getenv("CI") != null);
```

If the assumption fails, the test is aborted rather than reported as an
ordinary assertion failure.

Useful for:

-   Environment-specific tests
-   Optional external services
-   Operating-system-specific behavior

------------------------------------------------------------------------

# 22. Common Assumption APIs

``` java
assumeTrue(condition);
assumeFalse(condition);
assumingThat(condition, executable);
```

Example:

``` java
assumingThat(
    System.getProperty("os.name").contains("Linux"),
    () -> assertTrue(service.supportsLinux())
);
```

------------------------------------------------------------------------

# 23. Nested Tests

`@Nested` organizes related tests.

``` java
class OrderServiceTest {

    @Nested
    class WhenOrderIsValid {

        @Test
        void shouldCreateOrder() {
        }
    }

    @Nested
    class WhenOrderIsInvalid {

        @Test
        void shouldRejectOrder() {
        }
    }
}
```

This is excellent for behavior-driven organization.

------------------------------------------------------------------------

# 24. Tags

Tags allow tests to be categorized.

``` java
@Tag("integration")
@Test
void shouldCallDatabase() {
}
```

Another:

``` java
@Tag("unit")
@Test
void shouldCalculateDiscount() {
}
```

You can then configure Maven/IDE/CI to run subsets.

Typical tags:

``` text
unit
integration
e2e
slow
smoke
regression
```

------------------------------------------------------------------------

# 25. Parameterized Tests

One of the most useful JUnit 5 features.

Instead of:

``` java
@Test
void shouldRejectZero() {}

@Test
void shouldRejectNegativeOne() {}

@Test
void shouldRejectNegativeTen() {}
```

Use:

``` java
@ParameterizedTest
@ValueSource(ints = {0, -1, -10})
void shouldRejectNonPositiveNumbers(int value) {
    assertThrows(
        IllegalArgumentException.class,
        () -> calculator.validate(value)
    );
}
```

The test runs once for every supplied value.

------------------------------------------------------------------------

# 26. `@ValueSource`

Supports primitive/simple values.

``` java
@ValueSource(strings = {"", " ", "abc"})
```

``` java
@ValueSource(ints = {1, 2, 3})
```

Other supported source types include primitive arrays and strings.

------------------------------------------------------------------------

# 27. `@NullSource`

``` java
@ParameterizedTest
@NullSource
void shouldRejectNull(String value) {
    assertThrows(
        NullPointerException.class,
        () -> service.process(value)
    );
}
```

------------------------------------------------------------------------

# 28. `@EmptySource`

``` java
@ParameterizedTest
@EmptySource
void shouldRejectEmptyList(List<String> values) {
}
```

------------------------------------------------------------------------

# 29. `@NullAndEmptySource`

Convenient combination:

``` java
@ParameterizedTest
@NullAndEmptySource
void shouldRejectMissingInput(String input) {
}
```

------------------------------------------------------------------------

# 30. `@EnumSource`

``` java
@ParameterizedTest
@EnumSource(Status.class)
void shouldHandleEveryStatus(Status status) {
}
```

Can select subsets of enum constants.

------------------------------------------------------------------------

# 31. `@CsvSource`

Useful for multiple parameters.

``` java
@ParameterizedTest
@CsvSource({
    "2, 3, 5",
    "10, 20, 30",
    "0, 5, 5"
})
void shouldAddNumbers(int a, int b, int expected) {

    assertEquals(
        expected,
        calculator.add(a, b)
    );
}
```

This is extremely common in real projects.

------------------------------------------------------------------------

# 32. `@CsvFileSource`

For larger datasets:

``` java
@ParameterizedTest
@CsvFileSource(resources = "/calculator.csv", numLinesToSkip = 1)
void shouldCalculate(
    int a,
    int b,
    int expected
) {
}
```

Example CSV:

``` text
a,b,expected
2,3,5
10,20,30
```

------------------------------------------------------------------------

# 33. `@MethodSource`

For complex test data.

``` java
@ParameterizedTest
@MethodSource("provideUsers")
void shouldValidateUser(User user, boolean expected) {

    assertEquals(
        expected,
        validator.isValid(user)
    );
}

static Stream<Arguments> provideUsers() {
    return Stream.of(
        Arguments.of(new User("Ankit"), true),
        Arguments.of(new User(""), false)
    );
}
```

Method source is often preferable when objects or complex scenarios are
involved.

------------------------------------------------------------------------

# 34. `@ArgumentsSource`

For custom argument providers.

``` java
@ParameterizedTest
@ArgumentsSource(UserArgumentsProvider.class)
void shouldValidateUser(User user) {
}
```

Provider:

``` java
class UserArgumentsProvider
        implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(
            ExtensionContext context) {

        return Stream.of(
            Arguments.of(new User("Ankit")),
            Arguments.of(new User("John"))
        );
    }
}
```

This becomes useful when test data generation needs reusable custom
logic.

------------------------------------------------------------------------

# 35. Parameterized Test Lifecycle

Each invocation of a parameterized test is treated as a separate test
execution.

Conceptually:

``` text
Parameterized Test
       |
       +--> invocation 1
       |
       +--> invocation 2
       |
       +--> invocation 3
```

Lifecycle methods such as `@BeforeEach` run for each invocation.

------------------------------------------------------------------------

# 36. Dynamic Tests

Dynamic tests are generated at runtime.

Use:

``` java
@TestFactory
Stream<DynamicTest> tests() {
    return Stream.of(
        DynamicTest.dynamicTest(
            "2 + 3 = 5",
            () -> assertEquals(5, 2 + 3)
        ),
        DynamicTest.dynamicTest(
            "10 + 20 = 30",
            () -> assertEquals(30, 10 + 20)
        )
    );
}
```

Important distinction:

``` text
@Test
       -> statically declared test

@TestFactory
       -> tests generated at runtime
```

Dynamic tests are useful when test cases come from:

-   Files
-   Generated datasets
-   Runtime configuration
-   External metadata

------------------------------------------------------------------------

# 37. Dynamic Test Containers

You can group dynamic tests:

``` java
@TestFactory
Stream<DynamicNode> tests() {

    return Stream.of(
        DynamicContainer.dynamicContainer(
            "Addition",
            Stream.of(
                DynamicTest.dynamicTest(
                    "2 + 3",
                    () -> assertEquals(5, 2 + 3)
                )
            )
        )
    );
}
```

------------------------------------------------------------------------

# 38. Repeated Tests

Run the same test multiple times:

``` java
@RepeatedTest(5)
void shouldProduceStableResult() {
    assertEquals(4, calculator.add(2, 2));
}
```

You can access repetition information:

``` java
@RepeatedTest(5)
void test(RepetitionInfo info) {
    System.out.println(
        info.getCurrentRepetition()
    );
}
```

Use this carefully. Repetition is not a substitute for deterministic
tests.

------------------------------------------------------------------------

# 39. Timeout Testing

JUnit supports timeout assertions.

``` java
assertTimeout(
    Duration.ofSeconds(2),
    () -> service.process()
);
```

If execution exceeds the limit, the assertion fails.

------------------------------------------------------------------------

# 40. Preemptive Timeout

JUnit also provides:

``` java
assertTimeoutPreemptively(
    Duration.ofSeconds(2),
    () -> service.process()
);
```

Important difference:

``` text
assertTimeout
    -> waits for executable to finish, then determines whether timeout was exceeded

assertTimeoutPreemptively
    -> may interrupt execution by running it separately
```

### Important Spring warning

Preemptive timeout can be problematic when framework state is bound to
the executing thread, such as transaction/thread-local context.

Use it deliberately.

------------------------------------------------------------------------

# 41. Test Interfaces

JUnit 5 allows test interfaces with default methods.

Example:

``` java
interface ComparableContract {

    @Test
    default void shouldBeReflexive() {
    }
}
```

A test class can implement the interface and inherit the test behavior.

Useful for reusable test contracts.

------------------------------------------------------------------------

# 42. Default Methods and Lifecycle

JUnit recognizes supported Jupiter annotations on suitable interface
default methods.

This enables reusable:

-   Test cases
-   Lifecycle behavior
-   Contract tests

------------------------------------------------------------------------

# 43. Extensions --- The Most Important Advanced Topic

JUnit 5 replaced much of JUnit 4's runner/rule model with the
**Extension API**.

Extensions allow JUnit behavior to be customized.

Examples:

-   Dependency injection
-   Resource management
-   Conditional execution
-   Parameter resolution
-   Test instance processing
-   Exception handling
-   Invocation interception

------------------------------------------------------------------------

# 44. `@ExtendWith`

Register an extension:

``` java
@ExtendWith(MyExtension.class)
class MyTest {
}
```

You can also register extensions programmatically.

------------------------------------------------------------------------

# 45. Extension Model

JUnit extensions implement one or more extension interfaces.

Examples include:

``` text
BeforeEachCallback
AfterEachCallback
BeforeAllCallback
AfterAllCallback
TestExecutionExceptionHandler
ParameterResolver
TestInstancePostProcessor
InvocationInterceptor
ExecutionCondition
```

You generally implement only the callback types your extension needs.

------------------------------------------------------------------------

# 46. Simple Extension

``` java
class LoggingExtension
        implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(
            ExtensionContext context) {

        System.out.println(
            "Starting: " +
            context.getDisplayName()
        );
    }

    @Override
    public void afterEach(
            ExtensionContext context) {

        System.out.println(
            "Finished: " +
            context.getDisplayName()
        );
    }
}
```

Register:

``` java
@ExtendWith(LoggingExtension.class)
class UserServiceTest {
}
```

------------------------------------------------------------------------

# 47. ParameterResolver

A powerful extension mechanism.

Suppose your test wants a custom object:

``` java
@Test
void shouldProcess(UserService service) {
}
```

JUnit does not automatically know how to create `UserService`.

A custom `ParameterResolver` can provide it.

``` java
class ServiceResolver
        implements ParameterResolver {

    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext) {

        return parameterContext
            .getParameter()
            .getType()
            .equals(UserService.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext) {

        return new UserService();
    }
}
```

------------------------------------------------------------------------

# 48. ExtensionContext

`ExtensionContext` gives an extension access to information about the
current execution.

It can provide information such as:

-   Test class
-   Test method
-   Display name
-   Tags
-   Parent context
-   Configuration
-   Store

The **Store** is particularly important for safely sharing
extension-managed state.

------------------------------------------------------------------------

# 49. ExtensionContext.Store

Extensions can store objects:

``` java
ExtensionContext.Store store =
    context.getStore(
        ExtensionContext.Namespace.create(
            MyExtension.class
        )
    );

store.put("resource", resource);
```

Retrieve:

``` java
Resource resource =
    store.get("resource", Resource.class);
```

This helps extension authors manage lifecycle-specific state.

------------------------------------------------------------------------

# 50. Declarative vs Programmatic Extensions

Declarative:

``` java
@ExtendWith(MyExtension.class)
```

Programmatic:

``` java
@RegisterExtension
MyExtension extension =
    new MyExtension();
```

`@RegisterExtension` is useful when the extension needs configuration
through object construction.

------------------------------------------------------------------------

# 51. Automatic Extension Registration

JUnit supports automatic extension discovery in appropriate
configurations.

This can be useful for reusable libraries but should be used carefully
because hidden/global behavior can make test configuration less obvious.

------------------------------------------------------------------------

# 52. Conditional Test Execution

JUnit supports conditions such as:

``` java
@EnabledOnOs(OS.LINUX)
```

``` java
@DisabledOnOs(OS.WINDOWS)
```

Java version:

``` java
@EnabledOnJre(JRE.JAVA_17)
```

System property:

``` java
@EnabledIfSystemProperty(
    named = "environment",
    matches = "staging"
)
```

Environment variable:

``` java
@EnabledIfEnvironmentVariable(
    named = "CI",
    matches = "true"
)
```

These are useful when behavior genuinely depends on runtime environment.

------------------------------------------------------------------------

# 53. Configuration Parameters

JUnit configuration can be supplied through configuration mechanisms
supported by the platform.

Conceptually:

``` text
Build tool / IDE
       |
configuration parameters
       |
JUnit Platform
       |
test execution
```

Extensions can read configuration parameters through `ExtensionContext`.

------------------------------------------------------------------------

# 54. Parallel Execution

JUnit 5 supports parallel execution.

It is generally configured through JUnit Platform configuration
properties.

Conceptually:

``` text
Sequential:

Test A -> Test B -> Test C

Parallel:

Test A ─┐
Test B ─┼─> executor
Test C ─┘
```

Parallel testing can significantly reduce suite time.

But tests must be designed for concurrency.

Problems include:

-   Shared mutable static state
-   Shared files
-   Same database records
-   Non-thread-safe mocks/resources
-   Test-order assumptions

------------------------------------------------------------------------

# 55. Parallel Execution Strategy

JUnit provides configurable execution modes such as:

``` text
SAME_THREAD
CONCURRENT
```

The exact configuration belongs in your build/test configuration.

Do not simply enable parallel execution and assume the suite is safe.

A good suite is intentionally designed for isolation.

------------------------------------------------------------------------

# 56. Test Discovery --- Internal View

When you run:

``` bash
mvn test
```

or execute tests from an IDE, the process is approximately:

``` text
Build tool / IDE
       |
       v
JUnit Platform
       |
       v
Test discovery
       |
       v
JUnit Jupiter Engine
       |
       v
Test descriptors
       |
       v
Execution
       |
       v
Results
```

The Jupiter engine identifies classes and methods that conform to the
Jupiter programming model.

The Platform coordinates engines and execution.

------------------------------------------------------------------------

# 57. Test Engines

JUnit Platform is engine-based.

The Platform can execute tests from different frameworks through
engines.

For example:

``` text
JUnit Platform
   |
   +---- Jupiter Engine
   |
   +---- Vintage Engine
   |
   +---- Other compatible engines
```

This is one reason the Platform is more flexible than a framework tied
directly to one programming model.

------------------------------------------------------------------------

# 58. Launcher API

The JUnit Platform exposes a Launcher API for programmatic test
discovery and execution.

Conceptually:

``` java
LauncherDiscoveryRequest request = ...;

Launcher launcher = ...;

TestPlan plan =
    launcher.discover(request);

launcher.execute(request);
```

You normally do not need to write this in application code, but it
explains how IDEs/build integrations can interact with JUnit.

------------------------------------------------------------------------

# 59. Test Descriptors

Internally, tests are represented as a hierarchical test structure.

Conceptually:

``` text
Engine
  |
  +-- Class
       |
       +-- Nested class
       |
       +-- Test method
```

The Platform uses test descriptors to represent nodes in the test plan.

This hierarchy is useful for:

-   Discovery
-   Reporting
-   Execution
-   Selection
-   Filtering

------------------------------------------------------------------------

# 60. Test Execution Phases

A simplified model:

``` text
Discover
   ↓
Build test plan
   ↓
Select/filter tests
   ↓
Execute lifecycle
   ↓
Collect events
   ↓
Report result
```

Real execution contains considerably more lifecycle and extension
machinery, but this mental model is excellent for interviews.

------------------------------------------------------------------------

# 61. Test Instance Creation

A key internal detail:

JUnit does not simply call your test method on one permanent object.

With the default lifecycle:

``` text
discover test class
      ↓
create test instance
      ↓
run before callbacks
      ↓
run @BeforeEach
      ↓
run test
      ↓
run @AfterEach
      ↓
cleanup
```

A separate test instance is normally created for each test method under
`PER_METHOD`.

------------------------------------------------------------------------

# 62. Approximate Lifecycle Order

A simplified Jupiter lifecycle:

``` text
@BeforeAll
    ↓
@BeforeEach
    ↓
@BeforeTestExecution
    ↓
@Test
    ↓
@AfterTestExecution
    ↓
@AfterEach
    ↓
@AfterAll
```

Extension callbacks can participate around these stages.

The exact extension callback ordering depends on which extension
interfaces are registered and their ordering semantics.

------------------------------------------------------------------------

# 63. Assertions vs Matchers

JUnit assertions are built in:

``` java
assertEquals(expected, actual);
```

Libraries such as AssertJ provide fluent assertions:

``` java
assertThat(user.getName())
    .isEqualTo("Ankit");
```

Hamcrest offers matcher-style assertions.

In real projects, JUnit + AssertJ + Mockito is a common combination.

------------------------------------------------------------------------

# 64. JUnit + Mockito

JUnit runs the test.

Mockito creates and controls test doubles.

Example:

``` java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @InjectMocks
    UserService service;

    @Test
    void shouldFindUser() {

        User user = new User("Ankit");

        when(repository.findById(1L))
            .thenReturn(Optional.of(user));

        User result = service.findUser(1L);

        assertEquals("Ankit", result.getName());

        verify(repository).findById(1L);
    }
}
```

Important:

``` text
JUnit
    -> executes test lifecycle

Mockito
    -> provides mocks/stubbing/verification
```

------------------------------------------------------------------------

# 65. Why `@ExtendWith(MockitoExtension.class)`?

The Mockito extension integrates Mockito's lifecycle with JUnit Jupiter.

It handles annotation-driven initialization such as:

``` java
@Mock
@InjectMocks
@Spy
@Captor
```

Without the proper initialization mechanism, annotations may not behave
as expected.

------------------------------------------------------------------------

# 66. Unit Test vs Integration Test

### Unit test

Usually tests one component in isolation.

``` text
Service
  |
  X Repository
```

Repository is mocked.

### Integration test

Tests multiple real components together.

``` text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

### End-to-end test

Tests the application through a realistic user/business flow.

``` text
Client
 ↓
HTTP
 ↓
Application
 ↓
Database
 ↓
External systems
```

------------------------------------------------------------------------

# 67. Unit Testing Best Practices

A good unit test should be:

### Fast

Avoid unnecessary:

-   Network
-   Database
-   File system
-   Sleep calls

### Deterministic

Same input should produce the same result.

### Isolated

One test should not depend on another.

### Readable

A developer should understand why it failed.

### Focused

One test should verify one meaningful behavior.

------------------------------------------------------------------------

# 68. What to Test

Focus on behavior, not implementation details.

For a service:

``` text
Valid input
Invalid input
Boundary values
Exceptions
Important business rules
Interactions with dependencies
```

Example:

``` text
calculateDiscount(100)
calculateDiscount(1000)
calculateDiscount(1001)
calculateDiscount(-1)
calculateDiscount(null)
```

Boundary testing is particularly important.

------------------------------------------------------------------------

# 69. What NOT to Over-Test

Avoid tests that simply duplicate implementation.

For example, testing every private method independently is usually a
smell.

Prefer testing public behavior:

``` text
public method
    ↓
observable behavior
```

rather than:

``` text
private method A
private method B
private method C
```

Private implementation can change without changing behavior.

------------------------------------------------------------------------

# 70. Test Doubles

Common categories:

``` text
Dummy
Stub
Mock
Spy
Fake
```

### Dummy

Passed only because a parameter is required.

### Stub

Provides predetermined responses.

### Mock

Used to verify interactions.

### Spy

Wraps/records behavior of a real object or partial test double.

### Fake

A working but simplified implementation.

Example:

``` text
Production DB
     vs
In-memory fake repository
```

------------------------------------------------------------------------

# 71. Stubbing vs Verification

Stubbing:

``` java
when(repository.findById(1L))
    .thenReturn(Optional.of(user));
```

Means:

> When this interaction happens, return this value.

Verification:

``` java
verify(repository).findById(1L);
```

Means:

> Verify that this interaction occurred.

These are conceptually different.

------------------------------------------------------------------------

# 72. Strictness and Unnecessary Stubbing

Modern mocking setups often encourage detecting unnecessary stubs.

Why?

Because this:

``` java
when(repository.findById(1L))
    .thenReturn(...);
```

is suspicious if the test never calls `findById`.

Unused setup makes tests harder to understand and can hide mistakes.

------------------------------------------------------------------------

# 73. Spring Boot Testing

In Spring Boot projects, you will commonly see:

``` java
@SpringBootTest
```

This generally creates a Spring application context for the test.

It is much heavier than a pure unit test.

Use it when you actually need Spring integration.

------------------------------------------------------------------------

# 74. Spring Test Slices

Instead of loading everything, use focused test slices when appropriate.

Examples:

``` java
@WebMvcTest
```

for MVC/controller testing.

``` java
@DataJpaTest
```

for JPA/repository testing.

This can make tests faster and more focused.

------------------------------------------------------------------------

# 75. `@SpringBootTest` vs Unit Test

Pure unit:

``` java
new UserService(mockRepository)
```

Spring integration:

``` java
@SpringBootTest
```

Think:

``` text
Unit test
= no framework context required

Spring integration test
= framework context participates
```

Don't use `@SpringBootTest` for every test just because it is
convenient.

------------------------------------------------------------------------

# 76. Testing Controllers

For a controller, a common focused approach is:

``` java
@WebMvcTest(UserController.class)
```

Then mock the service layer.

This tests:

-   Routing
-   Serialization
-   Validation
-   HTTP status
-   Controller behavior

without necessarily loading the entire application.

------------------------------------------------------------------------

# 77. Testing Repositories

Repository integration tests can use:

``` java
@DataJpaTest
```

This is useful for testing:

-   Entity mappings
-   Queries
-   Persistence behavior
-   Repository methods

Often an embedded or test database configuration is used, depending on
the project.

------------------------------------------------------------------------

# 78. Test Containers

For realistic integration tests, Testcontainers can run real
infrastructure in containers.

Examples:

``` text
PostgreSQL
Redis
Kafka
MongoDB
```

Conceptually:

``` text
JUnit
  ↓
Testcontainers
  ↓
Docker container
  ↓
Real database/service
```

This often gives more realistic confidence than replacing everything
with mocks.

------------------------------------------------------------------------

# 79. Transactional Tests

Framework-managed transactions can make integration tests easier to
isolate.

However, understand what your framework is doing.

A test that passes because transactions are automatically rolled back
may behave differently from production behavior.

Always understand:

``` text
Who starts transaction?
Who commits?
Who rolls back?
What thread is used?
```

This becomes especially important with asynchronous execution.

------------------------------------------------------------------------

# 80. Testing Asynchronous Code

Avoid:

``` java
Thread.sleep(5000);
```

It makes tests:

-   Slow
-   Flaky
-   Environment dependent

Prefer synchronization mechanisms such as:

-   Awaitility
-   Futures
-   Latches
-   Framework-provided async testing support

The principle is:

> Wait for a condition, not an arbitrary amount of time.

------------------------------------------------------------------------

# 81. Flaky Tests

A flaky test sometimes passes and sometimes fails without a relevant
code change.

Common causes:

``` text
Time dependency
Randomness
Shared state
Concurrency
Network
External services
Database state
Test order
Environment assumptions
```

Example bad test:

``` java
assertEquals(
    LocalDateTime.now(),
    service.getCurrentTime()
);
```

Time changes between calls.

Better: inject a clock.

``` java
Clock clock;
```

Then test with a fixed clock.

------------------------------------------------------------------------

# 82. Deterministic Testing

Inject external sources of nondeterminism:

``` text
Time      -> Clock
Random    -> Random/seeded generator
UUID      -> UUID provider
Database  -> isolated test DB
Network   -> fake/mock
Environment -> configuration abstraction
```

This dramatically improves reliability.

------------------------------------------------------------------------

# 83. Test Data Builders

Instead of huge constructors:

``` java
new User(
    "Ankit",
    "ankit@example.com",
    true,
    25,
    ...
);
```

Use a builder:

``` java
User user = UserBuilder.aUser()
    .withName("Ankit")
    .withActive(true)
    .build();
```

Builders make tests readable and reduce setup noise.

------------------------------------------------------------------------

# 84. Object Mother Pattern

An Object Mother provides predefined test objects.

``` java
User validUser() { ... }
User adminUser() { ... }
User inactiveUser() { ... }
```

Useful, but beware of overly generic fixtures.

Test-specific builders are often easier to maintain.

------------------------------------------------------------------------

# 85. AAA vs Given-When-Then

### Arrange-Act-Assert

``` text
Arrange
Act
Assert
```

### Given-When-Then

``` text
Given initial state
When action occurs
Then expected result
```

Both communicate the same general testing structure.

------------------------------------------------------------------------

# 86. Testing Exceptions Properly

Bad:

``` java
try {
    service.process(null);
} catch (Exception e) {
}
```

This can accidentally pass for the wrong reason.

Better:

``` java
IllegalArgumentException ex =
    assertThrows(
        IllegalArgumentException.class,
        () -> service.process(null)
    );

assertEquals("input must not be null", ex.getMessage());
```

Test the exception type and, when useful, important error details.

------------------------------------------------------------------------

# 87. Testing Collections

Use meaningful collection assertions.

JUnit:

``` java
assertEquals(3, users.size());
assertTrue(users.contains(user));
```

AssertJ:

``` java
assertThat(users)
    .hasSize(3)
    .contains(user);
```

For unordered collections, don't accidentally test ordering unless
ordering is part of the contract.

------------------------------------------------------------------------

# 88. Testing Optional

``` java
Optional<User> result = service.findUser(1L);

assertTrue(result.isPresent());
assertEquals("Ankit", result.get().getName());
```

Or AssertJ:

``` java
assertThat(result)
    .isPresent()
    .get()
    .extracting(User::getName)
    .isEqualTo("Ankit");
```

------------------------------------------------------------------------

# 89. Testing Private Methods

Usually:

> Don't.

Instead, test the public API that uses the private method.

Reasons:

-   Private implementation can change
-   Tests become tightly coupled
-   Refactoring becomes painful

If a private method contains complex independent logic, consider
extracting it into a class with a clear responsibility.

------------------------------------------------------------------------

# 90. Code Coverage

Coverage tells you which code was executed by tests.

Common metrics:

``` text
Line coverage
Branch coverage
Method coverage
Instruction coverage
```

A test suite with 100% line coverage can still be poor.

Example:

``` java
if (age >= 18) {
    approve();
}
```

One test for age 20 gives line coverage but does not test the `false`
branch.

Therefore:

> Coverage measures execution, not correctness.

------------------------------------------------------------------------

# 91. Mutation Testing

Mutation testing changes production code intentionally.

Example:

``` java
if (amount > 100)
```

might be mutated to:

``` java
if (amount >= 100)
```

A strong test suite should detect the mutation.

Popular Java mutation-testing tooling includes PIT.

Mutation testing answers a deeper question:

> Do my tests actually detect bugs?

------------------------------------------------------------------------

# 92. Code Coverage vs Mutation Testing

``` text
Coverage:
"Did tests execute this code?"

Mutation testing:
"Would tests detect a change/bug in this code?"
```

Mutation testing is usually a stronger signal of test effectiveness.

------------------------------------------------------------------------

# 93. Integration with Maven

Common commands:

``` bash
mvn test
```

Run tests.

``` bash
mvn -Dtest=CalculatorTest test
```

Run a particular test class.

``` bash
mvn -Dtest=CalculatorTest#add_shouldReturnSum test
```

Run a particular test method.

For tag/group-based execution, configure the Maven test plugin
appropriately.

------------------------------------------------------------------------

# 94. Gradle

Typical:

``` bash
./gradlew test
```

JUnit Platform must be enabled in Gradle configuration.

Conceptually:

``` groovy
test {
    useJUnitPlatform()
}
```

Modern Gradle projects typically configure JUnit Jupiter dependencies
and `useJUnitPlatform()`.

------------------------------------------------------------------------

# 95. CI/CD

A real project often follows:

``` text
Developer push
      ↓
CI pipeline
      ↓
Compile
      ↓
Unit tests
      ↓
Integration tests
      ↓
Coverage/quality checks
      ↓
Build artifact
      ↓
Deploy
```

Fast unit tests should run early.

Slow integration/E2E tests can be separated into later pipeline stages
where appropriate.

------------------------------------------------------------------------

# 96. Test Pyramid

A useful mental model:

``` text
          E2E
         /   \
     Integration
       /       \
     Unit tests
```

Typically:

``` text
Many unit tests
Fewer integration tests
Few expensive E2E tests
```

The exact ratio is not a law. The important idea is balancing speed,
isolation, and confidence.

------------------------------------------------------------------------

# 97. Contract Testing

When multiple systems communicate, contract tests verify that
expectations between consumers and providers remain compatible.

Example:

``` text
Frontend/service A
        |
        | API contract
        v
Service B
```

Contract testing is particularly useful for microservices.

JUnit can execute contract test scenarios, while specialized tools can
manage the contract itself.

------------------------------------------------------------------------

# 98. Property-Based Testing

Traditional test:

``` text
input = 5
expected = 10
```

Property-based testing checks general properties over many generated
inputs.

Example property:

``` text
For every integer x:
x + 0 == x
```

JUnit itself is not primarily a property-based testing framework, but
Java libraries can integrate with JUnit.

------------------------------------------------------------------------

# 99. Testing Generics and Type Boundaries

Test meaningful runtime behavior rather than compile-time guarantees.

For generic APIs, focus on:

-   Empty collections
-   Different implementations
-   Null handling where allowed
-   Boundary behavior
-   Type-safe public contracts

------------------------------------------------------------------------

# 100. Testing Legacy JUnit 4 Code

JUnit 5 can coexist with older JUnit 4 tests when the Vintage engine is
included.

This supports gradual migration:

``` text
Existing JUnit 4 tests
        ↓
Vintage engine
        ↓
JUnit Platform

New JUnit 5 tests
        ↓
Jupiter engine
        ↓
JUnit Platform
```

This is valuable for large codebases.

------------------------------------------------------------------------

# 101. JUnit 4 vs JUnit 5

  JUnit 4                                 JUnit 5
  --------------------------------------- ------------------------------
  `@Before`                               `@BeforeEach`
  `@After`                                `@AfterEach`
  `@BeforeClass`                          `@BeforeAll`
  `@AfterClass`                           `@AfterAll`
  `@Ignore`                               `@Disabled`
  `@RunWith`                              Extensions
  Rules                                   Extensions
  Categories                              Tags
  Parameterized support less integrated   Strong parameterized testing
  Older architecture                      Platform + engines

------------------------------------------------------------------------

# 102. Common Interview Question: Why JUnit 5?

Good answer:

> JUnit 5 provides a modular architecture through the JUnit Platform,
> the Jupiter programming model, and optional Vintage support. It
> introduces a powerful extension model, parameterized and dynamic
> tests, better lifecycle control, improved IDE/build integration, and
> support for modern Java testing workflows.

------------------------------------------------------------------------

# 103. Common Interview Question: What is JUnit Platform?

Answer:

> The JUnit Platform is the foundation for launching and executing
> tests. It provides APIs and infrastructure for test discovery,
> execution, reporting, and integration with IDEs and build tools. Test
> engines such as Jupiter and Vintage run on the Platform.

------------------------------------------------------------------------

# 104. Common Interview Question: Jupiter vs Platform?

``` text
Platform
= execution infrastructure

Jupiter
= JUnit 5 programming model + engine
```

------------------------------------------------------------------------

# 105. Common Interview Question: Why does JUnit create a new instance for each test?

Main reason:

> Test isolation.

If tests shared the same test object by default, mutable fields could
leak state between tests.

`PER_METHOD` minimizes this problem.

------------------------------------------------------------------------

# 106. Common Interview Question: Why are `@BeforeAll` methods static?

Because by default JUnit uses a separate test instance per test method.
A class-level lifecycle callback cannot rely on an instance belonging to
one particular test.

`PER_CLASS` allows instance-level `@BeforeAll`/`@AfterAll`.

------------------------------------------------------------------------

# 107. Common Interview Question: `assertEquals` vs `assertSame`?

``` java
assertEquals(a, b)
```

checks equality.

``` java
assertSame(a, b)
```

checks identity.

Equivalent concepts:

``` text
equals()      vs      ==
```

------------------------------------------------------------------------

# 108. Common Interview Question: `assertThrows`?

Answer:

> It executes the supplied executable and verifies that an exception of
> the expected type is thrown. It returns the thrown exception, allowing
> the test to verify its message or other properties.

------------------------------------------------------------------------

# 109. Common Interview Question: Assertion vs Assumption?

``` text
Assertion
-> failure means tested behavior is wrong.

Assumption
-> condition for meaningful execution is not satisfied.
```

Example:

``` java
assumeTrue(isIntegrationEnvironment());
```

------------------------------------------------------------------------

# 110. Common Interview Question: What is `@Nested`?

It allows logically related tests to be organized inside nested classes.

It is useful for structuring scenarios:

``` text
UserServiceTest
├── When user exists
└── When user does not exist
```

------------------------------------------------------------------------

# 111. Common Interview Question: What is a Parameterized Test?

Answer:

> A parameterized test executes the same test logic multiple times using
> different argument sets, reducing duplicate test methods and making
> input coverage easier.

------------------------------------------------------------------------

# 112. Common Interview Question: `@ValueSource` vs `@MethodSource`?

``` text
@ValueSource
-> simple values

@MethodSource
-> complex/custom/generated arguments
```

------------------------------------------------------------------------

# 113. Common Interview Question: Dynamic vs Parameterized Tests?

### Parameterized

Test structure is fixed, data varies.

``` text
same test
+ predefined argument source
```

### Dynamic

The test nodes themselves are generated at runtime.

``` text
runtime-generated test structure
```

------------------------------------------------------------------------

# 114. Common Interview Question: What replaced JUnit 4 Rules/Runners?

JUnit 5's **Extension Model**.

Instead of:

``` java
@RunWith(...)
```

you commonly use:

``` java
@ExtendWith(...)
```

or:

``` java
@RegisterExtension
```

------------------------------------------------------------------------

# 115. Common Interview Question: How does Mockito integrate with JUnit 5?

Through:

``` java
@ExtendWith(MockitoExtension.class)
```

The extension participates in the Jupiter lifecycle and initializes
Mockito-related test fields and manages Mockito behavior.

------------------------------------------------------------------------

# 116. Common Interview Question: Should Every Service Have a Unit Test?

Not mechanically.

Good tests should target meaningful behavior and risk.

Testing every trivial getter/setter may provide little value.

Prioritize:

-   Business logic
-   Edge cases
-   Failure handling
-   Important integrations
-   Regression-prone behavior

------------------------------------------------------------------------

# 117. Common Interview Question: What Makes a Good Test?

A strong answer:

``` text
Fast
Independent
Repeatable
Deterministic
Readable
Focused
Maintainable
Meaningful
```

A good test also fails for a useful reason.

------------------------------------------------------------------------

# 118. Common Interview Question: Why Do Tests Become Flaky?

Typical causes:

``` text
Shared mutable state
Concurrency
Randomness
Current time
External APIs
Network
Database state
Filesystem state
Test ordering
Environment variables
```

Fix the source of nondeterminism rather than increasing arbitrary
sleeps.

------------------------------------------------------------------------

# 119. Common Interview Question: Should Tests Verify Implementation?

Generally, test externally observable behavior.

For interaction-heavy code, verifying important dependency interactions
can be valid.

The key is:

> Don't make tests so coupled to implementation that harmless
> refactoring breaks them.

------------------------------------------------------------------------

# 120. Real-Project Test Layering

A mature Java backend might look like:

``` text
src/test/java
├── unit/
│   ├── service/
│   └── utility/
│
├── integration/
│   ├── repository/
│   └── client/
│
└── e2e/
    └── workflows/
```

The exact directory structure varies by organization. Maven does not
require this exact layout.

------------------------------------------------------------------------

# 121. Example Real Service Test

Production:

``` java
class DiscountService {

    private final CustomerRepository repository;

    DiscountService(CustomerRepository repository) {
        this.repository = repository;
    }

    BigDecimal calculateDiscount(long customerId) {

        Customer customer =
            repository.findById(customerId)
                .orElseThrow(
                    () -> new CustomerNotFoundException()
                );

        if (customer.isPremium()) {
            return new BigDecimal("0.20");
        }

        return new BigDecimal("0.05");
    }
}
```

Test:

``` java
@ExtendWith(MockitoExtension.class)
class DiscountServiceTest {

    @Mock
    CustomerRepository repository;

    @InjectMocks
    DiscountService service;

    @Test
    void premiumCustomer_shouldReceive20PercentDiscount() {

        Customer customer = new Customer(true);

        when(repository.findById(1L))
            .thenReturn(Optional.of(customer));

        BigDecimal result =
            service.calculateDiscount(1L);

        assertEquals(
            new BigDecimal("0.20"),
            result
        );
    }

    @Test
    void missingCustomer_shouldThrowException() {

        when(repository.findById(1L))
            .thenReturn(Optional.empty());

        assertThrows(
            CustomerNotFoundException.class,
            () -> service.calculateDiscount(1L)
        );
    }
}
```

------------------------------------------------------------------------

# 122. Real Project: Testing a REST Controller

Typical strategy:

``` text
Controller test
     |
     +-- mock Service
```

Verify:

``` text
HTTP status
Response body
Validation
Request mapping
Error handling
```

Do not duplicate every service business rule in controller tests.

------------------------------------------------------------------------

# 123. Real Project: Testing Database Queries

For custom queries, prefer integration tests against a realistic
database environment when database semantics matter.

Mocks can verify:

``` java
repository.findById(...)
```

but cannot prove that a SQL/JPQL query actually works.

This distinction is extremely important:

> A mocked repository test does not test the database query.

------------------------------------------------------------------------

# 124. Real Project: External API Clients

For an HTTP client:

### Unit test

Mock the HTTP abstraction.

Test:

-   Request mapping
-   Response mapping
-   Error handling
-   Retry decision logic

### Integration/contract test

Use a real test server, WireMock, MockWebServer, or a contract-testing
setup.

------------------------------------------------------------------------

# 125. Test Isolation Strategies

For database tests:

``` text
Transaction rollback
Test schema
Test database
Containerized database
Unique test data
```

For filesystem tests:

``` text
Temporary directory
```

For time:

``` text
Fixed Clock
```

For network:

``` text
Mock server
```

------------------------------------------------------------------------

# 126. Temporary Files

JUnit provides temporary-directory support through its extension model.

A test can request a temporary directory parameter where supported:

``` java
@Test
void shouldWriteFile(TempDir tempDir) {
}
```

The exact API should match the JUnit version used by the project; modern
JUnit 5 supports `@TempDir`.

Example:

``` java
@Test
void shouldWriteFile(@TempDir Path tempDir) throws IOException {

    Path file = tempDir.resolve("data.txt");

    Files.writeString(file, "hello");

    assertEquals(
        "hello",
        Files.readString(file)
    );
}
```

JUnit manages the temporary resource lifecycle.

------------------------------------------------------------------------

# 127. Dependency Injection into Tests

JUnit itself can inject certain framework-provided parameters through
its extension mechanism.

Examples include:

``` java
TestInfo
RepetitionInfo
TestReporter
```

Example:

``` java
@Test
void test(TestInfo info) {

    System.out.println(
        info.getDisplayName()
    );
}
```

For application-specific objects, use an extension or framework
integration such as Spring.

------------------------------------------------------------------------

# 128. TestInfo

``` java
@Test
void example(TestInfo testInfo) {
    System.out.println(
        testInfo.getDisplayName()
    );
}
```

Useful for diagnostic or generic test infrastructure.

------------------------------------------------------------------------

# 129. TestReporter

JUnit can inject a `TestReporter`:

``` java
@Test
void example(TestReporter reporter) {

    reporter.publishEntry(
        "status",
        "running"
    );
}
```

This can integrate test-generated information with test reporting.

Do not use it as a replacement for structured logging.

------------------------------------------------------------------------

# 130. Test Execution Exception Handling

Extensions can implement:

``` java
TestExecutionExceptionHandler
```

This allows infrastructure to react to test exceptions.

Possible use cases:

-   Diagnostic logging
-   Screenshot capture
-   Resource cleanup
-   Additional reporting

This is especially useful in UI/integration test infrastructure.

------------------------------------------------------------------------

# 131. Invocation Interception

Advanced extensions can intercept invocation of:

-   Test methods
-   Lifecycle methods
-   Constructors

This is provided through JUnit's extension APIs such as
`InvocationInterceptor`.

It enables sophisticated behavior such as:

``` text
before invocation
    ↓
custom wrapper
    ↓
actual test
    ↓
after invocation
```

Use this for infrastructure, not ordinary business tests.

------------------------------------------------------------------------

# 132. Extension Composition

A class can use multiple extensions:

``` java
@ExtendWith({
    MockitoExtension.class,
    LoggingExtension.class
})
class MyTest {
}
```

In real applications, extensions may come from:

-   JUnit
-   Mockito
-   Spring
-   Company test libraries
-   Custom infrastructure

Understanding extension interactions becomes important in large
projects.

------------------------------------------------------------------------

# 133. Extension Ordering

When multiple extensions participate in execution, ordering matters.

JUnit provides mechanisms for controlling extension registration/order
where applicable.

Do not assume extensions execute in the same order they appear
everywhere. Understand the specific extension callback and registration
mechanism being used.

------------------------------------------------------------------------

# 134. Resource Management

Tests frequently allocate:

``` text
Database connections
Containers
Files
Servers
Clients
Threads
```

Use lifecycle mechanisms so cleanup occurs even when assertions fail.

For example:

``` java
@BeforeEach
void setup() {}

@AfterEach
void cleanup() {}
```

For extension-based infrastructure, use the corresponding extension
callbacks and stores.

------------------------------------------------------------------------

# 135. `@TempDir` vs Manual Temporary Files

Prefer:

``` java
@TempDir
Path tempDir;
```

over manually constructing random temporary paths when possible.

It provides lifecycle-managed temporary resources.

------------------------------------------------------------------------

# 136. Test Security

Do not commit:

``` text
API keys
Passwords
Production credentials
Private certificates
Tokens
```

Use:

-   Environment variables
-   CI secrets
-   Test-specific credentials
-   Ephemeral test services

Never point automated tests at production databases unless explicitly
designed and authorized.

------------------------------------------------------------------------

# 137. Testing Security Behavior

Test security rules such as:

``` text
Unauthenticated -> 401
Authenticated but unauthorized -> 403
Authorized -> 200
```

For service-level authorization:

``` text
normal user cannot perform admin operation
admin can perform admin operation
```

Security tests should verify behavior, not merely configuration
presence.

------------------------------------------------------------------------

# 138. Testing Validation

For input validation, cover:

``` text
Valid input
Null
Empty
Minimum
Maximum
Too long
Invalid format
Boundary values
```

For example:

``` text
age = 17
age = 18
age = 19
```

rather than testing only:

``` text
age = 25
```

------------------------------------------------------------------------

# 139. Boundary Value Analysis

A powerful testing principle.

If valid range is:

``` text
18 <= age <= 60
```

test:

``` text
17
18
19
59
60
61
```

These values are much more valuable than arbitrary values such as:

``` text
27
42
53
```

------------------------------------------------------------------------

# 140. Equivalence Partitioning

Divide input into groups expected to behave similarly.

For age:

``` text
< 18        -> invalid
18 to 60    -> valid
> 60        -> invalid
```

Test representative values from each partition plus important
boundaries.

------------------------------------------------------------------------

# 141. Regression Tests

Whenever a bug is fixed:

``` text
Bug found
   ↓
Add regression test
   ↓
Fix code
   ↓
Run suite
```

This prevents the same bug from returning.

A good regression test reproduces the original failure before the fix.

------------------------------------------------------------------------

# 142. Test Smells

Watch for:

### Huge setup

Test requires 100 lines before the assertion.

### Mystery guest

Test depends on hidden external state.

### Test duplication

Many tests repeat identical setup.

### Fragile assertions

Asserts entire giant object when only one field matters.

### Excessive mocking

Everything is mocked, so the test proves almost nothing.

### Test interdependence

Test B only works after Test A.

### Sleep-based synchronization

``` java
Thread.sleep(...)
```

### Random data without controlled seed

Can make failures impossible to reproduce.

------------------------------------------------------------------------

# 143. Testing Anti-Pattern: One Giant Test

Bad:

``` text
create user
login
create order
pay
ship
cancel
refund
```

One failure can obscure the actual problem.

Prefer focused tests or a deliberate E2E workflow.

------------------------------------------------------------------------

# 144. Testing Anti-Pattern: Testing Private Implementation

Avoid reflection-based private method testing unless there is a very
unusual framework/infrastructure reason.

Usually extract meaningful behavior into a public abstraction.

------------------------------------------------------------------------

# 145. Testing Anti-Pattern: Mock Everything

If you mock:

``` text
Service
Repository
Mapper
Validator
Clock
Config
Database
```

you might end up testing only your mocks.

Use mocks at architectural boundaries where isolation is useful, but use
real lightweight collaborators when that produces more meaningful
confidence.

------------------------------------------------------------------------

# 146. Unit Test vs Integration Test Decision

Ask:

### Can I test this without infrastructure?

If yes, a unit test is often appropriate.

### Does correctness depend on database behavior?

Use an integration test.

### Does correctness depend on an external API contract?

Use contract/integration testing.

### Does the complete user journey matter?

Use an E2E test.

------------------------------------------------------------------------

# 147. Performance Testing

JUnit is primarily a test execution framework, not a full
performance-testing platform.

A simple timeout can catch obvious regressions:

``` java
assertTimeout(
    Duration.ofMillis(100),
    () -> service.process()
);
```

For serious performance testing, use specialized tools/benchmarks.

Do not confuse:

``` text
functional correctness
```

with:

``` text
performance benchmarking
```

------------------------------------------------------------------------

# 148. Testing Concurrency

Concurrency tests are difficult because race conditions are
timing-dependent.

Useful approaches include:

``` text
CountDownLatch
CyclicBarrier
ExecutorService
Concurrent collections
Repeated execution
Deterministic coordination
```

Avoid relying on:

``` java
Thread.sleep(...)
```

as your synchronization mechanism.

------------------------------------------------------------------------

# 149. Testing Race Conditions

Instead of:

``` java
startThreads();
Thread.sleep(1000);
assertSomething();
```

coordinate execution:

``` text
Thread A waits
Thread B waits
release both
observe outcome
```

This makes the race scenario more reproducible.

------------------------------------------------------------------------

# 150. Test Naming in Large Teams

Choose a convention and keep it consistent.

Examples:

``` text
method_whenCondition_shouldExpectedBehavior
```

or:

``` text
shouldExpectedBehavior_whenCondition
```

Example:

``` java
calculateDiscount_whenCustomerIsPremium_shouldReturn20Percent()
```

Consistency matters more than the exact convention.

------------------------------------------------------------------------

# 151. Test Documentation

A good test often documents behavior better than comments.

Instead of:

``` java
// This checks that premium users get discount
```

write:

``` java
void premiumCustomer_shouldReceive20PercentDiscount()
```

The test itself becomes executable documentation.

------------------------------------------------------------------------

# 152. Assertions Should Be Specific

Instead of:

``` java
assertTrue(result != null);
```

prefer:

``` java
assertNotNull(result);
```

Instead of:

``` java
assertTrue(result.equals(expected));
```

prefer:

``` java
assertEquals(expected, result);
```

Specific assertions produce clearer failures.

------------------------------------------------------------------------

# 153. Multiple Assertions

Use multiple assertions when they collectively describe one meaningful
result.

Good:

``` java
assertAll(
    () -> assertEquals(200, response.status()),
    () -> assertEquals("OK", response.message())
);
```

Avoid turning one test into a completely unrelated checklist.

------------------------------------------------------------------------

# 154. Test Data and Readability

Bad:

``` java
new User(
    1L,
    "Ankit",
    "ankit@example.com",
    true,
    false,
    LocalDate.now(),
    ...
);
```

Better:

``` java
User user = UserBuilder.aUser()
    .withName("Ankit")
    .withEmail("ankit@example.com")
    .active()
    .build();
```

Readable data reduces cognitive load.

------------------------------------------------------------------------

# 155. Testing with Realistic Data

Do not always use:

``` text
foo
bar
test
123
```

When domain behavior matters, use meaningful values.

For example:

``` text
customerId
order amount
currency
timezone
status
```

But avoid production PII in test datasets.

------------------------------------------------------------------------

# 156. Time-Based Tests

Bad:

``` java
assertEquals(
    LocalDate.now(),
    service.getDate()
);
```

Better:

``` java
Clock fixedClock =
    Clock.fixed(
        Instant.parse("2026-01-01T00:00:00Z"),
        ZoneOffset.UTC
    );
```

Inject the clock.

This makes time deterministic.

------------------------------------------------------------------------

# 157. Randomness-Based Tests

If testing random behavior:

``` text
Use a controlled seed
```

or abstract the random generator.

A failing test should be reproducible.

------------------------------------------------------------------------

# 158. Logging in Tests

Avoid excessive console printing.

Use logging only when it helps diagnose:

``` text
integration failures
container startup
external interactions
```

JUnit reports failures; assertions should provide the main diagnostic
information.

------------------------------------------------------------------------

# 159. Debugging a Failed JUnit Test

Read:

``` text
Test name
↓
Assertion error
↓
Expected vs actual
↓
Stack trace
↓
First relevant application frame
```

Do not immediately look at the last stack-trace line without
understanding the assertion.

------------------------------------------------------------------------

# 160. Test Reports

Build tools generate test reports containing:

``` text
Passed
Failed
Skipped
Execution time
Failure stack trace
```

CI systems can aggregate these results.

The JUnit Platform produces structured execution events that tools can
consume.

------------------------------------------------------------------------

# 161. Surefire vs Failsafe

Important Maven interview topic.

### Surefire

Typically used for unit tests during:

``` bash
mvn test
```

Common naming patterns include:

``` text
*Test
*Tests
Test*
```

### Failsafe

Commonly used for integration-test phases.

Typical lifecycle:

``` text
integration-test
        ↓
verify
```

A common naming convention is:

``` text
*IT
*ITCase
```

Exact configuration depends on the project.

------------------------------------------------------------------------

# 162. Why Separate Unit and Integration Tests?

Unit tests:

``` text
Fast
Frequent
Isolated
```

Integration tests:

``` text
Slower
Infrastructure-dependent
Higher integration confidence
```

Separating them lets CI run the fastest feedback first.

------------------------------------------------------------------------

# 163. Test Execution Time

Track slow tests.

A test suite with thousands of tests can become unusable if every test
takes too long.

Optimize:

``` text
Unnecessary context startup
Database setup
Network calls
Containers
Large fixtures
Sleep
Repeated expensive initialization
```

But don't sacrifice test quality merely to make the suite fast.

------------------------------------------------------------------------

# 164. Test Context Caching in Frameworks

Frameworks such as Spring may cache application contexts between tests
when configuration is compatible.

This can dramatically improve integration-test speed.

However, changing test configuration frequently can cause context cache
misses and slower suites.

Understand the framework's behavior rather than assuming every test
starts the entire application from scratch.

------------------------------------------------------------------------

# 165. Test Isolation vs Performance

There is a tradeoff:

``` text
More isolation
    ↑
Potentially more setup

More shared state
    ↑
Potentially faster
    ↓
More risk of interference
```

Prefer isolation unless sharing is deliberate and safe.

------------------------------------------------------------------------

# 166. The "Unit" in Unit Test

A unit does not always mean:

> exactly one method.

It means a small, isolated unit of behavior whose collaborators are
controlled appropriately.

A service plus a pure mapper may reasonably be tested together if that
makes the test clearer and still isolated from infrastructure.

------------------------------------------------------------------------

# 167. Testing Architecture

A strong project may use:

``` text
Unit tests
    ↓
Component tests
    ↓
Integration tests
    ↓
Contract tests
    ↓
E2E tests
```

Each layer answers different questions.

------------------------------------------------------------------------

# 168. Interview: What happens when a JUnit test fails?

Conceptually:

``` text
Test executable throws assertion/other exception
             ↓
Jupiter captures execution outcome
             ↓
Extension callbacks may react
             ↓
Platform publishes execution event/result
             ↓
IDE/build tool reports failure
```

A test failure does not mean the Java process necessarily crashes. The
framework records the test outcome.

------------------------------------------------------------------------

# 169. Interview: What happens if `@BeforeEach` fails?

The test method itself is not normally executed because its required
setup failed.

The framework still handles the lifecycle according to its execution
rules and invokes applicable cleanup callbacks.

The exact failure/reporting behavior can involve extension callbacks.

------------------------------------------------------------------------

# 170. Interview: What happens if `@AfterEach` fails?

The test result can be affected by cleanup failure.

This is one reason cleanup code should be reliable and simple.

If cleanup is broken, you can get failures even when the test's business
assertion passed.

------------------------------------------------------------------------

# 171. Interview: Can a JUnit test method be private?

JUnit Jupiter test methods are expected to be non-private;
package-private methods are commonly used.

You generally do not need `public` on JUnit 5 test methods.

Example:

``` java
@Test
void shouldWork() {
}
```

This is valid and idiomatic.

------------------------------------------------------------------------

# 172. Interview: Can a JUnit test method be static?

JUnit test methods are normally instance methods.

Static methods are not the normal Jupiter test method model.

Static is primarily relevant to class-level lifecycle methods under the
default test instance lifecycle.

------------------------------------------------------------------------

# 173. Interview: Can constructors have parameters?

JUnit Jupiter can support parameterized test constructors through its
parameter resolution mechanisms/extensions.

However, do not confuse:

``` text
test method parameter injection
```

with ordinary application dependency injection.

Framework extensions control how supported parameters are resolved.

------------------------------------------------------------------------

# 174. Interview: Why use package-private test classes?

JUnit 5 does not require test classes and methods to be `public`.

This reduces unnecessary boilerplate.

``` java
class CalculatorTest {
}
```

is perfectly normal.

------------------------------------------------------------------------

# 175. Interview: What is an ExtensionContext.Store used for?

It gives extensions scoped storage for state.

For example:

``` text
create resource
      ↓
store resource
      ↓
retrieve later
      ↓
cleanup
```

This prevents extensions from relying on unsafe global static state.

------------------------------------------------------------------------

# 176. Interview: What is a Test Engine?

A Test Engine is a component that understands a particular testing
programming model and executes tests on the JUnit Platform.

Examples:

``` text
Jupiter Engine
Vintage Engine
```

------------------------------------------------------------------------

# 177. Interview: Why is JUnit Platform useful?

Because it decouples:

``` text
test framework
```

from:

``` text
test execution infrastructure
```

This enables a common execution platform for multiple test engines.

------------------------------------------------------------------------

# 178. Interview: What is the Launcher?

The Launcher is the programmatic entry point used to discover and
execute tests on the JUnit Platform.

IDE and build-tool integrations can use Platform APIs rather than
directly depending on internal Jupiter implementation details.

------------------------------------------------------------------------

# 179. Interview: Why are parameterized tests useful?

They reduce duplicated test methods and make boundary/edge-case coverage
explicit.

Instead of:

``` text
testZero()
testNegative()
testEmpty()
testWhitespace()
```

you can often express a single behavior with many inputs.

------------------------------------------------------------------------

# 180. Interview: When should you use `@MethodSource`?

Use it when test arguments are:

-   Complex
-   Multiple fields
-   Objects
-   Generated
-   Reused
-   Too large for annotations

------------------------------------------------------------------------

# 181. Interview: `@BeforeEach` vs `@BeforeAll`

``` text
@BeforeEach
-> before every test invocation

@BeforeAll
-> once for the test class
```

With parameterized tests, each parameterized invocation is a separate
test execution, so `@BeforeEach` runs for each invocation.

------------------------------------------------------------------------

# 182. Interview: Why avoid test ordering?

Because order-dependent tests are fragile.

If:

``` text
Test A must run before Test B
```

then:

``` text
parallel execution
random ordering
IDE selection
```

can break the suite.

Independent tests are easier to run, debug, and maintain.

------------------------------------------------------------------------

# 183. Interview: What is test isolation?

Each test should control its own:

``` text
Input
State
Dependencies
Environment
Cleanup
```

A test should not depend on what another test did.

------------------------------------------------------------------------

# 184. Interview: What is a flaky test?

A test whose result is nondeterministic under the same intended
conditions.

Common causes:

``` text
Timing
Concurrency
External services
Randomness
Shared state
Environment
```

------------------------------------------------------------------------

# 185. Interview: How would you make a test suite faster?

Good answer:

1.  Keep unit tests isolated.
2.  Remove unnecessary sleeps.
3.  Avoid unnecessary Spring/application context startup.
4.  Parallelize safe tests.
5.  Reuse expensive infrastructure deliberately.
6.  Separate unit/integration/E2E pipelines.
7.  Reduce redundant setup.
8.  Profile slow tests.
9.  Use test slices where appropriate.
10. Avoid unnecessary network calls.

------------------------------------------------------------------------

# 186. Interview: How would you test a service with a repository?

For a pure unit test:

``` text
Service = real
Repository = mock
```

Then:

``` text
Arrange repository response
Act service
Assert result/exception
Verify important interaction
```

For repository query correctness, add an integration test using a
real/test database.

------------------------------------------------------------------------

# 187. Interview: How do you test external API failure?

Cover scenarios such as:

``` text
200 success
400 client error
401/403 authorization
404 not found
429 rate limit
500 server error
timeout
malformed response
connection failure
```

Not every service needs all of these, but important failure modes should
be explicit.

------------------------------------------------------------------------

# 188. Interview: What is a regression test?

A test added specifically to ensure a previously discovered bug does not
return.

The ideal workflow:

``` text
Bug
 ↓
Reproduce with failing test
 ↓
Fix
 ↓
Test passes
 ↓
Keep regression test
```

------------------------------------------------------------------------

# 189. Interview: What is a smoke test?

A small set of tests that quickly checks whether a build/system is
basically functional.

Example:

``` text
Application starts
Login works
Health endpoint works
Basic API works
```

Smoke tests are usually broader than unit tests.

------------------------------------------------------------------------

# 190. Interview: What is a test fixture?

The data/environment required to run a test.

Examples:

``` text
User object
Database record
Temporary directory
Mock configuration
Test server
```

Good fixtures should be easy to understand and control.

------------------------------------------------------------------------

# 191. Interview: What is a test contract?

A reusable set of behavioral expectations that multiple implementations
must satisfy.

Example:

``` text
Repository implementations
    ↓
must satisfy
    ↓
same repository contract tests
```

JUnit interfaces/default methods can help implement contract-test
suites.

------------------------------------------------------------------------

# 192. Advanced Example: Contract Test

``` java
interface RepositoryContract {

    Repository createRepository();

    @Test
    default void saveThenFindShouldReturnEntity() {

        Repository repository =
            createRepository();

        Entity entity = new Entity("A");

        repository.save(entity);

        assertTrue(
            repository.findById(entity.id()).isPresent()
        );
    }
}
```

Multiple repository implementations can implement this interface.

------------------------------------------------------------------------

# 193. Testing with Testcontainers --- Mental Model

``` text
JUnit test starts
       ↓
Container starts
       ↓
Database becomes ready
       ↓
Test runs
       ↓
Assertions
       ↓
Container cleanup
```

This is powerful for realistic integration tests.

------------------------------------------------------------------------

# 194. Unit Test Checklist

Before committing a unit test, ask:

``` text
[ ] Does it test behavior?
[ ] Is it deterministic?
[ ] Is it isolated?
[ ] Is the name meaningful?
[ ] Does it cover important edge cases?
[ ] Does it fail for the right reason?
[ ] Is setup minimal?
[ ] Are mocks necessary?
[ ] Is the assertion specific?
[ ] Does it run quickly?
```

------------------------------------------------------------------------

# 195. Integration Test Checklist

``` text
[ ] Does it verify an actual integration?
[ ] Is infrastructure isolated?
[ ] Is test data controlled?
[ ] Is cleanup reliable?
[ ] Is the database/service realistic enough?
[ ] Are credentials safe?
[ ] Can failures be diagnosed?
[ ] Is the test separated from unit-test execution if necessary?
```

------------------------------------------------------------------------

# 196. Recommended Testing Strategy for a Spring Backend

For a typical backend:

### Unit

``` text
Business services
Domain logic
Mappers
Utilities
Validators
```

### Slice/component

``` text
Controllers
Repositories
Messaging adapters
```

### Integration

``` text
Database
External service adapters
Messaging
Security integration
```

### E2E

``` text
Critical business journeys
```

------------------------------------------------------------------------

# 197. A Practical Test Distribution

Do not blindly follow a fixed percentage.

A healthy suite generally has:

``` text
Large number of fast unit tests
Moderate integration coverage
Small number of high-value E2E tests
```

The right distribution depends on:

-   Architecture
-   Business risk
-   Infrastructure
-   Team size
-   Deployment frequency

------------------------------------------------------------------------

# 198. Golden Rules

Remember these:

1.  **Test behavior, not implementation.**
2.  **Keep unit tests fast.**
3.  **Keep tests independent.**
4.  **Avoid nondeterminism.**
5.  **Use parameterized tests for data variation.**
6.  **Use integration tests when real infrastructure behavior matters.**
7.  **Don't mock what you actually need to test.**
8.  **Don't use `Thread.sleep()` as synchronization.**
9.  **Add regression tests for bugs.**
10. **Coverage is not correctness.**
11. **Use extensions for reusable testing infrastructure.**
12. **Understand your framework's lifecycle.**

------------------------------------------------------------------------

# 199. Quick JUnit 5 Cheat Sheet

## Core

``` java
@Test
@DisplayName
@Disabled
```

## Lifecycle

``` java
@BeforeAll
@AfterAll
@BeforeEach
@AfterEach
```

## Organization

``` java
@Nested
@Tag
@Order
@TestMethodOrder
```

## Parameters

``` java
@ParameterizedTest
@ValueSource
@NullSource
@EmptySource
@NullAndEmptySource
@EnumSource
@CsvSource
@CsvFileSource
@MethodSource
@ArgumentsSource
```

## Dynamic/repeated

``` java
@TestFactory
@RepeatedTest
```

## Conditions

``` java
@EnabledOnOs
@DisabledOnOs
@EnabledOnJre
@DisabledOnJre
@EnabledIfSystemProperty
@EnabledIfEnvironmentVariable
```

## Extensions

``` java
@ExtendWith
@RegisterExtension
```

## Assertions

``` java
assertEquals
assertNotEquals
assertTrue
assertFalse
assertNull
assertNotNull
assertSame
assertNotSame
assertThrows
assertDoesNotThrow
assertAll
assertTimeout
assertTimeoutPreemptively
```

## Assumptions

``` java
assumeTrue
assumeFalse
assumingThat
```

------------------------------------------------------------------------

# 200. Final Mental Model

If you remember only one architecture diagram, remember:

``` text
                 IDE / Maven / Gradle / CI
                            |
                            v
                    JUnit Platform
                            |
                  +---------+---------+
                  |                   |
                  v                   v
           Jupiter Engine       Vintage Engine
                  |
                  v
        JUnit Jupiter Programming Model
                  |
       +----------+----------+
       |          |          |
     Tests    Assertions   Extensions
       |
       +--> Lifecycle
       +--> Parameterized Tests
       +--> Dynamic Tests
       +--> Repeated Tests
       +--> Conditions
```

And for real projects:

``` text
                  Testing Strategy
                         |
       +-----------------+-----------------+
       |                 |                 |
      Unit          Integration           E2E
       |                 |                 |
    Mockito         Test DB/API        Full system
       |                 |                 |
    Fast              Realistic          Expensive
```

The key skill is not memorizing annotations. It is knowing **which
testing level to use, what behavior to verify, how to isolate
dependencies, how JUnit executes the test, and how to keep the suite
deterministic and maintainable.**

------------------------------------------------------------------------

# 201. Interview Rapid-Fire Questions

Use these for revision:

### Q1. What are the main components of JUnit 5?

**JUnit Platform, JUnit Jupiter, and JUnit Vintage.**

### Q2. What is Jupiter?

**The JUnit 5 programming and extension model, including the Jupiter
engine.**

### Q3. What is the Platform?

**The infrastructure for discovering and executing tests through test
engines.**

### Q4. Default test instance lifecycle?

**PER_METHOD.**

### Q5. `@BeforeEach` vs `@BeforeAll`?

**Every test invocation vs once per class.**

### Q6. Why is `@BeforeAll` static by default?

**Because JUnit normally creates a new instance per test method.**

### Q7. How can `@BeforeAll` be non-static?

**Use `@TestInstance(PER_CLASS)`.**

### Q8. `assertEquals` vs `assertSame`?

**Equality vs object identity.**

### Q9. What does `assertThrows` return?

**The thrown exception.**

### Q10. Assertion vs assumption?

**Failure of behavior vs precondition for running the test.**

### Q11. What replaces JUnit 4 Rules/Runners?

**JUnit 5 Extension Model.**

### Q12. What is `@Nested`?

**Logical grouping of related tests.**

### Q13. What is `@MethodSource`?

**A parameter source backed by a method returning test arguments.**

### Q14. What is `@TestFactory`?

**Creates dynamic tests at runtime.**

### Q15. What is a flaky test?

**A nondeterministic test that can pass/fail under the same intended
conditions.**

### Q16. How do you avoid flaky tests?

**Control time, randomness, concurrency, external dependencies, state,
and ordering.**

### Q17. Does 100% coverage mean perfect tests?

**No. Coverage measures execution, not whether tests detect incorrect
behavior.**

### Q18. What is mutation testing?

**Intentionally modifying code and checking whether tests detect those
mutations.**

### Q19. Unit test or integration test for a SQL query?

**Integration test against a suitable test database if query correctness
matters.**

### Q20. Why not use `@SpringBootTest` everywhere?

**It is heavier and slower than focused unit/slice tests and can
unnecessarily increase feedback time.**

### Q21. How does Mockito integrate with JUnit 5?

**Through the Mockito Jupiter extension.**

### Q22. What is `ParameterResolver`?

**An extension mechanism that allows JUnit to resolve parameters for
supported test/lifecycle methods.**

### Q23. What is `ExtensionContext.Store`?

**Scoped storage for state managed by an extension.**

### Q24. Why avoid test ordering?

**It creates dependencies and makes parallel/random execution and
maintenance harder.**

### Q25. How should a backend testing strategy be layered?

**Many fast unit tests, focused component/slice tests, realistic
integration tests, and a small number of high-value E2E tests.**

------------------------------------------------------------------------

# 202. Suggested Learning Path

Study in this order:

``` text
1. JUnit architecture
2. @Test
3. Assertions
4. Lifecycle
5. Test instance lifecycle
6. Parameterized tests
7. Nested tests
8. Tags and conditions
9. Dynamic/repeated tests
10. Assumptions
11. Mockito integration
12. Unit testing best practices
13. Spring Boot testing
14. Integration testing
15. Testcontainers
16. Extensions
17. Parallel execution
18. JUnit Platform internals
19. CI/CD integration
20. Interview questions
```

After learning the concepts, build tests for a real backend project. The
fastest way to become comfortable with JUnit is to repeatedly decide:

> **What should be real, what should be mocked, what behavior matters,
> and at which testing level should I verify it?**
