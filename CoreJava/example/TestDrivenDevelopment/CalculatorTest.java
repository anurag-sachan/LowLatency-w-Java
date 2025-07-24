package example.TestDrivenDevelopment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Calculator Tests")
class CalculatorTest {
    
    private Calculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }
    
    @Test
    @DisplayName("Adding two numbers")
    void testAdd() {
        assertEquals(5, calculator.add(2, 3), "2 + 3 should equal 5");
    }
    
    @Test
    @DisplayName("Subtracting two numbers")
    void testSubtract() {
        assertEquals(1, calculator.subtract(3, 2), "3 - 2 should equal 1");
    }
    
    @Test
    @DisplayName("Multiplying two numbers")
    void testMultiply() {
        assertEquals(6, calculator.multiply(2, 3), "2 * 3 should equal 6");
    }
    
    @Test
    @DisplayName("Dividing two numbers")
    void testDivide() {
        assertEquals(2, calculator.divide(6, 3), "6 / 3 should equal 2");
    }
    
    @Test
    @DisplayName("Dividing by zero")
    void testDivideByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(1, 0);
        }, "Dividing by zero should throw ArithmeticException");
    }
    
    @ParameterizedTest
    @CsvSource({
        "0, 1, 1",
        "1, 2, 3",
        "-1, 1, 0",
        "5, -3, 2"
    })
    @DisplayName("Adding with multiple test cases")
    void testAddWithMultipleCases(int a, int b, int expected) {
        assertEquals(expected, calculator.add(a, b), 
            () -> a + " + " + b + " should equal " + expected);
    }
}