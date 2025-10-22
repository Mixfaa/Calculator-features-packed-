import com.mixfa.calculator.MathComponent;
import com.mixfa.calculator.ValueFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RatioTest {
    @Test
    public void test() {
        var ratio1 = new MathComponent.Value.RatioValue(
                ValueFactory.toValue(-1),
                ValueFactory.toValue(-2)
        );

        var ratio2 = new MathComponent.Value.RatioValue(
                ValueFactory.toValue(-1),
                ValueFactory.toValue(2)
        );

        var ratio3 = new MathComponent.Value.RatioValue(
                ValueFactory.toValue(1),
                ValueFactory.toValue(-2)
        );

        Assertions.assertEquals(ratio1.numerator(), ValueFactory.toValue(1));
        Assertions.assertEquals(ratio1.denominator(), ValueFactory.toValue(2));

        Assertions.assertEquals(ratio2.numerator(), ValueFactory.toValue(-1));
        Assertions.assertEquals(ratio2.denominator(), ValueFactory.toValue(2));

        Assertions.assertEquals(ratio3.numerator(), ValueFactory.toValue(-1));
        Assertions.assertEquals(ratio3.denominator(), ValueFactory.toValue(2));

        ratio1 = (MathComponent.Value.RatioValue) ratio1.abs();
        ratio2 = (MathComponent.Value.RatioValue) ratio2.abs();
        ratio3 = (MathComponent.Value.RatioValue) ratio3.abs();

        Assertions.assertEquals(ratio1.numerator(), ValueFactory.toValue(1));
        Assertions.assertEquals(ratio1.denominator(), ValueFactory.toValue(2));

        Assertions.assertEquals(ratio2.numerator(), ValueFactory.toValue(1));
        Assertions.assertEquals(ratio2.denominator(), ValueFactory.toValue(2));

        Assertions.assertEquals(ratio3.numerator(), ValueFactory.toValue(1));
        Assertions.assertEquals(ratio3.denominator(), ValueFactory.toValue(2));
    }
}
