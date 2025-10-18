import com.mixfa.calculator.MathParser;
import com.mixfa.calculator.exception.MathParsingException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    public void test1() throws MathParsingException {
        assertEquals(
                BigDecimal.valueOf(3),
                MathParser.defaultParser().parse("1+2").calculate().asBigDecimal()
        );
        assertEquals(
                BigDecimal.valueOf(4),
                MathParser.defaultParser().parse("2^2").calculate().asBigDecimal()
        );
        assertEquals(
                BigDecimal.valueOf(16),
                MathParser.defaultParser().parse("2^2^2").calculate().asBigDecimal()
        );
        assertEquals(
                BigDecimal.valueOf(8),
                MathParser.defaultParser().parse("2+2*2+2").calculate().asBigDecimal()
        );
        assertEquals(
                new BigDecimal(String.valueOf(Math.PI)),
                MathParser.defaultParser().parse("pi").calculate().asBigDecimal()
        );
    }

}
