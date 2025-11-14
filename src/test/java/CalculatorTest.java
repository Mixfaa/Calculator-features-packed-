import com.mixfa.calculator.MathParser;
import com.mixfa.calculator.ValueFactory;
import com.mixfa.calculator.exception.MathParsingException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    public void test1() throws MathParsingException {
        var parser = MathParser.defaultParser();
        assertEquals(
                BigInteger.TWO,
                parser.parseInput("1-(-1)").calculate().asBigInteger()
        );
        assertEquals(
                BigDecimal.valueOf(3),
                parser.parseInput("1+2").calculate().asBigDecimal()
        );
        assertEquals(
                BigDecimal.valueOf(4),
                parser.parseInput("2^2").calculate().asBigDecimal()
        );
        assertEquals(
                BigDecimal.valueOf(16),
                parser.parseInput("2^2^2").calculate().asBigDecimal()
        );
        assertEquals(
                BigDecimal.valueOf(8),
                parser.parseInput("2+2*2+2").calculate().asBigDecimal()
        );
        assertEquals(
                new BigDecimal(String.valueOf(Math.PI)),
                parser.parseInput("pi").calculate().asBigDecimal()
        );

        assertEquals(
                ValueFactory.ratio(
                        ValueFactory.toValue(7),
                        ValueFactory.toValue(3)
                ),
                parser.parseInput("(1/3)+2").calculate()
        );

        assertEquals(
                ValueFactory.ratio(
                        ValueFactory.toValue(2),
                        ValueFactory.toValue(3)
                ).toString(),
                parser.parseInput("(1/3)+(2/6)").calculate().toString()
        );

        assertEquals(
                ValueFactory.ratio(
                        ValueFactory.toValue(1),
                        ValueFactory.toValue(9)
                ),
                parser.parseInput("(1/3)*(1/3)").calculate()
        );

        assertEquals(
                ValueFactory.zero(),
                parser.parseInput("(1/3)-(1/3)").calculate()
        );

    }

}
