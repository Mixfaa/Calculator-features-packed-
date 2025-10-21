import com.mixfa.calculator.MathComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.mixfa.calculator.MathUtils.toValue;

public class RatioTest {
    @Test
    public void test() {
        var ratio = new MathComponent.Value.RatioValue(
                toValue(-1),
                toValue(-1)
        );

        Assertions.assertEquals(ratio.numerator(), toValue(1));
        Assertions.assertEquals(ratio.denominator(), toValue(1));
    }
}
