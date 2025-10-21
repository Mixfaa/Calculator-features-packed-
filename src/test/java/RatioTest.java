import com.mixfa.calculator.MathComponent;
import com.mixfa.calculator.ValueFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class RatioTest {
    @Test
    public void test() {
        var ratio = new MathComponent.Value.RatioValue(
                ValueFactory.toValue(-1),
                ValueFactory.toValue(-1)
        );

        Assertions.assertEquals(ratio.numerator(), ValueFactory.toValue(1));
        Assertions.assertEquals(ratio.denominator(), ValueFactory.toValue(1));
    }
}
